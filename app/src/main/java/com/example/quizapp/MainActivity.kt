package com.example.quizapp


import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
    val categoryGradients = mapOf(
        "역사" to Pair(HistoryGradientStart, HistoryGradientEnd),
        "상식" to Pair(CommonGradientStart, CommonGradientEnd),
        "과학" to Pair(ScienceGradientStart, ScienceGradientEnd),
        "영화" to Pair(MovieGradientStart, MovieGradientEnd)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FE),
                        Color(0xFFE8ECFD)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Header with animation
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { visible = true }
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎯",
                        fontSize = 56.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "퀴즈 마스터",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = QuizPrimary
                    )
                    Text(
                        text = "도전할 주제를 선택하세요",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            QuestionData.data.keys.forEachIndexed { index, category ->
                val gradient = categoryGradients[category] ?: Pair(QuizPrimary, QuizPrimaryVariant)
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = 100 * index)) +
                            slideInHorizontally(animationSpec = tween(300, delayMillis = 100 * index))
                ) {
                    CategoryCard(
                        category = category,
                        gradientColors = gradient,
                        onClick = { viewModel.startQuiz(category) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.goToWrongNotes() },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFE5E7EB)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("📝", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "오답노트",
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Button(
                    onClick = { viewModel.goToRanking() },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFE5E7EB)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🏆", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "랭킹",
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CategoryCard(category: String, gradientColors: Pair<Color, Color>, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = ""
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .shadow(
                elevation = if (isPressed) 2.dp else 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = gradientColors.first.copy(alpha = 0.3f)
            )
            .clickable(
                onClick = onClick,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(gradientColors.first, gradientColors.second)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "→",
                    fontSize = 24.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
@Composable
fun QuizScreen(viewModel: QuizViewModel, category: String) {
    val question = viewModel.questions[viewModel.currentIndex]
    val progress = (viewModel.currentIndex + 1).toFloat() / viewModel.questions.size
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FE),
                        Color(0xFFFFFFFF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Progress indicator with modern design
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(QuizPrimary, QuizPrimaryVariant)
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category.first().toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = category,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    QuizPrimary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${viewModel.currentIndex + 1}/${viewModel.questions.size}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuizPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFFE5E7EB))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(QuizPrimary, QuizAccent)
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question card with animation
            var questionVisible by remember { mutableStateOf(false) }
            LaunchedEffect(viewModel.currentIndex) {
                questionVisible = false
                kotlinx.coroutines.delay(100)
                questionVisible = true
            }
            
            AnimatedVisibility(
                visible = questionVisible,
                enter = fadeIn() + scaleIn(initialScale = 0.9f)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = QuizPrimary.copy(alpha = 0.2f)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = question.question,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = TextPrimary,
                            lineHeight = 32.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Options with staggered animation
            question.options.forEachIndexed { index, option ->
                AnimatedVisibility(
                    visible = questionVisible,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = 50 * index)) +
                            slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(300, delayMillis = 50 * index)
                            )
                ) {
                    OptionButton(
                        text = option,
                        optionNumber = index + 1,
                        onClick = { viewModel.answer(index) },
                        isSelected = viewModel.selectedAnswer == index,
                        isCorrect = question.answerIndex == index,
                        isAnswered = viewModel.isAnswered
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
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
        isAnswered && isSelected && isCorrect -> CorrectGreen.copy(alpha = 0.15f)
        isAnswered && isSelected && !isCorrect -> WrongRed.copy(alpha = 0.15f)
        isAnswered && isCorrect -> CorrectGreen.copy(alpha = 0.1f)
        isSelected && !isAnswered -> QuizPrimary.copy(alpha = 0.1f)
        else -> Color.White
    }

    val borderColor = when {
        isAnswered && isSelected && isCorrect -> CorrectGreen
        isAnswered && isSelected && !isCorrect -> WrongRed
        isAnswered && isCorrect -> CorrectGreen
        isSelected && !isAnswered -> QuizPrimary
        else -> Color(0xFFE5E7EB)
    }

    val iconBackgroundColor = when {
        isAnswered && isSelected && isCorrect -> CorrectGreen
        isAnswered && isSelected && !isCorrect -> WrongRed
        isAnswered && isCorrect -> CorrectGreen
        else -> QuizPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected && !isAnswered) 6.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = borderColor.copy(alpha = 0.3f)
            )
            .clickable(enabled = !isAnswered, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected || (isAnswered && isCorrect)) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isAnswered && isCorrect && !isSelected) {
                                iconBackgroundColor.copy(alpha = 0.2f)
                            } else {
                                iconBackgroundColor
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = optionNumber.toString(),
                        color = if (isAnswered && isCorrect && !isSelected) iconBackgroundColor else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    fontSize = 17.sp,
                    fontWeight = if (isSelected || (isAnswered && isCorrect)) FontWeight.SemiBold else FontWeight.Normal,
                    color = TextPrimary
                )
            }

            // O/X 표시 with animation
            if (isAnswered) {
                var iconVisible by remember { mutableStateOf(false) }
                LaunchedEffect(isAnswered) {
                    iconVisible = true
                }
                AnimatedVisibility(
                    visible = iconVisible,
                    enter = scaleIn() + fadeIn()
                ) {
                    Text(
                        text = when {
                            isCorrect -> "✓"
                            isSelected && !isCorrect -> "✗"
                            else -> ""
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCorrect -> CorrectGreen
                            else -> WrongRed
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
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
    
    val resultColor = when {
        percentage >= 80 -> CorrectGreen
        percentage >= 60 -> QuizAccent
        percentage >= 40 -> Color(0xFFFF9800)
        else -> WrongRed
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FE),
                        Color(0xFFFFFFFF)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Result card with animation
            item {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(28.dp),
                                spotColor = resultColor.copy(alpha = 0.3f)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "퀴즈 완료!",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = QuizPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = resultMessage,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = resultColor
                            )
                            Spacer(modifier = Modifier.height(32.dp))

                            // Animated score circle
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(80.dp),
                                        spotColor = resultColor.copy(alpha = 0.4f)
                                    )
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                resultColor,
                                                resultColor.copy(alpha = 0.8f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(80.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$percentage%",
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${viewModel.score}/${viewModel.questions.size}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }

            // Answer review section title
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    QuizPrimary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📋",
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "정답 확인",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Answer review items
            items(viewModel.questions) { question ->
                val isWrong = viewModel.wrongAnswers.contains(question)
                AnswerReviewItem(
                    question = question,
                    isCorrect = !isWrong
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Back button
            item {
                Button(
                    onClick = { viewModel.goToMain() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = QuizPrimary.copy(alpha = 0.3f)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuizPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "메인으로 돌아가기",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AnswerReviewItem(question: Question, isCorrect: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (isCorrect) CorrectGreen.copy(alpha = 0.2f) else WrongRed.copy(alpha = 0.2f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) CorrectGreen.copy(alpha = 0.08f)
            else WrongRed.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (isCorrect) CorrectGreen.copy(alpha = 0.3f) else WrongRed.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (isCorrect) CorrectGreen else WrongRed,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCorrect) "✓" else "✗",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = question.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .background(
                            if (isCorrect) CorrectGreen.copy(alpha = 0.15f)
                            else Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "정답: ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) CorrectGreen else TextSecondary
                        )
                        Text(
                            text = question.options[question.answerIndex],
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
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
        
        // 오답노트 초기화 버튼
        if (viewModel.wrongAnswers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { viewModel.clearWrongAnswers() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WrongRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🗑️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("오답노트 초기화", fontSize = 18.sp)
                }
            }
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
                                score = ranking.score,
                                date = ranking.timestamp
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
        
        // 랭킹 초기화 버튼
        if (viewModel.rankings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { viewModel.clearRankings() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WrongRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🗑️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("랭킹 기록 초기화", fontSize = 18.sp)
                }
            }
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