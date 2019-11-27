package me.hyuck.kakaoanalyzer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.ChatListItemBinding
import me.hyuck.kakaoanalyzer.db.entity.Chat

class ChatListAdapter : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {
    private var mChatList: List<Chat?>? = null
    private lateinit var clickCallback: OnItemClickListener

    /**
     * chatList에 add/remove 를 할지 set 을 할지 고민중...
     */
    fun setChatList(chatList: List<Chat?>) {
        if (mChatList == null) {
            mChatList = chatList
            notifyItemRangeInserted(0, chatList.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mChatList!!.size
                }

                override fun getNewListSize(): Int {
                    return chatList.size
                }

                override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return mChatList!![oldItemPosition]!!.id == chatList[newItemPosition]!!.id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val newChat: Chat? = chatList[newItemPosition]
                    val oldChat: Chat? = mChatList!![oldItemPosition]
                    return (newChat!!.id == oldChat!!.id
                            && newChat.title == oldChat.title
                            && newChat.date == oldChat.date
                            && newChat.size == oldChat.size)
                }
            })
            mChatList = chatList
            result.dispatchUpdatesTo(this)
        }
    }

    fun getChatAt(position: Int): Chat? {
        return mChatList!![position]
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val binding: ChatListItemBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context), R.layout.chat_list_item,
                parent, false
            )
        binding.callback = clickCallback
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {
        holder.binding.chat = mChatList!![position]
        holder.binding.executePendingBindings()
        holder.itemView.setOnLongClickListener {
            Log.d("TEST", "ITEM LONGCLICK!!!!")
            false
        }
    }

    override fun getItemCount(): Int {
        return if (mChatList == null) 0 else mChatList!!.size
    }

    override fun getItemId(position: Int): Long {
        return mChatList!![position]!!.id
    }

    /**
     * Item Click Event Callback Listener
     */
    interface OnItemClickListener {
        fun OnItemClick(chat: Chat?)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        clickCallback = listener
    }

    class ChatViewHolder(val binding: ChatListItemBinding) : RecyclerView.ViewHolder(binding.root)

}