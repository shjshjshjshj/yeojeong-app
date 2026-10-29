package com.tts.yeojeong.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.model.Review
import com.tts.yeojeong.model.Visit
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.yeojeongDataStore by preferencesDataStore(name = "yeojeong_preferences")

class AppPreferences(private val context: Context) {
    private object Keys {
        val onboardingDone = booleanPreferencesKey("onboarding_done")
        val nickname = stringPreferencesKey("nickname")
        val gender = stringPreferencesKey("gender")
        val mbti = stringPreferencesKey("mbti")
        val homeStation = stringPreferencesKey("home_station")
        val workStation = stringPreferencesKey("work_station")
        val savedIds = stringSetPreferencesKey("saved_ids")
        val visitLog = stringPreferencesKey("visit_log")
        val reviews = stringPreferencesKey("reviews")
        val notifications = booleanPreferencesKey("notifications")
    }

    val flow: Flow<Prefs> = context.yeojeongDataStore.data.map { values ->
        Prefs(
            loaded = true,
            onboardingDone = values[Keys.onboardingDone] ?: false,
            nickname = values[Keys.nickname] ?: "여정",
            gender = values[Keys.gender].orEmpty(),
            mbti = values[Keys.mbti].orEmpty(),
            homeStation = values[Keys.homeStation] ?: "홍대입구",
            workStation = values[Keys.workStation] ?: "시청",
            savedIds = values[Keys.savedIds] ?: emptySet(),
            visitLog = decodeVisits(values[Keys.visitLog].orEmpty()),
            reviews = decodeReviews(values[Keys.reviews].orEmpty()),
            notifications = values[Keys.notifications] ?: true
        )
    }

    suspend fun setOnboardingDone() = context.yeojeongDataStore.edit {
        it[Keys.onboardingDone] = true
    }

    suspend fun setRoute(home: String, work: String) = context.yeojeongDataStore.edit {
        it[Keys.homeStation] = home
        it[Keys.workStation] = work
    }

    suspend fun setNickname(name: String) = context.yeojeongDataStore.edit {
        it[Keys.nickname] = name.trim().ifBlank { "여정" }
    }

    suspend fun setProfile(name: String, gender: String, mbti: String) = context.yeojeongDataStore.edit {
        it[Keys.nickname] = name.trim().ifBlank { "여정" }
        it[Keys.gender] = gender
        it[Keys.mbti] = mbti
    }

    suspend fun setNotifications(enabled: Boolean) = context.yeojeongDataStore.edit {
        it[Keys.notifications] = enabled
    }

    suspend fun toggleSaved(spotId: String) = context.yeojeongDataStore.edit { values ->
        val current = values[Keys.savedIds]?.toMutableSet() ?: mutableSetOf()
        if (!current.add(spotId)) current.remove(spotId)
        values[Keys.savedIds] = current
    }

    suspend fun markVisited(spotId: String) = context.yeojeongDataStore.edit { values ->
        val today = LocalDate.now().toEpochDay()
        val visits = decodeVisits(values[Keys.visitLog].orEmpty()).toMutableList()
        if (visits.none { it.spotId == spotId && it.epochDay == today }) {
            visits.add(Visit(spotId, today))
        }
        values[Keys.visitLog] = encodeVisits(visits.takeLast(200))
    }

    suspend fun addReview(review: Review) = context.yeojeongDataStore.edit { values ->
        val reviews = decodeReviews(values[Keys.reviews].orEmpty()).toMutableList()
        reviews.add(0, review)
        values[Keys.reviews] = encodeReviews(reviews.take(100))
    }

    suspend fun deleteReview(reviewId: String) = context.yeojeongDataStore.edit { values ->
        val reviews = decodeReviews(values[Keys.reviews].orEmpty())
            .filterNot { it.id == reviewId }
        values[Keys.reviews] = encodeReviews(reviews)
    }

    suspend fun reset() = context.yeojeongDataStore.edit { it.clear() }

    private fun decodeVisits(raw: String): List<Visit> = raw.lineSequence()
        .mapNotNull { line ->
            val parts = line.split('|')
            if (parts.size != 2) return@mapNotNull null
            val day = parts[1].toLongOrNull() ?: return@mapNotNull null
            Visit(parts[0], day)
        }
        .toList()

    private fun encodeVisits(visits: List<Visit>): String =
        visits.joinToString("\n") { it.spotId + "|" + it.epochDay }

    private fun decodeReviews(raw: String): List<Review> = runCatching {
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val keywords = item.optJSONArray("keywords")
            val photos = item.optJSONArray("photos")
            Review(
                id = item.optString("id"),
                spotId = item.optString("spotId"),
                spotName = item.optString("spotName"),
                nickname = item.optString("nickname"),
                title = item.optString("title"),
                content = item.optString("content"),
                keywords = if (keywords == null) emptyList()
                else (0 until keywords.length()).map { keywords.optString(it) },
                epochDay = item.optLong("epochDay"),
                photos = if (photos == null) emptyList()
                else (0 until photos.length()).map { photos.optString(it) }
            )
        }
    }.getOrDefault(emptyList())

    private fun encodeReviews(reviews: List<Review>): String {
        val array = JSONArray()
        reviews.forEach { review ->
            array.put(
                JSONObject()
                    .put("id", review.id)
                    .put("spotId", review.spotId)
                    .put("spotName", review.spotName)
                    .put("nickname", review.nickname)
                    .put("title", review.title)
                    .put("content", review.content)
                    .put("keywords", JSONArray(review.keywords))
                    .put("photos", JSONArray(review.photos))
                    .put("epochDay", review.epochDay)
            )
        }
        return array.toString()
    }
}
