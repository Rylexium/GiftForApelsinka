package com.example.gift_for_apelsinka.util.views

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import kotlin.math.max

class DynamicViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var height = 0
        val childWidthSpec = MeasureSpec.makeMeasureSpec(
            max(
                0,
                MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight),
                   MeasureSpec.getMode(widthMeasureSpec)
                )
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(childWidthSpec, MeasureSpec.UNSPECIFIED)
            val h = child.measuredHeight
            if (h > height) height = h
        }
        if (height != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}