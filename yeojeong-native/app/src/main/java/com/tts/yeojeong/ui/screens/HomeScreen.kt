package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tts.yeojeong.R
import com.tts.yeojeong.data.SampleSpots
import com.tts.yeojeong.data.SpotCategories
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.model.RoutePlan
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.model.WeatherUi
import com.tts.yeojeong.ui.components.SpotHeroCard
import com.tts.yeojeong.ui.components.SpotSquareCard
import com.tts.yeojeong.ui.components.YWeatherChip
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    prefs: Prefs,
    weather: WeatherUi,
    route: RoutePlan?,
    spots: List<Spot>,
    onSpotClick: (Spot) -> Unit,
    onSearch: () -> Unit,
    onRouteClick: () -> Unit,
    onRefreshWeather: () -> Unit
) {
    val list = spots.ifEmpty { SampleSpots.all }
    val hero = list.firstOrNull { it.contentTypeId == "12" && !it.imageUrl.isNullOrBlank() }
        ?: list.first()
    val horizontal = list.filter { it.id != hero.id }.take(8)
    var categoryGroup by remember { mutableStateOf("산책") }
    var categorySub by remember { mutableStateOf<String?>(null) }
    val categorySpots = remember(list, categoryGroup, categorySub) {
        SpotCategories.spotsFor(list, categoryGroup, categorySub).take(8)
    }
    val temp = when (weather) {
        is WeatherUi.Ready -> "${weather.temperature}°"
        WeatherUi.Loading, WeatherUi.Unavailable -> "--°"
    }
    var showReportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 84.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                YWeatherChip(
                    location = "서대문구",
                    temperature = temp,
                    modifier = Modifier.clickable(onClick = onRefreshWeather)
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(onClick = onSearch),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "검색",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            SpotHeroCard(spot = hero, onClick = { onSpotClick(hero) })
        }

        item {
            SectionHeader(text = "가보고 싶은 스팟")
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(horizontal, key = { it.id }) { spot ->
                    SpotSquareCard(
                        spot = spot,
                        size = 200.dp,
                        onClick = { onSpotClick(spot) }
                    )
                }
            }
        }

        item {
            SectionHeader(text = "카테고리별 추천")
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(SpotCategories.groups, key = { it.name }) { group ->
                    CategoryChip(
                        text = group.name,
                        selected = categoryGroup == group.name,
                        onClick = {
                            categoryGroup = group.name
                            categorySub = null
                        }
                    )
                }
            }
        }
        item {
            val subs = SpotCategories.groups.first { it.name == categoryGroup }.subs
            LazyRow(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    CategoryChip(
                        text = "전체",
                        selected = categorySub == null,
                        onClick = { categorySub = null }
                    )
                }
                items(subs, key = { it }) { sub ->
                    CategoryChip(
                        text = sub,
                        selected = categorySub == sub,
                        onClick = { categorySub = sub }
                    )
                }
            }
        }
        item {
            if (categorySpots.isEmpty()) {
                Text(
                    text = "이 근처에는 아직 " + categoryGroup + " 스팟이 없어요",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categorySpots, key = { it.id }) { spot ->
                        SpotSquareCard(
                            spot = spot,
                            size = 200.dp,
                            onClick = { onSpotClick(spot) }
                        )
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp)) {
                Text(
                    text = "자신만의 스팟이 있다면?",
                    fontSize = 16.sp,
                    color = Ink
                )
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceWhite)
                        .clickable { showReportDialog = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "나만의 스팟 제보하기",
                        fontSize = 16.sp,
                        color = Primary
                    )
                }
            }
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(16.dp),
            title = { Text("스팟 제보", style = MaterialTheme.typography.titleSmall, color = Ink) },
            text = {
                Text(
                    text = "제보 기능은 준비 중이에요. 지금은 저장한 스팟을 마이페이지에서 다시 볼 수 있어요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("확인", color = Primary)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 24.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Ink
    )
}

@Composable
private fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Soft else SurfaceWhite)
            .border(
                1.dp,
                if (selected) Soft else Disabled,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (selected) Primary else TextSecondary
        )
    }
}

