package com.example.markdowntasks.data

import java.time.LocalDate

data class Task(
    val description: String,
    val isCompleted: Boolean,
    val startDate: LocalDate?,
    val dueDate: LocalDate?,
    val tags: List<String>,
    val priority: Priority,
    val originalLine: String // Original markdown line for updating the file
) {
    enum class Priority {
        LOWEST,  // ⏬
        LOW,     // 🔽
        NORMAL,  // (no symbol)
        HIGH,    // 🔼
        HIGHEST, // ⏫
        CRITICAL // 🔺
    }

    companion object {
        private val TASK_REGEX = Regex("""- \[([ xX])\] (.+)""")
        private val DATE_REGEX = Regex("""(➕|📅) (\d{4}-\d{2}-\d{2})""")
        private val TAG_REGEX = Regex("""#[\w/]+""")
        private val PRIORITY_MAP = mapOf(
            "⏬" to Priority.LOWEST,
            "🔽" to Priority.LOW,
            "🔼" to Priority.HIGH,
            "⏫" to Priority.HIGHEST,
            "🔺" to Priority.CRITICAL
        )

        fun fromMarkdown(line: String): Task? {
            val match = TASK_REGEX.find(line) ?: return null
            val (status, content) = match.destructured
            
            val startDate = DATE_REGEX.find(content)?.let { 
                if (it.groupValues[1] == "➕") LocalDate.parse(it.groupValues[2]) else null 
            }
            
            val dueDate = DATE_REGEX.find(content)?.let { 
                if (it.groupValues[1] == "📅") LocalDate.parse(it.groupValues[2]) else null 
            }
            
            val tags = TAG_REGEX.findAll(content).map { it.value }.toList()
            
            val priority = PRIORITY_MAP.entries.find { content.contains(it.key) }?.value ?: Priority.NORMAL
            
            return Task(
                description = content.trim(),
                isCompleted = status.trim().lowercase() == "x",
                startDate = startDate,
                dueDate = dueDate,
                tags = tags,
                priority = priority,
                originalLine = line
            )
        }

        fun toMarkdown(task: Task): String {
            return "- [${if (task.isCompleted) "x" else " "}] ${task.description}"
        }
    }
}
