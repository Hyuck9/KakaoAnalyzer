package me.hyuck.kakaoanalyzer.ui.statistics.keyword


import android.content.Intent
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
import me.hyuck.kakaoanalyzer.adapter.KeywordAdapter
import me.hyuck.kakaoanalyzer.databinding.FragmentKeywordBinding
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.model.KeywordInfo
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity
import me.hyuck.kakaoanalyzer.ui.statistics.common.PeiChartFragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class KeywordFragment : PeiChartFragment() {

    private lateinit var viewModel: KeywordViewModel
    private lateinit var binding: FragmentKeywordBinding
    private lateinit var adapter: KeywordAdapter
    private lateinit var chat: Chat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_keyword, container, false)

        initChart(binding.keywordPieChart)
        initRecyclerView()
        initButton()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(KeywordViewModel::class.java)
        chat = (Objects.requireNonNull(activity) as StatisticsActivity).chat
        viewModel.set10Data(chat)
        subscribeUi(viewModel.keywordInfo)
    }

    private fun initRecyclerView() {
        adapter = KeywordAdapter()
        binding.rvKeywordList.adapter = adapter
        binding.rvKeywordList.addItemDecoration(DividerItemDecoration(requireContext(), 1))
    }

    private fun initButton() {
        binding.btnMoreKeyword.setOnClickListener {
            val intent = Intent(requireContext(), KeywordActivity::class.java)
            intent.putExtra(StatisticsActivity.EXTRA_CHAT, chat)
            startActivity(intent)
        }
    }

    private fun subscribeUi(liveData: LiveData<List<KeywordInfo>>?) {
        liveData!!.observe(
            this,
            Observer<List<KeywordInfo>> { keywordInfos: List<KeywordInfo>? ->
                if (keywordInfos != null) {
                    setData(keywordInfos)
                    adapter.setKeywordList(keywordInfos)
                }
                binding.executePendingBindings()
            }
        )
    }

    private fun setData(keywordInfos: List<KeywordInfo>) {
        val entries = ArrayList<PieEntry>()

        for (i in keywordInfos.indices) {
            if (i >= 10) break
            entries.add(PieEntry(keywordInfos[i].count.toFloat(), keywordInfos[i].keyword))
        }

        val dataSet = PieDataSet(entries, "키워드 사용 빈도수")

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
        data.setValueFormatter(PercentFormatter(binding.keywordPieChart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        binding.keywordPieChart.data = data

        binding.keywordPieChart.highlightValues(null)
        binding.keywordPieChart.invalidate()
    }

}
