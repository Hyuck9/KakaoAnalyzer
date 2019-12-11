package me.hyuck.kakaoanalyzer.ui.statistics.participant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.adapter.ParticipantAdapter
import me.hyuck.kakaoanalyzer.databinding.ActivityParticipantBinding
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity

class ParticipantActivity : AppCompatActivity() {

    private lateinit var viewModel: ParticipantViewModel
    private lateinit var binding: ActivityParticipantBinding
    private lateinit var adapter: ParticipantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_participant)
        binding.lifecycleOwner = this

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)

        initRecyclerView()

        viewModel = ViewModelProviders.of(this).get(ParticipantViewModel::class.java)
        val chatId = intent.getLongExtra(StatisticsActivity.EXTRA_CHAT_ID, 0)
        binding.viewModel = viewModel
        subscribeUi(viewModel.getAllData(chatId))

        binding.search.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                subscribeUi(viewModel.findData(chatId, s.toString()))
            }

        })
    }

    private fun initRecyclerView() {
        adapter = ParticipantAdapter()
        binding.rvParticipantList.adapter = adapter
        binding.rvParticipantList.addItemDecoration(DividerItemDecoration(this, 1))
    }

    private fun subscribeUi(liveData: LiveData<List<ParticipantInfo>>?) {
        liveData!!.observe(
            this,
            Observer<List<ParticipantInfo>> { participantInfoList: List<ParticipantInfo>? ->
                if (participantInfoList != null) {
                    adapter.setParticipantList(participantInfoList)
                }
                binding.executePendingBindings()
            }
        )
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
