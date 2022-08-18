package com.example.gift_for_apelsinka.activity.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.PhotosActivity
import com.example.gift_for_apelsinka.adapter.ImageViewPageAdapter
import com.example.gift_for_apelsinka.adapter.StatementViewPageAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var viewModel: MainViewModel
    private lateinit var viewPageOfImage : ViewPager
    private lateinit var viewPageOfStatement : ViewPager
    private lateinit var greetingTextView : TextView
    private lateinit var photoWithApelsinka : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()
        applyEvents()
    }

    private fun initComponents() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewPageOfImage = findViewById(R.id.view_pager_of_image)
        viewPageOfStatement = findViewById(R.id.view_pager_of_statement)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        greetingTextView = findViewById(R.id.greetings_for_apelsinka)
        photoWithApelsinka = findViewById(R.id.photo_with_apelsinka)

        initViewPager(viewPageOfImage, 10, ImageViewPageAdapter(this))
        initViewPager(viewPageOfStatement, 0, StatementViewPageAdapter(this, viewModel.getStatements()))
        setGreeting()

        Glide.with(this)
            .load("https://sun9-78.userapi.com/impg/QgrDaDWK9_CvRfUeVWponxtFKP6LYzhPSXl5Aw/uUbMrSVTCvo.jpg?size=1200x1600&quality=95&sign=08262cc283fdb4f7594de83f46527425&type=album")
            .error(R.drawable.developer)
            .format(DecodeFormat.PREFER_RGB_565)
            .apply(RequestOptions().transform(CircleCrop(), RoundedCorners(40)))
            .into(findViewById(R.id.photo_of_developer))
    }

    private fun initViewPager(viewPager : ViewPager, padding : Int, adapter : PagerAdapter) {
        viewPager.adapter = adapter
        viewPager.clipToPadding = false
        viewPager.setPadding(65 - padding,0,65 - padding,0)
    }

    private fun setGreeting() {
        val greetingValue = viewModel.getGreetingsForApelsinka()
        if(greetingValue == null) greetingTextView.visibility = View.GONE
        else {
            greetingTextView.text = greetingValue
            Handler(Looper.getMainLooper()).postDelayed({
                greetingTextView.visibility = View.GONE
            }, 5_000)
        }
    }

    private fun applyEvents() {
        viewPageOfImage.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, v: Float, i1: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE)
            }
        })
        viewPageOfStatement.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, v: Float, i1: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE)
            }
        })
        swipeRefreshLayout.setOnRefreshListener {
            Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show()
            swipeRefreshLayout.isRefreshing = false
        }

        findViewById<ImageView>(R.id.photo_of_developer).setOnClickListener {
            Toast.makeText(this, "Open my VK?", Toast.LENGTH_SHORT).show()
        }

        photoWithApelsinka.setOnClickListener {
            startActivity(Intent(this, PhotosActivity::class.java))
        }
    }

    private fun enableDisableSwipeRefresh(enable: Boolean) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.isEnabled = enable
        }
    }
}