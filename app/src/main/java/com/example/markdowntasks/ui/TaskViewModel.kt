package com.example.markdowntasks.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.markdowntasks.data.Task
import com.example.markdowntasks.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    var searchQuery by mutableStateOf("")
        private set

    var selectedTags by mutableStateOf<Set<String>>(emptySet())
        private set

    var startDate by mutableStateOf<LocalDate?>(null)
        private set

    var endDate by mutableStateOf<LocalDate?>(null)
        private set

    fun setDirectory(uri: Uri) {
        repository.setDirectory(uri)
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            repository.getTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun searchTasks() {
        viewModelScope.launch {
            repository.searchTasks(
                query = searchQuery.takeIf { it.isNotBlank() },
                tags = selectedTags.toList().takeIf { it.isNotEmpty() },
                startDate = startDate,
                endDate = endDate
            ).collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        searchTasks()
    }

    fun toggleTag(tag: String) {
        selectedTags = if (tag in selectedTags) {
            selectedTags - tag
        } else {
            selectedTags + tag
        }
        searchTasks()
    }

    fun setDateRange(start: LocalDate?, end: LocalDate?) {
        startDate = start
        endDate = end
        searchTasks()
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
            loadTasks()
        }
    }
}
