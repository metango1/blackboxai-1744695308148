package com.example.markdowntasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.markdowntasks.ui.TaskScreen
import com.example.markdowntasks.ui.TaskViewModel
import com.example.markdowntasks.ui.theme.MarkdownTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: TaskViewModel = viewModel()
            val directoryPicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree()
            ) { uri ->
                uri?.let { viewModel.setDirectory(it) }
            }

            MarkdownTasksTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TaskScreen(
                        viewModel = viewModel,
                        onSelectDirectory = { directoryPicker.launch(null) }
                    )
                }
            }
        }
    }
}
