package me.hyuck.kakaoanalyzer.ui.statistics.participant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.model.ParticipantInfo

class ParticipantViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var participantInfo: LiveData<List<ParticipantInfo>>? = null
    var totalCount: LiveData<String>? = null
    var participantCount: LiveData<Int>? = null

    fun set10Data(chatId: Long) {
        participantInfo = db!!.messageDao().getParticipantInfoLimit10(chatId)
    }
    fun getAllData(chatId: Long): LiveData<List<ParticipantInfo>>? {
        totalCount = db!!.messageDao().getTotalCount(chatId)
        participantCount = db.messageDao().getUserCount(chatId)
        return db.messageDao().getParticipantInfo(chatId)
    }

    fun findData(chatId: Long, user: String?): LiveData<List<ParticipantInfo>>? {
        return if (user == null || user.isEmpty())
            db!!.messageDao().getParticipantInfo(chatId)
        else
            db!!.messageDao().findParticipantInfo(chatId, "%${user}%")
    }


}