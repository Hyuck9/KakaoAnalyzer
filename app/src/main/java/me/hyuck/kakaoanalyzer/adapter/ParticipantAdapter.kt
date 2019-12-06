package me.hyuck.kakaoanalyzer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.ParticipantListItemBinding
import me.hyuck.kakaoanalyzer.model.ParticipantInfo

class ParticipantAdapter : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    private var mParticipantList: List<ParticipantInfo>? = null

    /**
     * ParticipantList data setting
     */
    fun setParticipantList(ParticipantList: List<ParticipantInfo>) {
        if (mParticipantList == null) {
            mParticipantList = ParticipantList
            notifyItemRangeInserted(0, ParticipantList.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mParticipantList!!.size
                }

                override fun getNewListSize(): Int {
                    return ParticipantList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mParticipantList!![oldItemPosition].userName == ParticipantList[newItemPosition].userName
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val newItem = ParticipantList[newItemPosition]
                    val oldItem = mParticipantList!![oldItemPosition]
                    return (newItem.count == oldItem.count && newItem.userName == oldItem.userName)
                }

            })
            mParticipantList = ParticipantList
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val binding: ParticipantListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.participant_list_item, parent, false)
        return ParticipantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.binding.participant = mParticipantList!![position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (mParticipantList == null) 0 else mParticipantList!!.size
    }


    class ParticipantViewHolder(val binding: ParticipantListItemBinding) : RecyclerView.ViewHolder(binding.root)
}