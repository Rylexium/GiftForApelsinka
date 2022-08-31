package com.example.gift_for_apelsinka.activity.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.main.MainActivity
import com.example.gift_for_apelsinka.cache.defaultPhotosApelsinka
import com.example.gift_for_apelsinka.cache.setAndroidId
import com.example.gift_for_apelsinka.db.initDB
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.WorkWithServices
import kotlin.random.Random

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var layoutSplash : LinearLayout
    private lateinit var imageView : ImageView
    private lateinit var imageViewDev : ImageView
    private lateinit var imageApelsinka : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        startServices()
        initDB(applicationContext)
        initComponents()
        initData()
        startMainActivity()
    }

    @SuppressLint("HardwareIds")
    private fun startServices() {
        setAndroidId(this)
        WorkWithServices.restartAllServices(applicationContext)
    }

    private fun startMainActivity() {
        layoutSplash.alpha = 0f
        layoutSplash.animate().setDuration(Random.nextInt(400, 700).toLong()).alpha(1f).withEndAction {
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun initComponents() {
        layoutSplash = findViewById(R.id.layout_splash)
        imageView = findViewById(R.id.image_splash)
        imageViewDev = findViewById(R.id.image_developer_splash)
        imageApelsinka = findViewById(R.id.image_apelsinka_splash)

    }
    private fun initData() {
        val list = defaultPhotosApelsinka(getSharedPreferences("preference_key", Context.MODE_PRIVATE))

        InitView.setImageWithCircle(R.drawable.mouse_of_apelsinka, imageApelsinka, this)
        InitView.setImageWithCircle(R.drawable.developer, imageViewDev, this)
        InitView.setImageWithCorners(list[(System.currentTimeMillis() % (list.size - 1)).toInt()].picture.toInt(), imageView, this)
    }


}