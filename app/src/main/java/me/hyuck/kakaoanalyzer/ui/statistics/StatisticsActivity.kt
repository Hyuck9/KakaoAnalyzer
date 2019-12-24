package me.hyuck.kakaoanalyzer.ui.statistics

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TextTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.ActivityStatisticsBinding
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.KeywordInfo
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.ui.statistics.basic.BasicInfoFragment
import me.hyuck.kakaoanalyzer.ui.statistics.basic.BasicInfoViewModel
import me.hyuck.kakaoanalyzer.ui.statistics.keyword.KeywordFragment
import me.hyuck.kakaoanalyzer.ui.statistics.participant.ParticipantFragment
import me.hyuck.kakaoanalyzer.ui.statistics.time.TimeSeriesFragment
import me.hyuck.kakaoanalyzer.util.DateUtils
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CHAT = "me.hyuck.kakaoanalyzer.ui.statistics.EXTRA_CHAT"
    }

    var callback: ResponseCallback<KakaoLinkResponse>? = null

    private lateinit var basicViewModel: BasicInfoViewModel

    private lateinit var binding: ActivityStatisticsBinding
    lateinit var chat: Chat

    private var participantList: List<ParticipantInfo>? = null
    private var keywordList: List<KeywordInfo>? = null

    private val calendar = GregorianCalendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics)
        binding.lifecycleOwner = this

        basicViewModel = ViewModelProviders.of(this).get(BasicInfoViewModel::class.java)

        chat = intent.getSerializableExtra(EXTRA_CHAT) as Chat
        binding.toolbarTitle.text = chat.title
        basicViewModel.chat.value = chat

        subscribeChat()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)

        setViewPager()
        initDateButton()

        callback = object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult) {
                Toast.makeText( applicationContext, errorResult.errorMessage, Toast.LENGTH_LONG ).show()
            }

            override fun onSuccess(result: KakaoLinkResponse?) {
                Toast.makeText( applicationContext, "카카오톡 공유 성공", Toast.LENGTH_LONG ).show()
            }
        }

        initAdView()
    }

    private fun subscribeChat() {
        basicViewModel.chat.observe(this, Observer {
            basicViewModel.set5ParticipantData(it).observe(this,  Observer {pList -> participantList = pList})
            basicViewModel.set5KeywordData(it).observe(this,  Observer {kList -> keywordList = kList})
        })
    }


    private fun initDateButton() {
        binding.startDate.text = DateUtils.convertDateToStringFormat(chat.startDate, "yyyy-MM-dd")
        binding.endDate.text = DateUtils.convertDateToStringFormat(chat.endDate, "yyyy-MM-dd")
        initDatePicker()
    }

    private fun initDatePicker() {
        calendar.time = chat.startDate
        val startDateDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val date = "${year}-${String.format("%02d",month+1)}-${String.format("%02d",dayOfMonth)}"
                binding.startDate.text = date
                chat.startDate = DateUtils.convertStringFormatToDate("$date 00:00:00", "yyyy-MM-dd HH:mm:ss")
                basicViewModel.chat.value = chat
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        calendar.time = chat.endDate
        val endDateDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val date = "${year}-${String.format("%02d",month+1)}-${String.format("%02d",dayOfMonth)}"
                binding.endDate.text = date
                chat.endDate = DateUtils.convertStringFormatToDate("$date 23:59:59", "yyyy-MM-dd HH:mm:ss")
                basicViewModel.chat.value = chat
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )

        startDateDialog.datePicker.minDate = chat.startDate.time
        startDateDialog.datePicker.maxDate = chat.endDate.time
        endDateDialog.datePicker.minDate = chat.startDate.time
        endDateDialog.datePicker.maxDate = chat.endDate.time

        binding.startDate.setOnClickListener {
            startDateDialog.show()
        }

        binding.endDate.setOnClickListener {
            endDateDialog.show()
        }
    }

    private fun initAdView() {
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        val adRequest = AdRequest.Builder().addTestDevice("F80CD049DDCCD3D36E5E1C4FD8E8F6A0").build()
        binding.adView.loadAd(adRequest)
    }

    private fun setViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(BasicInfoFragment(), "기본정보")
        adapter.addFragment(ParticipantFragment(), "사용자")
        adapter.addFragment(KeywordFragment(), "키워드")
        adapter.addFragment(TimeSeriesFragment(), "시간대")
        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun share() {
        var participant = ""
        var keyword = ""
        participantList?.forEachIndexed { index, pInfo ->
            participant += "${index+1}. ${pInfo.userName} ${pInfo.count}회\n"
        }
        keywordList?.forEachIndexed { index, kInfo ->
            keyword += "${index+1}. ${kInfo.keyword} ${kInfo.count}회\n"
        }

        val params = TextTemplate
            .newBuilder("${chat.title}\n${basicViewModel.period.value}\n\n<사용자>\n$participant\n<키워드>\n$keyword"
                , LinkObject.newBuilder().build()).setButtonTitle("앱 열기").build()
        KakaoLinkService.getInstance().sendDefault(this, params, null, callback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_share -> {
                share()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private class ViewPagerAdapter internal constructor(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragmentList = mutableListOf<Fragment>()
        private val fragmentTitleList = mutableListOf<String>()

        override fun getItem(position: Int): Fragment = fragmentList[position]

        override fun getPageTitle(position: Int): CharSequence? = fragmentTitleList[position]

        override fun getCount(): Int = fragmentList.size

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }
    }

}