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
        var height = 0
        (0 until childCount).forEach {
            val child = getChildAt(it)
            child.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            height = max(height, child.measuredHeight)
        }
        if (height > 0) {
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}