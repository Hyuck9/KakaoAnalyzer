package me.hyuck.kakaoanalyzer.viewmodel

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.util.StringUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

class ChatViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private var chats: LiveData<List<Chat>>

    private val txtFilePath = Environment.getExternalStorageDirectory().absolutePath + "/KakaoTalk/Chats"
    private val txtFileName = "KakaoTalkChats.txt"

    init {
        chats = getAllChats()
    }

    fun getAllChats(): LiveData<List<Chat>> {
        return db!!.chatDao().getAllChats()
    }

//    fun getFilePaths(): List<String> {
//        return db!!.chatDao().getFilePaths()
//    }

    fun insert(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            db!!.chatDao().insert(chat)
        }
    }

    /**
     * 카카오톡에서 내보낸 txt 파일 읽어오기
     */
    private fun getFileList(): Array<File>? {
        Log.d("TEST", "txtFilePath : $txtFilePath")
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

    fun parseChatInfo() {
        val saveFile: Array<File>? = getFileList()
        if (saveFile != null) for (folder in saveFile) {
            val file = File(folder.absolutePath + File.separator + txtFileName)
            if (file.exists()) {
                Log.d(
                    "parseChatInfo",
                    "[1] [파일유무체크] [file exists] [" + file.absolutePath + "]"
                )
//                var isExist = false
//                for (filePath in Objects.requireNonNull(getFilePaths())) {
//                    if (filePath == file.absolutePath) isExist = true
//                }

//                if (!isExist) {
                    Log.d("parseChatInfo", "[2] [DB에 해당 데이터 없음] [insertChat]")
                    val filePath = file.absolutePath
                    try {
                        BufferedReader(FileReader(file)).use { br ->
                            val title = br.readLine()
                            val date = br.readLine()
                            val size: String = StringUtils.parseMemory(file.length())
                            val chat = Chat(title, date, size, filePath)
                            insert(chat)
                            Log.i("parseChatFile", "chat : $chat")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
//                } else {
//                    Log.d("parseChatInfo", "[2] [DB에 해당 데이터 이미 있음] [PASS]")
//                }
            } else {
                Log.w(
                    "parseChatInfo",
                    "[1] [파일유무체크] [file not exists] [" + file.absolutePath + "]"
                )
            }
        }
    }
}