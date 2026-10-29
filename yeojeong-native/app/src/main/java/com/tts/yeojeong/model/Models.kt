package com.tts.yeojeong.model

import androidx.annotation.DrawableRes

data class Spot(
    val id: String,
    val name: String,
    val category: String,
    val tagline: String,
    val description: String,
    val station: String,
    val line: String,
    val exit: String,
    val walkMinutes: Int,
    val address: String,
    val tags: List<String>,
    val lat: Double,
    val lng: Double,
    @param:DrawableRes val imageRes: Int,
    val imageUrl: String? = null,
    val contentId: String? = null,
    val contentTypeId: String? = null,
    val overview: String = "",
    val phone: String = "",
    val hours: String = "",
    val homepage: String = "",
    val distanceMeters: Int? = null
)

data class Station(
    val name: String,
    val code: String,
    val lines: List<String>,
    val lat: Double,
    val lng: Double
)

data class Edge(val to: String, val line: String)

data class RouteLeg(
    val line: String,
    val stations: List<String>
)

data class RoutePlan(
    val stations: List<String>,
    val legs: List<RouteLeg>,
    val transfers: Int,
    val minutes: Int
) {
    val stops: Int get() = (stations.size - 1).coerceAtLeast(0)
}

data class Prefs(
    val loaded: Boolean = false,
    val onboardingDone: Boolean = false,
    val nickname: String = "여정",
    val gender: String = "",
    val mbti: String = "",
    val homeStation: String = "홍대입구",
    val workStation: String = "시청",
    val savedIds: Set<String> = emptySet(),
    val visitLog: List<Visit> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val notifications: Boolean = true
)

data class Visit(val spotId: String, val epochDay: Long)

data class Review(
    val id: String,
    val spotId: String,
    val spotName: String,
    val nickname: String,
    val title: String,
    val content: String,
    val keywords: List<String>,
    val epochDay: Long,
    val photos: List<String> = emptyList()
)

sealed interface WeatherUi {
    data object Loading : WeatherUi
    data class Ready(val temperature: Int, val label: String) : WeatherUi
    data object Unavailable : WeatherUi
}
