package com.ishwal.beverageinventory.utils.chart

import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class InventoryChartValueFormatter : ValueFormatter() {

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return value.toInt().toString()
    }
}