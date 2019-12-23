package me.hyuck.kakaoanalyzer.util

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

//    fun convertDateFormat(sDate: String): String {
//        @SuppressLint("SimpleDateFormat")
//        val dateParser: DateFormat = SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm")
//        @SuppressLint("SimpleDateFormat")
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
//        lateinit var date: Date
//        try {
//            date = dateParser.parse(sDate)!!
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        return format.format(date)
//    }
//
//    fun convertDateFormat(sDate: String, sFormat: String): String {
//        @SuppressLint("SimpleDateFormat")
//        val dateParser: DateFormat = SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm")
//        @SuppressLint("SimpleDateFormat")
//        val format = SimpleDateFormat(sFormat)
//        lateinit var date: Date
//        try {
//            date = dateParser.parse(sDate)!!
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        return format.format(date)
//    }

    fun convertDateToStringFormat(date: Date, sFormat: String): String {
        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat(sFormat)
        return format.format(date)
    }

    @JvmStatic
    fun convertDateToStringFormat(date: Date): String {
        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return format.format(date)
    }

    @JvmStatic
    fun calcMinusDay(date: Date): String {
        val subtract = calcTime(date, Date()) / 24 / 60 / 60
        return StringUtils.getFormattedNumber(subtract.toInt())
    }

    fun isDiff1Hour(preDate: Date, postDate: Date): Boolean {
        val subtract = calcTime(preDate, postDate) / 60
        return subtract > 60
    }

    fun calcReplyTime(subtract: Double): String {
        val minute = ( subtract / 60 ).toLong()
        val second = (subtract % 60 ).toLong()
        return "${minute}분 ${second}초"
    }

    fun calcTime(preDate: Date, postDate: Date): Long {
        val preTime = Calendar.getInstance()
        val postTime = Calendar.getInstance()
        preTime.time = preDate
        postTime.time = postDate

        val preHour = preTime.timeInMillis / 1000
        val postHour = postTime.timeInMillis / 1000
        return postHour - preHour
    }

    fun convertStringToDate(sDate: String): Date {
        @SuppressLint("SimpleDateFormat")
        val dateParser: DateFormat = SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm")
        lateinit var date: Date
        try {
            date = dateParser.parse(sDate)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun convertStringFormatToDate(sDate: String, sFormat: String): Date {
        @SuppressLint("SimpleDateFormat")
        val dateParser: DateFormat = SimpleDateFormat(sFormat)
        lateinit var date: Date
        try {
            date = dateParser.parse(sDate)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }
}