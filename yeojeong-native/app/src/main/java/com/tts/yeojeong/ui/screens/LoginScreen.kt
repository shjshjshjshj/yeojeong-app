package com.tts.yeojeong.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tts.yeojeong.R
import com.tts.yeojeong.ui.theme.SurfaceWhite

/**
 * 로그인 화면 레이아웃: 가운데 로고 150dp, 그 아래 카카오·구글 로그인 버튼 208x40dp.
 * 인증 서버를 쓰지 않는 재구현이라 어느 곳을 눌러도 다음 단계로 넘어갑니다.
 */
@Composable
fun LoginScreen(onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .statusBarsPadding()
            .clickable(onClick = onContinue)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))
            Image(
                painter = painterResource(R.drawable.ic_login),
                contentDescription = "여정",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(120.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = painterResource(R.drawable.kakao_login_medium_narrow),
                    contentDescription = "카카오로 시작하기",
                    modifier = Modifier
                        .width(208.dp)
                        .height(40.dp),
                    contentScale = ContentScale.FillBounds
                )
                Spacer(Modifier.height(24.dp))
                Image(
                    painter = painterResource(R.drawable.button_googlelogin),
                    contentDescription = "구글로 시작하기",
                    modifier = Modifier
                        .width(208.dp)
                        .height(40.dp),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}
