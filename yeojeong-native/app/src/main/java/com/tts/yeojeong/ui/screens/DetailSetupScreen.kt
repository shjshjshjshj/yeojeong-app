package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tts.yeojeong.ui.components.YIconButton
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary

@Composable
fun DetailSetupScreen(
    nickname: String,
    onBack: () -> Unit,
    onFinish: (gender: String, mbti: String) -> Unit
) {
    var gender by remember { mutableStateOf("") }
    var mbti by remember { mutableStateOf("") }
    val enabled = gender.isNotEmpty() && mbti.length == 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .statusBarsPadding()
    ) {
        YIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            onClick = onBack,
            modifier = Modifier.padding(start = 12.dp, top = 8.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "상세 설정",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "나의 성별과 MBTI를 입력하세요",
                style = MaterialTheme.typography.bodyLarge,
                color = TextTertiary
            )
            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceWhite)
                    .padding(20.dp)
            ) {
                Text(
                    text = "성별을 선택하세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GenderCard("남", gender == "M", Modifier.weight(1f)) { gender = "M" }
                    GenderCard("여", gender == "W", Modifier.weight(1f)) { gender = "W" }
                }
            }
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceWhite)
                    .padding(20.dp)
            ) {
                Text(
                    text = "MBTI를 선택하세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink
                )
                Spacer(Modifier.height(14.dp))
                val pairs = listOf("E" to "I", "S" to "N", "T" to "F", "J" to "P")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        pairs.forEach { (a, _) ->
                            MbtiCircle(a, mbti.contains(a)) {
                                mbti = toggleMbti(mbti, a, pairs)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        pairs.forEach { (_, b) ->
                            MbtiCircle(b, mbti.contains(b)) {
                                mbti = toggleMbti(mbti, b, pairs)
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (enabled) Primary else Disabled)
                .navigationBarsPadding()
                .height(56.dp)
                .clickable(enabled = enabled) { onFinish(gender, mbti) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음",
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) Color.White else TextSecondary
            )
        }
    }
}

@Composable
private fun GenderCard(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .border(2.dp, if (selected) Primary else Disabled, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.displayLarge,
            color = if (selected) Primary else Disabled
        )
    }
}

@Composable
private fun MbtiCircle(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(SurfaceWhite)
            .border(2.dp, if (selected) Primary else Disabled, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = if (selected) Primary else Disabled
        )
    }
}

private fun toggleMbti(current: String, letter: String, pairs: List<Pair<String, String>>): String {
    val pair = pairs.first { it.first == letter || it.second == letter }
    val without = current.filterNot { it.toString() == pair.first || it.toString() == pair.second }
    return without + letter
}
