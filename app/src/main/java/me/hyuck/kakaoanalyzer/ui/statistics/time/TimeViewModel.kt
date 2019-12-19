package me.hyuck.kakaoanalyzer.ui.statistics.time

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.TimeInfo

class TimeViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    var userList: LiveData<List<String>>? = null

    fun setData(chat: Chat, userName: String): LiveData<List<TimeInfo>> {
        return if ( userName == "전체" ) {
            db!!.messageDao().getTimeInfo(chat.id, chat.startDate, chat.endDate)
        } else {
            db!!.messageDao().getTimeInfoUser(chat.id, chat.startDate, chat.endDate, userName)
        }
    }

    fun setUserData(chat: Chat) {
        userList = db!!.messageDao().getUserNames(chat.id)
    }
}