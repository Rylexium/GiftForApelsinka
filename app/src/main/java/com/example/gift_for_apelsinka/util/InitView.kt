package com.example.gift_for_apelsinka.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

object InitView {
    fun setImageWithCircle(id : Int, imageView: ImageView, context: Context) {
        Glide.with(context)
            .load(id)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(CircleCrop(), RoundedCorners(40)))
            .into(imageView)
    }

    fun setImageWithCorners(id : Int, imageView : ImageView, context: Context) {
        Glide.with(context)
            .load(id)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(40)))
            .into(imageView)
    }
    fun setImageWithCorners(bitmap: Bitmap, imageView : ImageView, context: Context) {
        Glide.with(context)
            .load(bitmap)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(40)))
            .into(imageView)
    }

    suspend fun getCircleImage(image : Any?, context: Context) : Bitmap {
        return suspendCoroutine {
            Glide.with(context)
                .asBitmap()
                .load(image)
                .apply(RequestOptions().transform(CircleCrop(), RoundedCorners(40)))
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        it.resume(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }

    fun setImage(id : Int, imageView : ImageView, context: Context) {
        Glide.with(context)
            .load(id)
            .format(DecodeFormat.PREFER_RGB_565)
            .into(imageView)
    }

    fun initViewPager(viewPager : ViewPager, padding : Int, adapter : PagerAdapter) {
        val state = viewPager.onSaveInstanceState()
        viewPager.adapter = adapter
        viewPager.clipToPadding = false
        viewPager.setPadding(65 - padding,0,65 - padding,0)
        viewPager.onRestoreInstanceState(state)
    }
    fun dpToPx(resources : Resources, dp: Int): Int {
        val density: Float = resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }
    fun enableDisableSwipeRefresh(swipeRefreshLayout : SwipeRefreshLayout, enable: Boolean) {
        swipeRefreshLayout.isEnabled = enable
    }
}