package com.example.quizapp


import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val soundManager = SoundManager(application.applicationContext)
    var currentScreen  by mutableStateOf<Screen>(Screen.Main)
    var selectedCategory by mutableStateOf("")
    var questions by mutableStateOf(listOf<Question>())
    var currentIndex by mutableStateOf(0)
    var score by mutableStateOf(0)
    var wrongAnswers = mutableListOf<Question>()
    var rankings = mutableStateListOf<Pair<Int, String>>()

    // 선택한 답변과 정답 여부를 추적
    var selectedAnswer by mutableStateOf<Int?>(null)
    var isAnswered by mutableStateOf(false)


    fun startQuiz(category: String) {
        selectedCategory = category
        questions = QuestionData.getQuestions(category)
        currentIndex = 0
        score = 0
        wrongAnswers.clear()
        selectedAnswer = null
        isAnswered = false
        currentScreen = Screen.Quiz(category)
    }


    fun answer(selected: Int) {
        if (isAnswered) return // 이미 답변한 경우 무시

        selectedAnswer = selected
        isAnswered = true

        val correct = questions[currentIndex].answerIndex
        if (selected == correct) {
            score++
            playCorrectSound()
        } else {
            wrongAnswers.add(questions[currentIndex])
            playWrongSound()
        }

        // 1.5초 후 다음 문제로 이동
        viewModelScope.launch {
            delay(1500)
            if (currentIndex < questions.lastIndex) {
                currentIndex++
                selectedAnswer = null
                isAnswered = false
            } else {
                rankings.add(Pair(score, getCurrentTime()))
                rankings.sortByDescending { it.first }
                currentScreen = Screen.Result
            }
        }
    }


    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }


    private fun playCorrectSound() {
        soundManager.playCorrectSound()
    }


    private fun playWrongSound() {
        soundManager.playWrongSound()
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }


    fun goToMain() { currentScreen = Screen.Main }
    fun goToWrongNotes() { currentScreen = Screen.WrongNotes }
    fun goToRanking() { currentScreen = Screen.Ranking }
}