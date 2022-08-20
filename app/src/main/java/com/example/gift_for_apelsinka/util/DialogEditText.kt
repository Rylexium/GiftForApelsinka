package com.example.gift_for_apelsinka.util

import android.content.Context
import android.content.DialogInterface
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

object DialogEditText {
    fun editTextView(textView: TextView, context : Context, r : Runnable) {
        val editText = EditText(context)
        editText.setText(textView.text)
        val dialog = AlertDialog.Builder(context).create()
        dialog.setTitle("Редактирование \uD83D\uDD8A️")
        dialog.setView(editText)
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Сохранить изменения") { _, _ ->
            textView.text = editText.text
            r.run()
        }
        dialog.show()
    }
}