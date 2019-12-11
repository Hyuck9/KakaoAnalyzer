package me.hyuck.kakaoanalyzer.ui.statistics.keyword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.model.KeywordInfo

class KeywordViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var keywordInfo: LiveData<List<KeywordInfo>>? = null
    var totalCount: LiveData<String>? = null

    fun set10Data(chatId: Long) {
        keywordInfo = db!!.keywordDao().getKeywordInfoLimit10(chatId)
    }
    fun getAllData(chatId: Long): LiveData<List<KeywordInfo>>? {
        totalCount = db!!.keywordDao().getTotalCount(chatId)
        return db.keywordDao().getKeywordInfo(chatId)
    }

    fun findData(chatId: Long, keyword: String?): LiveData<List<KeywordInfo>>? {
        return if (keyword == null || keyword.isEmpty())
            db!!.keywordDao().getKeywordInfo(chatId)
        else
            db!!.keywordDao().findKeywordInfo(chatId, "%${keyword}%")
    }
}