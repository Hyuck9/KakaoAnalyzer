package me.hyuck.kakaoanalyzer.ui.statistics.participant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.db.entity.Message
import me.hyuck.kakaoanalyzer.model.OneOnOneAnalyticsInfo
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.model.ReplyInfo
import me.hyuck.kakaoanalyzer.util.DateUtils
import me.hyuck.kakaoanalyzer.util.StringUtils
import java.util.*
import kotlin.math.roundToInt

class ParticipantViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var totalCount: MutableLiveData<String> = MutableLiveData()
    var participantCount: MutableLiveData<Int> = MutableLiveData()
    private var firstMessages = mutableListOf<Message>()
    private var firstReplies = mutableListOf<ReplyInfo>()
    private var allReplies = mutableListOf<ReplyInfo>()

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

    fun selectMessageData(chat: Chat): LiveData<List<Message>> {
        return db!!.messageDao().getMessage(chat.id, chat.startDate, chat.endDate)
    }

    fun setOneOnOneAnalytics(messageList: List<Message>) {
        var preUser = "me.hyuck.kakaoanalyzer.ui.statistics.participant.preUser"
        var isFirst = true
        var preDate = Date(0)
        firstMessages = mutableListOf()
        firstReplies = mutableListOf()
        allReplies = mutableListOf()

        messageList.forEach { message ->
            if ( DateUtils.isDiff1Hour(preDate, message.dateTime) ) {
                firstMessages.add(message)
                isFirst = true
            } else if ( preUser != message.userName ) {
                if (isFirst) {
                    firstReplies.add(ReplyInfo(message.userName, DateUtils.calcTime(preDate, message.dateTime)))
                    isFirst = false
                }
                allReplies.add(ReplyInfo(message.userName, DateUtils.calcTime(preDate, message.dateTime)))
            }

            preUser = message.userName
            preDate = message.dateTime
        }
    }

    fun getOneOnOneAnalyticsInfo(messageList: List<Message>): OneOnOneAnalyticsInfo {
        val title = allReplies.filter { it.userName != "회원님" }.distinctBy { it.userName }[0].userName
        val oneOnOneAnalyticsInfo = OneOnOneAnalyticsInfo(title)

        val userFirstList = firstMessages.filter { it.userName == "회원님" }
        val someFirstList = firstMessages.filter { it.userName != "회원님" }
        val userAverageList: List<Long> = allReplies.filter{it.userName == "회원님"}.map{ it.replyTime }
        val someAverageList: List<Long> = allReplies.filter{it.userName != "회원님"}.map{ it.replyTime }
        val userFirstReplies: List<Long> = firstReplies.filter{it.userName == "회원님"}.map{ it.replyTime }
        val someFirstReplies: List<Long> = firstReplies.filter{it.userName != "회원님"}.map{ it.replyTime }
        val allAverageList: List<Long> = allReplies.map{ it.replyTime }
        val allFirstList: List<Long> = firstReplies.map{ it.replyTime }
        val userMessage = messageList.filter { it.userName == "회원님" }
        val someMessage = messageList.filter { it.userName != "회원님" }

        oneOnOneAnalyticsInfo.userFirstCount = "${userFirstList.size}회 (${(userFirstList.size.toFloat() / firstMessages.size * 100 * 100).roundToInt() /100.0}%)"      // 회원님 선톡 횟수
        oneOnOneAnalyticsInfo.someoneUserFirstCount = "${someFirstList.size}회(${(someFirstList.size.toFloat()/firstMessages.size * 100 * 100).roundToInt() / 100.0}%)" // 상대방 선톡 횟수
        oneOnOneAnalyticsInfo.userFirstReply = DateUtils.calcReplyTime(userFirstReplies.average())              // 회원님 선톡 답장 시간
        oneOnOneAnalyticsInfo.someoneUserFirstReply = DateUtils.calcReplyTime(someFirstReplies.average())       // 상대방 선톡 답장 시간
        oneOnOneAnalyticsInfo.userAverageReply = DateUtils.calcReplyTime(userAverageList.average())             // 회원님 평균 답장 시간
        oneOnOneAnalyticsInfo.someoneUserAverageReply = DateUtils.calcReplyTime(someAverageList.average())      // 상대방 평균 답장 시간
        oneOnOneAnalyticsInfo.allFirstReply = DateUtils.calcReplyTime(allFirstList.average())                   // 대화방 전체 평균 선톡 답장 시간
        oneOnOneAnalyticsInfo.allAverageReply = DateUtils.calcReplyTime(allAverageList.average())               // 대화방 전체 평균 답장 시간
        oneOnOneAnalyticsInfo.userMessage = StringUtils.getFormattedNumber(userMessage.size.toString())         // 회원님 보낸 메시지
        oneOnOneAnalyticsInfo.someoneUserMessage = StringUtils.getFormattedNumber(someMessage.size.toString())  // 상대방 보낸 메시지
        return oneOnOneAnalyticsInfo
    }

}