package com.example.markdowntasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.markdowntasks.data.Task
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    onSelectDirectory: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Markdown Tasks") },
                actions = {
                    IconButton(onClick = onSelectDirectory) {
                        Icon(Icons.Default.Folder, "Select Directory")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search Bar
            SearchBar(
                query = viewModel.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Tags Filter
            TagsFilter(
                selectedTags = viewModel.selectedTags,
                onTagToggle = viewModel::toggleTag,
                availableTags = tasks.flatMap { it.tags }.distinct(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Tasks List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = { viewModel.toggleTaskCompletion(task) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search tasks...") },
        leadingIcon = { Icon(Icons.Default.Search, "Search") },
        singleLine = true
    )
}

@Composable
fun TagsFilter(
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit,
    availableTags: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        availableTags.forEach { tag ->
            FilterChip(
                selected = tag in selectedTags,
                onClick = { onTagToggle(tag) },
                label = { Text(tag) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onTaskClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onTaskClick() }
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            // Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                task.startDate?.let { date ->
                    Text(
                        text = "Start: ${date.format(DateTimeFormatter.ISO_DATE)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                task.dueDate?.let { date ->
                    Text(
                        text = "Due: ${date.format(DateTimeFormatter.ISO_DATE)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Tags
            if (task.tags.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    task.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(tag) }
                        )
                    }
                }
            }
            
            // Priority
            if (task.priority != Task.Priority.NORMAL) {
                Text(
                    text = "Priority: ${task.priority}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
