package me.hyuck.kakaoanalyzer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import me.hyuck.kakaoanalyzer.db.entity.Message
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.model.TimeInfo
import java.util.*

@Dao
interface MessageDao {
    @Insert
    fun insert(messages: List<Message>)

    @Query("DELETE FROM message_info WHERE chatId = :chatId")
    fun deleteAllMessagesFromChatId(chatId: Long)

    @Query("SELECT COUNT(DISTINCT userName) FROM message_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate")
    fun getUserCount(chatId: Long, startDate: Date, endDate: Date): LiveData<Int>

    @Query("SELECT COUNT(*) FROM message_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate")
    fun getMessageCount(chatId: Long, startDate: Date, endDate: Date): LiveData<Int>

    @Query("SELECT userName, COUNT(*) AS count FROM message_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate GROUP BY userName ORDER BY count DESC")
    fun getParticipantInfo(chatId: Long, startDate: Date, endDate: Date): LiveData<List<ParticipantInfo>>

    @Query("SELECT userName, COUNT(*) AS count FROM message_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate GROUP BY userName ORDER BY count DESC LIMIT :limit")
    fun getParticipantInfoLimit(chatId: Long, startDate: Date, endDate: Date, limit: Int): LiveData<List<ParticipantInfo>>

    @Query("SELECT hour, COUNT(*) AS count FROM message_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate GROUP BY hour")
    fun getTimeInfo(chatId: Long, startDate: Date, endDate: Date): LiveData<List<TimeInfo>>

    @Query("SELECT userName, COUNT(*) AS count FROM message_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate AND userName LIKE :query GROUP BY userName ORDER BY count DESC")
    fun findParticipantInfo(chatId: Long, startDate: Date, endDate: Date, query: String): LiveData<List<ParticipantInfo>>

    @Query("SELECT COUNT(*) FROM message_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate")
    fun getTotalCount(chatId: Long, startDate: Date, endDate: Date): LiveData<String>

    @Query("SELECT userName FROM message_info WHERE chatId = :chatId GROUP BY userName ORDER BY userName")
    fun getUserNames(chatId: Long): LiveData<List<String>>
}