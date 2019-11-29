package me.hyuck.kakaoanalyzer.ui.statistics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.ActivityStatisticsBinding
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)

        setViewPager()
    }

    fun setViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        //TODO: Add Fragment
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
}