package com.shan.word

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.shan.word.presentation.screen.MainScreen
import com.shan.word.presentation.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Your Compose UI or layout
            MainScreen(viewModel = viewModel)
        }
    }
}