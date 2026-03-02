package com.example.hiragana.ui.views

import com.example.hiragana.data.Hiragana
import com.example.hiragana.data.HiraganaData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private var currentLevel = 0
    private var completions = 0
    private var currentIndexInLevel = 0
    private var score = 0

    private val _uiState = MutableStateFlow(GameUiState(isLoading = true))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun startGame() {
        currentLevel = 0
        completions = 0
        currentIndexInLevel = 0
        score = 0
        loadNextInLevel()
    }

    fun selectAnswer(answer: String): Boolean {
        val state = uiState.value
        val correct = state.currentHiragana.romaji == answer

        if (correct) {
            score += 10
            currentIndexInLevel++

            if (currentIndexInLevel >= state.currentLevelHiraganas.size) {
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
                loadNextInLevel()
            }
        } else {
            currentIndexInLevel = 0
            loadNextInLevel()
        }

        _uiState.value = uiState.value.copy(score = score)
        return correct
    }

    private fun startNewLevel() {
        currentIndexInLevel = 0
        loadNextInLevel()
    }

    private fun loadNextInLevel() {
        try {
            if (currentLevel >= HiraganaData.levels.size) {
                _uiState.value = GameUiState(gameWon = true, score = score, isLoading = false)
                return
            }

            val levelHiraganas = HiraganaData.levels[currentLevel]
            if (currentIndexInLevel >= levelHiraganas.size) {
                currentIndexInLevel = 0
            }

            val currentHiragana = levelHiraganas[currentIndexInLevel]

            val allRomaji = HiraganaData.levels.flatten().map { it.romaji }.shuffled()
            val options = mutableListOf<String>()
            options.add(currentHiragana.romaji)

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
                error = "Ошибка загрузки: ${e.message}",
                isLoading = false
            )
        }
    }
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
