package me.hyuck.kakaoanalyzer.ui.statistics.keyword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.KeywordInfo

class KeywordViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var keywordInfo: LiveData<List<KeywordInfo>>? = null
    var totalCount: LiveData<String>? = null

    fun set10Data(chat: Chat) {
        keywordInfo = db!!.keywordDao().getKeywordInfoLimit10(chat.id)
    }
    fun getAllData(chat: Chat): LiveData<List<KeywordInfo>>? {
        totalCount = db!!.keywordDao().getTotalCount(chat.id)
        return db.keywordDao().getKeywordInfo(chat.id)
    }

    fun findData(chat: Chat, keyword: String?): LiveData<List<KeywordInfo>>? {
        return if (keyword == null || keyword.isEmpty())
            db!!.keywordDao().getKeywordInfo(chat.id)
        else
            db!!.keywordDao().findKeywordInfo(chat.id, "%${keyword}%")
    }
}