package com.tts.yeojeong.data

import android.content.Context
import com.tts.yeojeong.BuildConfig
import com.tts.yeojeong.R
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.model.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TourApiRepository(private val context: Context) {
    private val nearbyCache = HashMap<String, List<Spot>>()
    private val searchCache = HashMap<String, List<Spot>>()
    private val detailCache = HashMap<String, Spot>()
    private var seedCache: List<Spot>? = null

    suspend fun nearby(
        lat: Double,
        lng: Double,
        stations: List<Station>,
        radius: Int = 3000,
        rows: Int = 40
    ): List<Spot> = withContext(Dispatchers.IO) {
        val cacheKey = "%.4f,%.4f,$radius,$rows".format(lat, lng)
        nearbyCache[cacheKey]?.let { return@withContext it }

        val remote = runCatching {
            val url = apiUrl(
                "locationBasedList2",
                linkedMapOf(
                    "numOfRows" to rows.toString(),
                    "pageNo" to "1",
                    "MobileOS" to "AND",
                    "MobileApp" to "YeoJeong",
                    "_type" to "json",
                    "mapX" to lng.toString(),
                    "mapY" to lat.toString(),
                    "radius" to radius.toString(),
                    "arrange" to "E"
                )
            )
            fetchItems(url).mapNotNull { toSpot(it, stations) }
                .filter { it.contentTypeId in setOf("12", "14", "28", "39") }
                .map { it.copy(distanceMeters = haversine(lat, lng, it.lat, it.lng).toInt()) }
                .filter { (it.distanceMeters ?: Int.MAX_VALUE) <= radius }
        }.getOrDefault(emptyList())

        val seed = seed(stations)
            .map { it.copy(distanceMeters = haversine(lat, lng, it.lat, it.lng).toInt()) }
            .filter { (it.distanceMeters ?: Int.MAX_VALUE) <= radius }
        val merged = (remote + seed.filter { local ->
            remote.none { it.contentId == local.contentId }
        })
            .distinctBy { it.contentId ?: it.id }
            .sortedBy { it.distanceMeters ?: Int.MAX_VALUE }
            .take(rows)
        nearbyCache[cacheKey] = merged
        merged
    }

    suspend fun search(
        keyword: String,
        stations: List<Station>,
        rows: Int = 30
    ): List<Spot> = withContext(Dispatchers.IO) {
        val query = keyword.trim()
        if (query.isEmpty()) return@withContext emptyList()
        searchCache[query]?.let { return@withContext it }

        val remote = runCatching {
            val url = apiUrl(
                "searchKeyword2",
                linkedMapOf(
                    "numOfRows" to rows.toString(),
                    "pageNo" to "1",
                    "MobileOS" to "AND",
                    "MobileApp" to "YeoJeong",
                    "_type" to "json",
                    "keyword" to query,
                    "areaCode" to "1",
                    "arrange" to "E"
                )
            )
            fetchItems(url).mapNotNull { toSpot(it, stations) }
                .filter { it.contentTypeId in setOf("12", "14", "28", "39") }
        }.getOrDefault(emptyList())

        val fallback = seed(stations).filter { spot ->
            spot.name.contains(query, ignoreCase = true) ||
                spot.address.contains(query, ignoreCase = true) ||
                spot.station.contains(query, ignoreCase = true) ||
                spot.tags.any { it.contains(query, ignoreCase = true) }
        }
        val merged = (remote + fallback.filter { local ->
            remote.none { it.contentId == local.contentId }
        }).distinctBy { it.contentId ?: it.id }.take(rows)
        searchCache[query] = merged
        merged
    }

    fun seed(stations: List<Station>): List<Spot> {
        seedCache?.let { return it }
        val all = mutableListOf<Spot>()
        for (file in listOf("tour_seed.json", "tour_cafe_seed.json")) {
            runCatching {
                val text = context.assets.open(file).bufferedReader().use { it.readText() }
                val root = JSONObject(text)
                val items = itemsOf(root)
                items.forEach { item ->
                    toSpot(item, stations, isSeed = true)?.let { all.add(it) }
                }
            }
        }
        val result = all.distinctBy { it.contentId ?: it.id }
            .filter { it.contentTypeId in setOf("12", "14", "28", "39") }
            .sortedBy { it.distanceMeters ?: Int.MAX_VALUE }
        seedCache = result
        return result
    }

    fun seedNear(lat: Double, lng: Double, stations: List<Station>, radius: Int): List<Spot> =
        seed(stations)
            .map { it.copy(distanceMeters = haversine(lat, lng, it.lat, it.lng).toInt()) }
            .filter { (it.distanceMeters ?: Int.MAX_VALUE) <= radius }
            .sortedBy { it.distanceMeters }

    suspend fun detail(spot: Spot): Spot = withContext(Dispatchers.IO) {
        val contentId = spot.contentId ?: return@withContext spot
        detailCache[contentId]?.let { return@withContext it }
        val contentTypeId = spot.contentTypeId.orEmpty()
        val common = runCatching {
            fetchSingle(
                apiUrl(
                    "detailCommon2",
                    linkedMapOf(
                        "contentId" to contentId,
                        "MobileOS" to "AND",
                        "MobileApp" to "YeoJeong",
                        "_type" to "json"
                    )
                )
            )
        }.getOrNull()
        val intro = if (contentTypeId.isNotBlank()) {
            runCatching {
                fetchSingle(
                    apiUrl(
                        "detailIntro2",
                        linkedMapOf(
                            "contentId" to contentId,
                            "contentTypeId" to contentTypeId,
                            "MobileOS" to "AND",
                            "MobileApp" to "YeoJeong",
                            "_type" to "json"
                        )
                    )
                )
            }.getOrNull()
        } else null
        val overview = common?.optString("overview").orEmpty()
        val homepage = common?.optString("homepage").orEmpty()
            .replace(Regex("<[^>]+>"), "")
            .trim()
        val tel = common?.optString("tel").orEmpty().ifBlank {
            intro?.optString("infocenter").orEmpty()
        }
        val useTime = intro?.optString("usetime").orEmpty()
        val restDate = intro?.optString("restdate").orEmpty()
        val hours = listOf(useTime, restDate)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
        val image = listOf(
            common?.optString("firstimage").orEmpty(),
            common?.optString("firstimage2").orEmpty()
        ).firstOrNull { it.isNotBlank() }?.replace("http://", "https://")
        val enriched = spot.copy(
            description = overview.ifBlank { spot.description },
            overview = overview.ifBlank { spot.overview },
            phone = tel,
            hours = hours,
            homepage = homepage,
            imageUrl = image ?: spot.imageUrl
        )
        detailCache[contentId] = enriched
        enriched
    }

    private fun apiUrl(operation: String, params: LinkedHashMap<String, String>): URL {
        val key = URLEncoder.encode(BuildConfig.PUBLIC_DATA_API_KEY, "UTF-8")
        val query = params.entries.joinToString("&") { (k, v) ->
            k + "=" + URLEncoder.encode(v, "UTF-8")
        }
        return URL("https://apis.data.go.kr/B551011/KorService2/$operation?serviceKey=$key&$query")
    }

    private fun fetchItems(url: URL): List<JSONObject> {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 7000
            readTimeout = 7000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Yeojeong/1.0 (Android)")
        }
        return try {
            if (connection.responseCode !in 200..299) return emptyList()
            val text = connection.inputStream.bufferedReader().use { it.readText() }
            itemsOf(JSONObject(text))
        } catch (_: Exception) {
            emptyList()
        } finally {
            connection.disconnect()
        }
    }

    private fun fetchSingle(url: URL): JSONObject? {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 7000
            readTimeout = 7000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Yeojeong/1.0 (Android)")
        }
        return try {
            if (connection.responseCode !in 200..299) return null
            val root = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            itemsOf(root).firstOrNull()
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun itemsOf(root: JSONObject): List<JSONObject> {
        val body = root.optJSONObject("response")?.optJSONObject("body") ?: return emptyList()
        val items = body.opt("items")
        val node = when (items) {
            is JSONObject -> items.opt("item")
            is JSONArray -> items
            else -> null
        }
        return when (node) {
            is JSONArray -> (0 until node.length()).mapNotNull { node.optJSONObject(it) }
            is JSONObject -> listOf(node)
            else -> emptyList()
        }
    }

    private fun toSpot(
        item: JSONObject,
        stations: List<Station>,
        isSeed: Boolean = false
    ): Spot? {
        val name = item.optString("title").trim()
        if (name.isBlank()) return null
        val contentId = item.optString("contentid").ifBlank { null }
        val contentTypeId = item.optString("contenttypeid").ifBlank { null }
        val lat = item.optString("mapy").toDoubleOrNull() ?: return null
        val lng = item.optString("mapx").toDoubleOrNull() ?: return null
        val station = nearestStation(lat, lng, stations)
            ?: Station("서울시청", "", listOf("1호선"), 37.5665, 126.9780)
        val distanceToStation = haversine(lat, lng, station.lat, station.lng)
        val distance = item.optString("dist").toDoubleOrNull()?.toInt()
            ?: distanceToStation.toInt()
        val category = categoryOf(name, contentTypeId)
        val address = listOf(
            item.optString("addr1"),
            item.optString("addr2")
        ).filter { it.isNotBlank() }.joinToString(" ")
        val image = listOf(
            item.optString("firstimage"),
            item.optString("firstimage2")
        ).firstOrNull { it.isNotBlank() }?.replace("http://", "https://")
        val tags = buildList {
            add(category)
            add("공공데이터")
            val area = address.split(' ').getOrNull(1)
            if (!area.isNullOrBlank()) add(area)
        }
        return Spot(
            id = contentId ?: ("tour-" + name.hashCode()),
            name = name,
            category = category,
            tagline = if (address.isBlank()) "TourAPI에서 제공하는 $category 장소" else address,
            description = if (address.isBlank()) "TourAPI에서 제공하는 $category 장소입니다."
            else "$category 장소입니다. 주소는 ${address}입니다.",
            station = station.name,
            line = station.lines.firstOrNull() ?: "서울",
            exit = "인근",
            walkMinutes = (distanceToStation / 67.0).toInt().coerceAtLeast(1),
            address = address,
            tags = tags,
            lat = lat,
            lng = lng,
            imageRes = fallbackImage(category),
            imageUrl = image,
            contentId = contentId,
            contentTypeId = contentTypeId,
            phone = item.optString("tel"),
            distanceMeters = distance
        )
    }

    private fun categoryOf(name: String, contentTypeId: String?): String = when {
        name.contains("카페") || name.contains("커피") || name.contains("찻집") -> "카페"
        contentTypeId == "39" -> "맛집"
        contentTypeId == "14" -> "문화"
        contentTypeId == "28" -> "레포츠"
        else -> "산책"
    }

    private fun fallbackImage(category: String): Int = when (category) {
        "카페" -> R.drawable.spot_cafe
        "맛집" -> R.drawable.spot_city
        "문화" -> R.drawable.spot_city
        "레포츠" -> R.drawable.spot_park
        else -> R.drawable.spot_park
    }

    private fun nearestStation(lat: Double, lng: Double, stations: List<Station>): Station? =
        stations.minByOrNull { haversine(lat, lng, it.lat, it.lng) }

    private fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2) * sin(dLng / 2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
