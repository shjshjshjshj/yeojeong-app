package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.ui.components.YGlassIconButton
import com.tts.yeojeong.ui.components.YLineBadge
import com.tts.yeojeong.ui.components.YOutlineButton
import com.tts.yeojeong.ui.components.YPrimaryButton
import com.tts.yeojeong.ui.components.SpotImage
import com.tts.yeojeong.ui.components.YTag
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Page
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary

@Composable
fun SpotDetailScreen(
    spot: Spot,
    prefs: Prefs,
    onBack: () -> Unit,
    onToggleSaved: () -> Unit,
    onVisited: () -> Unit,
    onWriteReview: () -> Unit,
    onShare: () -> Unit,
    onMap: () -> Unit
) {
    val saved = spot.id in prefs.savedIds
    val visited = prefs.visitLog.any { it.spotId == spot.id }
    val reviews = prefs.reviews.filter { it.spotId == spot.id }
    val exitText = if (spot.exit == "인근") "${spot.station}역 인근"
    else "${spot.station}역 ${spot.exit}번 출구"
    // 헤더까지 스크롤되므로, 스크롤된 내용이 상태바 글자와 겹치지 않도록 흰색 띠를 덮습니다.
    val statusBarHeight = with(LocalDensity.current) {
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding().toPx()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
            .drawWithContent {
                drawContent()
                drawRect(Color.White, size = Size(size.width, statusBarHeight))
            },
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .statusBarsPadding()
                    .padding(start = 12.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                com.tts.yeojeong.ui.components.YIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    onClick = onBack
                )
                Text(
                    text = "상세보기",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                SpotImage(spot = spot, modifier = Modifier.fillMaxSize())
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color(0x99000000),
                                0.45f to Color.Transparent,
                                1f to Color(0xCC000000)
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))
                    YGlassIconButton(
                        icon = if (saved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "저장",
                        onClick = onToggleSaved
                    )
                    Spacer(Modifier.width(8.dp))
                    YGlassIconButton(
                        icon = Icons.Filled.Share,
                        contentDescription = "공유",
                        onClick = onShare
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        YLineBadge(line = spot.line, large = true)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = spot.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = spot.name,
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${spot.station}역 · 도보 ${spot.walkMinutes}분",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = spot.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = spot.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    spot.tags.forEach { YTag(it) }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                InfoRow("카테고리", spot.category)
                InfoRow("영업시간", spot.hours.ifBlank { "정보 없음" })
                InfoRow("전화번호", spot.phone.ifBlank { "정보 없음" })
                InfoRow("홈페이지", spot.homepage.ifBlank { spot.address })
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "왜 여기일까요",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = spot.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "가는 길",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Soft)
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = exitText,
                            style = MaterialTheme.typography.titleSmall,
                            color = Ink
                        )
                        Text(
                            text = "도보 약 ${spot.walkMinutes}분 · ${spot.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "지도",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                        color = Primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceWhite)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .clickable(onClick = onMap)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "지도 버튼은 구글맵으로 연결됩니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TOP3 키워드",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        color = Ink
                    )
                    Text(
                        text = "리뷰 작성",
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary,
                        modifier = Modifier.clickable(onClick = onWriteReview)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    spot.tags.take(3).forEach { YTag(it) }
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "리뷰",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink
                )
                Spacer(Modifier.height(8.dp))
                if (reviews.isEmpty()) {
                    Text(
                        text = "아직 작성된 리뷰가 없어요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                } else {
                    reviews.take(3).forEach { review ->
                        Text(
                            text = review.nickname + " · " + review.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink
                        )
                        Text(
                            text = review.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }

        item {
                Column(
                modifier = Modifier
                    .fillMaxWidth()
                        .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                YPrimaryButton(
                    text = if (saved) "저장됨" else "이 스팟 저장하기",
                    onClick = onToggleSaved
                )
                Spacer(Modifier.height(8.dp))
                YOutlineButton(
                    text = if (visited) "다녀온 스팟" else "다녀왔어요",
                    onClick = onVisited,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !visited
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceWhite)
                        .clickable(onClick = onMap)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "구글맵에서 길찾기",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "열기",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}
