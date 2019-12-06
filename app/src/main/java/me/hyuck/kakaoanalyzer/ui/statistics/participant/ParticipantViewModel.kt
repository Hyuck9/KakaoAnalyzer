package me.hyuck.kakaoanalyzer.ui.statistics.participant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.model.ParticipantInfo

class ParticipantViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var participantInfo: LiveData<List<ParticipantInfo>>? = null

    fun setData(chatId: Long) {
        participantInfo = db!!.messageDao().getParticipantInfo(chatId)
    }

}