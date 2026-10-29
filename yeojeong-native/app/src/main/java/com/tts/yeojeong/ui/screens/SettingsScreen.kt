package com.tts.yeojeong.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tts.yeojeong.data.ReminderScheduler
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.ui.components.YIconButton
import com.tts.yeojeong.ui.components.YTextField
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Page
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary

@Composable
fun SettingsScreen(
    prefs: Prefs,
    onBack: () -> Unit,
    onNickname: (String) -> Unit,
    onNotifications: (Boolean) -> Unit,
    onEditRoute: () -> Unit,
    onTerms: () -> Unit,
    onReset: () -> Unit
) {
    var showNickname by remember { mutableStateOf(false) }
    var nickname by remember(prefs.nickname) { mutableStateOf(prefs.nickname) }
    var showReset by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onNotifications(granted)
        if (granted) ReminderScheduler.schedule(context) else ReminderScheduler.cancel(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceWhite)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                onClick = onBack
            )
            Text(
                text = "설정",
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 16.sp,
                color = TextSecondary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            SettingsSection {
                SettingsRow(
                    title = "프로필 수정",
                    value = prefs.nickname,
                    onClick = {
                        nickname = prefs.nickname
                        showNickname = true
                    }
                )
                SettingsDivider()
                SettingsRow(
                    title = "출퇴근 루트",
                    value = prefs.homeStation + " → " + prefs.workStation,
                    onClick = onEditRoute
                )
            }

            Spacer(Modifier.height(16.dp))
            SettingsSection {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "주간 알림", fontSize = 16.sp, color = Ink)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "일요일 18시에 한 번 알려드려요",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                    Switch(
                        checked = prefs.notifications,
                        onCheckedChange = { enabled ->
                            val needsPermission = enabled &&
                                Build.VERSION.SDK_INT >= 33 &&
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            if (needsPermission) {
                                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onNotifications(enabled)
                                if (enabled) ReminderScheduler.schedule(context)
                                else ReminderScheduler.cancel(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Primary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Disabled,
                            uncheckedBorderColor = Disabled
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            SettingsSection {
                SettingsRow(title = "현재버전", value = "1.0.0", onClick = null)
            }

            Spacer(Modifier.height(16.dp))
            SettingsSection {
                SettingsRow(title = "약관보기", value = null, onClick = onTerms, showArrow = true)
            }

            Spacer(Modifier.height(16.dp))
            SettingsSection {
                Text(
                    text = "데이터 초기화",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReset = true }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    fontSize = 16.sp,
                    color = Color(0xFFD23B3B)
                )
            }
        }
    }

    if (showNickname) {
        AlertDialog(
            onDismissRequest = { showNickname = false },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(16.dp),
            title = { Text("닉네임 수정", fontSize = 18.sp, color = Ink) },
            text = {
                YTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    placeholder = "닉네임을 입력하세요"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (nickname.trim().isNotEmpty()) {
                        onNickname(nickname)
                        showNickname = false
                    }
                }) {
                    Text("저장", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNickname = false }) {
                    Text("취소", color = TextSecondary)
                }
            }
        )
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(16.dp),
            title = { Text("데이터를 초기화할까요?", color = Ink) },
            text = {
                Text(
                    "저장한 스팟과 다녀온 기록이 모두 사라집니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showReset = false
                    onReset()
                }) {
                    Text("초기화", color = Color(0xFFD23B3B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showReset = false }) {
                    Text("취소", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
    ) {
        content()
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Soft)
    )
}

@Composable
private fun SettingsRow(
    title: String,
    value: String?,
    onClick: (() -> Unit)?,
    showArrow: Boolean = false
) {
    val base = Modifier
        .fillMaxWidth()
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        .padding(horizontal = 20.dp, vertical = 16.dp)
    Row(
        modifier = base,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Ink)
        if (value != null) {
            Text(text = value, fontSize = 16.sp, color = Ink)
        }
        if (showArrow) {
            Spacer(Modifier.size(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

