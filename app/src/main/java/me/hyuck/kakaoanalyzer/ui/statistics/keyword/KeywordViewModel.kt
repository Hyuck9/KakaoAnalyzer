package me.hyuck.kakaoanalyzer.ui.statistics.keyword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.KeywordInfo

class KeywordViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var totalCount: MutableLiveData<String> = MutableLiveData()

    fun set10Data(chat: Chat): LiveData<List<KeywordInfo>> {
        return db!!.keywordDao().getKeywordInfoLimit(chat.id, chat.startDate, chat.endDate, 10)
    }

    fun getAllData(chat: Chat): LiveData<List<KeywordInfo>>? {
//        totalCount = db!!.keywordDao().getTotalCount(chat.id, chat.startDate, chat.endDate)
        return db!!.keywordDao().getKeywordInfo(chat.id, chat.startDate, chat.endDate)
    }

    fun findData(chat: Chat, keyword: String?): LiveData<List<KeywordInfo>>? {
        return if (keyword == null || keyword.isEmpty())
            db!!.keywordDao().getKeywordInfo(chat.id, chat.startDate, chat.endDate)
        else
            db!!.keywordDao().findKeywordInfo(chat.id, chat.startDate, chat.endDate, "%${keyword}%")
    }
}