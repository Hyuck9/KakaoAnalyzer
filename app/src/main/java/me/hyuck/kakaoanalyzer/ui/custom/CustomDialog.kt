package me.hyuck.kakaoanalyzer.ui.custom

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.custom_dialog.*
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity
import me.hyuck.kakaoanalyzer.util.DateUtils
import java.util.*

class CustomDialog(context: Context, var chat: Chat) : AppCompatDialog(context) {

    private val calendar = GregorianCalendar.getInstance()

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        Log.d("TEST", "$chat")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        setContentView(R.layout.custom_dialog)

        title.text = chat.title
        startDate.text = DateUtils.convertDateToStringFormat(chat.startDate, "yyyy-MM-dd")
        endDate.text = DateUtils.convertDateToStringFormat(chat.endDate, "yyyy-MM-dd")
        initDatePicker()
        initButtonClickEvent()
    }

    private fun initDatePicker() {
        startDate.setOnClickListener {
            calendar.time = chat.startDate
            DatePickerDialog(context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val date = "${year}-${String.format("%02d",month+1)}-${String.format("%02d",dayOfMonth)}"
                    startDate.text = date
                    chat.startDate = DateUtils.convertStringFormatToDate(date, "yyyy-MM-dd")
                }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        endDate.setOnClickListener {
            calendar.time = chat.endDate
            DatePickerDialog(context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val date = "${year}-${String.format("%02d",month+1)}-${String.format("%02d",dayOfMonth)}"
                    endDate.text = date
                    chat.endDate = DateUtils.convertStringFormatToDate(date, "yyyy-MM-dd")
                }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun initButtonClickEvent() {
        btNegative.setOnClickListener {
            dismiss()
        }

        btPositive.setOnClickListener {
            dismiss()
                val intent = Intent(context, StatisticsActivity::class.java)
                intent.putExtra(StatisticsActivity.EXTRA_CHAT_ID, chat.id)
                intent.putExtra(StatisticsActivity.EXTRA_TITLE, chat.title)
                intent.putExtra(StatisticsActivity.EXTRA_CHAT, chat)
                context.startActivity(intent)
        }
    }

}