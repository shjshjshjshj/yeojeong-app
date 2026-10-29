package com.tts.yeojeong.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tts.yeojeong.ui.components.YScreenHeader
import com.tts.yeojeong.ui.theme.Ink
import com.tts.yeojeong.ui.theme.Page
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.Soft
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.TextSecondary

private data class TermArticle(val title: String, val body: String)

private val serviceTerms = listOf(
    TermArticle(
        "제1조 (목적)",
        "이 약관은 여정(이하 '서비스')을 이용하는 데 필요한 조건과 절차, 이용자와 운영자의 권리와 의무를 정하는 것을 목적으로 합니다."
    ),
    TermArticle(
        "제2조 (서비스의 내용)",
        "서비스는 출퇴근 지하철 경로를 기준으로 잠깐 들를 수 있는 힐링 스팟을 추천합니다. 장소 정보는 한국관광공사 TourAPI와 공공데이터를, 날씨는 기상청 초단기예보를 사용합니다. 저장한 스팟과 방문 기록을 바탕으로 주간 리포트를 제공하고, 이용자가 작성한 리뷰를 보여줍니다."
    ),
    TermArticle(
        "제3조 (이용 계약)",
        "서비스는 별도의 회원가입 없이 이용할 수 있습니다. 프로필과 기록은 이용자의 기기 안에만 저장되며 운영자 서버로 전송되지 않습니다. 앱을 삭제하거나 데이터를 초기화하면 함께 사라집니다."
    ),
    TermArticle(
        "제4조 (이용자의 의무)",
        "이용자는 다른 사람의 권리를 침해하거나 허위 사실을 게시해서는 안 됩니다. 리뷰는 직접 방문한 경험을 바탕으로 작성해 주세요. 운영자는 약관을 위반한 게시물을 숨기거나 삭제할 수 있습니다."
    ),
    TermArticle(
        "제5조 (정보의 정확성)",
        "장소의 주소, 운영시간, 휴무일, 요금은 공공데이터 제공 시점의 정보입니다. 실제 운영 상황과 다를 수 있으므로 방문 전에 장소에 직접 확인해 주세요. 날씨 정보 역시 예보이며 실제 날씨와 차이가 있을 수 있습니다."
    ),
    TermArticle(
        "제6조 (서비스의 변경과 중단)",
        "운영자는 데이터 제공 기관의 사정, 시스템 점검, 서비스 개편 등의 이유로 서비스의 전부나 일부를 변경하거나 중단할 수 있습니다. 중요한 변경은 앱 안에서 미리 알립니다."
    ),
    TermArticle(
        "제7조 (책임의 한계)",
        "서비스는 장소를 추천하고 정보를 보여줄 뿐, 장소에서 발생한 사고나 분쟁의 당사자가 아닙니다. 지도와 길 안내는 외부 지도 서비스(Google Maps)로 연결되며, 해당 서비스의 약관이 함께 적용됩니다."
    ),
    TermArticle(
        "제8조 (준거법)",
        "이 약관은 대한민국 법률에 따라 해석되고 적용됩니다."
    ),
    TermArticle(
        "부칙",
        "이 약관은 2026년 10월 24일부터 적용됩니다."
    )
)

private val privacyTerms = listOf(
    TermArticle(
        "1. 수집하는 개인정보 항목",
        "필수 항목: 닉네임, 출발역, 도착역\n선택 항목: 성별, MBTI, 리뷰 제목·본문·키워드, 저장한 스팟, 방문 기록\n서비스는 위치 권한을 요청하지 않으며, 기기 위치를 수집하지 않습니다. 리뷰에 첨부한 사진은 기기에서 선택한 파일에 접근하는 권한만 사용하고 서버로 전송하지 않습니다."
    ),
    TermArticle(
        "2. 개인정보의 수집 방법",
        "온보딩과 설정 화면에서 이용자가 직접 입력한 정보, 그리고 스팟 저장·방문·리뷰 작성 과정에서 생성되는 기록을 수집합니다."
    ),
    TermArticle(
        "3. 개인정보의 이용 목적",
        "출퇴근 경로에 맞는 스팟 추천, 주간 리포트와 통계 표시, 이용자가 작성한 리뷰의 표시, 기기 간 기록 유지에 사용합니다. 광고나 마케팅 목적으로는 사용하지 않습니다."
    ),
    TermArticle(
        "4. 보관 장소와 기간",
        "프로필과 기록은 이용자의 기기 내부 저장소에만 저장됩니다. 보관 기간은 이용자가 해당 항목을 삭제하거나 설정의 “데이터 초기화”를 실행할 때까지이며, 초기화하면 즉시 삭제됩니다."
    ),
    TermArticle(
        "5. 개인정보 처리의 위탁",
        "운영자는 이용자의 개인정보를 외부에 위탁하지 않습니다. 장소·날씨 정보를 불러오는 과정에서 검색어와 좌표가 공공데이터 기관에 전송될 뿐이며, 그 요청에는 이용자를 식별할 수 있는 정보가 포함되지 않습니다."
    ),
    TermArticle(
        "6. 제3자 제공",
        "운영자는 이용자의 개인정보를 제3자에게 판매하거나 제공하지 않습니다. 다만 장소·날씨 정보를 불러오고 지도로 연결하는 과정에서 검색어, 좌표, 기기 정보가 한국관광공사 TourAPI, 기상청, Open-Meteo, Google Maps로 전송될 수 있습니다."
    ),
    TermArticle(
        "7. 이용자의 권리",
        "이용자는 설정 화면에서 닉네임을 바꾸고, 저장한 스팟과 리뷰를 직접 삭제할 수 있습니다. “데이터 초기화”로 기기에 저장된 전체 기록을 삭제할 수 있으며, 그 밖의 열람·정정·삭제 요청은 아래 연락처로 보내주세요."
    ),
    TermArticle(
        "8. 쿠키와 추적",
        "서비스는 웹 쿠키를 사용하지 않고, 광고 식별자를 수집하지 않습니다. 이용 행태를 분석하는 별도 도구도 사용하지 않습니다."
    ),
    TermArticle(
        "9. 아동의 개인정보",
        "서비스는 만 14세 미만 아동을 대상으로 하지 않으며, 아동의 개인정보를 의도적으로 수집하지 않습니다."
    ),
    TermArticle(
        "10. 개인정보 보호 문의",
        "개인정보 처리에 관한 문의는 앱 내 문의 채널로 접수해 주세요. 확인 후 지체 없이 답변드립니다."
    ),
    TermArticle(
        "11. 고지의 의무",
        "이 방침의 내용이 바뀌면 시행 전에 앱 안에서 알립니다. 방침은 2026년 10월 24일부터 적용됩니다."
    )
)

@Composable
fun TermsScreen(onBack: () -> Unit) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val articles = remember(tab) {
        if (tab == 0) serviceTerms else privacyTerms
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(modifier = Modifier.background(SurfaceWhite)) {
            YScreenHeader(title = "약관", onBack = onBack)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("이용약관", "개인정보처리방침").forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (tab == index) Soft else SurfaceWhite)
                        .clickable { tab = index }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (tab == index) Primary else TextSecondary
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp)
        ) {
            item {
                Text(
                    text = if (tab == 0) "여정 이용약관" else "여정 개인정보처리방침",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink
                )
                Spacer(Modifier.height(16.dp))
            }
            items(articles) { article ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Ink
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = article.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
