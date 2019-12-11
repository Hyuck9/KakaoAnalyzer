package me.hyuck.kakaoanalyzer.ui.statistics.keyword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.model.KeywordInfo

class KeywordViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var keywordInfo: LiveData<List<KeywordInfo>>? = null

    fun set10Data(chatId: Long) {
        keywordInfo = db!!.keywordDao().getKeywordInfoLimit10(chatId)
    }
    fun setData(chatId: Long) {
        keywordInfo = db!!.keywordDao().getKeywordInfo(chatId)
    }

}