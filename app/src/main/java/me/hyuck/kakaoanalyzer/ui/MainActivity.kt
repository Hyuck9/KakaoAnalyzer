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
import me.hyuck.kakaoanalyzer.ui.custom.CustomDialog
import me.hyuck.kakaoanalyzer.ui.guide.GuideActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)

        binding.viewModel = viewModel

        binding.btnGoKakao.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage("com.kakao.talk")
            startActivity(intent)
        }
        binding.btnHelp.setOnClickListener {
            val intent = Intent(this@MainActivity, GuideActivity::class.java)
            startActivity(intent)
        }

        initRecyclerView()
        subscribeUi(viewModel.getAllChats())
    }

    private fun initRecyclerView() {
        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.setHasFixedSize(true)
        chatAdapter = ChatListAdapter(object: ChatListAdapter.OnItemClickListener {
            override fun OnItemClick(chat: Chat?) {
                chat?.run {
                    val cloneChat = copy(title=title, date = date, size = size, filePath = filePath, startDate = startDate, endDate = endDate)
                    cloneChat.id = id
                    CustomDialog(this@MainActivity, cloneChat).show()
                }
            }
        })
        binding.rvChatList.adapter = chatAdapter
    }

    private fun subscribeUi(liveData: LiveData<List<Chat>>) {
        liveData.observe(
            this,
            Observer<List<Chat>> { chatEntities: List<Chat>? ->
                if (chatEntities != null) {
                    chatAdapter.setChatList(chatEntities)
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
