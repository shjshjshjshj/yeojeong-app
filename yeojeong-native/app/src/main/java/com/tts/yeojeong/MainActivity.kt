package com.tts.yeojeong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tts.yeojeong.ui.YeojeongApp
import com.tts.yeojeong.ui.theme.YeojeongTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            YeojeongTheme {
                YeojeongApp()
            }
        }
    }
}
