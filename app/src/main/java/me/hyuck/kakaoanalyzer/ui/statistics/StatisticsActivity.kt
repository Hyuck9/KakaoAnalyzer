package me.hyuck.kakaoanalyzer.ui.statistics

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

class StatisticsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CHAT = "me.hyuck.kakaoanalyzer.ui.statistics.EXTRA_CHAT"
    }

    private var callback: ResponseCallback<KakaoLinkResponse>? = null

    private lateinit var basicViewModel: BasicInfoViewModel

    private lateinit var binding: ActivityStatisticsBinding
    lateinit var chat: Chat

    private var participantList: List<ParticipantInfo>? = null
    private var keywordList: List<KeywordInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics)

        basicViewModel = ViewModelProviders.of(this).get(BasicInfoViewModel::class.java)

        chat = intent.getSerializableExtra(EXTRA_CHAT) as Chat
        binding.toolbarTitle.text = chat.title
        basicViewModel.set5ParticipantData(chat)
        basicViewModel.set5KeywordData(chat)
        basicViewModel.setData(chat)
        subscribe(basicViewModel.participantInfo, basicViewModel.keywordInfo)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)

        setViewPager()

        callback = object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult) {
                Toast.makeText( applicationContext, errorResult.errorMessage, Toast.LENGTH_LONG ).show()
            }

            override fun onSuccess(result: KakaoLinkResponse?) {
                Toast.makeText( applicationContext, "Successfully sent KakaoLink v2 message.", Toast.LENGTH_LONG ).show()
            }
        }
    }
    private fun subscribe(liveData1: LiveData<List<ParticipantInfo>>?, liveData2: LiveData<List<KeywordInfo>>?) {
        liveData1!!.observe(
            this,
            Observer<List<ParticipantInfo>> { participantInfos: List<ParticipantInfo>? ->
                participantList = participantInfos
            }
        )
        liveData2!!.observe(
            this,
            Observer<List<KeywordInfo>> { keywordInfos: List<KeywordInfo>? ->
                keywordList = keywordInfos
            }
        )
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
            .newBuilder("${chat.title}\n${basicViewModel.period}\n\n<사용자>\n$participant\n<키워드>\n$keyword"
                , LinkObject.newBuilder().build()).setButtonTitle("앱 열기").build()
        KakaoLinkService.getInstance().sendDefault(this, params, null, callback)
    }

}