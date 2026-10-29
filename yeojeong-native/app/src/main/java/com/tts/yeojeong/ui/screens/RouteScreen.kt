package com.tts.yeojeong.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tts.yeojeong.data.SampleSpots
import com.tts.yeojeong.data.SubwayRepository
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.model.RoutePlan
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.ui.components.SpotListRow
import com.tts.yeojeong.ui.components.StationHeader
import com.tts.yeojeong.ui.components.YIconButton
import com.tts.yeojeong.ui.components.YLineBadge
import com.tts.yeojeong.ui.components.YSearchField
import com.tts.yeojeong.ui.components.YSectionTitle
import com.tts.yeojeong.ui.theme.Disabled
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Page
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.ui.theme.TextTertiary
import com.tts.yeojeong.ui.theme.lineColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(
    prefs: Prefs,
    route: RoutePlan?,
    subway: SubwayRepository?,
    availableSpots: List<Spot>,
    onSpotClick: (Spot) -> Unit,
    onUpdateRoute: (String, String) -> Unit,
    onSettings: () -> Unit
) {
    var pickerTarget by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "출퇴근 루트",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "내 동선에서 내릴 만한 역을 골라볼게요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                }
                YIconButton(
                    icon = Icons.Filled.Settings,
                    contentDescription = "설정",
                    onClick = onSettings,
                    tint = Color.White
                )
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StationSelector(
                    label = "출발역",
                    station = prefs.homeStation,
                    modifier = Modifier.weight(1f),
                    onClick = { pickerTarget = "home" }
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            onUpdateRoute(prefs.workStation, prefs.homeStation)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⇅", color = Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                StationSelector(
                    label = "도착역",
                    station = prefs.workStation,
                    modifier = Modifier.weight(1f),
                    onClick = { pickerTarget = "work" }
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                route?.legs?.forEach { leg ->
                    YLineBadge(line = leg.line, large = true)
                    Spacer(Modifier.width(4.dp))
                }
                if (route == null) {
                    Text(
                        text = "경로를 찾는 중이에요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                } else {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${route.stops}개 역 · 약 ${route.minutes}분",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
            val startStation = subway?.station(prefs.homeStation)
            val endStation = subway?.station(prefs.workStation)
            if (startStation != null && endStation != null) {
                Text(
                    text = "구글맵 길찾기",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            val uri = Uri.parse(
                                "https://www.google.com/maps/dir/?api=1" +
                                    "&origin=" + startStation.lat + "," + startStation.lng +
                                    "&destination=" + endStation.lat + "," + endStation.lng +
                                    "&travelmode=transit"
                            )
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp)
                ) {
                    if (route != null) {
                        RouteDiagram(route = route)
                        Spacer(Modifier.height(24.dp))
                    }
                    YSectionTitle(text = "이 루트의 힐링 스팟")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "한 정거장 내려서 천천히 걸어가 보세요",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }

            if (route == null) {
                item {
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceWhite)
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = if (subway == null) "루트를 준비하고 있어요" else "이 구간은 아직 경로 데이터가 없어요",
                                style = MaterialTheme.typography.titleSmall,
                                color = Ink
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "출발역과 도착역을 다시 선택해보세요.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                val spotsByStation = route.stations.mapNotNull { station ->
                    val spots = availableSpots.filter { it.station == station }
                    if (spots.isEmpty()) null else station to spots
                }
                spotsByStation.forEach { (station, spots) ->
                    item(key = "station-$station") {
                        Column(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        ) {
                            StationHeader(
                                station = station,
                                line = spots.first().line
                            )
                        }
                    }
                    items(spots, key = { it.id }) { spot ->
                        SpotListRow(
                            spot = spot,
                            saved = spot.id in prefs.savedIds,
                            onClick = { onSpotClick(spot) },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 24.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Soft)
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "루트를 바꾸면 추천도 함께 바뀌어요",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "역 선택",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                        color = Primary,
                        modifier = Modifier.clickable { pickerTarget = "home" }
                    )
                }
            }
        }
    }

    if (pickerTarget != null) {
        StationPickerSheet(
            title = if (pickerTarget == "home") "출발역 선택" else "도착역 선택",
            subway = subway,
            onDismiss = { pickerTarget = null },
            onSelect = { station ->
                if (pickerTarget == "home") onUpdateRoute(station, prefs.workStation)
                else onUpdateRoute(prefs.homeStation, station)
                pickerTarget = null
            }
        )
    }
}

@Composable
private fun RouteDiagram(route: RoutePlan, modifier: Modifier = Modifier) {
    val stationLines = route.stations.map { station ->
        route.legs.firstOrNull { leg -> leg.stations.contains(station) }?.line
            ?: route.legs.firstOrNull()?.line
            ?: "2호선"
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "노선도",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                color = Ink
            )
            Text(
                text = "${route.stops}개 역 · 약 ${route.minutes}분",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
        Spacer(Modifier.height(14.dp))
        route.stations.forEachIndexed { index, station ->
            val color = lineColor(stationLines[index])
            val isTransfer = index in 1 until route.stations.lastIndex &&
                stationLines[index] != stationLines[index - 1]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight()
                ) {
                    if (index > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .width(4.dp)
                                .height(19.dp)
                                .background(color)
                        )
                    }
                    if (index < route.stations.lastIndex) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .width(4.dp)
                                .height(19.dp)
                                .background(color)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(2.dp, color, CircleShape)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = station,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink
                )
                if (index == 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Primary)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text("출발", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                } else if (index == route.stations.lastIndex) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Soft)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text("도착", style = MaterialTheme.typography.labelSmall, color = Primary)
                    }
                } else if (isTransfer) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Soft)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text("환승", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StationSelector(
    label: String,
    station: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
        Spacer(Modifier.height(2.dp))
        Text(
            text = station,
            style = MaterialTheme.typography.titleSmall,
            color = Ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StationPickerSheet(
    title: String,
    subway: SubwayRepository?,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedLine by remember { mutableStateOf("전체") }
    val lines = listOf("전체", "1호선", "2호선", "3호선", "4호선", "5호선", "6호선", "7호선", "8호선", "9호선")
    val stations = remember(subway, query, selectedLine) {
        subway?.stations.orEmpty()
            .filter { query.isBlank() || it.name.contains(query.trim(), ignoreCase = true) }
            .filter { selectedLine == "전체" || selectedLine in it.lines }
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
            Row(
                modifier = Modifier.padding(start = 20.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "이동경로",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                YIconButton(
                    icon = Icons.Filled.Close,
                    contentDescription = "닫기",
                    onClick = onDismiss
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(lines, key = { it }) { line ->
                    val selected = line == selectedLine
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) Soft else SurfaceWhite)
                            .clickable { selectedLine = line }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selected) Primary else TextSecondary
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            YSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "역 이름을 검색하세요",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
            if (stations.isEmpty()) {
                Text(
                    text = if (subway == null) "역 정보를 불러오는 중이에요" else "검색 결과가 없어요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(stations, key = { it.name }) { station ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(station.name) }
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = station.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Ink
                                )
                                Spacer(Modifier.height(3.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    station.lines.take(3).forEach { line ->
                                        YLineBadge(line = line)
                                    }
                                }
                            }
                            Text("›", style = MaterialTheme.typography.titleLarge, color = TextTertiary)
                        }
                    }
                }
            }
            }
        }
    }
}
