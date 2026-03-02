package com.example.hiragana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.hiragana.ui.views.AlphabetScreen
import com.example.hiragana.ui.views.GameScreen

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
fun App() {
    var currentScreen by remember { mutableStateOf("game") }

    when (currentScreen) {
        "game" -> GameScreen(
            onNavigateToAlphabet = { currentScreen = "alphabet" }
        )
        "alphabet" -> AlphabetScreen(
            onBack = { currentScreen = "game" }
        )
    }
}
