package me.hyuck.kakaoanalyzer.ui.statistics

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.ActivityStatisticsBinding
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.ui.statistics.basic.BasicInfoFragment
import me.hyuck.kakaoanalyzer.ui.statistics.basic.BasicInfoViewModel

class StatisticsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CHAT_ID = "me.hyuck.kakaoanalyzer.ui.statistics.EXTRA_CHAT_ID"
        const val EXTRA_CHAT = "me.hyuck.kakaoanalyzer.ui.statistics.EXTRA_CHAT"
        const val EXTRA_TITLE = "me.hyuck.kakaoanalyzer.ui.statistics.EXTRA_TITLE"
    }

    private lateinit var basicViewModel: BasicInfoViewModel

    private lateinit var binding: ActivityStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics)

        basicViewModel = ViewModelProviders.of(this).get(BasicInfoViewModel::class.java)

        binding.toolbarTitle.text = intent.getStringExtra(EXTRA_TITLE)

        val chatId = intent.getLongExtra(EXTRA_CHAT_ID, 0)
        basicViewModel.setData(chatId)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)

        setViewPager()
    }

    private fun setViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(BasicInfoFragment(), "기본정보")
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}