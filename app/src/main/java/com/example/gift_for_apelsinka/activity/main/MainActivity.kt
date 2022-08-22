package com.example.gift_for_apelsinka.activity.main

import android.Manifest
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Network
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.about.AboutActivity
import com.example.gift_for_apelsinka.activity.main.adapter.ImageViewPageAdapter
import com.example.gift_for_apelsinka.activity.main.adapter.StatementViewPageAdapter
import com.example.gift_for_apelsinka.activity.photo.PhotosActivity
import com.example.gift_for_apelsinka.db.model.Statements
import com.example.gift_for_apelsinka.retrofit.RetrofitInstance
import com.example.gift_for_apelsinka.retrofit.model.StatementsList
import com.example.gift_for_apelsinka.retrofit.network.NetworkStatements
import com.example.gift_for_apelsinka.retrofit.service.StatementsServiceApi
import com.example.gift_for_apelsinka.service.GoodMorningService
import com.example.gift_for_apelsinka.service.LocationService
import com.example.gift_for_apelsinka.service.RandomQuestionService
import com.example.gift_for_apelsinka.util.AnimView
import com.example.gift_for_apelsinka.util.InitView.initViewPager
import com.example.gift_for_apelsinka.util.InitView.setImage
import com.example.gift_for_apelsinka.util.InitView.setImageWithCircle
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowHour
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt


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
        startServices()
    }

    private fun startServices() {
        startService(Intent(this, GoodMorningService::class.java))
        startService(Intent(this, RandomQuestionService::class.java))
        //requestPermission()
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//            startService(Intent(this, LocationService::class.java))
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1 && grantResults.isNotEmpty())
            startService(Intent(this, LocationService::class.java))
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
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
        setImageWithCircle(R.drawable.developer, findViewById(R.id.photo_of_developer),this)
        setImageWithCircle(R.drawable.icon_of_developer, findViewById(R.id.icon_of_developer),this)

        if(getNowHour() in 0..5) {
            val chance = (0..10).random()
            if(chance in 1..3) Toast.makeText(this, "Хули не спим?!?!?!? ❤️", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setGreeting() {
        val image = viewModel.getImageOfTime()
        setImage(image, findViewById(R.id.image_of_time1), this)
        setImage(image, findViewById(R.id.image_of_time2), this)

        val greetingValue = viewModel.getGreetingsForApelsinka(false)
        if(greetingValue == null) layoutGreeting.visibility = View.GONE
        else {
            greetingTextView.text = greetingValue
            if(greetingValue.length <= 24)
                greetingTextView.height = dpToPx(65)
            Handler(Looper.getMainLooper()).postDelayed({
                AnimView.animTransitionUp(layoutGreeting)
            }, 3_000)
        }
    }
    fun dpToPx(dp: Int): Int {
        val density: Float = resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
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
            Toast.makeText(this, "Да, это Ryletikum...", Toast.LENGTH_SHORT).show()
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

        findViewById<TextView>(R.id.text_vk_developer).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/rylexium")))
        }

        findViewById<TextView>(R.id.address_developer).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/place/%D1%83%D0%BB.+%D0%95%D0%BD%D0%B8%D1%81%D0%B5%D0%B9%D1%81%D0%BA%D0%B0%D1%8F,+41,+%D0%A1%D0%B0%D0%BC%D0%B0%D1%80%D0%B0,+%D0%A1%D0%B0%D0%BC%D0%B0%D1%80%D1%81%D0%BA%D0%B0%D1%8F+%D0%BE%D0%B1%D0%BB.,+443034/@53.2330359,50.2687255,18.24z/data=!4m5!3m4!1s0x41661bd65a8f0227:0x11132f830c4e06a1!8m2!3d53.2326652!4d50.2699214?hl=ru")))
        }

        findViewById<TextView>(R.id.international_agreement).setOnClickListener {
            Snackbar.make(it, "ВНИМАНИЕ! Данная программа не защищена законами об" +
                    " авторских правах и международными соглашениями. Копирование не запрещено", Snackbar.LENGTH_LONG)
                .setTextMaxLines(6)
                .setAction("Похуй + Похуй") {
                    Toast.makeText(this, "Теперь Вам похуй", Toast.LENGTH_SHORT).show()
                }
                .show()
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