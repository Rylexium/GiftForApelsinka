package com.example.gift_for_apelsinka.util.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class ImageViewPager(context: Context, attrs: AttributeSet? = null) :
    ViewPager(context, attrs) {
    var mStartDragX = 0f
    var mListener: OnSwipeOutListener? = null
    fun setOnSwipeOutListener(listener: OnSwipeOutListener?) {
        mListener = listener
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> mStartDragX = x
            MotionEvent.ACTION_MOVE -> if (mStartDragX < x && currentItem == 0) {
                mListener!!.onSwipeOutAtStart()
            } else if (mStartDragX > x && currentItem == adapter!!.count - 1) {
                mListener!!.onSwipeOutAtEnd()
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    interface OnSwipeOutListener {
        fun onSwipeOutAtStart()
        fun onSwipeOutAtEnd()
    }
}