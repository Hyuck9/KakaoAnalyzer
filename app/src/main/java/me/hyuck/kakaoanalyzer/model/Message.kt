package me.hyuck.kakaoanalyzer.model

import java.util.*

data class Message(/*var id:Long, var chatId:Long, */var dateTime: Date, var userName: String, var msgContent:String, var hour: Int)