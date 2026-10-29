package com.tts.yeojeong

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tts.yeojeong.data.AppPreferences
import com.tts.yeojeong.data.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** 재부팅이나 앱 업데이트 뒤 AlarmManager 예약이 사라지는 것을 막습니다. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            return
        }
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = AppPreferences(context.applicationContext).flow.first()
                if (prefs.loaded && prefs.notifications) {
                    ReminderScheduler.schedule(context.applicationContext)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
