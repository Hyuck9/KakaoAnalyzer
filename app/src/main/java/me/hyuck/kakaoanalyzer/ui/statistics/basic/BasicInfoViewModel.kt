package me.hyuck.kakaoanalyzer.ui.statistics.basic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.KeywordInfo
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.util.DateUtils

class BasicInfoViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var userCount: LiveData<Int>? = null
    var messageCount: LiveData<Int>? = null
    var keywordCount: LiveData<Int>? = null
    var participantInfo: LiveData<List<ParticipantInfo>>? = null
    var keywordInfo: LiveData<List<KeywordInfo>>? = null

    lateinit var period: String

    fun setData(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            userCount = db!!.messageDao().getUserCount(chat.id, chat.startDate, chat.endDate)
            messageCount = db.messageDao().getMessageCount(chat.id, chat.startDate, chat.endDate)
            keywordCount = db.keywordDao().getKeywordCount(chat.id, chat.startDate, chat.endDate)
            period = "${DateUtils.convertDateToStringFormat(chat.startDate, "yyyy-MM-dd")} ~ ${DateUtils.convertDateToStringFormat(chat.endDate, "yyyy-MM-dd")}"
        }
    }

    fun set5ParticipantData(chat: Chat) {
        participantInfo = db!!.messageDao().getParticipantInfoLimit(chat.id, chat.startDate, chat.endDate, 5)
    }

    fun set5KeywordData(chat: Chat) {
        keywordInfo = db!!.keywordDao().getKeywordInfoLimit(chat.id, chat.startDate, chat.endDate, 5)
    }

}