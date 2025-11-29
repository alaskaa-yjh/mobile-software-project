package com.example.quizapp


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*


class QuizViewModel : ViewModel() {
    var currentScreen  by mutableStateOf<Screen>(Screen.Main)
    var selectedCategory by mutableStateOf("")
    var questions by mutableStateOf(listOf<Question>())
    var currentIndex by mutableStateOf(0)
    var score by mutableStateOf(0)
    var wrongAnswers = mutableListOf<Question>()
    var rankings = mutableStateListOf<Pair<Int, String>>()


    fun startQuiz(category: String) {
        selectedCategory = category
        questions = QuestionData.getQuestions(category)
        currentIndex = 0
        score = 0
        wrongAnswers.clear()
        currentScreen = Screen.Quiz(category)
    }


    fun answer(selected: Int) {
        val correct = questions[currentIndex].answerIndex
        if (selected == correct) {
            score++
            playCorrectSound()
        } else {
            wrongAnswers.add(questions[currentIndex])
            playWrongSound()
        }
        if (currentIndex < questions.lastIndex) {
            currentIndex++
        } else {
            rankings.add(Pair(score, getCurrentTime()))
            rankings.sortByDescending { it.first }
            currentScreen = Screen.Result
        }
    }


    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }


    private fun playCorrectSound() {
// MediaPlayer.create(context, R.raw.correct).start()
    }


    private fun playWrongSound() {
// MediaPlayer.create(context, R.raw.wrong).start()
    }


    fun goToMain() { currentScreen = Screen.Main }
    fun goToWrongNotes() { currentScreen = Screen.WrongNotes }
    fun goToRanking() { currentScreen = Screen.Ranking }
}