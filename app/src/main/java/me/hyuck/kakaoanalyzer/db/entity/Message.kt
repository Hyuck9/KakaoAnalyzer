package me.hyuck.kakaoanalyzer.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "message_info",
    foreignKeys = [ForeignKey(
        entity = Chat::class,
        parentColumns = ["id"],
        childColumns = ["chatId"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["chatId"])]
)
data class Message(var chatId:Long,
                   var dateTime: Date,
                   var userName: String,
                   var msgContent:String,
                   var hour: Int) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}