package com.example.gift_for_apelsinka.util

import android.content.Context
import android.widget.Toast

object ShowToast {
    private var toast: Toast? = null
    fun show(context: Context?, text: String?) {
        if (toast != null)
            toast!!.cancel()
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast?.show()
    }
}