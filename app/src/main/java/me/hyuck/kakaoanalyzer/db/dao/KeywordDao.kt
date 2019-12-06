package me.hyuck.kakaoanalyzer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import me.hyuck.kakaoanalyzer.db.entity.Keyword
import me.hyuck.kakaoanalyzer.model.KeywordInfo

@Dao
interface KeywordDao {
    @Insert
    fun insert(keywords: List<Keyword>)

    @Query("DELETE FROM keyword_info WHERE chatId = :chatId")
    fun deleteAllKeywordsFromChatId(chatId: Long)

    @Query("SELECT COUNT(DISTINCT keyword) FROM keyword_info WHERE chatId = :chatId")
    fun getKeywordCount(chatId: Long): LiveData<Int>

    @Query("SELECT keyword, COUNT(*) AS count FROM keyword_info WHERE chatId = :chatId GROUP BY keyword ORDER BY count DESC")
    fun getKeywordInfo(chatId: Long): LiveData<List<KeywordInfo>>
}