package com.tts.yeojeong.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.ui.components.YScreenHeader
import com.tts.yeojeong.ui.components.YPrimaryButton
import com.tts.yeojeong.ui.components.YTextField
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary

private val keywordGroups = listOf(
    "음식/가격" to listOf(
        "음료가 맛있어요", "특별한 메뉴가 있어요", "디저트가 맛있어요",
        "가성비가 좋아요", "종류가 다양해요"
    ),
    "좌석/친절" to listOf(
        "좌석이 편해요", "친절해요", "실내가 청결해요",
        "오래 머무르기 좋아요", "선물하기 좋아요"
    ),
    "분위기" to listOf(
        "뷰가 좋아요", "인테리어가 멋져요", "아늑해요",
        "공간이 넓어요", "야외 공간이 멋져요"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WriteReviewScreen(
    spot: Spot,
    onBack: () -> Unit,
    onSave: (title: String, content: String, keywords: List<String>, photos: List<String>) -> Unit
) {
    var title by remember { mutableStateOf(spot.name) }
    var content by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(listOf<String>()) }
    var photos by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.take(3).forEach { uri ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
        photos = uris.take(3).map { it.toString() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
        YScreenHeader(title = "리뷰작성", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 80.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Soft)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = spot.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(14.dp))
            YTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "제목"
            )
            Spacer(Modifier.height(22.dp))
            Text(
                text = "TOP3 키워드",
                style = MaterialTheme.typography.titleLarge,
                color = Ink
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "이 장소를 가장 잘 표현하는 키워드를 골라주세요",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
            keywordGroups.forEach { (group, keywords) ->
                Spacer(Modifier.height(14.dp))
                Text(
                    text = group,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    keywords.forEach { keyword ->
                        val isSelected = keyword in selected
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Primary else Soft)
                                .clickable {
                                    selected = if (isSelected) selected - keyword
                                    else if (selected.size < 3) selected + keyword else selected
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = keyword,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "나만의 이야기",
                style = MaterialTheme.typography.titleLarge,
                color = Ink
            )
            Spacer(Modifier.height(8.dp))
            BasicTextField(
                value = content,
                onValueChange = { if (it.length <= 200) content = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Ink),
                cursorBrush = SolidColor(Primary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Soft)
                            .padding(12.dp)
                    ) {
                        if (content.isEmpty()) {
                            Text(
                                "하고 싶은 말을 자유롭게 적어주세요",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                        inner()
                    }
                }
            )
            Text(
                text = "${content.length}/200",
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 6.dp),
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                photos.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "리뷰 사진",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Text(
                            text = "×",
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable { photos = photos - uri }
                                .background(Color(0x88000000))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
                if (photos.size < 3) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Soft)
                            .clickable { photoPicker.launch(arrayOf("image/*")) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("+", style = MaterialTheme.typography.titleLarge, color = Primary)
                            Text("사진", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        }
        YPrimaryButton(
            text = "저장",
            enabled = content.trim().isNotEmpty() && title.trim().isNotEmpty(),
            onClick = { onSave(title.trim(), content.trim(), selected, photos) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}
