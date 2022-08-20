package com.example.gift_for_apelsinka.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.gift_for_apelsinka.R

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

    fun setImage(id : Int, imageView : ImageView, context: Context) {
        Glide.with(context)
            .load(id)
            .format(DecodeFormat.PREFER_RGB_565)
            .into(imageView)
    }

    fun initViewPager(viewPager : ViewPager, padding : Int, adapter : PagerAdapter) {
        viewPager.adapter = adapter
        viewPager.clipToPadding = false
        viewPager.setPadding(65 - padding,0,65 - padding,0)
    }
}