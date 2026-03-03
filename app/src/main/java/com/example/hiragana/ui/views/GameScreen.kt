package com.example.hiragana.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToAlphabet: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var lastResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            scope.launch {
                viewModel.startPracticeRow(0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Практика Хираганы") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToAlphabet) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "К алфавиту")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> Text("Ошибка: ${state.error}", color = Color.Red)
                state.gameWon -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉 Поздравляем!", fontSize = 28.sp)
                    Text("Счёт: ${state.score}", fontSize = 18.sp)
                    Button(onClick = { viewModel.startPracticeRow(0) }) {
                        Text("Играть снова")
                    }
                }
                else -> {
                    // 1. ПРОГРЕСС
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Ряд ${state.level}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Проходов: ${state.completions + 1}/3")
                            LinearProgressIndicator(progress = { (state.completions + 1f) / 3f })
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. РОМАДЗИ (раскомментировано)
/*                    Text(
                        text = state.currentHiragana.romaji,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )*/

                    // 3. ЦВЕТНАЯ БУКВА
                    GameCard(
                        currentKana = state.currentHiragana.symbol,
                        isCorrect = lastResult?.first,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. КНОПКИ ОТВЕТОВ
                    state.options.forEach { option ->
                        OutlinedButton(
                            onClick = {
                                val correct = viewModel.selectAnswer(option)
                                lastResult = correct to option
                                scope.launch {
                                    kotlinx.coroutines.delay(50)
                                    lastResult = null
                                }
                            },
                            enabled = lastResult == null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(option, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. СЧЁТ (раскомментировано)
/*                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "Очки: ${state.score}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }*/
                }
            }
        }
    }
}

@Composable
private fun GameCard(
    currentKana: String,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (isCorrect) {
        true -> Color(0xFF4CAF50).copy(alpha = 0.3f)
        false -> Color(0xFFF44336).copy(alpha = 0.3f)
        null -> Color.Transparent
    }

    Card(
        modifier = modifier.size(180.dp),  // Компактнее
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentKana,
                fontSize = 90.sp,  // Компактнее
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
