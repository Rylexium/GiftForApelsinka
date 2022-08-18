package com.example.gift_for_apelsinka.activity.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.about.adapter.ImageViewOfPersonPageAdapter

class AboutActivity : AppCompatActivity() {
    private lateinit var viewPageOfImageOscar : ViewPager
    private lateinit var viewPageOfImageLera : ViewPager
    private lateinit var viewPageOfImageLexa : ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initComponents()
    }

    private fun initComponents() {
        viewPageOfImageOscar = findViewById(R.id.view_pager_of_image_oscar)
        viewPageOfImageLera = findViewById(R.id.view_pager_of_image_lera)
        viewPageOfImageLexa = findViewById(R.id.view_pager_of_image_lexa)

        initViewPager(viewPageOfImageOscar, 65, ImageViewOfPersonPageAdapter(this,
            listOf( R.drawable.oscar, R.drawable.oscar1, R.drawable.oscar2,
                R.drawable.oscar3, R.drawable.oscar4, R.drawable.oscar5,
                R.drawable.oscar6, R.drawable.oscar7, R.drawable.oscar8, R.drawable.oscar9).shuffled()))

        initViewPager(viewPageOfImageLera, 65, ImageViewOfPersonPageAdapter(this,
            listOf(R.drawable.lera_ksixa, R.drawable.lera_ksixa2, R.drawable.lera_ksixa3,
            R.drawable.lera1, R.drawable.lera2)
                .shuffled()))

        initViewPager(viewPageOfImageLexa, 65, ImageViewOfPersonPageAdapter(this,
            listOf(R.drawable.lexa1, R.drawable.lexa2, R.drawable.lexa3, R.drawable.lexa4,
                R.drawable.lexa_ksixa, R.drawable.lexa_ksixa2, R.drawable.lexa_ksixa3, R.drawable.lexa_ksixa4).shuffled()))

        setImageWithCircle(R.drawable.mouse_of_apelsinka ,findViewById(R.id.mouse_of_apelsinka))
        setImageWithCorners(R.drawable.logo, findViewById(R.id.logo_of_apelsinka))
    }

    private fun setImageWithCircle(id: Int, imageView: ImageView) {
        Glide.with(this)
            .load(id)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(CircleCrop(), RoundedCorners(40)))
            .into(imageView)
    }

    private fun setImageWithCorners(id : Int, imageView : ImageView) {
        Glide.with(this)
            .load(id)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(RoundedCorners(40)))
            .into(imageView)
    }

    private fun initViewPager(viewPager : ViewPager, padding : Int, adapter : PagerAdapter) {
        viewPager.adapter = adapter
        viewPager.clipToPadding = false
        viewPager.setPadding(65 - padding,0,65 - padding,0)
    }
}