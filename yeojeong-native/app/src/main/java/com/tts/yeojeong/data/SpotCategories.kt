package com.tts.yeojeong.data

import com.tts.yeojeong.model.Spot

data class CategoryGroup(val name: String, val subs: List<String>)

/**
 * TourAPI contentTypeId와 카테고리·태그를 묶어 대분류/소분류를 만듭니다.
 * 홈에서는 대분류를 고른 뒤 소분류까지 좁힐 수 있습니다.
 */
object SpotCategories {
    val groups = listOf(
        CategoryGroup("산책", listOf("강변", "숲길", "공원", "골목길", "도심")),
        CategoryGroup("카페", listOf("감성 카페", "북카페", "디저트")),
        CategoryGroup("맛집", listOf("한식", "분식", "양식", "디저트")),
        CategoryGroup("문화", listOf("전시", "공연", "서점", "도서관"))
    )

    fun groupOf(spot: Spot): String = when {
        spot.contentTypeId == "39" -> "맛집"
        spot.contentTypeId == "14" -> "문화"
        spot.category == "카페" -> "카페"
        else -> "산책"
    }

    fun subOf(spot: Spot): String {
        val text = (spot.name + " " + spot.category + " " + spot.tags.joinToString(" ")).lowercase()
        fun has(vararg keys: String) = keys.any { text.contains(it) }
        return when (groupOf(spot)) {
            "카페" -> when {
                has("북카페", "서점", "책") -> "북카페"
                has("디저트", "베이커리", "빵", "케이크", "도넛") -> "디저트"
                else -> "감성 카페"
            }
            "맛집" -> when {
                has("분식", "떡볶이", "김밥", "만두") -> "분식"
                has("양식", "파스타", "이탈리안", "브런치", "스테이크", "프렌치") -> "양식"
                has("디저트", "빵", "케이크") -> "디저트"
                else -> "한식"
            }
            "문화" -> when {
                has("도서관") -> "도서관"
                has("서점", "책방") -> "서점"
                has("공연", "극장", "연극", "뮤지컬", "음악", "콘서트") -> "공연"
                else -> "전시"
            }
            else -> when {
                has("한강", "강변", "천변", "호수", "물가", "리버") -> "강변"
                has("숲", "수목원", "림", "산책로", "산림") -> "숲길"
                has("공원", "정원") -> "공원"
                has("골목", "거리", "마을", "길") -> "골목길"
                else -> "도심"
            }
        }
    }

    fun spotsFor(spots: List<Spot>, group: String, sub: String?): List<Spot> =
        spots.filter { spot ->
            groupOf(spot) == group && (sub == null || subOf(spot) == sub)
        }
}
