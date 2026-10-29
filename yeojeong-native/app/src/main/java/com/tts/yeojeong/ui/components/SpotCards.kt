package com.tts.yeojeong.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary

/** 홈 히어로 카드: 216dp, 왼쪽 224dp 그라데이션 열, 위에서부터 배지·제목·주소. */
@Composable
fun SpotHeroCard(
    spot: Spot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(216.dp)
            .clickable(onClick = onClick)
    ) {
        SpotImage(spot = spot, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(224.dp)
                .background(
                    Brush.horizontalGradient(
                        0f to Color.Black,
                        0.5f to Color(0x50000000),
                        1f to Color.Transparent
                    )
                )
        )
        Column(
            modifier = Modifier
                .padding(start = 24.dp, top = 16.dp)
                .width(224.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceWhite)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "추천",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = spot.name,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = spot.address.ifBlank { spot.station + "역 도보 " + spot.walkMinutes + "분" },
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xCC4E4E4E))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(text = "1/5", fontSize = 14.sp, color = Color.White)
        }
    }
}

/** 홈 카드: 200x200, 8dp 모서리, 하단 73dp 그라데이션 위 16sp bold 제목만. */
@Composable
fun SpotSquareCard(
    spot: Spot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        SpotImage(spot = spot, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(73.dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        1f to Color(0xCC000000)
                    )
                )
                .padding(start = 8.dp, top = 8.dp)
        ) {
            Text(
                text = spot.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SpotCompactCard(
    spot: Spot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(148.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            SpotImage(spot = spot, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            1f to Color(0xB3000000)
                        )
                    )
                    .padding(start = 8.dp, top = 8.dp)
            ) {
                Text(
                    text = spot.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SpotListRow(
    spot: Spot,
    saved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpotImage(
            spot = spot,
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                YLineBadge(line = spot.line)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = spot.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = spot.name,
                style = MaterialTheme.typography.titleSmall,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = spot.station + "역 · 도보 " + spot.walkMinutes + "분",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        if (saved) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Soft)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("저장됨", style = MaterialTheme.typography.labelSmall, color = Primary)
            }
        }
    }
}

@Composable
fun RouteSummaryCard(
    home: String,
    work: String,
    legs: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = home + "  →  " + work,
                style = MaterialTheme.typography.titleSmall,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (legs.isEmpty()) {
                    YLineBadge(line = "루트", large = true)
                } else {
                    legs.forEach { YLineBadge(line = it, large = true) }
                }
            }
        }
        Text(
            text = "편집",
            style = MaterialTheme.typography.bodySmall,
            color = Primary
        )
    }
}

@Composable
fun DayBadge(text: String, active: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) Primary else Soft),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) Color.White else TextSecondary
        )
    }
}

@Composable
fun StationHeader(
    station: String,
    line: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        YLineBadge(line = line, large = true)
        Spacer(Modifier.width(8.dp))
        Text(
            text = station,
            style = MaterialTheme.typography.titleSmall,
            color = Ink
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(Disabled)
        )
    }
}

@Composable
fun SpotImage(
    spot: Spot,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (spot.imageUrl.isNullOrBlank()) {
        Image(
            painter = painterResource(spot.imageRes),
            contentDescription = spot.name,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        AsyncImage(
            model = spot.imageUrl,
            contentDescription = spot.name,
            contentScale = contentScale,
            placeholder = painterResource(spot.imageRes),
            error = painterResource(spot.imageRes),
            fallback = painterResource(spot.imageRes),
            modifier = modifier
        )
    }
}

