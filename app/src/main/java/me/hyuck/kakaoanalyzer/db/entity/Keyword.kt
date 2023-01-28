package me.hyuck.kakaoanalyzer.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "keyword_info",
    foreignKeys = [ForeignKey(
        entity = Chat::class,
        parentColumns = ["id"],
        childColumns = ["chatId"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["chatId"])]
)
data class Keyword(var chatId:Long,
                   var userName:String,
                   var keyword:String,
                   var dateTime:Date) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}