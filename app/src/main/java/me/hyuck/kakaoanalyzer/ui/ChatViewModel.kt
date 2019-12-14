package me.hyuck.kakaoanalyzer.ui

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.db.entity.Keyword
import me.hyuck.kakaoanalyzer.db.entity.Message
import me.hyuck.kakaoanalyzer.util.DateUtils
import me.hyuck.kakaoanalyzer.util.StringUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

class ChatViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    @Suppress("PrivatePropertyName")
    private val TAG:String? = ChatViewModel::class.simpleName

    @Suppress("DEPRECATION")
    private val txtFilePath = Environment.getExternalStorageDirectory().absolutePath + "/KakaoTalk/Chats"
    private val txtFileName = "KakaoTalkChats.txt"

    private lateinit var messages: MutableList<Message>
    private lateinit var keywords: MutableList<Keyword>

    private lateinit var startDate: Date
    private lateinit var endDate: Date

    var isComplete: MutableLiveData<Boolean>? = MutableLiveData()

    init {
        isComplete!!.postValue(true)
    }

    fun getAllChats(): LiveData<List<Chat>> {
        return db!!.chatDao().getAllChats()
    }

    private fun getFilePaths(): List<String> {
        return db!!.chatDao().getFilePaths()
    }

    private fun insertChat(chat: Chat): Long {
        return db!!.chatDao().insert(chat)
    }

    fun deleteChat(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            db!!.chatDao().delete(chat)
            db.messageDao().deleteAllMessagesFromChatId(chat.id)
            db.keywordDao().deleteAllKeywordsFromChatId(chat.id)
        }
    }

    private fun insertMessages(messages: List<Message>) {
        db!!.messageDao().insert(messages)
    }

    private fun insertKeywords(keywords: List<Keyword>) {
        db!!.keywordDao().insert(keywords)
    }

    /**
     * 카카오톡에서 내보낸 txt 파일 읽어오기
     */
    private fun getFileList(): Array<File>? {
        val saveFile = File(txtFilePath)
        return saveFile.listFiles()
    }

    /**
     * txt 파일이 있는지 유무
     */
    fun isChatExsist(): Boolean {
        val files = getFileList()
        return files != null && files.isNotEmpty()
    }

    /**
     * 파일 파싱 및 DB 체크, Insert
     */
    fun parseChatInfo() {
        FileAsyncTask().execute()
    }

    /**
     * 파일 파싱 및 DB 체크, Insert (비동기 스레드)
     */
    @SuppressLint("StaticFieldLeak")
    inner class FileAsyncTask : AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val saveFile: Array<File>? = getFileList()
            saveFile?.let {files ->
                files.forEach {
                    val file = File(it.absolutePath + File.separator + txtFileName)
                    checkFileAndInsertLogic(file)
                }
            }
            return null
        }
    }


    /**
     * 파일 유무 체크 및 DB에 해당 데이터 있는지 체크
     */
    private fun checkFileAndInsertLogic(file: File) {
        if ( isFileExists(file) ) {
            var isExist = false
            for (filePath in Objects.requireNonNull(getFilePaths())) {
                if (filePath == file.absolutePath) isExist = true
            }
            if (!isExist) {
                Log.d(TAG, "[2] [DB에 해당 데이터 없음] [insertChat]")
                parseFile(file)
            } else {
                Log.d(TAG, "[2] [DB에 해당 데이터 이미 있음] [PASS]")
            }
        }
    }

    /**
     * 파일 유무 체크
     */
    private fun isFileExists(file: File): Boolean {
        return if (file.exists()) {
            Log.d(TAG, "[1] [파일유무체크] [file exists] [${file.absolutePath}]")
            true
        } else {
            Log.d(TAG, "[1] [파일유무체크] [file not exists] [${file.absolutePath}]]")
            false
        }
    }

    /**
     * KakaoTalkChats.txt 파일 파싱
     */
    private fun parseFile(file: File) {
        isComplete!!.postValue(false)
        messages = mutableListOf()
        keywords = mutableListOf()
        val filePath = file.absolutePath
        try {
            BufferedReader(FileReader(file)).use { br ->
                val title = br.readLine()
                val date = br.readLine()
                br.readLine()
                br.readLine()
                startDate = DateUtils.convertStringToDate(br.readLine())
                val size: String = StringUtils.parseMemory(file.length())
                var message: StringBuilder? = null
                var read: String?
                while (br.readLine().also { read = it } != null) {
                    if (StringUtils.isFirstDateTimeMessage(read!!)) {
                        Log.d(TAG, "[2] [메시지형태체크] ['10월 24일 오전 10:44,' 형식으로 시작하는 데이터]")
                        if (message != null) {
                            Log.i(TAG, "[3] [Null체크] [하나의 완성된 메시지 - 메시지 파싱 시작]")
                            parseMessage(message.toString().trim())
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
                    parseMessage(message.toString().trim())
                    insertData(Chat(title, date, size, filePath, startDate, endDate))
                } else {
                    Log.w(TAG, "[6] [Null체크] [Null!!]")
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        isComplete!!.postValue(true)
    }

    /**
     * chats, messages, keywords Insert
     */
    private fun insertData(chat: Chat) {
        val chatId = insertChat(chat)
        insertMessages(messages.map { it.copy(chatId = chatId) })
        insertKeywords(keywords.map { it.copy(chatId = chatId) })
    }

    /**
     * 각 메시지 파싱
     * ex) 2019년 6월 12일 오전 1:24, 김영훈 : 과외가 어딨냐 시골에
     */
    private fun parseMessage(msg: String) {
        if (StringUtils.isPassedInOutMessage(msg)) {
            Log.d(TAG, "[MESSAGE] [PASS] [$msg]")
            return
        }
        val firstSplitIndex = msg.indexOf(" : ")
        val dateAndName = msg.split(" : ").toTypedArray()[0]
//        val dateTime: Date = DateUtils.convertStringToDate(dateAndName.split(", ").toTypedArray()[0])
        endDate = DateUtils.convertStringToDate(dateAndName.split(", ").toTypedArray()[0])
        val userName = dateAndName.split(", ").toTypedArray()[1]
        val content = msg.substring(firstSplitIndex + 3)
        val calendar = GregorianCalendar.getInstance()
        calendar.time = endDate
        val message = Message(0, endDate, userName, content, calendar[Calendar.HOUR_OF_DAY])
        messages.add(message)
        if (StringUtils.isPassedMessage(content)) {
            Log.d(TAG, "[MESSAGE] [PASS] [$content]")
            passMessageInsertKeyword(message)
        } else {
            parseContent(message)
            Log.d(TAG, message.toString())
        }
    }

    /**
     * 메시지 내용 단어별 파싱
     */
    private fun parseContent(message: Message) {
        val userName: String = message.userName
        val content: String = message.msgContent
        val dateTime = message.dateTime
        val words = content.split(" ").toTypedArray()
        for (word in words) {
            if (StringUtils.isPassedKeyword(word)) {
                Log.d(TAG, "[KEYWORDS] [PASS] [$word]")
                continue
            }
            keywords.add(Keyword(0, userName, word, dateTime))
        }
    }

    private fun passMessageInsertKeyword(message: Message) {
        val userName: String = message.userName
        val dateTime = message.dateTime
        val word: String = StringUtils.replacePassedMessage(message.msgContent)
        keywords.add(Keyword(0, userName, word, dateTime))
    }

}