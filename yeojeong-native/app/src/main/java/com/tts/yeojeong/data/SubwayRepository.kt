package com.tts.yeojeong.data

import android.content.Context
import com.tts.yeojeong.model.Edge
import com.tts.yeojeong.model.RouteLeg
import com.tts.yeojeong.model.RoutePlan
import com.tts.yeojeong.model.Station
import org.json.JSONArray
import org.json.JSONObject
import java.util.PriorityQueue
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SubwayRepository private constructor(
    val stations: List<Station>,
    private val edges: Map<String, List<Edge>>
) {
    private val stationByName = stations.associateBy { it.name }

    fun station(name: String): Station? = stationByName[name]

    fun linesFor(name: String): List<String> = stationByName[name]?.lines.orEmpty()

    fun findRoute(start: String, end: String): RoutePlan? {
        if (start == end) return RoutePlan(listOf(start), emptyList(), 0, 0)
        if (start !in stationByName || end !in stationByName) return null

        data class State(val station: String, val line: String)
        data class Item(val state: State, val cost: Int)

        val distance = HashMap<State, Int>()
        val previous = HashMap<State, State?>()
        val queue = PriorityQueue<Item>(compareBy { it.cost })

        stationByName[start]?.lines.orEmpty().distinct().forEach { line ->
            val state = State(start, line)
            distance[state] = 0
            previous[state] = null
            queue.add(Item(state, 0))
        }
        if (queue.isEmpty()) return null

        var best: State? = null
        while (queue.isNotEmpty()) {
            val item = queue.poll() ?: break
            if (item.cost != distance[item.state]) continue
            if (item.state.station == end) {
                best = item.state
                break
            }
            val nextEdges = edges[item.state.station].orEmpty()
            for (edge in nextEdges) {
                val transfer = if (edge.line == item.state.line) 0 else 1
                val next = State(edge.to, edge.line)
                val nextCost = item.cost + 1 + transfer * 2
                if (nextCost < (distance[next] ?: Int.MAX_VALUE)) {
                    distance[next] = nextCost
                    previous[next] = item.state
                    queue.add(Item(next, nextCost))
                }
            }
        }

        val endState = best ?: return null
        val states = ArrayDeque<State>()
        var cursor: State? = endState
        while (cursor != null) {
            states.addFirst(cursor)
            cursor = previous[cursor]
        }

        val stationPath = mutableListOf<String>()
        val legs = mutableListOf<RouteLeg>()
        var currentLine = ""
        var legStations = mutableListOf<String>()
        for (state in states) {
            if (stationPath.lastOrNull() != state.station) stationPath.add(state.station)
            if (currentLine.isEmpty()) {
                currentLine = state.line
                legStations.add(state.station)
            } else if (state.line == currentLine) {
                legStations.add(state.station)
            } else {
                if (legStations.lastOrNull() != state.station) legStations.add(state.station)
                legs.add(RouteLeg(currentLine, legStations))
                currentLine = state.line
                legStations = mutableListOf(state.station)
            }
        }
        if (legStations.isNotEmpty()) legs.add(RouteLeg(currentLine, legStations))

        val stops = (stationPath.size - 1).coerceAtLeast(0)
        val transfers = (legs.size - 1).coerceAtLeast(0)
        val distanceMeters = stationPath.zipWithNext().sumOf { (from, to) ->
            val a = stationByName[from]
            val b = stationByName[to]
            if (a == null || b == null) 0.0
            else haversine(a.lat, a.lng, b.lat, b.lng)
        }
        val rideMinutes = if (distanceMeters > 0) {
            ceil(distanceMeters / 1000.0 / 30.0 * 60.0).toInt()
        } else {
            stops * 2
        }
        return RoutePlan(
            stations = stationPath,
            legs = legs,
            transfers = transfers,
            minutes = (rideMinutes + (stops * 0.4).toInt() + transfers * 4 + 2)
                .coerceAtLeast(1)
        )
    }

    companion object {
        fun load(context: Context): SubwayRepository {
            val stationLines = HashMap<String, MutableSet<String>>()
            val positions = HashMap<String, Pair<String, Pair<Double, Double>>>()
            val adjacency = HashMap<String, MutableList<Edge>>()
            val capital = runCatching {
                val text = context.assets.open("capitalStations.json")
                    .bufferedReader().use { it.readText() }
                    .removePrefix("\uFEFF")
                JSONArray(text)
            }.getOrNull()
            if (capital != null && capital.length() > 0) {
                val rawLines = HashMap<String, List<String>>()
                for (i in 0 until capital.length()) {
                    val item = capital.optJSONObject(i) ?: continue
                    val name = item.optString("name").trim()
                    if (name.isBlank()) continue
                    val lines = mutableListOf<String>()
                    val lineArray = item.optJSONArray("lines")
                    if (lineArray != null) {
                        for (j in 0 until lineArray.length()) {
                            lines.add(normalizeLine(lineArray.optString(j)))
                        }
                    }
                    if (lines.isEmpty()) continue
                    rawLines[name] = lines.distinct()
                    stationLines.getOrPut(name) { linkedSetOf() }.addAll(lines.distinct())
                    val lat = item.optDouble("latitude", 0.0)
                    val lng = item.optDouble("longitude", 0.0)
                    if (lat != 0.0 && lng != 0.0) positions[name] = "" to (lat to lng)
                }
                for (i in 0 until capital.length()) {
                    val item = capital.optJSONObject(i) ?: continue
                    val name = item.optString("name").trim()
                    if (name !in rawLines) continue
                    val around = item.optJSONArray("around_stations") ?: continue
                    for (j in 0 until around.length()) {
                        val other = around.optString(j).trim()
                        if (other.isBlank() || other == name || other !in rawLines) continue
                        val common = rawLines[name].orEmpty()
                            .intersect(rawLines[other].orEmpty().toSet())
                            .firstOrNull()
                        val line = common ?: rawLines[name].orEmpty().firstOrNull() ?: "수도권"
                        val listA = adjacency.getOrPut(name) { mutableListOf() }
                        if (listA.none { it.to == other && it.line == line }) {
                            listA.add(Edge(other, line))
                        }
                        val listB = adjacency.getOrPut(other) { mutableListOf() }
                        if (listB.none { it.to == name && it.line == line }) {
                            listB.add(Edge(name, line))
                        }
                    }
                }
                return buildRepository(stationLines, positions, adjacency)
            }
            val groups = LinkedHashMap<String, MutableList<Pair<Int, String>>>()
            val array: JSONArray = runCatching {
                JSONArray(context.assets.open("subwayPos.json").bufferedReader().use { it.readText() })
            }.getOrElse { JSONArray() }
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                val name = item.optString("station_nm").trim()
                val line = item.optString("station_line").trim()
                if (name.isBlank() || line.isBlank()) continue
                val code = item.opt("station_cd")?.toString().orEmpty()
                val lat = item.optDouble("posy", 0.0)
                val lng = item.optDouble("posx", 0.0)
                stationLines.getOrPut(name) { linkedSetOf() }.add(line)
                if (lat != 0.0 && lng != 0.0) {
                    positions[name] = code to (lat to lng)
                }
                val codeValue = code.replace(Regex("[^0-9]"), "").toIntOrNull()
                    ?: (100000 + i)
                groups.getOrPut(line) { mutableListOf() }.add(codeValue to name)
            }
            for ((line, group) in groups) {
                val names = group
                    .distinctBy { it.second }
                    .sortedBy { it.first }
                    .map { it.second }
                for (i in 0 until names.lastIndex) {
                    val a = names[i]
                    val b = names[i + 1]
                    if (a == b) continue
                    adjacency.getOrPut(a) { mutableListOf() }.add(Edge(b, line))
                    adjacency.getOrPut(b) { mutableListOf() }.add(Edge(a, line))
                }
            }

            return buildRepository(stationLines, positions, adjacency)
        }

        private fun buildRepository(
            stationLines: Map<String, Set<String>>,
            positions: Map<String, Pair<String, Pair<Double, Double>>>,
            adjacency: Map<String, List<Edge>>
        ): SubwayRepository {
            val stationList = stationLines.map { (name, lines) ->
                val pos = positions[name]
                Station(
                    name = name,
                    code = pos?.first.orEmpty(),
                    lines = lines.toList().sorted(),
                    lat = pos?.second?.first ?: 0.0,
                    lng = pos?.second?.second ?: 0.0
                )
            }.sortedBy { it.name }
            return SubwayRepository(
                stationList.filter { it.lines.isNotEmpty() },
                repairSkippedEdges(stationLines, positions, adjacency)
            )
        }

        /**
         * around_stations는 "인접"이 아니라 "같은 노선의 주변역"이라
         * 홍대입구-공덕처럼 중간역(서강대)을 건너뛴 간선이 섞여 있습니다.
         * 두 역 사이 선형 안에 같은 노선 역이 있으면 그 역을 경유하도록 간선을 다시 잇습니다.
         */
        private fun repairSkippedEdges(
            stationLines: Map<String, Set<String>>,
            positions: Map<String, Pair<String, Pair<Double, Double>>>,
            adjacency: Map<String, List<Edge>>
        ): Map<String, List<Edge>> {
            val byLine = HashMap<String, MutableList<String>>()
            stationLines.forEach { (name, lines) ->
                lines.forEach { line ->
                    byLine.getOrPut(line) { mutableListOf() }.add(name)
                }
            }
            val rebuilt = HashMap<String, MutableList<Edge>>()
            adjacency.forEach { (from, list) -> rebuilt[from] = list.toMutableList() }
            val additions = mutableListOf<Triple<String, String, String>>()

            adjacency.forEach { (from, list) ->
                val a = positions[from]?.second ?: return@forEach
                for (edge in list) {
                    val b = positions[edge.to]?.second ?: continue
                    val direct = haversine(a.first, a.second, b.first, b.second)
                    if (direct < 600.0) continue
                    var via: String? = null
                    var viaDistance = Double.MAX_VALUE
                    for (candidate in byLine[edge.line].orEmpty()) {
                        if (candidate == from || candidate == edge.to) continue
                        val c = positions[candidate]?.second ?: continue
                        val t = projectionRatio(a, b, c)
                        if (t < 0.1 || t > 0.9) continue
                        val first = haversine(a.first, a.second, c.first, c.second)
                        val second = haversine(c.first, c.second, b.first, b.second)
                        if (first + second - direct > 150.0) continue
                        if (first < viaDistance) {
                            viaDistance = first
                            via = candidate
                        }
                    }
                    val stop = via ?: continue
                    rebuilt[from]?.removeAll { it.to == edge.to && it.line == edge.line }
                    additions.add(Triple(from, stop, edge.line))
                    additions.add(Triple(stop, edge.to, edge.line))
                }
            }

            additions.forEach { (from, to, line) ->
                val list = rebuilt.getOrPut(from) { mutableListOf() }
                if (list.none { it.to == to && it.line == line }) list.add(Edge(to, line))
            }
            return rebuilt
        }

        /** 선분 a-b 위로 투영한 c의 위치를 0~1 비율로 돌려줍니다. */
        private fun projectionRatio(
            a: Pair<Double, Double>,
            b: Pair<Double, Double>,
            c: Pair<Double, Double>
        ): Double {
            val ax = a.second
            val ay = a.first
            val bx = b.second
            val by = b.first
            val cx = c.second
            val cy = c.first
            val dx = bx - ax
            val dy = by - ay
            val lengthSquared = dx * dx + dy * dy
            if (lengthSquared == 0.0) return -1.0
            return ((cx - ax) * dx + (cy - ay) * dy) / lengthSquared
        }

        private fun normalizeLine(raw: String): String {
            val trimmed = raw.trim()
            val numbered = Regex("^0([1-9])호선$").find(trimmed)
            if (numbered != null) return numbered.groupValues[1] + "호선"
            if (trimmed == "경의선") return "경의중앙선"
            return trimmed
        }

        private fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            if (lat1 == 0.0 || lat2 == 0.0) return 0.0
            val r = 6371000.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
            return r * 2 * atan2(sqrt(a), sqrt(1 - a))
        }
    }
}
