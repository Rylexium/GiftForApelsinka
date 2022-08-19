package com.example.gift_for_apelsinka.activity.about

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.about.adapter.ImageViewOfPersonPageAdapter
import com.example.gift_for_apelsinka.util.DoubleClickListener
import com.example.gift_for_apelsinka.util.InitView.initViewPager
import com.example.gift_for_apelsinka.util.InitView.setImageWithCircle

class AboutActivity : AppCompatActivity() {
    private lateinit var viewModel: AboutViewModel
    private lateinit var viewPageOfImageOscar : ViewPager
    private lateinit var viewPageOfImageLera : ViewPager
    private lateinit var viewPageOfImageLexa : ViewPager
    private lateinit var viewPageOfImageLogo : ViewPager

    private lateinit var textViewAboutApelsinka : TextView
    private lateinit var textViewTextOfGoodnight : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initComponents()
        applyEvents()
    }

    private fun initComponents() {
        viewModel = ViewModelProvider(this)[AboutViewModel::class.java]
        viewPageOfImageOscar = findViewById(R.id.view_pager_of_image_oscar)
        viewPageOfImageLera = findViewById(R.id.view_pager_of_image_lera)
        viewPageOfImageLexa = findViewById(R.id.view_pager_of_image_lexa)
        viewPageOfImageLogo = findViewById(R.id.view_pager_of_image_logo)

        textViewAboutApelsinka = findViewById(R.id.textview_about_apelsinka)
        textViewTextOfGoodnight = findViewById(R.id.textview_text_of_goodnight)

        textViewAboutApelsinka.text = viewModel.getTextAboutApelsinka(applicationContext.getSharedPreferences("preference_key", Context.MODE_PRIVATE))
        textViewTextOfGoodnight.text = viewModel.getTextGoodnight(applicationContext.getSharedPreferences("preference_key", Context.MODE_PRIVATE))

        initViewPager(viewPageOfImageLogo, 65, ImageViewOfPersonPageAdapter(this, viewModel.getImagesOfLogo()))
        initViewPager(viewPageOfImageOscar, 65, ImageViewOfPersonPageAdapter(this, viewModel.getImageOfOscar()))
        initViewPager(viewPageOfImageLera, 65, ImageViewOfPersonPageAdapter(this, viewModel.getImageOfLera()))
        initViewPager(viewPageOfImageLexa, 65, ImageViewOfPersonPageAdapter(this, viewModel.getImageOfLexa()))

        setImageWithCircle(R.drawable.mouse_of_apelsinka, findViewById(R.id.mouse_of_apelsinka), this)
    }

    private fun applyEvents() {
        textViewAboutApelsinka.setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(textViewAboutApelsinka, this@AboutActivity) {
                    viewModel.setTextAboutApelsinka(
                        textViewAboutApelsinka.text.toString(),
                        applicationContext.getSharedPreferences("preference_key", Context.MODE_PRIVATE))
                }
            }
        })
        textViewTextOfGoodnight.setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(textViewTextOfGoodnight, this@AboutActivity) {
                    viewModel.setTextGoodnight(
                        textViewTextOfGoodnight.text.toString(),
                        applicationContext.getSharedPreferences("preference_key", Context.MODE_PRIVATE))
                }
            }
        })
    }

    private fun editTextView(textView: TextView, context : Context, r : Runnable) {
        val editText = EditText(context)
        editText.setText(textView.text)
        val dialog = AlertDialog.Builder(context).create()
        dialog.setTitle("Отредактируй информацию о себе")
        dialog.setView(editText)
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Сохранить изменения") { _, _ ->
            textView.text = editText.text
            r.run()
        }
        dialog.show()
    }
}


