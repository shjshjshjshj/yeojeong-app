package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tts.yeojeong.R
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.ui.components.YIconButton
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Highlight
import com.tts.yeojeong.ui.theme.PageAlt
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun ReportScreen(
    prefs: Prefs,
    spots: List<Spot>,
    onSpotClick: (Spot) -> Unit,
    onExplore: () -> Unit,
    onBack: () -> Unit
) {
    val weekStart = LocalDate.now().with(DayOfWeek.MONDAY).toEpochDay()
    val weekVisits = prefs.visitLog.filter { it.epochDay >= weekStart && it.epochDay < weekStart + 7 }
    val counts = IntArray(7)
    weekVisits.forEach { visit ->
        val index = (visit.epochDay - weekStart).toInt()
        if (index in 0..6) counts[index] = counts[index] + 1
    }
    val labels = listOf("월", "화", "수", "목", "금", "토", "일")
    val progress = (weekVisits.size / 3f).coerceIn(0f, 1f)
    val completedChallenges = listOf(
        prefs.reviews.size >= 5,
        prefs.savedIds.size >= 10,
        weekVisits.size >= 3,
        prefs.visitLog.map { it.spotId }.distinct().size >= 30
    ).count { it }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PageAlt)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 84.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 12.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                YIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    onClick = onBack
                )
                Text(
                    text = "주간 여정 리포트",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextTertiary
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                labels.forEachIndexed { index, label ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (counts[index] > 0) Ink else TextSecondary
                        )
                        Spacer(Modifier.height(8.dp))
                        if (counts[index] > 0) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Spacer(Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(376.dp)
                    .shadow(4.dp, RoundedCornerShape(0.dp))
                    .background(SurfaceWhite)
                    .padding(20.dp)
            ) {
                Text(
                    text = "내 지난 주 리포트",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink
                )
                if (weekVisits.isEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.ic_report_none),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "아직 표시할 데이터가 없어요...",
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DonutChart(
                            progress = progress,
                            modifier = Modifier.size(132.dp)
                        )
                        Spacer(Modifier.width(18.dp))
                        Column {
                            Text(
                                text = "이번 주 ${weekVisits.size}곳",
                                style = MaterialTheme.typography.titleLarge,
                                color = Ink
                            )
                            Text(
                                text = "저장 ${prefs.savedIds.size} · 리뷰 ${prefs.reviews.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = buildAnnotatedString {
                        append("여정과 함께")
                        withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                            append(" 당신의 하루")
                        }
                        append("를 채워주세요")
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Ink
                )
            }
        }

        item {
            Text(
                text = "주간 챌린지  ${completedChallenges}/4 달성",
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleSmall,
                color = Ink
            )
        }
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.btn_challenge1),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1f)
                        .height(49.dp)
                )
                Image(
                    painter = painterResource(R.drawable.btn_challenge2),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1f)
                        .height(49.dp)
                )
            }
        }
        item {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.btn_challenge3),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1f)
                        .height(49.dp)
                )
                Image(
                    painter = painterResource(R.drawable.btn_challenge4),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1f)
                        .height(49.dp)
                )
            }
        }
    }
}

@Composable
private fun DonutChart(progress: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 18.dp.toPx()
            val pad = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = Disabled,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = arcSize,
                style = Stroke(width = stroke)
            )
            drawArc(
                color = Highlight,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).roundToInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink
            )
            Text(
                text = "이번 주",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
