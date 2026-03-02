package com.example.hiragana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hiragana.ui.views.AlphabetScreen
import com.example.hiragana.ui.views.GameScreen
import com.example.hiragana.ui.views.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App(gameViewModel: GameViewModel = viewModel()) {  // ← Shared ViewModel!
    var currentScreen by remember { mutableStateOf("game") }

    when (currentScreen) {
        "game" -> GameScreen(
            viewModel = gameViewModel,  // ← ТОТ ЖЕ ViewModel!
            onNavigateToAlphabet = { currentScreen = "alphabet" }
        )
        "alphabet" -> AlphabetScreen(
            onBack = { currentScreen = "game" }
        )
    }
}
