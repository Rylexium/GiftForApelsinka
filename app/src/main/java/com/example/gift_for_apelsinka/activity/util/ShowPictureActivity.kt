package com.example.gift_for_apelsinka.activity.util

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        imageView.swipeToDismissEnabled = true

        imageView.onDismiss = {
            finishAfterTransition()
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
    private fun isNumeric(str: String) = str.all { it in '0'..'9' }
}