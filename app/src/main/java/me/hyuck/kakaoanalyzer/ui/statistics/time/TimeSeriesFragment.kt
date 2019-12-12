package me.hyuck.kakaoanalyzer.ui.statistics.time


import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import me.hyuck.kakaoanalyzer.R
import me.hyuck.kakaoanalyzer.databinding.FragmentTimeseriesBinding
import me.hyuck.kakaoanalyzer.model.TimeInfo
import me.hyuck.kakaoanalyzer.ui.statistics.StatisticsActivity
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TimeSeriesFragment : Fragment() {

    private lateinit var viewModel: TimeViewModel
    private lateinit var binding: FragmentTimeseriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeseries, container, false)

        initChart()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(TimeViewModel::class.java)
        val chat = (Objects.requireNonNull(activity) as StatisticsActivity).chat
        viewModel.setData(chat)
        subscribeUi(viewModel.timeInfo)
    }

    private fun subscribeUi(liveData: LiveData<List<TimeInfo>>?) {
        liveData!!.observe(
            this,
            Observer<List<TimeInfo>> { timeInfos: List<TimeInfo>? ->
                timeInfos?.let { setData(it) }
                binding.executePendingBindings()
            }
        )
    }

    private fun initChart() {
        // // Chart Style // //
        // background color
        binding.timeSeriesChart.setBackgroundColor(Color.WHITE)
        // disable description text
        binding.timeSeriesChart.description.isEnabled = false
        // enable touch gestures
        binding.timeSeriesChart.setTouchEnabled(true)
        // set listeners
        binding.timeSeriesChart.setDrawGridBackground(false)
        // enable scaling and dragging
        binding.timeSeriesChart.isDragEnabled = true
        binding.timeSeriesChart.setScaleEnabled(true)
        // force pinch zoom along both axis
        binding.timeSeriesChart.setPinchZoom(true)


        // draw points over time
        binding.timeSeriesChart.animateX(1500)
        // get the legend (only possible after setting data)
        val l: Legend = binding.timeSeriesChart.legend
        // draw legend entries as lines
        l.form = Legend.LegendForm.LINE
    }


    private fun setData(timeInfos: List<TimeInfo>) {
        val values =
            ArrayList<Entry>()
        var value = 0
        var maximum = 0
        for (i in 0..23) {
            for (time in timeInfos) {
                if (time.hour == i) {
                    if (maximum < time.count) {
                        maximum = time.count
                    }
                    value = time.count
                    break
                }
                value = 0
            }
            values.add(Entry(i.toFloat(), value.toFloat()))
        }
        setMaxValue(maximum)
        val dataSet: LineDataSet
        if (
            binding.timeSeriesChart.data != null &&
            binding.timeSeriesChart.data.dataSetCount > 0
        ) {
            dataSet = binding.timeSeriesChart.data.getDataSetByIndex(0) as (LineDataSet)
            dataSet.values = values
            dataSet.notifyDataSetChanged()
            binding.timeSeriesChart.data.notifyDataChanged()
            binding.timeSeriesChart.notifyDataSetChanged()
        } else { // create a dataset and give it a type
            dataSet = LineDataSet(values, "채팅 시간대 분석")
            dataSet.setDrawIcons(false)
            // draw dashed line
            dataSet.enableDashedLine(10f, 5f, 0f)
            // black lines and points
            dataSet.color = Color.BLACK
            dataSet.setCircleColor(Color.BLACK)
            // line thickness and point size
            dataSet.lineWidth = 1f
            dataSet.circleRadius = 3f
            // draw points as solid circles
            dataSet.setDrawCircleHole(false)
            // customize legend entry
            dataSet.formLineWidth = 1f
            dataSet.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            dataSet.formSize = 15f
            // text size of values
            dataSet.valueTextSize = 9f
            // draw selection line as dashed
            dataSet.enableDashedHighlightLine(10f, 5f, 0f)
            // set the filled area
            dataSet.setDrawFilled(true)
            dataSet.fillFormatter = IFillFormatter { _, _ -> binding.timeSeriesChart.axisLeft.axisMinimum }
            val drawable= ContextCompat.getDrawable(requireContext(), R.drawable.fade_red)
            dataSet.fillDrawable = drawable
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(dataSet) // add the data sets
            // create a data object with the data sets
            val data = LineData(dataSets)
            // set data
            binding.timeSeriesChart.data = data
        }
    }

    private fun setMaxValue(m: Int) {
//        var maximum = m
        var yAxis: YAxis
        run {
            // // Y-Axis Style // //
            yAxis = binding.timeSeriesChart.axisLeft
            // disable dual axis (only use LEFT axis)
            binding.timeSeriesChart.axisRight.isEnabled = false
            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f)
            // axis range
//            val mod = maximum % 50
//            val addNum = 50 - mod
//            maximum += addNum
            yAxis.axisMaximum = m * 1.1f
            yAxis.axisMinimum = 0f
            yAxis.setDrawLimitLinesBehindData(true)
        }
    }
}
