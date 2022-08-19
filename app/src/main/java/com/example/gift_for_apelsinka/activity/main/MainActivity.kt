package com.example.gift_for_apelsinka.activity.main

import android.R.attr.label
import android.app.UiModeManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bumptech.glide.Glide
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.about.AboutActivity
import com.example.gift_for_apelsinka.activity.main.adapter.ImageViewPageAdapter
import com.example.gift_for_apelsinka.activity.main.adapter.StatementViewPageAdapter
import com.example.gift_for_apelsinka.activity.photo.PhotosActivity
import com.example.gift_for_apelsinka.util.AnimView
import com.example.gift_for_apelsinka.util.InitView.setPhotoDeveloper


class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var viewModel: MainViewModel
    private lateinit var viewPageOfImage : ViewPager
    private lateinit var viewPageOfStatement : ViewPager
    private lateinit var greetingTextView : TextView
    private lateinit var photoWithApelsinka : Button
    private lateinit var factsAboutApelsinka : Button
    private lateinit var changeTheme : Button
    private lateinit var layoutGreeting : LinearLayout

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


        layoutGreeting = findViewById(R.id.layout_greetings_for_apelsinka)
        greetingTextView = findViewById(R.id.greetings_for_apelsinka)
        photoWithApelsinka = findViewById(R.id.photo_with_apelsinka)

        factsAboutApelsinka = findViewById(R.id.facts_about_apelsinka)
        changeTheme = findViewById(R.id.change_theme)

        initViewPager(viewPageOfImage, 10, ImageViewPageAdapter(this, viewModel.getPictures()))
        initViewPager(viewPageOfStatement, 0, StatementViewPageAdapter(this, viewModel.getStatements()))
        setGreeting()
        setPhotoDeveloper(findViewById(R.id.photo_of_developer), this)

        if(viewModel.getNowHour() in 1..5) {
            val chance = (0..10).random()
            if(chance in 1..2) Toast.makeText(this, "Хули не спим?!?!?!?", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViewPager(viewPager : ViewPager, padding : Int, adapter : PagerAdapter) {
        viewPager.adapter = adapter
        viewPager.clipToPadding = false
        viewPager.setPadding(65 - padding,0,65 - padding,0)
    }

    private fun setGreeting() {
        val image = viewModel.getImageOfTime()
        setImageOfTime(image, R.id.image_of_time1)
        setImageOfTime(image, R.id.image_of_time2)

        val greetingValue = viewModel.getGreetingsForApelsinka()
        if(greetingValue == null) layoutGreeting.visibility = View.GONE
        else {
            greetingTextView.text = greetingValue
            Handler(Looper.getMainLooper()).postDelayed({
                AnimView.animTransitionUp(layoutGreeting)
            }, 3_000)
        }
    }
    private fun setImageOfTime(image : Int, id : Int) {
        Glide.with(this)
            .load(image)
            .into(findViewById(id))
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

        factsAboutApelsinka.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        changeTheme.setOnClickListener {
            setNightMode(this)
        }
    }

    private fun setNightMode(target: Context) {
        val uiManager = target.getSystemService(UI_MODE_SERVICE) as UiModeManager
        if (uiManager.nightMode == 1) {
            uiManager.nightMode = UiModeManager.MODE_NIGHT_YES
        } else {
            uiManager.nightMode = UiModeManager.MODE_NIGHT_NO
        }
    }
    private fun enableDisableSwipeRefresh(enable: Boolean) {
        swipeRefreshLayout.isEnabled = enable
    }
}