package me.hyuck.kakaoanalyzer.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
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
    @Suppress("PrivatePropertyName")
    private val TAG:String? = ChatViewModel::class.simpleName



    private var chats: LiveData<List<Chat>>

    @Suppress("DEPRECATION")
    private val txtFilePath = Environment.getExternalStorageDirectory().absolutePath + "/KakaoTalk/Chats"
    private val txtFileName = "KakaoTalkChats.txt"

    init {
        chats = getAllChats()
    }

    fun getAllChats(): LiveData<List<Chat>> {
        return db!!.chatDao().getAllChats()
    }

    private fun getFilePaths(): List<String> {
        return db!!.chatDao().getFilePaths()
    }

    private fun insertChat(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            db!!.chatDao().insert(chat)
        }
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
        if ( file.exists() ) {
            Log.d(TAG, "[1] [파일유무체크] [file exists] [${file.absolutePath}]")
            var isExist = false
            for (filePath in Objects.requireNonNull(getFilePaths())) {
                if (filePath == file.absolutePath) isExist = true
            }
            if (!isExist) {
                Log.d(TAG, "[2] [DB에 해당 데이터 없음] [insertChat]")
                parseFileAndInsertChat(file)
            } else {
                Log.d(TAG, "[2] [DB에 해당 데이터 이미 있음] [PASS]")
            }
        } else {
            Log.d(TAG, "[1] [파일유무체크] [file not exists] [${file.absolutePath}]")
        }
    }

    /**
     * 파일 읽어서 title, date, size 파싱 후 DB Insert
     */
    private fun parseFileAndInsertChat(file: File) {
        val filePath = file.absolutePath
        try {
            BufferedReader(FileReader(file)).use { br ->
                val title = br.readLine()
                val date = br.readLine()
                val size: String = StringUtils.parseMemory(file.length())
                val chat = Chat(title, date, size, filePath)
                insertChat(chat)
                Log.i(TAG, "chat : $chat")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}