package com.example.gift_for_apelsinka.activity.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        viewPageOfImageOscar.adapter = ImageViewOfPersonPageAdapter(this,
            listOf( R.drawable.oscar, R.drawable.oscar1, R.drawable.oscar2,
                    R.drawable.oscar3, R.drawable.oscar4, R.drawable.oscar5,
                    R.drawable.oscar6, R.drawable.oscar7, R.drawable.oscar8, R.drawable.oscar9).shuffled())

        viewPageOfImageLera = findViewById(R.id.view_pager_of_image_lera)
        viewPageOfImageLera.adapter = ImageViewOfPersonPageAdapter(this,
            listOf(R.drawable.lera_ksixa, R.drawable.lera_ksixa2, R.drawable.lera_ksixa3).shuffled())

        viewPageOfImageLexa = findViewById(R.id.view_pager_of_image_lexa)
        viewPageOfImageLexa.adapter = ImageViewOfPersonPageAdapter(this,
            listOf(R.drawable.lexa1, R.drawable.lexa2, R.drawable.lexa3, R.drawable.lexa4,
                R.drawable.lexa_ksixa, R.drawable.lexa_ksixa2, R.drawable.lexa_ksixa3).shuffled())




        Glide.with(this)
            .load(R.drawable.mouse_of_apelsinka)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(CircleCrop(), RoundedCorners(40)))
            .into(findViewById(R.id.mouse_of_apelsinka))
        Glide.with(this)
            .load(R.drawable.logo)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(RoundedCorners(40)))
            .into(findViewById(R.id.logo_of_apelsinka))
    }
}