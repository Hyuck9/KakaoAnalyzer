package me.hyuck.kakaoanalyzer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.adapter.ChatListAdapter
import me.hyuck.kakaoanalyzer.databinding.ActivityMainBinding
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.ui.custom.CustomDialog
import me.hyuck.kakaoanalyzer.ui.guide.GuideActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatListAdapter
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)

        MobileAds.initialize(this, getString(R.string.admob_app_id))
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.front_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

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

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                chatAdapter.getChatAt(viewHolder.adapterPosition)?.let {
                    if (deleteChatFile(it)) {
                        viewModel.deleteChat(it)
                        Toast.makeText(this@MainActivity, "삭제 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).attachToRecyclerView(binding.rvChatList)
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

    private fun deleteChatFile(chat: Chat): Boolean {
        val chatFileDir: File = File(chat.filePath).parentFile ?: return true
        return if (chatFileDir.exists()) {
            chatFileDir.listFiles()?.let { files -> files.forEach { it.delete() } }
            chatFileDir.delete()
        } else {
            true
        }
    }

    override fun onResume() {
        super.onResume()
        if ( viewModel.isChatExsist() ) {
            viewModel.parseChatInfo()
        }
    }

    override fun onDestroy() {
        if ( mInterstitialAd.isLoaded ) mInterstitialAd.show()
        super.onDestroy()
    }

}
