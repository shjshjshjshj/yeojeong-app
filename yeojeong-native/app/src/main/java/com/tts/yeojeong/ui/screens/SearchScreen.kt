package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tts.yeojeong.data.SampleSpots
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.ui.components.SpotListRow
import com.tts.yeojeong.ui.components.SpotSquareCard
import com.tts.yeojeong.ui.components.YIconButton
import com.tts.yeojeong.ui.components.YSearchField
import com.tts.yeojeong.ui.components.YSectionTitle
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary

@Composable
fun SearchScreen(
    prefs: Prefs,
    results: List<Spot>,
    searching: Boolean,
    popularSpots: List<Spot>,
    onSearch: (String) -> Unit,
    onSpotClick: (Spot) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("전체") }
    val filteredResults = results.filter {
        category == "전체" || it.category == category
    }
    LaunchedEffect(query) {
        delay(300)
        onSearch(query)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 20.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                YIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    onClick = onBack
                )
                YSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "장소, 역, 키워드 검색",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(listOf("전체", "산책", "카페", "맛집", "문화", "물가"), key = { it }) { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (category == item) Soft else SurfaceWhite)
                            .clickable { category = item }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (category == item) Primary else TextSecondary
                        )
                    }
                }
            }
        }

        if (query.isBlank()) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "장소, 역, 키워드로 검색해보세요",
                        style = MaterialTheme.typography.titleSmall,
                        color = Ink
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "공공데이터에서 제공하는 서울 장소와 추천 스팟을 찾아드려요.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            val popular = popularSpots.take(6)
            item {
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(popular, key = { it.id }) { spot ->
                        SpotSquareCard(
                            spot = spot,
                            size = 200.dp,
                            onClick = { onSpotClick(spot) }
                        )
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = if (searching) "검색 중..." else "검색 결과 ${filteredResults.size}",
                        style = MaterialTheme.typography.titleSmall,
                        color = Ink
                    )
                    Text(
                        text = "‘$query’에 맞는 스팟을 찾았어요",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }
            if (searching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary, strokeWidth = 2.dp)
                    }
                }
            } else if (filteredResults.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 40.dp)
                    ) {
                        Text(
                            text = "검색 결과가 없어요",
                            style = MaterialTheme.typography.titleLarge,
                            color = Ink
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "역 이름이나 ‘산책’, ‘카페’ 같은 키워드로 다시 찾아보세요.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredResults, key = { it.id }) { spot ->
                            SpotSquareCard(
                                spot = spot,
                                size = 200.dp,
                                onClick = { onSpotClick(spot) }
                            )
                        }
                    }
                }
            }
        }
    }
}
