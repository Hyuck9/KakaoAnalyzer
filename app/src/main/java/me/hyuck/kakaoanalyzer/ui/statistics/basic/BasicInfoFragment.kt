package me.hyuck.kakaoanalyzer.ui.statistics.basic


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.FragmentBasicInfoBinding
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity
import java.util.*

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
        val chat = (Objects.requireNonNull(activity) as StatisticsActivity).chat
        viewModel.setData(chat)
        binding.viewModel = viewModel
    }


}
