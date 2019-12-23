package me.hyuck.kakaoanalyzer.ui.statistics.participant

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.db.entity.Message
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.model.ReplyInfo
import me.hyuck.kakaoanalyzer.util.DateUtils
import java.util.*

class ParticipantViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var totalCount: MutableLiveData<String> = MutableLiveData()
    var participantCount: MutableLiveData<Int> = MutableLiveData()

    fun set10Data(chat: Chat): LiveData<List<ParticipantInfo>> {
        return db!!.messageDao().getParticipantInfoLimit(chat.id, chat.startDate, chat.endDate, 10)
    }

    fun getAllData(chat: Chat): LiveData<List<ParticipantInfo>>? {
        return db!!.messageDao().getParticipantInfo(chat.id, chat.startDate, chat.endDate)
    }

    fun findData(chat: Chat, user: String?): LiveData<List<ParticipantInfo>>? {
        return if (user == null || user.isEmpty())
            db!!.messageDao().getParticipantInfo(chat.id, chat.startDate, chat.endDate)
        else
            db!!.messageDao().findParticipantInfo(chat.id, chat.startDate, chat.endDate, "%${user}%")
    }

    fun setMessageData(chat: Chat): LiveData<List<Message>> {
        return db!!.messageDao().getMessage(chat.id, chat.startDate, chat.endDate)
    }

    val firstMessages = mutableListOf<Message>()
    val firstReplys = mutableListOf<ReplyInfo>()
    val allReplys = mutableListOf<ReplyInfo>()
    fun setOneToOneAnalytics(messageList: List<Message>) {
        var preUser = "me.hyuck.kakaoanalyzer.ui.statistics.participant.preUser"
        var isFirst = true
        var preDate = Date(0)

        messageList.forEachIndexed { index, message ->
            if ( DateUtils.isDiff1Hour(preDate, message.dateTime) ) {
                firstMessages.add(message)
                isFirst = true
            } else if ( preUser != message.userName ) {
                if (isFirst) {
                    firstReplys.add(ReplyInfo(message.userName, DateUtils.calcTime(preDate, message.dateTime)))
                    isFirst = false
                }
                allReplys.add(ReplyInfo(message.userName, DateUtils.calcTime(preDate, message.dateTime)))
            }

            preUser = message.userName
            preDate = message.dateTime
        }
    }

    fun test() {
        val list: List<Long> = allReplys.filter{it.userName == "회원님"}.map{ it.replyTime }
        Log.d("TEST", "회원님 평균 : ${DateUtils.calcReplyTime(list.average())}")
        val list2: List<Long> = allReplys.filter{it.userName != "회원님"}.map{ it.replyTime }
        Log.d("TEST", "상대방 평균 : ${DateUtils.calcReplyTime(list2.average())}")
        val list3: List<Long> = allReplys.map{ it.replyTime }
        Log.d("TEST", "전체 평균 : ${DateUtils.calcReplyTime(list3.average())}")

        val list4: List<Long> = firstReplys.filter{it.userName == "회원님"}.map{ it.replyTime }
        Log.d("TEST", "회원님 평균 선톡 : ${DateUtils.calcReplyTime(list4.average())}")
        val list5: List<Long> = firstReplys.filter{it.userName != "회원님"}.map{ it.replyTime }
        Log.d("TEST", "상대방 평균 선톡 : ${DateUtils.calcReplyTime(list5.average())}")
        val list6: List<Long> = firstReplys.map{ it.replyTime }
        Log.d("TEST", "전체 평균 선톡 : ${DateUtils.calcReplyTime(list6.average())}")

        val list7 = firstMessages.filter { it.userName == "회원님" }
        Log.d("TEST", "회원님 선톡횟수 : ${list7.size}회(${list7.size.toFloat()/firstMessages.size*100}%)")
        val list8 = firstMessages.filter { it.userName != "회원님" }
        Log.d("TEST", "상대방 선톡횟수 : ${list8.size}회(${list8.size.toFloat()/firstMessages.size*100}%)")
    }

}