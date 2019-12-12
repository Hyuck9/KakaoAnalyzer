package me.hyuck.kakaoanalyzer.ui.statistics.basic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.util.DateUtils
import java.util.*

class BasicInfoViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var userCount: LiveData<Int>? = null
    var messageCount: LiveData<Int>? = null
    var keywordCount: LiveData<Int>? = null
    lateinit var period: String

    fun setData(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            userCount = db!!.messageDao().getUserCount(chat.id)
            messageCount = db.messageDao().getMessageCount(chat.id)
            keywordCount = db.keywordDao().getKeywordCount(chat.id)
            period = "${DateUtils.convertDateToStringFormat(chat.startDate, "yyyy-MM-dd")} ~ ${DateUtils.convertDateToStringFormat(chat.endDate, "yyyy-MM-dd")}"
        }
    }


}