package com.example.hiragana.ui.views

import com.example.hiragana.data.Hiragana
import com.example.hiragana.data.HiraganaData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private var currentLevel = 0
    private var completions = 0
    private var score = 0

    // СЛУЧАЙНЫЙ ПОРЯДОК внутри уровня
    private val levelHiraganas = mutableListOf<Hiragana>()
    private var currentHiraganaIndex = 0

    // НОВОЕ: состояние для цветной обратной связи
    private val _answerResult = MutableStateFlow<AnswerResult?>(null)
    val answerResult: StateFlow<AnswerResult?> = _answerResult.asStateFlow()

    private val _uiState = MutableStateFlow(GameUiState(isLoading = true))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _currentScreen = MutableStateFlow("main")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _showAlphabet = MutableStateFlow(false)
    val showAlphabet: StateFlow<Boolean> = _showAlphabet.asStateFlow()

    fun goToAlphabet() {
        _showAlphabet.value = true
    }

    fun backFromAlphabet() {
        _showAlphabet.value = false
    }


    fun startGame() {
        currentLevel = 0
        completions = 0
        score = 0
        _answerResult.value = null

        levelHiraganas.clear()
        levelHiraganas.addAll(HiraganaData.levels[currentLevel].shuffled())
        currentHiraganaIndex = 0

        _currentScreen.value = "game"
        loadNextInLevel()
    }

    fun startPracticeRow(rowIndex: Int) {
        currentLevel = rowIndex
        completions = 0
        score = 0
        _answerResult.value = null

        levelHiraganas.clear()
        levelHiraganas.addAll(HiraganaData.levels[rowIndex].shuffled())
        currentHiraganaIndex = 0

        _currentScreen.value = "game"  // ← Переход в GameScreen!
        loadNextInLevel()
    }

    fun selectAnswer(answer: String): Boolean {
        val state = uiState.value
        val correct = state.currentHiragana.romaji == answer

        // Показываем цвет сразу
        _answerResult.value = if (correct) AnswerResult.CORRECT else AnswerResult.INCORRECT

        if (correct) {
            score += 10
            currentHiraganaIndex++

            if (currentHiraganaIndex >= levelHiraganas.size) {
                completions++
                if (completions >= 3) {
                    currentLevel++
                    completions = 0
                    if (currentLevel >= HiraganaData.levels.size) {
                        _uiState.value = uiState.value.copy(gameWon = true, score = score)
                        return true
                    } else {
                        startNewLevel()
                    }
                } else {
                    startNewLevel()
                }
            } else {
                nextHiraganaWithDelay()
            }
        } else {
            // Неправильно — новый рандом из уровня
            levelHiraganas.clear()
            levelHiraganas.addAll(HiraganaData.levels[currentLevel].shuffled())
            currentHiraganaIndex = 0
            nextHiraganaWithDelay()
        }

        _uiState.value = uiState.value.copy(score = score)
        return correct
    }

    private fun nextHiraganaWithDelay() {
        viewModelScope.launch {
            delay(50)  // 1 секунда на цвет
            _answerResult.value = null
            loadNextInLevel()
        }
    }

    private fun startNewLevel() {
        levelHiraganas.clear()
        levelHiraganas.addAll(HiraganaData.levels[currentLevel].shuffled())
        currentHiraganaIndex = 0
        nextHiraganaWithDelay()
    }

    private fun loadNextInLevel() {
        try {
            if (currentLevel >= HiraganaData.levels.size) {
                _uiState.value = GameUiState(gameWon = true, score = score, isLoading = false)
                return
            }

            val currentHiragana = levelHiraganas[currentHiraganaIndex]

            val currentRowRomaji = levelHiraganas.map { it.romaji }.shuffled()
            val allRomaji = HiraganaData.levels.flatten().map { it.romaji }.shuffled()

            val options = mutableListOf<String>()
            options.add(currentHiragana.romaji)

            repeat(2) {
                val randomRomaji = currentRowRomaji.random()
                if (randomRomaji != currentHiragana.romaji && !options.contains(randomRomaji)) {
                    options.add(randomRomaji)
                    return@repeat
                }
            }

            var attempts = 0
            while (options.size < 3 && attempts < 50) {
                val randomRomaji = allRomaji.random()
                if (randomRomaji != currentHiragana.romaji && !options.contains(randomRomaji)) {
                    options.add(randomRomaji)
                }
                attempts++
            }
            options.shuffle()

            _uiState.value = GameUiState(
                currentHiragana = currentHiragana,
                options = options,
                currentLevelHiraganas = levelHiraganas,
                level = currentLevel + 1,
                completions = completions,
                score = score,
                isLoading = false
            )
        } catch (e: Exception) {
            _uiState.value = GameUiState(
                error = "Ошибка: ${e.message}",
                isLoading = false
            )
        }
    }

    fun goBackToMain() {
        _currentScreen.value = "main"
        resetGame()
    }

    private fun resetGame() {
        _answerResult.value = null
        levelHiraganas.clear()
        currentHiraganaIndex = 0
    }
}

// НОВОЕ: enum для цветовой обратной связи
enum class AnswerResult {
    CORRECT, INCORRECT
}

data class GameUiState(
    val currentHiragana: Hiragana = Hiragana("", ""),
    val options: List<String> = emptyList(),
    val currentLevelHiraganas: List<Hiragana> = emptyList(),
    val level: Int = 1,
    val completions: Int = 0,
    val score: Int = 0,
    val gameWon: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
