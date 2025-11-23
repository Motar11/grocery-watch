package com.motar11.grocerywatch.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PriceChart(
    points: List<Pair<Long, Double>>,
    trendLine: List<Pair<Long, Double>>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true
                setNoDataText("No price history yet")
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                setTouchEnabled(true)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) { chart ->
        if (points.isEmpty()) {
            chart.clear()
            return@AndroidView
        }
        val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        val historyData = LineDataSet(
            points.mapIndexed { index, entry -> Entry(index.toFloat(), entry.second.toFloat()) },
            "Price"
        ).apply {
            color = Color.parseColor("#1B5E20")
            setCircleColor(Color.parseColor("#81C784"))
            valueTextColor = Color.DKGRAY
            lineWidth = 2.5f
        }

        val trendData = LineDataSet(
            trendLine.mapIndexed { index, entry -> Entry(index.toFloat(), entry.second.toFloat()) },
            "Trend"
        ).apply {
            color = Color.parseColor("#FFA000")
            setDrawCircles(false)
            lineWidth = 2f
        }

        val lineData = if (trendLine.isNotEmpty()) LineData(historyData, trendData) else LineData(historyData)
        chart.data = lineData

        chart.xAxis.valueFormatter = { value, _ ->
            val index = value.toInt().coerceIn(0, points.lastIndex)
            dateFormat.format(Date(points.getOrNull(index)?.first ?: System.currentTimeMillis()))
        }
        chart.invalidate()
    }
}
