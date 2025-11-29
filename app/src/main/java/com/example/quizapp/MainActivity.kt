package com.example.quizapp


import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.ui.theme.*


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
    val categoryColors = mapOf(
        "역사" to HistoryColor,
        "상식" to CommonColor,
        "과학" to ScienceColor,
        "영화" to MovieColor
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "🎯 퀴즈 앱",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = QuizPrimary
        )
        Text(
            text = "주제를 선택하세요",
            fontSize = 16.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        QuestionData.data.keys.forEach { category ->
            CategoryCard(
                category = category,
                color = categoryColors[category] ?: QuizPrimary,
                onClick = { viewModel.startQuiz(category) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = { viewModel.goToWrongNotes() },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("📝 오답 노트")
            }
            OutlinedButton(
                onClick = { viewModel.goToRanking() },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("🏆 랭킹")
            }
        }
    }
}

@Composable
fun CategoryCard(category: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
@Composable
fun QuizScreen(viewModel: QuizViewModel, category: String) {
    val question = viewModel.questions[viewModel.currentIndex]
    val progress = (viewModel.currentIndex + 1).toFloat() / viewModel.questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Progress indicator
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuizPrimary
                )
                Text(
                    text = "${viewModel.currentIndex + 1}/${viewModel.questions.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = QuizPrimary,
                trackColor = OptionGray,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.question,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = TextPrimary,
                    lineHeight = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Options
        question.options.forEachIndexed { index, option ->
            OptionButton(
                text = option,
                optionNumber = index + 1,
                onClick = { viewModel.answer(index) },
                isSelected = viewModel.selectedAnswer == index,
                isCorrect = question.answerIndex == index,
                isAnswered = viewModel.isAnswered
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun OptionButton(
    text: String,
    optionNumber: Int,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    isCorrect: Boolean = false,
    isAnswered: Boolean = false
) {
    val backgroundColor = when {
        isAnswered && isSelected && isCorrect -> CorrectGreen.copy(alpha = 0.3f)
        isAnswered && isSelected && !isCorrect -> WrongRed.copy(alpha = 0.3f)
        isAnswered && isCorrect -> CorrectGreen.copy(alpha = 0.2f)
        else -> OptionGray
    }

    val borderColor = when {
        isAnswered && isSelected && isCorrect -> CorrectGreen
        isAnswered && isSelected && !isCorrect -> WrongRed
        isAnswered && isCorrect -> CorrectGreen
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isAnswered, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isAnswered && isSelected && isCorrect -> CorrectGreen
                                isAnswered && isSelected && !isCorrect -> WrongRed
                                isAnswered && isCorrect -> CorrectGreen
                                else -> QuizPrimary
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = optionNumber.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            // O/X 표시
            if (isAnswered) {
                Text(
                    text = when {
                        isCorrect -> "⭕"
                        isSelected && !isCorrect -> "❌"
                        else -> ""
                    },
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
@Composable
fun ResultScreen(viewModel: QuizViewModel) {
    val percentage = (viewModel.score.toFloat() / viewModel.questions.size * 100).toInt()
    val resultMessage = when {
        percentage >= 80 -> "훌륭해요! 🎉"
        percentage >= 60 -> "잘했어요! 👏"
        percentage >= 40 -> "조금만 더! 💪"
        else -> "다시 도전! 📚"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Result card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "퀴즈 완료!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuizPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = resultMessage,
                            fontSize = 24.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Score display
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(60.dp))
                                .background(QuizPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$percentage%",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${viewModel.score}/${viewModel.questions.size}",
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Answer review section title
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "정답 확인",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Answer review items
            items(viewModel.questions) { question ->
                val isWrong = viewModel.wrongAnswers.contains(question)
                AnswerReviewItem(
                    question = question,
                    isCorrect = !isWrong
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Back button
            item {
                Button(
                    onClick = { viewModel.goToMain() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("메인으로 돌아가기", fontSize = 18.sp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AnswerReviewItem(question: Question, isCorrect: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) CorrectGreen.copy(alpha = 0.1f)
            else WrongRed.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isCorrect) "✅" else "❌",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = question.question,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "정답: ${question.options[question.answerIndex]}",
                    fontSize = 12.sp,
                    color = if (isCorrect) CorrectGreen else WrongRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun WrongNoteScreen(viewModel: QuizViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "📝 오답 노트",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = QuizPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.wrongAnswers.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "모든 문제를 맞췄어요!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "완벽합니다!",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = "틀린 문제: ${viewModel.wrongAnswers.size}개",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = WrongRed
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn {
                        items(viewModel.wrongAnswers) { question ->
                            WrongAnswerCard(question)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.goToMain() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("메인으로 돌아가기", fontSize = 18.sp)
        }
    }
}

@Composable
fun WrongAnswerCard(question: Question) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WrongRed.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text("❌", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = question.question,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CorrectGreen.copy(alpha = 0.2f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "정답: ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CorrectGreen
                        )
                        Text(
                            text = question.options[question.answerIndex],
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun RankingScreen(viewModel: QuizViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "🏆 나의 랭킹",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = QuizPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (viewModel.rankings.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📊", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "아직 기록이 없어요",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "퀴즈를 풀고 기록을 남겨보세요!",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = "전체 기록: ${viewModel.rankings.size}개",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuizPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn {
                        items(viewModel.rankings.withIndex().toList()) { (index, ranking) ->
                            RankingCard(
                                rank = index + 1,
                                score = ranking.first,
                                date = ranking.second
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.goToMain() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QuizPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("메인으로 돌아가기", fontSize = 18.sp)
        }
    }
}

@Composable
fun RankingCard(rank: Int, score: Int, date: String) {
    val medalEmoji = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "🎖️"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (rank) {
                1 -> Color(0xFFFFD700).copy(alpha = 0.15f)
                2 -> Color(0xFFC0C0C0).copy(alpha = 0.15f)
                3 -> Color(0xFFCD7F32).copy(alpha = 0.15f)
                else -> OptionGray
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = medalEmoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "$rank 위",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(QuizPrimary)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$score 점",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}