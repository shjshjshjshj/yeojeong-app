package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.tts.yeojeong.data.SampleSpots
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.model.Review
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.ui.components.RouteGlyph
import com.tts.yeojeong.ui.components.SpotListRow
import com.tts.yeojeong.ui.components.YEmptyState
import com.tts.yeojeong.ui.components.YIconButton
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Page
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary

@Composable
fun SavedScreen(
    prefs: Prefs,
    spots: List<Spot>,
    onSpotClick: (Spot) -> Unit,
    onSettings: () -> Unit,
    onExplore: () -> Unit,
    onReport: () -> Unit,
    onDeleteReview: (String) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val source = spots.ifEmpty { SampleSpots.all }
    val saved = source.filter { it.id in prefs.savedIds }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "마이페이지",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
                YIconButton(
                    icon = Icons.Filled.Settings,
                    contentDescription = "설정",
                    onClick = onSettings
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    RouteGlyph(color = Color.White, modifier = Modifier.size(36.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prefs.nickname,
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "퇴근길 탐험가",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("레벨 1", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .height(64.dp)
            ) {
                TabCell("북마크", tab == 0, Modifier.weight(1f)) { tab = 0 }
                TabCell("리뷰", tab == 1, Modifier.weight(1f)) { tab = 1 }
                TabCell("리포트", tab == 2, Modifier.weight(1f)) { tab = 2 }
            }
        }

        when (tab) {
            0 -> {
                if (saved.isEmpty()) {
                    item {
                        YEmptyState(
                            title = "아직 저장한 스팟이 없어요",
                            body = "출퇴근길에 들러보고 싶은 곳을 저장해보세요.",
                            action = "스팟 둘러보기",
                            onAction = onExplore,
                            modifier = Modifier.background(SurfaceWhite)
                        )
                    }
                } else {
                    items(saved, key = { it.id }) { spot ->
                        SpotListRow(
                            spot = spot,
                            saved = true,
                            onClick = { onSpotClick(spot) },
                            modifier = Modifier
                                .background(SurfaceWhite)
                                .padding(horizontal = 20.dp)
                        )
                    }
                }
            }
            1 -> {
                if (prefs.reviews.isEmpty()) {
                    item {
                        YEmptyState(
                            title = "아직 작성한 리뷰가 없어요",
                            body = "다녀온 스팟에 짧은 키워드 리뷰를 남겨보세요.",
                            action = "스팟 둘러보기",
                            onAction = onExplore,
                            modifier = Modifier.background(SurfaceWhite)
                        )
                    }
                } else {
                    items(prefs.reviews, key = { it.id }) { review ->
                        ReviewRow(review, onDeleteReview)
                    }
                }
            }
            else -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceWhite)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "나의 여정 기록",
                            style = MaterialTheme.typography.titleLarge,
                            color = Ink
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "저장한 스팟 ${prefs.savedIds.size} · 다녀온 곳 ${prefs.visitLog.map { it.spotId }.distinct().size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary)
                                .clickable(onClick = onReport)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "주간 리포트 보기",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewRow(review: Review, onDelete: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = review.spotName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                color = Ink
            )
            Text(review.nickname, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "삭제",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                modifier = Modifier.clickable { onDelete(review.id) }
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(review.title, style = MaterialTheme.typography.bodyMedium, color = Ink)
        Text(
            text = review.content,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            maxLines = 2
        )
        if (review.keywords.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = review.keywords.joinToString(" · "),
                style = MaterialTheme.typography.labelSmall,
                color = Primary
            )
        }
        if (review.photos.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
                review.photos.take(3).forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "리뷰 사진",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun TabCell(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Ink else TextSecondary
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (selected) 3.dp else 0.dp)
                .background(Primary)
        )
    }
}
