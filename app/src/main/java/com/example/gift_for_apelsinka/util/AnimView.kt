package com.example.gift_for_apelsinka.util

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.ViewCompat

object AnimView {
    fun animVisible(view : View, time : Long){
        ViewCompat.animate(view)
            .withStartAction { view.visibility = View.VISIBLE }
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(time)
            .start()
    }

    fun animGone(view : View, time : Long){
        ViewCompat.animate(view)
            .withStartAction { view.visibility = View.GONE }
            .alpha(1f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(time)
            .start()
    }
}