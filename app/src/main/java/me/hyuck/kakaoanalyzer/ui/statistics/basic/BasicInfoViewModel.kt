package me.hyuck.kakaoanalyzer.ui.statistics.basic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.KeywordInfo
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.util.DateUtils

class BasicInfoViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var userCount = MutableLiveData<Int>()
    var messageCount = MutableLiveData<Int>()
    var keywordCount = MutableLiveData<Int>()
    var chat = MutableLiveData<Chat>()
    var period = MutableLiveData<String>()
    var oneOnOneAnalytics = MutableLiveData<Boolean>()

    fun selectUserCount(chat: Chat): LiveData<Int> {
        return db!!.messageDao().getUserCount(chat.id, chat.startDate, chat.endDate)
    }

    fun selectUserCountIgnoreUser(chat: Chat): LiveData<Int> {
        return db!!.messageDao().getUserCountIgnoreUser(chat.id, chat.startDate, chat.endDate)
    }

    fun selectMessageCount(chat: Chat): LiveData<Int> {
        return db!!.messageDao().getMessageCount(chat.id, chat.startDate, chat.endDate)
    }

    fun selectKeywordCount(chat: Chat): LiveData<Int> {
        return db!!.keywordDao().getKeywordCount(chat.id, chat.startDate, chat.endDate)
    }

    fun setPeriod(chat: Chat) {
        period.value = "${DateUtils.convertDateToStringFormat(chat.startDate, "yyyy-MM-dd")} ~ ${DateUtils.convertDateToStringFormat(chat.endDate, "yyyy-MM-dd")}"
    }

    fun set5ParticipantData(chat: Chat): LiveData<List<ParticipantInfo>> {
        return db!!.messageDao().getParticipantInfoLimit(chat.id, chat.startDate, chat.endDate, 5)
    }

    fun set5KeywordData(chat: Chat): LiveData<List<KeywordInfo>> {
        return db!!.keywordDao().getKeywordInfoLimit(chat.id, chat.startDate, chat.endDate, 5)
    }

}