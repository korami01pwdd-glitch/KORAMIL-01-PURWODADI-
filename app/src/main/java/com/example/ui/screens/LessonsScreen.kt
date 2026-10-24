package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LessonModule
import com.example.data.model.QuizQuestion
import com.example.ui.theme.ArmyContainer
import com.example.ui.theme.ArmyGreenDark
import com.example.ui.theme.ArmyGreenPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.OnArmyContainer
import com.example.ui.theme.OnGoldContainer
import com.example.viewmodel.QuizState

@Composable
fun LessonsScreen(
    modules: List<LessonModule>,
    quizQuestions: List<QuizQuestion>,
    quizState: QuizState,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onResetQuiz: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var viewingModule by remember { mutableStateOf<LessonModule?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lessons_screen")
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = ArmyGreenPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Materi Modul (${modules.size})", fontWeight = FontWeight.SemiBold)
                    }
                },
                modifier = Modifier.testTag("tab_modules")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kuis Pemahaman", fontWeight = FontWeight.SemiBold)
                    }
                },
                modifier = Modifier.testTag("tab_quiz")
            )
        }

        if (selectedTab == 0) {
            // Modules List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Kurikulum Pelajaran Komsos Babinsa",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ArmyGreenDark
                    )
                    Text(
                        text = "Pedoman resmi pembinaan teritorial dan metode interaksi sosial kemasyarakatan prajurit TNI AD.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(modules) { module ->
                    ModuleItemCard(
                        module = module,
                        onClick = { viewingModule = module }
                    )
                }
            }
        } else {
            // Interactive Quiz Section
            QuizSection(
                quizQuestions = quizQuestions,
                quizState = quizState,
                onSelectOption = onSelectOption,
                onSubmitAnswer = onSubmitAnswer,
                onNextQuestion = onNextQuestion,
                onResetQuiz = onResetQuiz
            )
        }
    }

    // Module Reader Dialog
    viewingModule?.let { module ->
        AlertDialog(
            onDismissRequest = { viewingModule = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = ArmyGreenPrimary
                )
            },
            title = {
                Column {
                    Text(
                        text = "Modul ${module.number}: ${module.title}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ArmyGreenDark
                    )
                    Text(
                        text = module.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ArmyContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = module.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnArmyContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Sections
                    items(module.sections) { sec ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(
                                text = sec.heading,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ArmyGreenPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sec.content,
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (sec.bulletPoints.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                sec.bulletPoints.forEach { pt ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
                                        Text("• ", color = ArmyGreenPrimary, fontWeight = FontWeight.Bold)
                                        Text(text = pt, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }

                    // Tips Box
                    if (module.tips.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = GoldContainer)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = OnGoldContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Tips Lapangan Babinsa",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = OnGoldContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    module.tips.forEach { tip ->
                                        Text(
                                            text = "✓ $tip",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnGoldContainer,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingModule = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ArmyGreenPrimary)
                ) {
                    Text("Tutup Materi")
                }
            }
        )
    }
}

@Composable
fun ModuleItemCard(
    module: LessonModule,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("module_card_${module.number}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(ArmyContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${module.number}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ArmyGreenDark
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ArmyGreenDark
                )
                Text(
                    text = module.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Estimasi belajar: ${module.durationMinutes} menit",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Buka Modul",
                tint = ArmyGreenPrimary
            )
        }
    }
}

@Composable
fun QuizSection(
    quizQuestions: List<QuizQuestion>,
    quizState: QuizState,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onResetQuiz: () -> Unit
) {
    val total = quizQuestions.size

    if (quizState.isCompleted) {
        // Quiz Result Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("quiz_result_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(ArmyContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ArmyGreenPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Kuis Selesai!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = ArmyGreenDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Nilai Akhir: ${quizState.score} / 100",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = GoldAccent
            )
            Spacer(modifier = Modifier.height(8.dp))
            val feedback = if (quizState.score >= 80) {
                "Sangat Memuaskan! Anda memiliki pemahaman doktrin dan teknik Komsos Babinsa yang sangat baik."
            } else if (quizState.score >= 60) {
                "Cukup Baik! Pelajari kembali modul metode pendekatan dan format pelaporan untuk hasil maksimal."
            } else {
                "Perlu Mengulang! Silakan baca kembali modul 1 sampai 5 untuk memperdalam pemahaman Binter."
            }
            Text(
                text = feedback,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onResetQuiz,
                colors = ButtonDefaults.buttonColors(containerColor = ArmyGreenPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_reset_quiz")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ulangi Kuis", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        val currentQ = quizQuestions[quizState.currentQuestionIndex]
        val progress = (quizState.currentQuestionIndex + 1).toFloat() / total.toFloat()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("quiz_in_progress"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pertanyaan ${quizState.currentQuestionIndex + 1} dari $total",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ArmyGreenDark
                    )
                    Text(
                        text = "Skor: ${quizState.score}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldAccent
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ArmyGreenPrimary,
                    trackColor = ArmyContainer
                )
            }

            // Question Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentQ.question,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1B2418)
                        )
                    }
                }
            }

            // Options
            items(currentQ.options.size) { index ->
                val optionText = currentQ.options[index]
                val isSelected = quizState.selectedOptionIndex == index
                val isCorrect = currentQ.correctIndex == index

                val containerColor = when {
                    quizState.isSubmitted && isCorrect -> Color(0xFFE8F5E9)
                    quizState.isSubmitted && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                    isSelected -> ArmyContainer
                    else -> Color.White
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !quizState.isSubmitted) { onSelectOption(index) }
                        .testTag("quiz_option_$index"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { if (!quizState.isSubmitted) onSelectOption(index) },
                            colors = RadioButtonDefaults.colors(selectedColor = ArmyGreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF222222)
                        )
                    }
                }
            }

            // Explanation Card after submission
            if (quizState.isSubmitted) {
                item {
                    val wasCorrect = quizState.selectedOptionIndex == currentQ.correctIndex
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (wasCorrect) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (wasCorrect) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (wasCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (wasCorrect) "Jawaban Benar! (+20 poin)" else "Jawaban Kurang Tepat",
                                    fontWeight = FontWeight.Bold,
                                    color = if (wasCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQ.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
            }

            // Action Button (Kirim Jawaban or Lanjut)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                if (!quizState.isSubmitted) {
                    Button(
                        onClick = onSubmitAnswer,
                        enabled = quizState.selectedOptionIndex != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_submit_quiz"),
                        colors = ButtonDefaults.buttonColors(containerColor = ArmyGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Kirim Jawaban", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_next_question"),
                        colors = ButtonDefaults.buttonColors(containerColor = ArmyGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (quizState.currentQuestionIndex + 1 < total) "Pertanyaan Berikutnya" else "Lihat Hasil Akhir",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
