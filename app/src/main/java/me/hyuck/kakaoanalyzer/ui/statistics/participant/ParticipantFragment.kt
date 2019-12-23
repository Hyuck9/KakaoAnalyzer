package me.hyuck.kakaoanalyzer.ui.statistics.participant


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.adapter.ParticipantAdapter
import me.hyuck.kakaoanalyzer.databinding.FragmentParticipantBinding
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity
import me.hyuck.kakaoanalyzer.ui.statistics.basic.BasicInfoViewModel
import me.hyuck.kakaoanalyzer.ui.statistics.common.PieChartFragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ParticipantFragment : PieChartFragment() {

    private lateinit var viewModel: ParticipantViewModel
    private lateinit var basicViewModel: BasicInfoViewModel
    private lateinit var binding: FragmentParticipantBinding
    private lateinit var adapter: ParticipantAdapter
    private lateinit var chat: Chat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_participant, container, false)
        binding.lifecycleOwner = this

        initChart(binding.participantPieChart)
        initRecyclerView()
        initButton()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ParticipantViewModel::class.java)
        basicViewModel = ViewModelProviders.of(requireActivity()).get(BasicInfoViewModel::class.java)
        binding.viewModel = basicViewModel
        chat = (Objects.requireNonNull(activity) as StatisticsActivity).chat
        subscribeUi()
    }

    private fun initRecyclerView() {
        adapter = ParticipantAdapter()
        binding.rvParticipantList.adapter = adapter
        binding.rvParticipantList.addItemDecoration(DividerItemDecoration(requireContext(), 1))
    }

    private fun initButton() {
        binding.btnMoreParticipant.setOnClickListener {
            val intent = Intent(requireContext(), ParticipantActivity::class.java)
            intent.putExtra(StatisticsActivity.EXTRA_CHAT, chat)
            startActivity(intent)
        }
    }

    private fun subscribeUi() {
        basicViewModel.chat.observe(this, Observer { chat ->
            viewModel.set10Data(chat).observe(this,  Observer { pInfoList ->
                setData(pInfoList)
                adapter.setParticipantList(pInfoList)
                binding.executePendingBindings()
            })
            basicViewModel.selectUserCount(chat).observe(this, Observer { userCount ->
                basicViewModel.userCount.value = userCount
                basicViewModel.oneToOneAnalytics.postValue( userCount == 2 )
                if ( userCount == 2 ) {
                    viewModel.setMessageData(chat).observe(this, Observer {
                        viewModel.setOneToOneAnalytics(it)
                        viewModel.test()
                    })
                }
                Log.d("TEST", "oneToOneAnalytics : ${basicViewModel.oneToOneAnalytics.value}")
            })
        })

    }

    private fun setData(participantInfos: List<ParticipantInfo>) {
        val entries = ArrayList<PieEntry>()
        //        TOP 10만 그래프로 표시하기 위해 아래 로직으로 변경
//        for (ParticipantInfo info : participantInfos) {
//            entries.add(new PieEntry(info.getCount(), info.getUserName()));
//        }
        for (i in participantInfos.indices) {
            if (i >= 10) break
            entries.add(
                PieEntry(
                    participantInfos[i].count.toFloat(),
                    participantInfos[i].userName
                )
            )
        }
        val dataSet = PieDataSet(entries, "사용자 별 채팅 비율")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.participantPieChart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        binding.participantPieChart.data = data
        binding.participantPieChart.highlightValues(null)
        binding.participantPieChart.invalidate()
    }

}
