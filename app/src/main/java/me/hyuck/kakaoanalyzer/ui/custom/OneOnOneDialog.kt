package me.hyuck.kakaoanalyzer.ui.custom

import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TextTemplate
import com.kakao.network.callback.ResponseCallback
import kotlinx.android.synthetic.main.one_on_one_analytics_dialog.*
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.model.OneOnOneAnalyticsInfo

class OneOnOneDialog(context: Context, private var oneOnOneAnalyticsInfo: OneOnOneAnalyticsInfo, private var shareTitle: String, private var callback: ResponseCallback<KakaoLinkResponse>?) : AppCompatDialog(context) {

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
        someone_count.text = context.getString(R.string.message_count, oneOnOneAnalyticsInfo.someoneUserMessage)
        user_count.text = context.getString(R.string.message_count, oneOnOneAnalyticsInfo.userMessage)
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
        button_share.setOnClickListener {
            share()
        }
    }

    private fun share() {
        val messageCount = "회원님: ${oneOnOneAnalyticsInfo.userMessage}\n${oneOnOneAnalyticsInfo.title}: ${oneOnOneAnalyticsInfo.someoneUserMessage}"
        val firstCount = "회원님: ${oneOnOneAnalyticsInfo.userFirstCount}\n${oneOnOneAnalyticsInfo.title}: ${oneOnOneAnalyticsInfo.someoneUserFirstCount}"
        val firstReply = "회원님: ${oneOnOneAnalyticsInfo.userFirstReply}\n${oneOnOneAnalyticsInfo.title}: ${oneOnOneAnalyticsInfo.someoneUserFirstReply}"
        val averageReply = "회원님: ${oneOnOneAnalyticsInfo.userAverageReply}\n${oneOnOneAnalyticsInfo.title}: ${oneOnOneAnalyticsInfo.someoneUserAverageReply}"

        val params = TextTemplate
            .newBuilder("${shareTitle}\n1:1 대화 분석\n\n<보낸 메시지수>\n$messageCount\n\n<선톡횟수>\n$firstCount\n\n<선톡답장시간>\n$firstReply\n\n<평균 답장시간>\n$averageReply"
                , LinkObject.newBuilder().build()).setButtonTitle("앱 열기").build()
        KakaoLinkService.getInstance().sendDefault(context, params, null, callback)
    }

}