package com.example.markdowntasks.data

import android.net.Uri
import com.example.markdowntasks.TasksApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

class TaskRepository {
    private var currentDirectory: File? = null

    fun setDirectory(uri: Uri) {
        val context = TasksApplication.appContext
        val docFile = context.contentResolver.openInputStream(uri)?.use { input ->
            File(context.filesDir, "tasks").apply {
                parentFile?.mkdirs()
                outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        currentDirectory = docFile
    }

    fun getTasks(): Flow<List<Task>> = flow {
        val tasks = withContext(Dispatchers.IO) {
            currentDirectory?.let { dir ->
                dir.walkTopDown()
                    .filter { it.isFile && it.extension.lowercase() == "md" }
                    .flatMap { file ->
                        file.readLines().mapNotNull { line ->
                            Task.fromMarkdown(line)
                        }
                    }
                    .toList()
            } ?: emptyList()
        }
        emit(tasks)
    }

    fun searchTasks(
        query: String? = null,
        tags: List<String>? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Flow<List<Task>> = flow {
        val tasks = withContext(Dispatchers.IO) {
            getTasks().collect { allTasks ->
                allTasks.filter { task ->
                    var matches = true
                    
                    if (!query.isNullOrBlank()) {
                        matches = matches && task.description.contains(query, ignoreCase = true)
                    }
                    
                    if (!tags.isNullOrEmpty()) {
                        matches = matches && task.tags.any { it in tags }
                    }
                    
                    if (startDate != null) {
                        matches = matches && (task.startDate?.isAfter(startDate) ?: false)
                    }
                    
                    if (endDate != null) {
                        matches = matches && (task.dueDate?.isBefore(endDate) ?: false)
                    }
                    
                    matches
                }
            }
        }
        emit(tasks)
    }

    suspend fun toggleTaskCompletion(task: Task) {
        withContext(Dispatchers.IO) {
            currentDirectory?.walkTopDown()?.forEach { file ->
                if (file.isFile && file.extension.lowercase() == "md") {
                    val lines = file.readLines().toMutableList()
                    val index = lines.indexOf(task.originalLine)
                    if (index != -1) {
                        val updatedTask = task.copy(isCompleted = !task.isCompleted)
                        lines[index] = Task.toMarkdown(updatedTask)
                        file.writeText(lines.joinToString("\n"))
                        return@withContext
                    }
                }
            }
        }
    }
}
