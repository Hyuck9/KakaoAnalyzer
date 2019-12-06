package me.hyuck.kakaoanalyzer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import me.hyuck.kakaoanalyzer.db.entity.Message
import java.util.*

@Dao
interface MessageDao {
    @Insert
    fun insert(messages: List<Message>)

    @Query("DELETE FROM message_info WHERE chatId = :chatId")
    fun deleteAllMessagesFromChatId(chatId: Long)

    @Query("SELECT COUNT(DISTINCT userName) FROM message_info WHERE chatId = :chatId")
    fun getUserCount(chatId: Long): LiveData<Int>

    @Query("SELECT COUNT(*) FROM message_info WHERE chatId = :chatId")
    fun getMessageCount(chatId: Long): LiveData<Int>

    @Query("SELECT MIN(dateTime) AS dateTime FROM message_info WHERE chatId = :chatId")
    fun getStartDate(chatId: Long): Date

    @Query("SELECT MAX(dateTime) AS dateTime FROM message_info WHERE chatId = :chatId")
    fun getEndDate(chatId: Long): Date

}