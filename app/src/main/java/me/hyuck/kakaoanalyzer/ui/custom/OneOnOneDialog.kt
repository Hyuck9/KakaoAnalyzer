package me.hyuck.kakaoanalyzer.ui.custom

import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.one_on_one_analytics_dialog.*
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.model.OneOnOneAnalyticsInfo

class OneOnOneDialog(context: Context, private var oneOnOneAnalyticsInfo: OneOnOneAnalyticsInfo) : AppCompatDialog(context) {

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        setContentView(R.layout.one_on_one_analytics_dialog)

        initText()
        initButtonClickEvent()
    }

    private fun initText() {
        someone_user.text = oneOnOneAnalyticsInfo.title
        someone_user_first_count.text = oneOnOneAnalyticsInfo.someoneUserFirstCount
        user_first_count.text = oneOnOneAnalyticsInfo.userFirstCount
        someone_user_first_reply.text = oneOnOneAnalyticsInfo.someoneUserFirstReply
        user_first_reply.text = oneOnOneAnalyticsInfo.userFirstReply
        someone_user_average_reply.text = oneOnOneAnalyticsInfo.someoneUserAverageReply
        user_average_reply.text = oneOnOneAnalyticsInfo.userAverageReply
        all_first_reply.text = oneOnOneAnalyticsInfo.allFirstReply
        all_average_reply.text = oneOnOneAnalyticsInfo.allAverageReply
    }

    private fun initButtonClickEvent() {
        btOk.setOnClickListener {
            dismiss()
        }
    }

}