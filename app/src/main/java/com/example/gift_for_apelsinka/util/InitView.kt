package com.example.gift_for_apelsinka.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
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
            .apply(RequestOptions().transform(RoundedCorners(40)))
            .into(imageView)
    }
    fun setPhotoDeveloper(imageView : ImageView, context : Context) {
        Glide.with(context)
            .load("https://sun9-78.userapi.com/impg/QgrDaDWK9_CvRfUeVWponxtFKP6LYzhPSXl5Aw/uUbMrSVTCvo.jpg?size=1200x1600&quality=95&sign=08262cc283fdb4f7594de83f46527425&type=album")
            .error(R.drawable.developer)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(CircleCrop(), RoundedCorners(40)))
            .into(imageView)
    }
    fun initViewPager(viewPager : ViewPager, padding : Int, adapter : PagerAdapter) {
        viewPager.adapter = adapter
        viewPager.clipToPadding = false
        viewPager.setPadding(65 - padding,0,65 - padding,0)
    }
}