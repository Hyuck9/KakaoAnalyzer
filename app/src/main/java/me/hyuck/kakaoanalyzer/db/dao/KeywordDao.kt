package me.hyuck.kakaoanalyzer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import me.hyuck.kakaoanalyzer.db.entity.Keyword
import me.hyuck.kakaoanalyzer.model.KeywordInfo
import java.util.*

@Dao
interface KeywordDao {
    @Insert
    fun insert(keywords: List<Keyword>)

    @Query("DELETE FROM keyword_info WHERE chatId = :chatId")
    fun deleteAllKeywordsFromChatId(chatId: Long)

    @Query("SELECT COUNT(DISTINCT keyword) FROM keyword_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate")
    fun getKeywordCount(chatId: Long, startDate: Date, endDate: Date): LiveData<Int>

    @Query("SELECT keyword, COUNT(*) AS count FROM keyword_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate GROUP BY keyword ORDER BY count DESC")
    fun getKeywordInfo(chatId: Long, startDate: Date, endDate: Date): LiveData<List<KeywordInfo>>

    @Query("SELECT keyword, COUNT(*) AS count FROM keyword_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate GROUP BY keyword ORDER BY count DESC LIMIT 10")
    fun getKeywordInfoLimit10(chatId: Long, startDate: Date, endDate: Date): LiveData<List<KeywordInfo>>

    @Query("SELECT keyword, COUNT(*) AS count FROM keyword_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate AND keyword LIKE :query GROUP BY keyword ORDER BY count DESC")
    fun findKeywordInfo(chatId: Long, startDate: Date, endDate: Date, query: String): LiveData<List<KeywordInfo>>

    @Query("SELECT COUNT(*) FROM keyword_info WHERE chatId = :chatId AND dateTime BETWEEN :startDate AND :endDate")
    fun getTotalCount(chatId: Long, startDate: Date, endDate: Date): LiveData<String>
}