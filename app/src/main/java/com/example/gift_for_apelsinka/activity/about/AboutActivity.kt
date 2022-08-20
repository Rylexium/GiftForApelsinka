package com.example.gift_for_apelsinka.activity.about

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.about.adapter.ImageViewOfPersonPageAdapter
import com.example.gift_for_apelsinka.util.DialogEditText.editTextView
import com.example.gift_for_apelsinka.util.DoubleClickListener
import com.example.gift_for_apelsinka.util.InitView.initViewPager
import com.example.gift_for_apelsinka.util.InitView.setImageWithCircle

class AboutActivity : AppCompatActivity() {
    private lateinit var viewModel: AboutViewModel
    private lateinit var viewPageOfImageOscar : ViewPager
    private lateinit var viewPageOfImageLera : ViewPager
    private lateinit var viewPageOfImageLexa : ViewPager
    private lateinit var viewPageOfImageLogo : ViewPager

    private lateinit var aboutApelsinkaTitle : TextView
    private lateinit var aboutOscarTitle : TextView
    private lateinit var aboutLeraTitle : TextView
    private lateinit var aboutLexaTitle : TextView

    private lateinit var textViewAboutApelsinka : TextView
    private lateinit var textViewTextOfGoodnight : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        initComponents()
        applyEvents()
    }

    private fun initComponents() {
        viewModel = ViewModelProvider(this,
            AboutViewModelFactory(applicationContext.getSharedPreferences("preference_key", Context.MODE_PRIVATE)))[AboutViewModel::class.java]
        viewPageOfImageOscar = findViewById(R.id.view_pager_of_image_oscar)
        viewPageOfImageLera = findViewById(R.id.view_pager_of_image_lera)
        viewPageOfImageLexa = findViewById(R.id.view_pager_of_image_lexa)
        viewPageOfImageLogo = findViewById(R.id.view_pager_of_image_logo)

        aboutApelsinkaTitle = findViewById(R.id.field_about_apelsinka_title)
        aboutOscarTitle = findViewById(R.id.field_about_oscar_title)
        aboutLeraTitle = findViewById(R.id.field_about_lera_title)
        aboutLexaTitle = findViewById(R.id.field_about_lexa_title)

        textViewAboutApelsinka = findViewById(R.id.textview_about_apelsinka)
        textViewTextOfGoodnight = findViewById(R.id.textview_text_of_goodnight)

        textViewAboutApelsinka.text = viewModel.getTextAboutApelsinka()
        textViewTextOfGoodnight.text = viewModel.getTextGoodnight()

        aboutApelsinkaTitle.text = viewModel.getApelsinkaTitle()
        aboutOscarTitle.text = viewModel.getOscarTitle()
        aboutLeraTitle.text = viewModel.getLeraTitle()
        aboutLexaTitle.text = viewModel.getLexaTitle()

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
                    viewModel.setTextAboutApelsinka(textViewAboutApelsinka.text.toString())
                }
            }
        })
        textViewTextOfGoodnight.setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(textViewTextOfGoodnight, this@AboutActivity) {
                    viewModel.setTextGoodnight(textViewTextOfGoodnight.text.toString())
                }
            }
        })
        aboutApelsinkaTitle.setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(aboutApelsinkaTitle, this@AboutActivity) {
                    viewModel.setApelsinkaTitle(textViewTextOfGoodnight.text.toString())
                }
            }
        })
        aboutOscarTitle.setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(aboutOscarTitle, this@AboutActivity) {
                    viewModel.setOscarTitle(aboutOscarTitle.text.toString())
                }
            }
        })
        aboutLeraTitle.setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(aboutLeraTitle, this@AboutActivity) {
                    viewModel.setLeraTitle(aboutLeraTitle.text.toString())
                }
            }
        })
        aboutLexaTitle.setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(aboutLexaTitle, this@AboutActivity) {
                    viewModel.setLexaTitle(aboutLexaTitle.text.toString())
                }
            }
        })
    }
}


