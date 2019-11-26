package me.hyuck.kakaoanalyzer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hyuck.kakaoanalyzer.db.AppDatabase
import me.hyuck.kakaoanalyzer.db.entity.Chat

class ChatViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private var chats: LiveData<List<Chat>>

    init {
        chats = getAllChats()
    }

    fun getAllChats(): LiveData<List<Chat>> {
        return db!!.chatDao().getAllChats()
    }

    fun insert(chat: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            db!!.chatDao().insert(chat)
        }
    }
}