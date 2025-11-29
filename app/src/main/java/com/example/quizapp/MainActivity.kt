package com.example.quizapp


import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.ui.theme.QuizAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizAppTheme {
                QuizApp()
            }
        }
    }
}
@Composable
fun QuizApp(viewModel: QuizViewModel = viewModel()) {
    when (val screen = viewModel.currentScreen) {
        is Screen.Main -> MainScreen(viewModel)
        is Screen.Quiz -> QuizScreen(viewModel, screen.category)
        is Screen.Result -> ResultScreen(viewModel)
        is Screen.WrongNotes -> WrongNoteScreen(viewModel)
        is Screen.Ranking -> RankingScreen(viewModel)
    }
}


sealed class Screen {
    object Main : Screen()
    data class Quiz(val category: String) : Screen()
    object Result : Screen()
    object WrongNotes : Screen()
    object Ranking : Screen()
}
@Composable
fun MainScreen(viewModel: QuizViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("퀴즈 주제 선택", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        QuestionData.data.keys.forEach { category ->
            Button(
                onClick = { viewModel.startQuiz(category) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(category)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { viewModel.goToWrongNotes() }) { Text("오답 노트") }
        Button(onClick = { viewModel.goToRanking() }) { Text("랭킹 보기") }
    }
}
@Composable
fun QuizScreen(viewModel: QuizViewModel, category: String) {
    val question = viewModel.questions[viewModel.currentIndex]
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("[${category}] 문제 ${viewModel.currentIndex + 1}/${viewModel.questions.size}", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = question.question, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))
        question.options.forEachIndexed { index, option ->
            Button(
                onClick = { viewModel.answer(index) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = option)
            }
        }
    }
}
@Composable
fun ResultScreen(viewModel: QuizViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("결과 화면", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("총 점수: ${viewModel.score} / ${viewModel.questions.size}", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Text("정답 확인", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(viewModel.questions) { question ->
                val isWrong = viewModel.wrongAnswers.contains(question)
                Text(
                    text = if (isWrong) "❌ ${question.question} → 정답: ${question.options[question.answerIndex]}"
                    else "✅ ${question.question}",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { viewModel.goToMain() }) {
            Text("메인으로 돌아가기")
        }
    }
}
@Composable
fun WrongNoteScreen(viewModel: QuizViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("오답 노트", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.wrongAnswers.isEmpty()) {
            Text("틀린 문제가 없습니다!", fontSize = 16.sp)
        } else {
            LazyColumn {
                items(viewModel.wrongAnswers) { question ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("❌ ${question.question}", fontSize = 16.sp)
                        Text("정답: ${question.options[question.answerIndex]}", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { viewModel.goToMain() }) {
            Text("메인으로 돌아가기")
        }
    }
}
@Composable
fun RankingScreen(viewModel: QuizViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("내 랭킹 기록", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.rankings.isEmpty()) {
            Text("아직 저장된 기록이 없습니다.", fontSize = 16.sp)
        } else {
            LazyColumn {
                items(viewModel.rankings) { (score, date) ->
                    Text("📅 $date - 점수: $score", modifier = Modifier.padding(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { viewModel.goToMain() }) {
            Text("메인으로 돌아가기")
        }
    }
}