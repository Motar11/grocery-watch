package com.example.grocerywatch.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.grocerywatch.data.entity.PriceHistoryEntry
import com.example.grocerywatch.util.TrendCalculator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PriceChart(history: List<PriceHistoryEntry>, trend: TrendCalculator.TrendLine?) {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = {
            LineChart(context).apply {
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val sorted = history.sortedBy { it.date }
            val entries = sorted.mapIndexed { index, item -> Entry(index.toFloat(), item.price.toFloat()) }
            val priceDataSet = LineDataSet(entries, "Price").apply {
                color = Color.BLUE
                setCircleColor(Color.BLUE)
                valueTextColor = Color.DKGRAY
            }

            val dataSets = mutableListOf(priceDataSet)

            trend?.let {
                val trendEntries = listOf(
                    Entry(0f, (it.intercept).toFloat()),
                    Entry(entries.size.toFloat(), it.predictedNextPrice.toFloat())
                )
                val trendSet = LineDataSet(trendEntries, "Trend").apply {
                    color = Color.MAGENTA
                    setDrawCircles(false)
                    lineWidth = 2f
                }
                dataSets.add(trendSet)
            }

            chart.data = LineData(dataSets)
            val formatter = object : ValueFormatter() {
                private val df = SimpleDateFormat("MM/dd", Locale.getDefault())
                override fun getAxisLabel(value: Float, axis: com.github.mikephil.charting.components.AxisBase?): String {
                    val index = value.toInt().coerceIn(sorted.indices)
                    return sorted.getOrNull(index)?.date?.let { df.format(Date(it)) } ?: ""
                }
            }
            chart.xAxis.valueFormatter = formatter
            chart.invalidate()
        }
    )
}
