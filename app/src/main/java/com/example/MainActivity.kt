package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainAppScreen
import com.example.ui.theme.MuslimProTheme
import com.example.ui.viewmodel.MuslimViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuslimProTheme {
                val viewModel: MuslimViewModel = viewModel()
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}
