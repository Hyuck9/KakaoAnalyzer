package me.hyuck.kakaoanalyzer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.KeywordListItemBinding
import me.hyuck.kakaoanalyzer.model.KeywordInfo

class KeywordAdapter : RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder>() {

    private var mKeywordList: List<KeywordInfo>? = null

    /**
     * keywordList data setting
     */
    fun setKeywordList(keywordList: List<KeywordInfo>) {
        if (mKeywordList == null) {
            mKeywordList = keywordList
            notifyItemRangeInserted(0, keywordList.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mKeywordList!!.size
                }

                override fun getNewListSize(): Int {
                    return keywordList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mKeywordList!![oldItemPosition].keyword == keywordList[newItemPosition].keyword
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val newItem = keywordList[newItemPosition]
                    val oldItem = mKeywordList!![oldItemPosition]
                    return (newItem.count == oldItem.count && newItem.keyword == oldItem.keyword)
                }

            })
            mKeywordList = keywordList
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder {
        val binding: KeywordListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.keyword_list_item, parent, false)
        return KeywordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
        holder.binding.keyword = mKeywordList!![position]
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (mKeywordList == null) 0 else mKeywordList!!.size
    }


    class KeywordViewHolder(val binding: KeywordListItemBinding) : RecyclerView.ViewHolder(binding.root)
}