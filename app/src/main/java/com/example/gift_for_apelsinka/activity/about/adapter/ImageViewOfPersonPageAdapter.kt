package com.example.gift_for_apelsinka.activity.about.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.util.ConvertClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewOfPersonPageAdapter (context: Context, list : List<Any>) : PagerAdapter() {
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
        CoroutineScope(Dispatchers.IO).launch {
            val image = if(imageArray[position] is Int) imageArray[position] as Int
                        else ConvertClass.convertStringToBitmap((imageArray[position] as FieldPhoto).picture)
            Handler(Looper.getMainLooper()).post {
                Glide.with(ctx)
                    .load(image)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
                    .into(imageView)
                container.addView(imageView, 0)
            }
        }
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }
}