package com.example.hiragana.ui.views

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
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToAlphabet: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var lastResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // ✅ УБИРАЕМ LaunchedEffect — НЕ сбрасываем прогресс!

    // Автозапуск ТОЛЬКО при первой загрузке
    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            scope.launch {
                viewModel.startGame()
            }
        }
    }

    Scaffold(
        bottomBar = {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToAlphabet,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .height(56.dp)
            ) {
                Text("📚 Посмотреть алфавит", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> Text(
                    "Ошибка: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
                state.gameWon -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉 Поздравляем! 🎉", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Все уровни пройдены!", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Итоговый счёт: ${state.score}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.startGame() }) {
                        Text("Начать заново")
                    }
                }
                else -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // ✅ FEEDBACK ПОД СИМВОЛОМ (компактный, 60dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(vertical = 8.dp)
                    ) {
                        lastResult?.let { (isCorrect, _) ->
                            Text(
                                text = if (isCorrect) "✅ Правильно!" else "❌ Неверно!",
                                fontSize = 20.sp,
                                color = if (isCorrect) Color.Green else Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    // ✅ СИМВОЛ ХИРАГАНА (центр экрана)
                    Text(
                        text = state.currentHiragana.symbol,
                        fontSize = 140.sp,
                        fontWeight = Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Прогресс уровня
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Уровень ${state.level} (${state.currentLevelHiraganas.size} букв)",
                                fontSize = 18.sp
                            )
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
                                    kotlinx.coroutines.delay(2000)
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

                    // Счёт
                    Text(
                        text = "Очки: ${state.score}",
                        fontSize = 28.sp,
                        fontWeight = Bold
                    )
                }
            }
        }
    }
}
