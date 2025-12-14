package com.example.quizapp


import android.app.Application
import android.content.Context
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val soundManager = SoundManager(application.applicationContext)
    private val sharedPreferences = application.getSharedPreferences("QuizAppPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    var currentScreen  by mutableStateOf<Screen>(Screen.Main)
    var selectedCategory by mutableStateOf("")
    var questions by mutableStateOf(listOf<Question>())
    var currentIndex by mutableStateOf(0)
    var score by mutableStateOf(0)
    var wrongAnswers = mutableStateListOf<Question>()
    var rankings = mutableStateListOf<RankingEntry>()

    // 선택한 답변과 정답 여부를 추적
    var selectedAnswer by mutableStateOf<Int?>(null)
    var isAnswered by mutableStateOf(false)

    init {
        loadWrongAnswers()
        loadRankings()
    }

    private fun loadWrongAnswers() {
        val json = sharedPreferences.getString("wrongAnswers", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Question>>() {}.type
                val savedList: List<Question> = gson.fromJson(json, type)
                wrongAnswers.clear()
                wrongAnswers.addAll(savedList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveWrongAnswers() {
        val json = gson.toJson(wrongAnswers.toList())
        sharedPreferences.edit().putString("wrongAnswers", json).apply()
    }

    private fun loadRankings() {
        val json = sharedPreferences.getString("rankings", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<RankingEntry>>() {}.type
                val savedList: List<RankingEntry> = gson.fromJson(json, type)
                rankings.clear()
                rankings.addAll(savedList)
            } catch (e: Exception) {
                e.printStackTrace()
                // 오류 발생 시 기존 데이터 삭제하고 빈 리스트로 초기화
                sharedPreferences.edit().remove("rankings").apply()
                rankings.clear()
            }
        }
    }

    private fun saveRankings() {
        val json = gson.toJson(rankings.toList())
        sharedPreferences.edit().putString("rankings", json).apply()
    }


    fun startQuiz(category: String) {
        selectedCategory = category
        questions = QuestionData.getQuestions(category)
        currentIndex = 0
        score = 0
        // wrongAnswers.clear() 제거 - 이전 오답들을 누적해서 보관
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
            val wrongQuestion = questions[currentIndex]
            // 중복되지 않은 경우에만 추가
            if (!wrongAnswers.contains(wrongQuestion)) {
                wrongAnswers.add(wrongQuestion)
                saveWrongAnswers() // 오답 저장
            }
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
                rankings.add(RankingEntry(score, getCurrentTime()))
                rankings.sortByDescending { it.score }
                saveRankings() // 랭킹 저장
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
    
    fun clearWrongAnswers() {
        wrongAnswers.clear()
        saveWrongAnswers()
    }
    
    fun clearRankings() {
        rankings.clear()
        saveRankings()
    }
}