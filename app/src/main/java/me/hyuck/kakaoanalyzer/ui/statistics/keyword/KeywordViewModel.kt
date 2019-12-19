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
    var userList: LiveData<List<String>>? = null

    fun set10Data(chat: Chat, userName: String): LiveData<List<KeywordInfo>> {
        return if ( userName == "전체" ) {
            db!!.keywordDao().getKeywordInfoLimit(chat.id, chat.startDate, chat.endDate, 10)
        } else {
            db!!.keywordDao().getKeywordInfoUserLimit(chat.id, chat.startDate, chat.endDate, 10, userName)
        }
    }

    fun setUserData(chat: Chat) {
        userList = db!!.messageDao().getUserNames(chat.id)
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