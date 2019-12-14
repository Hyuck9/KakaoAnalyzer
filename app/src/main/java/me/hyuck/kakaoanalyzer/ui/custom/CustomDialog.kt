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
        calendar.time = chat.startDate
        val startDateDialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val date = "${year}-${String.format("%02d",month+1)}-${String.format("%02d",dayOfMonth)}"
                startDate.text = date
                chat.startDate = DateUtils.convertStringFormatToDate("$date 00:00:00", "yyyy-MM-dd HH:mm:ss")
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
            calendar.time = chat.endDate
        val endDateDialog = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val date = "${year}-${String.format("%02d",month+1)}-${String.format("%02d",dayOfMonth)}"
                endDate.text = date
                chat.endDate = DateUtils.convertStringFormatToDate("$date 23:59:59", "yyyy-MM-dd HH:mm:ss")
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )

        startDateDialog.datePicker.minDate = chat.startDate.time
        startDateDialog.datePicker.maxDate = chat.endDate.time
        endDateDialog.datePicker.minDate = chat.startDate.time
        endDateDialog.datePicker.maxDate = chat.endDate.time

        startDate.setOnClickListener {
            startDateDialog.datePicker.maxDate
            startDateDialog.show()
        }

        endDate.setOnClickListener {
            endDateDialog.show()
        }
    }

    private fun initButtonClickEvent() {
        btNegative.setOnClickListener {
            dismiss()
        }

        btPositive.setOnClickListener {
            dismiss()
                val intent = Intent(context, StatisticsActivity::class.java)
                intent.putExtra(StatisticsActivity.EXTRA_CHAT, chat)
                context.startActivity(intent)
        }
    }

}