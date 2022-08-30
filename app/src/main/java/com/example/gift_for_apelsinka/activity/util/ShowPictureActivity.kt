package com.example.gift_for_apelsinka.activity.util

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.DebugFunctions
import com.example.gift_for_apelsinka.util.views.ZoomImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowPictureActivity : AppCompatActivity() {
    companion object {
        lateinit var image : String
    }
    private lateinit var imageView: ZoomImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_picture)
        DebugFunctions.addDebug("ShowPictureActivity","onCreate")
        initComponents()
        initDataComponents()
    }

    private fun initComponents() {
        DebugFunctions.addDebug("ShowPictureActivity","initComponents")
        imageView = findViewById(R.id.iv)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        imageView.swipeToDismissEnabled = true
        imageView.onDismiss = {
            if(imageView.currentZoom.toDouble() == 1.0) {
                finishAfterTransition()
            }
        }
    }

    private fun initDataComponents() {
        DebugFunctions.addDebug("ShowPictureActivity","initDataComponents")
        CoroutineScope(Dispatchers.IO).launch {
            val res = if (isNumeric(image)) image.toInt()
            else ConvertClass.convertStringToBitmap(image)

            Handler(Looper.getMainLooper()).post {
                Glide.with(this@ShowPictureActivity)
                    .load(res)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(imageView)
            }
        }
    }

    override fun finishAfterTransition() {
        DebugFunctions.addDebug("ShowPictureActivity","finishAfterTransition")
        super.finishAfterTransition()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {
        DebugFunctions.addDebug("ShowPictureActivity","onBackPressed")
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    private fun isNumeric(str: String) = str.all { it in '0'..'9' }
}