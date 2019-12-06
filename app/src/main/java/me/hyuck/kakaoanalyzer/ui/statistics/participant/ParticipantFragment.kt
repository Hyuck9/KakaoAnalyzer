package me.hyuck.kakaoanalyzer.ui.statistics.participant


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
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
import me.hyuck.kakaoanalyzer.model.ParticipantInfo
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity
import me.hyuck.kakaoanalyzer.ui.statistics.common.PeiChartFragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ParticipantFragment : PeiChartFragment() {

    private lateinit var viewModel: ParticipantViewModel
    private lateinit var binding: FragmentParticipantBinding
    private lateinit var adapter: ParticipantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_participant, container, false)

        initChart(binding.participantPieChart)
        initRecyclerView()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ParticipantViewModel::class.java)
        val chatId: Long = (Objects.requireNonNull(activity) as StatisticsActivity).chatId
        viewModel.setData(chatId)
        subscribeUi(viewModel.participantInfo)
    }

    private fun initRecyclerView() {
        binding.rvParticipantList.setHasFixedSize(true)

        adapter = ParticipantAdapter()
        binding.rvParticipantList.adapter = adapter
        binding.rvParticipantList.addItemDecoration(DividerItemDecoration(requireContext(), 1))
    }

    private fun subscribeUi(liveData: LiveData<List<ParticipantInfo>>?) {
        liveData!!.observe(
            this,
            Observer<List<ParticipantInfo>> { participantInfos: List<ParticipantInfo>? ->
                if (participantInfos != null) {
                    setData(participantInfos)
                    adapter.setParticipantList(participantInfos)
                }
                binding.executePendingBindings()
            }
        )
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
