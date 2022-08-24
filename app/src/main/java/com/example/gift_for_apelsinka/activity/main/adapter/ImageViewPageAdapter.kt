package com.example.gift_for_apelsinka.activity.main.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.InitView.setImageWithCorners
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ImageViewPageAdapter(
    context: Context,
    list: List<Any>
) : PagerAdapter() {
    private var ctx : Context = context
    private var imageArray = list

    override fun getCount(): Int {
        return imageArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any = runBlocking {
        val imageView = ImageView(ctx)
        imageView.setPadding(18, 9, 18, 9)
        if(imageArray[position] is Int)
            setImageWithCorners(imageArray[position] as Int, imageView, ctx)
        else {
            println(imageArray[position])
            val task = async { ConvertClass.convertStringToBitmap((imageArray[position] as FieldPhoto).picture) }
            setImageWithCorners(task.await()!!, imageView, ctx)
        }
        container.addView(imageView, 0)
        return@runBlocking imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }
}