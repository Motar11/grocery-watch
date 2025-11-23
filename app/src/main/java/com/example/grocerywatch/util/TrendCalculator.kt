package com.example.grocerywatch.util

import com.example.grocerywatch.data.entity.PriceHistoryEntry

object TrendCalculator {
    data class TrendLine(val slope: Double, val intercept: Double, val predictedNextPrice: Double)

    fun calculate(history: List<PriceHistoryEntry>): TrendLine? {
        if (history.size < 2) return null
        val points = history.sortedBy { it.date }
        val xs = points.mapIndexed { index, _ -> index.toDouble() }
        val ys = points.map { it.price }
        val meanX = xs.average()
        val meanY = ys.average()
        val numerator = xs.zip(ys).sumOf { (x, y) -> (x - meanX) * (y - meanY) }
        val denominator = xs.sumOf { (it - meanX) * (it - meanX) }
        if (denominator == 0.0) return null
        val slope = numerator / denominator
        val intercept = meanY - slope * meanX
        val predictedNext = slope * xs.size + intercept
        return TrendLine(slope = slope, intercept = intercept, predictedNextPrice = predictedNext)
    }
}
