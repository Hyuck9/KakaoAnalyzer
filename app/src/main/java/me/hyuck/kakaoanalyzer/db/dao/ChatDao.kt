package me.hyuck.kakaoanalyzer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.hyuck.kakaoanalyzer.db.entity.Chat

@Dao
interface ChatDao {

    @Insert
    fun insert(chat: Chat)

    @Update
    fun update(chat: Chat)

    @Delete
    fun delete(chat: Chat)

    @Query("DELETE FROM chat_info")
    fun deleteAllChats()

    @Query("SELECT * FROM chat_info ORDER BY date DESC")
    fun getAllChats(): LiveData<List<Chat>>

    @Query("SELECT filePath FROM chat_info")
    fun getFilePaths(): List<String>
}