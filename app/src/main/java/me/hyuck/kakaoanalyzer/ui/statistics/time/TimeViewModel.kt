package me.hyuck.kakaoanalyzer.ui.statistics.time

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.TimeInfo

class TimeViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var timeInfo: LiveData<List<TimeInfo>>? = null

    fun setData(chat: Chat) {
        timeInfo = db!!.messageDao().getTimeInfo(chat.id, chat.startDate, chat.endDate)
    }
}