package com.example.gift_for_apelsinka.activity.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.views.ZoomImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowPictureActivity : AppCompatActivity() {
    companion object {
        lateinit var image : String
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_picture)
        val imageView = findViewById<ZoomImageView>(R.id.iv)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        imageView.swipeToDismissEnabled = true
        imageView.onDismiss = {
            if(imageView.currentZoom.toDouble() == 1.0) {
                finishAfterTransition()
            }
        }

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
        super.finishAfterTransition()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    private fun isNumeric(str: String) = str.all { it in '0'..'9' }
}