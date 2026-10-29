package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tts.yeojeong.R
import com.tts.yeojeong.ui.components.YIconButton
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.PageAlt
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.TextSecondary

private data class OnboardingPage(
    val title: String,
    val body: String,
    val image: Int
)

private val pages = listOf(
    OnboardingPage(
        title = "출퇴근길, 한 정거장의 여유",
        body = "지금 내 위치와 날씨를 확인하고,\n오늘 들를 곳을 골라보세요.",
        image = R.drawable.tuto_f
    ),
    OnboardingPage(
        title = "서울의 숨은 힐링 스팟",
        body = "사람이 적은 공원, 조용한 카페, 물가 산책로를\n여정이 골라 소개해요.",
        image = R.drawable.tuto_sec
    ),
    OnboardingPage(
        title = "내 루트에 맞춘 추천",
        body = "출발역과 도착역을 정하면,\n내릴 만한 역과 가까운 장소를 알려드려요.",
        image = R.drawable.tuto_thir
    ),
    OnboardingPage(
        title = "매주 새로운 챌린지",
        body = "작은 목표를 하나씩 달성하며\n나만의 서울 지도를 채워보세요.",
        image = R.drawable.tuto_flour
    ),
    OnboardingPage(
        title = "짧은 기록, 오래 남는 여정",
        body = "키워드로 가볍게 리뷰를 남기고\n주간 리포트로 한 주를 돌아보세요.",
        image = R.drawable.tuto_fift
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pager = rememberPagerState(pageCount = { pages.size })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageAlt)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "뒤로가기",
                onClick = onFinish
            )
            Spacer(Modifier.weight(1f))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { index ->
                val active = pager.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = if (active) 16.dp else 6.dp, height = 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (active) Primary else Soft)
                )
            }
        }

        HorizontalPager(
            state = pager,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = pages[page].title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Ink,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = pages[page].body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Image(
                    painter = painterResource(pages[page].image),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(onClick = onFinish),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "여정 시작하기",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.White
                )
            }
        }
    }
}
