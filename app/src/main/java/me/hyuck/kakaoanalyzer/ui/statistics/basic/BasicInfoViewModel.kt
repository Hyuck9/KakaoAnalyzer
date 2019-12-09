package me.hyuck.kakaoanalyzer.ui.statistics.basic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.util.DateUtils
import java.util.*

class BasicInfoViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var userCount: LiveData<Int>? = null
    var messageCount: LiveData<Int>? = null
    var keywordCount: LiveData<Int>? = null
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    lateinit var period: String

    fun setData(chatId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            userCount = db!!.messageDao().getUserCount(chatId)
            messageCount = db.messageDao().getMessageCount(chatId)
            keywordCount = db.keywordDao().getKeywordCount(chatId)
            startDate = db.messageDao().getStartDate(chatId)
            endDate = db.messageDao().getEndDate(chatId)
            period = "${DateUtils.convertDateToStringFormat(startDate, "yyyy-MM-dd")} ~ ${DateUtils.convertDateToStringFormat(endDate, "yyyy-MM-dd")}"
        }
    }


}