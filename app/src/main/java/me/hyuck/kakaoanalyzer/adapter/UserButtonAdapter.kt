package me.hyuck.kakaoanalyzer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.UserButtonListItemBinding

class UserButtonAdapter : RecyclerView.Adapter<UserButtonAdapter.UserButtonViewHolder>() {

    private var mUserList: MutableList<String>? = null
    private var mSelectedItem = 0

    var selectedUserName = MutableLiveData<String>()

    /**
     * ParticipantList data setting
     */
    fun setUserList(userList: List<String>) {
        mUserList = mutableListOf("전체")
        userList.forEach {
            mUserList!!.add(it)
        }
        selectedUserName.value = "전체"
        notifyItemRangeInserted(0, mUserList!!.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserButtonViewHolder {
        val binding: UserButtonListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_button_list_item, parent, false)
        return UserButtonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserButtonViewHolder, position: Int) {
        holder.binding.userName = mUserList!![position]
        holder.binding.userButton.isChecked = position == mSelectedItem
        holder.binding.userButton.setOnClickListener {
            mSelectedItem = holder.adapterPosition
            mUserList?.let {
                selectedUserName.value = it[mSelectedItem]
            }
            notifyDataSetChanged()
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (mUserList == null) 0 else mUserList!!.size
    }


    class UserButtonViewHolder(val binding: UserButtonListItemBinding) : RecyclerView.ViewHolder(binding.root)
}