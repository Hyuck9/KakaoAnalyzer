package me.hyuck.kakaoanalyzer.ui.statistics.participant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.ParticipantInfo

class ParticipantViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var participantInfo: LiveData<List<ParticipantInfo>>? = null
    var totalCount: LiveData<String>? = null
    var participantCount: LiveData<Int>? = null

    fun set10Data(chat: Chat) {
        participantInfo = db!!.messageDao().getParticipantInfoLimit10(chat.id)
    }
    fun getAllData(chat: Chat): LiveData<List<ParticipantInfo>>? {
        totalCount = db!!.messageDao().getTotalCount(chat.id)
        participantCount = db.messageDao().getUserCount(chat.id)
        return db.messageDao().getParticipantInfo(chat.id)
    }

    fun findData(chat: Chat, user: String?): LiveData<List<ParticipantInfo>>? {
        return if (user == null || user.isEmpty())
            db!!.messageDao().getParticipantInfo(chat.id)
        else
            db!!.messageDao().findParticipantInfo(chat.id, "%${user}%")
    }


}