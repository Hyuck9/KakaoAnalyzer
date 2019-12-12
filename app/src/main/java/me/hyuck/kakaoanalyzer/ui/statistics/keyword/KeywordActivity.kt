package me.hyuck.kakaoanalyzer.ui.statistics.keyword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import me.hyuck.kakaoanalyzer.db.entity.Chat
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
        val chat = intent.getSerializableExtra(StatisticsActivity.EXTRA_CHAT) as Chat
        binding.viewModel = viewModel
        subscribeUi(viewModel.getAllData(chat))

        binding.search.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                subscribeUi(viewModel.findData(chat, s.toString()))
            }

        })
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
