package me.hyuck.kakaoanalyzer.util

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun convertDateFormat(sDate: String): String {
        @SuppressLint("SimpleDateFormat")
        val dateParser: DateFormat = SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm")
        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        lateinit var date: Date
        try {
            date = dateParser.parse(sDate)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return format.format(date)
    }

    fun convertDateFormat(sDate: String, sFormat: String): String {
        @SuppressLint("SimpleDateFormat")
        val dateParser: DateFormat = SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm")
        @SuppressLint("SimpleDateFormat")
        val format = SimpleDateFormat(sFormat)
        lateinit var date: Date
        try {
            date = dateParser.parse(sDate)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return format.format(date)
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
}