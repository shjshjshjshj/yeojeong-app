package com.tts.yeojeong.data

import com.tts.yeojeong.BuildConfig
import com.tts.yeojeong.model.WeatherUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.tan

class WeatherRepository {
    suspend fun current(): WeatherUi = withContext(Dispatchers.IO) {
        fetchKma() ?: fetchOpenMeteo()
    }

    private fun fetchKma(): WeatherUi? {
        val key = BuildConfig.KMA_API_KEY.ifBlank { BuildConfig.PUBLIC_DATA_API_KEY }
        if (key.isBlank()) return null
        val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        val minute = if (now.minute < 30) 0 else 30
        val base = now.minusMinutes(45).withSecond(0).withNano(0).withMinute(minute)
        val grid = kmaGrid(37.5665, 126.9780)
        val encodedKey = URLEncoder.encode(key, "UTF-8")
        val date = base.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val time = base.format(DateTimeFormatter.ofPattern("HHmm"))
        val url = URL(
            "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst" +
                "?serviceKey=" + encodedKey +
                "&numOfRows=60&pageNo=1&dataType=JSON" +
                "&base_date=" + date + "&base_time=" + time +
                "&nx=" + grid.first + "&ny=" + grid.second
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 5000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Yeojeong/1.0 (Android)")
        }
        return try {
            if (connection.responseCode !in 200..299) return null
            val root = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            val response = root.optJSONObject("response") ?: return null
            if (response.optJSONObject("header")?.optString("resultCode") != "00") return null
            val items = response.optJSONObject("body")
                ?.optJSONObject("items")
                ?.opt("item") ?: return null
            val list = when (items) {
                is JSONArray -> (0 until items.length()).mapNotNull { items.optJSONObject(it) }
                is JSONObject -> listOf(items)
                else -> emptyList()
            }
            val values = list.associate {
                it.optString("category") to it.optString("fcstValue")
            }
            val temp = values["T1H"]?.toDoubleOrNull() ?: return null
            val sky = values["SKY"].orEmpty()
            val pty = values["PTY"].orEmpty()
            WeatherUi.Ready(
                temperature = kotlin.math.round(temp).toInt(),
                label = when {
                    pty !in setOf("", "0") -> "비"
                    sky == "1" -> "맑음"
                    sky == "3" -> "구름 많음"
                    sky == "4" -> "흐림"
                    else -> "구름 조금"
                }
            )
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun fetchOpenMeteo(): WeatherUi {
        val url = URL(
            "https://api.open-meteo.com/v1/forecast" +
                "?latitude=37.5665&longitude=126.9780" +
                "&current=temperature_2m,weather_code&timezone=Asia%2FSeoul"
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 4000
            readTimeout = 4000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Yeojeong/1.0 (Android)")
        }
        return try {
            if (connection.responseCode !in 200..299) return WeatherUi.Unavailable
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val current = JSONObject(body).optJSONObject("current")
            val temperature = current?.optDouble("temperature_2m", Double.NaN) ?: Double.NaN
            val code = current?.optInt("weather_code", -1) ?: -1
            if (temperature.isNaN()) WeatherUi.Unavailable
            else WeatherUi.Ready(kotlin.math.round(temperature).toInt(), labelFor(code))
        } catch (_: Exception) {
            WeatherUi.Unavailable
        } finally {
            connection.disconnect()
        }
    }

    private fun labelFor(code: Int): String = when (code) {
        0 -> "맑음"
        1, 2 -> "구름 조금"
        3 -> "흐림"
        45, 48 -> "안개"
        in 51..57 -> "이슬비"
        in 61..67 -> "비"
        in 71..77 -> "눈"
        in 80..82 -> "소나기"
        in 85..86 -> "눈"
        in 95..99 -> "천둥"
        else -> "서울"
    }

    private fun kmaGrid(lat: Double, lng: Double): Pair<Int, Int> {
        val grid = 5.0
        val re = 6371.00877 / grid
        val degrad = Math.PI / 180.0
        val slat1 = 30.0 * degrad
        val slat2 = 60.0 * degrad
        val olon = 126.0 * degrad
        val olat = 38.0 * degrad
        val xo = 43
        val yo = 136
        val sn = ln(cos(slat1) / cos(slat2)) /
            ln(tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5))
        val sf = Math.pow(tan(Math.PI * 0.25 + slat1 * 0.5), sn) *
            cos(slat1) / sn
        val ro = re * sf / Math.pow(tan(Math.PI * 0.25 + olat * 0.5), sn)
        val ra = re * sf / Math.pow(
            tan(Math.PI * 0.25 + lat * degrad * 0.5),
            sn
        )
        var theta = lng * degrad - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn
        val nx = (ra * sin(theta) + xo + 1.5).toInt()
        val ny = (ro - ra * cos(theta) + yo + 1.5).toInt()
        return nx to ny
    }
}
