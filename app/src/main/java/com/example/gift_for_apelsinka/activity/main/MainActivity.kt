package com.example.gift_for_apelsinka.activity.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.about.AboutActivity
import com.example.gift_for_apelsinka.activity.main.adapter.ImageViewPageAdapter
import com.example.gift_for_apelsinka.activity.main.adapter.StatementViewPageAdapter
import com.example.gift_for_apelsinka.activity.photo.PhotosActivity
import com.example.gift_for_apelsinka.db.initDB
import com.example.gift_for_apelsinka.service.GoodMorningService
import com.example.gift_for_apelsinka.service.LocationService
import com.example.gift_for_apelsinka.service.RandomQuestionService
import com.example.gift_for_apelsinka.util.AnimView
import com.example.gift_for_apelsinka.util.InitView.initViewPager
import com.example.gift_for_apelsinka.util.InitView.setImage
import com.example.gift_for_apelsinka.util.InitView.setImageWithCircle
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowHour
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
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

    private lateinit var textMailDeveloper : TextView
    private lateinit var textVkDeveloper : TextView
    private lateinit var textPhoneDeveloper : TextView
    private lateinit var textDiscordDeveloper : TextView
    private lateinit var textAddressDeveloper : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDB(applicationContext)

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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMain)

        layoutGreeting = findViewById(R.id.layout_greetings_for_apelsinka)
        greetingTextView = findViewById(R.id.greetings_for_apelsinka)
        photoWithApelsinka = findViewById(R.id.photo_with_apelsinka)

        factsAboutApelsinka = findViewById(R.id.facts_about_apelsinka)
        changeTheme = findViewById(R.id.change_theme)

        textMailDeveloper = findViewById(R.id.text_mail_developer)
        textVkDeveloper = findViewById(R.id.text_vk_developer)
        textPhoneDeveloper = findViewById(R.id.text_phone_developer)
        textDiscordDeveloper = findViewById(R.id.text_discord_developer)
        textAddressDeveloper = findViewById(R.id.text_address_developer)

        initViewPager(viewPageOfImage, 10, ImageViewPageAdapter(applicationContext, viewModel.getPictures()))
        initViewPager(viewPageOfStatement, 0, StatementViewPageAdapter(this, viewModel.getStatements()))
        setGreeting()
        setImageWithCircle(R.drawable.developer, findViewById(R.id.photo_of_developer),this)
        setImageWithCircle(R.drawable.icon_of_developer, findViewById(R.id.icon_of_developer),this)
        updateDeveloper(viewModel.getHandbook())

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
            val context = this
            viewModel.viewModelScope.launch {
                // SocketTimeoutException
                try {
                    updateDeveloper(viewModel.updateDataOfDeveloper())
                    initViewPager(viewPageOfStatement, 0, StatementViewPageAdapter(context, viewModel.updateStatements()))
                    initViewPager(viewPageOfImage, 0, ImageViewPageAdapter(context, viewModel.updateMainPictures()))
                }
                catch (e : SocketTimeoutException) {
                    Handler(Looper.getMainLooper()).post { Toast.makeText(context, "Not Refresh", Toast.LENGTH_SHORT).show() }
                    Log.e("timeout", e.message.toString())
                }
                finally {
                    Handler(Looper.getMainLooper()).post { swipeRefreshLayout.isRefreshing = false }
                }
            }
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

        textVkDeveloper.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/rylexium")))
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

    @SuppressLint("SetTextI18n")
    private fun updateDeveloper(map : Map<String, String>) {
        textMailDeveloper.text = "Почта : ${map["mail"]}"

        textVkDeveloper.text = Html.fromHtml("ВК : <u><font color=\"#328fa8\">${map["VK"]}</font></u>")

        textPhoneDeveloper.text = "Телефон : ${map["phone"]}"
        textDiscordDeveloper.text = "Discord : ${map["discord"]}"

        textAddressDeveloper.text = Html.fromHtml("Адрес : <u><font color=\"#328fa8\">${map["address"]}</font></u>")
        textAddressDeveloper.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(map["url_address_developer"])))
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