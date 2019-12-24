package me.hyuck.kakaoanalyzer.model

data class OneOnOneAnalyticsInfo(var title: String) {
    var someoneUserFirstCount = ""
    var userFirstCount = ""
    var someoneUserFirstReply = ""
    var userFirstReply = ""
    var someoneUserAverageReply = ""
    var userAverageReply = ""
    var allFirstReply = ""
    var allAverageReply = ""
    var userMessage = ""
    var someoneUserMessage = ""
}