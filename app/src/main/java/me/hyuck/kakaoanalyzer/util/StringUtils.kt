package me.hyuck.kakaoanalyzer.util

import java.text.DecimalFormat
import java.util.regex.Pattern

object StringUtils {
    // "2019년 10월 24일 오전 10:44,"
    private const val FIRST_DATE_TIME_COMMA_PATTERN_STRING =
        "^([2-9][0-9][0-9][0-9]년)(\\s)([1-9]월|1[0-2]월)(\\s)([1-9]일|[1-3][0-9]일)(\\s)(오전|오후)(\\s)([1-9]:|1[0-2]:)([1-9],|[0-5][0-9],)+.*"

    // "2019년 10월 24일 오전 10:44"
    private const val FIRST_DATE_TIME_PATTERN_STRING =
        "^([2-9][0-9][0-9][0-9]년)(\\s)([1-9]월|1[0-2]월)(\\s)([1-9]일|[1-3][0-9]일)(\\s)(오전|오후)(\\s)([1-9]:|1[0-2]:)([1-9]|[0-5][0-9])+.*"
//    private static final String mp4 = "^(.){64}.mp4";

    //    private static final String mp4 = "^(.){64}.mp4";
    private const val VOICE_TALK_TIME = "^보이스톡\\s([0-9]|[1-9][0-9]):[0-5][0-9]"
    private const val VOICE_TALK_MESSAGE = "^보이스톡\\s.{2,3}"
    private const val NOT_READ_MESSAGE = "^<.{2,3}\\s읽지\\s않음>"
    private const val ATTACHED_FILE =
        "^파일:\\s(.*?doc|.*?docx|.*?hwp|.*?txt|.*?rtf|.*?xml|.*?pdf|.*?wks|.*?wps|.*?xps|.*?md|.*?odf|.*?odt|.*?ods|.*?odp|.*?csv|.*?tsv|.*?xls|.*?xlsx|.*?ppt|.*?pptx|.*?pages|.*?key|.*?numbers|.*?show|.*?ce|.*?zip|.*?gz|.*?bz2|.*?rar|.*?7z|.*?lzh|.*?alz)"
    private const val IMAGE_MOVIE_FILE =
        "^\\w{64}(.jpg|.jpeg|.gif|.bmp|.png|.tif|.tiff|.tga|.psd|.ai|.mp4|.m4v|.avi|.asf|.wmv|.mkv|.ts|.mpg|.mpeg|.mov|.flv|.ogv|.mp3|.wav|.flac|.tta|.tak|.aac|.wma|.ogg|.m4a)"

    private const val COME_IN_USER = ".+?님이\\s.+?님을\\s초대했습니다."
    private const val GET_OUT_USER = ".+?님이\\s나갔습니다."


    fun parseMemory(byteNumber: Long): String {
        var number = byteNumber
        return when {
            number < 1024 -> {
                number.toString() + "byte"
            }
            number < 1024 * 1024 -> {
                number /= 1024
                number.toString() + "KB"
            }
            number < 1024 * 1024 * 1024 -> {
                number /= 1024 * 1024.toLong()
                number.toString() + "MB"
            }
            else -> {
                number /= 1024 * 1024 * 1024.toLong()
                number.toString() + "GB"
            }
        }
    }

    fun isFirstDateTimeMessage(str: String): Boolean {
        return Pattern.matches(FIRST_DATE_TIME_COMMA_PATTERN_STRING, str)
    }

    fun isDateTimeMessage(str: String): Boolean {
        return Pattern.matches(FIRST_DATE_TIME_PATTERN_STRING, str)
    }

    fun getFormattedNumber(number: Int): String? {
        val myFormatter = DecimalFormat("###,###")
        return myFormatter.format(number.toLong())
    }

    fun isNotDateTimeMessage(str: String): Boolean {
        return !isDateTimeMessage(str)
    }

    fun isPassedInOutMessage(str: String): Boolean {
        return Pattern.matches(COME_IN_USER, str) ||
                Pattern.matches(GET_OUT_USER, str)
    }

    fun isPassedMessage(str: String): Boolean {
        return Pattern.matches(VOICE_TALK_TIME, str) ||
                Pattern.matches(VOICE_TALK_MESSAGE, str) ||
                Pattern.matches(NOT_READ_MESSAGE, str) ||
                Pattern.matches(ATTACHED_FILE, str) ||
                Pattern.matches(IMAGE_MOVIE_FILE, str)
    }

    fun isPassedKeyword(str: String): Boolean {
        return str == ":" || str == "." || str == "," || str == "\"" || str == "-" || str.trim { it <= ' ' }.isEmpty() || str == "\'" || str == "[" || str == "]" || str.length > 20
    }
}