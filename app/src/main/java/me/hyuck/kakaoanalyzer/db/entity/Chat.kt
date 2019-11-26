package me.hyuck.kakaoanalyzer.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_info")
data class Chat(var title: String, var date: String, var size: String, var filePath: String) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}