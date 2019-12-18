package me.hyuck.kakaoanalyzer.ui.statistics.basic


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.FragmentBasicInfoBinding
import me.hyuck.kakaoanalyzer.db.entity.Chat

/**
 * A simple [Fragment] subclass.
 */
class BasicInfoFragment : Fragment() {

    private lateinit var viewModel: BasicInfoViewModel
    private lateinit var binding: FragmentBasicInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_basic_info, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(BasicInfoViewModel::class.java)

        subscribeChat( viewModel.chat )

        binding.viewModel = viewModel
    }

    private fun subscribeChat(chatData: LiveData<Chat>) {
        chatData.observe(this, Observer {

            viewModel.setPeriod(it)
            viewModel.selectUserCount(it).observe(this, Observer { userCount ->
                viewModel.userCount.value = userCount
            })
            viewModel.selectMessageCount(it).observe(this, Observer { messageCount ->
                viewModel.messageCount.value = messageCount
            })
            viewModel.selectKeywordCount(it).observe(this, Observer { keywordCount ->
                viewModel.keywordCount.value = keywordCount
            })

        })
    }

}
