package me.hyuck.kakaoanalyzer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.Keyword
import me.hyuck.kakaoanalyzer.model.Message
import me.hyuck.kakaoanalyzer.util.DateUtils
import me.hyuck.kakaoanalyzer.util.StringUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class StatisticsViewModel(application: Application): AndroidViewModel(application) {

    @Suppress("PrivatePropertyName") private val TAG:String? = StatisticsViewModel::class.simpleName


    private var messages = mutableListOf<Message>()
    private var keywords = mutableListOf<Keyword>()
    private var users = mutableListOf<String>()
    private lateinit var startDate: String
    private lateinit var endDate: String
    lateinit var period: String


    /**
     * 메시지 내용 파싱
     */
    fun parseChatContent(chat: Chat) {
        val file = getFile(chat.filePath)
        startDate = DateUtils.convertDateFormat(chat.startDate, "yyyy-MM-dd")
        file?.let {
            try {
                BufferedReader(FileReader(file)).use { br ->
                    var message: StringBuilder? = null
                    var read: String?
                    while (br.readLine().also { read = it } != null) {
                        if (StringUtils.isFirstDateTimeMessage(read!!)) {
                            Log.d(TAG, "[2] [메시지형태체크] ['10월 24일 오전 10:44,' 형식으로 시작하는 데이터]")
                            if (message != null) {
                                Log.i(TAG, "[3] [Null체크] [하나의 완성된 메시지 - 메시지 파싱 시작]")
                                parseMessage(message.toString())
                            } else {
                                Log.w(TAG, "[3] [Null체크] [Null!!]")
                            }
                            message = StringBuilder(read!!)
                        } else {
                            Log.d(TAG, "[2] [메시지형태체크] ['10월 24일 오전 10:44,' 형식으로 시작하지 않는 데이터]")
                            if (StringUtils.isNotDateTimeMessage(read!!)) {
                                Log.i(TAG, "[4] [날짜인지체크] [\'10월 24일 오전 10:44\' 형식이 아닌 데이터]")
                                if (message != null) {
                                    Log.i(TAG, "[5] [Null체크] [개행된 메시지 이므로 이전 메시지 뒤에 붙이기]")
                                    message.append(" ").append(read)
                                } else {
                                    Log.w(TAG, "[5] [Null체크] [Null!!]")
                                }
                            } else {
                                Log.d(TAG, "[4] [날짜인지체크] [\'10월 24일 오전 10:44\' 형식인 데이터] [BY-PASS]")
                            }
                        }
                    }

                    if (message != null) {
                        Log.i("parseChatFile", "[6] [Null체크] [마지막 메시지 - 메시지 파싱 시작]")
                        parseMessage(message.toString())
                        val dateAndName = message.toString().split(" : ")[0]
                        endDate = DateUtils.convertDateFormat(dateAndName.split(", ")[0], "yyyy-MM-dd")
                        period = "$startDate ~ $endDate"
                    } else {
                        Log.w("parseChatFile", "[6] [Null체크] [Null!!]")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getFile(filePath: String): File? {
        val file = File(filePath)
        return if (file.exists()) {
            Log.d(TAG, "[1] [파일유무체크] [file exists] [${file.absolutePath}]")
            file
        } else {
            Log.d(TAG, "[1] [파일유무체크] [file not exists] [${file.absolutePath}]]")
            null
        }
    }

    private fun parseMessage(msg: String) {
        if (StringUtils.isPassedInOutMessage(msg)) {
            Log.d(TAG, "[MESSAGE] [PASS] [$msg]")
            return
        }
        // TODO: Message Parsing And Insert Message
        val firstSplitIndex = msg.indexOf(" : ")
        val dateAndName = msg.split(" : ").toTypedArray()[0]
        val dateTime: Date = DateUtils.convertStringToDate(dateAndName.split(", ").toTypedArray()[0])
        val userName = dateAndName.split(", ").toTypedArray()[1]
        val content = msg.substring(firstSplitIndex + 3)
        val calendar = GregorianCalendar.getInstance()
        calendar.time = dateTime
        val message = Message(
            dateTime,
            userName,
            content,
            calendar[Calendar.HOUR_OF_DAY]
        )
        users.add(userName)
        messages.add(message)
        if (StringUtils.isPassedMessage(content)) {
            Log.d(TAG, "[MESSAGE] [PASS] [$content]")
            return
        }
        parseContent(message)
        Log.d(TAG, message.toString())
    }

    private fun parseContent(message: Message) {
        val userName: String = message.userName
        val content: String = message.msgContent
        val words = content.split(" ").toTypedArray()
        for (word in words) {
            if (StringUtils.isPassedKeyword(word)) {
                Log.d(TAG, "[KEYWORDS] [PASS] [$word]")
                continue
            }
            keywords.add(Keyword(userName, word))
        }
    }

    fun getUserCount(): Int {
        val hUsers = HashSet<String>(users)
        return ArrayList<String>(hUsers).size
    }
    fun getMessageCount(): Int = messages.size
    fun getKeywordCount(): Int = keywords.size

}