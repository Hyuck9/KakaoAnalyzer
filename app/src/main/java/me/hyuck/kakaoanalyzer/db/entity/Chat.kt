package me.hyuck.kakaoanalyzer.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "chat_info")
data class Chat(var title: String, var date: String, var size: String, var filePath: String, var startDate: String): Serializable {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}