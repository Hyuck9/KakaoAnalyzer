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
        val today = Calendar.getInstance()
        val dDay = Calendar.getInstance()
        dDay.time = date

        val nToDay = today.timeInMillis / (24 * 60 * 60 * 1000)
        val nDDay = dDay.timeInMillis / (24 * 60 * 60 * 1000)
        val substract = nToDay - nDDay
        return StringUtils.getFormattedNumber(substract.toInt())
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