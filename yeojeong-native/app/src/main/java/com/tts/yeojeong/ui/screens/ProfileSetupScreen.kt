package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tts.yeojeong.R
import com.tts.yeojeong.ui.components.YIconButton
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary

@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onNext: (nickname: String) -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    val enabled = nickname.trim().isNotEmpty()
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
                text = "프로필 설정",
                style = MaterialTheme.typography.titleLarge,
                color = Ink
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "나의 대표사진과 닉네임을 설정하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceWhite)
                    .border(1.dp, TextSecondary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (nickname.isEmpty()) {
                    Text(
                        text = "닉네임을 입력하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                BasicTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink),
                    cursorBrush = SolidColor(Primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (enabled) Primary else Disabled)
                .navigationBarsPadding()
                .height(56.dp)
                .clickable(enabled = enabled) { onNext(nickname.trim()) },
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
