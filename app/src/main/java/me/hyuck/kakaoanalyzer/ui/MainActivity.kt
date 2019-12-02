package me.hyuck.kakaoanalyzer.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.adapter.ChatListAdapter
import me.hyuck.kakaoanalyzer.databinding.ActivityMainBinding
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity
import me.hyuck.kakaoanalyzer.viewmodel.ChatViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)

        initRecyclerView()
        subscribeUi(viewModel.getAllChats())
    }

    private fun initRecyclerView() {
        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.setHasFixedSize(true)
        chatAdapter = ChatListAdapter()
        chatAdapter.setItemClickListener(object: ChatListAdapter.OnItemClickListener{
            override fun OnItemClick(chat: Chat?) {
                val intent = Intent(this@MainActivity, StatisticsActivity::class.java)
                intent.putExtra(StatisticsActivity.EXTRA_CHAT_ID, chat!!.id)
                intent.putExtra(StatisticsActivity.EXTRA_TITLE, chat.title)
                intent.putExtra(StatisticsActivity.EXTRA_CHAT, chat)
                startActivity(intent)
            }
        })
        binding.rvChatList.adapter = chatAdapter
    }

    private fun subscribeUi(liveData: LiveData<List<Chat>>) {
        liveData.observe(
            this,
            Observer<List<Chat>> { chatEntities: List<Chat>? ->
                if (chatEntities != null) {
                    binding.isLoading = false
                    chatAdapter.setChatList(chatEntities)
                } else {
                    binding.isLoading = true
                }
                binding.executePendingBindings()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        if ( viewModel.isChatExsist() ) {
            viewModel.parseChatInfo()
        }
    }
}
