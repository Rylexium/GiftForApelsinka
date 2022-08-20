package com.example.gift_for_apelsinka.activity.main.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.InitView.setImageWithCorners

class ImageViewPageAdapter(
    context: Context,
    private val list: List<Int>
) : PagerAdapter() {
    private var ctx : Context = context
    private var imageArray = list

    override fun getCount(): Int {
        return imageArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(ctx)
        imageView.setPadding(18, 9, 18, 9)
        setImageWithCorners(imageArray[position], imageView, ctx)
        container.addView(imageView, 0)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }
}