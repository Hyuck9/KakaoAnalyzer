package me.hyuck.kakaoanalyzer.ui.statistics.keyword

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.adapter.KeywordAdapter
import me.hyuck.kakaoanalyzer.databinding.ActivityKeywordBinding
import me.hyuck.kakaoanalyzer.model.KeywordInfo
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity

class KeywordActivity : AppCompatActivity() {

    private lateinit var viewModel: KeywordViewModel
    private lateinit var binding: ActivityKeywordBinding
    private lateinit var adapter: KeywordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keyword)
        binding.lifecycleOwner = this

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)

        initRecyclerView()

        viewModel = ViewModelProviders.of(this).get(KeywordViewModel::class.java)
        val chatId = intent.getLongExtra(StatisticsActivity.EXTRA_CHAT_ID, 0)
        binding.viewModel = viewModel
        subscribeUi(viewModel.getAllData(chatId))
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

    private fun initRecyclerView() {
        adapter = KeywordAdapter()
        binding.rvKeywordList.adapter = adapter
        binding.rvKeywordList.addItemDecoration(DividerItemDecoration(this, 1))
    }

    private fun subscribeUi(liveData: LiveData<List<KeywordInfo>>?) {
        liveData!!.observe(
            this,
            Observer<List<KeywordInfo>> { keywordInfoList: List<KeywordInfo>? ->
                if (keywordInfoList != null) {
                    adapter.setKeywordList(keywordInfoList)
                }
                binding.executePendingBindings()
            }
        )
    }
}
