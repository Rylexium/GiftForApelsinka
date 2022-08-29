package com.example.gift_for_apelsinka.activity.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.InitView.setImageWithCorners
import com.example.gift_for_apelsinka.util.wrapperOpenShowPictureActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ImageViewPageAdapter(
    context: Context,
    list: List<Any>
) : PagerAdapter() {
    private var ctx : Context = context
    private var imageArray = list
    private lateinit var layoutInflater : LayoutInflater

    override fun getCount(): Int {
        return imageArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any = runBlocking {
        layoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(R.layout.field_of_picture, container, false)

        val imageView = view.findViewById<ImageView>(R.id.field_of_picture)

        view.setPadding(18, 9, 18, 9)
        if(imageArray[position] is Int) {
            wrapperOpenShowPictureActivity(imageView, ctx, imageArray[position].toString())
            setImageWithCorners(imageArray[position] as Int, imageView, ctx)
        }
        else {
            wrapperOpenShowPictureActivity(imageView, ctx, (imageArray[position] as FieldPhoto).picture)
            val task = async { ConvertClass.convertStringToBitmap((imageArray[position] as FieldPhoto).picture) }
            setImageWithCorners(task.await()!!, imageView, ctx)
        }
        container.addView(view, 0)
        return@runBlocking view
    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }
}