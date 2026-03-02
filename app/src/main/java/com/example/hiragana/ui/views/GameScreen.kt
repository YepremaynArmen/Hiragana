
package com.example.hiragana.ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onNavigateToAlphabet: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var lastResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.startGame()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()
            state.error != null -> Text("Ошибка: ${state.error}", color = MaterialTheme.colorScheme.error)
            state.gameWon -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉 Поздравляем! 🎉", fontSize = 32.sp)
                Text("Все уровни пройдены!", fontSize = 24.sp)
                Text("Итоговый счёт: ${state.score}", fontSize = 20.sp)
                Button(onClick = { viewModel.startGame() }) { Text("Начать заново") }
            }
            else -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Feedback
                lastResult?.let { (isCorrect, _) ->
                    AnimatedVisibility(visible = true) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) Color.Green.copy(alpha = 0.2f)
                                else Color.Red.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = if (isCorrect) "✅ Правильно!" else "❌ Неверно!",
                                fontSize = 24.sp,
                                color = if (isCorrect) Color.Green else Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // Символ
                Text(
                    state.currentHiragana.symbol,
                    fontSize = 140.sp,
                    fontWeight = Bold,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Прогресс
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Уровень ${state.level} (${state.currentLevelHiraganas.size} букв)", fontSize = 18.sp)
                        Text("Проходов: ${state.completions + 1} из 3")
                        LinearProgressIndicator(
                            progress = { (state.completions + 1f) / 3f },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Кнопки ответов
                state.options.forEach { option ->
                    OutlinedButton(
                        onClick = {
                            val correct = viewModel.selectAnswer(option)
                            lastResult = correct to option
                            scope.launch {
                                delay(2000)
                                lastResult = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(64.dp)
                    ) {
                        Text(option, fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Очки: ${state.score}", fontSize = 28.sp, fontWeight = Bold)

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = onNavigateToAlphabet) {
                    Text("📚 Посмотреть алфавит")
                }
            }
        }
    }
}
