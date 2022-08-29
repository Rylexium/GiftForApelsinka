package com.example.gift_for_apelsinka.activity.about

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.staticHandbook
import com.example.gift_for_apelsinka.util.*
import com.example.gift_for_apelsinka.util.DialogEditText.editTextView
import com.example.gift_for_apelsinka.util.InitView.setImageWithCircle
import com.example.gift_for_apelsinka.util.views.ImageViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class AboutActivity : AppCompatActivity() {
    private lateinit var switchRefreshLayout : SwipeRefreshLayout
    private lateinit var viewModel: AboutViewModel
    private lateinit var viewPageOfImageOscar : ImageViewPager
    private lateinit var viewPageOfImageLera : ImageViewPager
    private lateinit var viewPageOfImageLexa : ImageViewPager
    private lateinit var viewPageOfImageLogo : ImageViewPager

    private lateinit var aboutApelsinkaTitle : TextView
    private lateinit var aboutOscarTitle : TextView
    private lateinit var aboutLeraTitle : TextView
    private lateinit var aboutLexaTitle : TextView

    private lateinit var textViewAboutApelsinka : TextView
    private lateinit var textViewTextOfGoodnight : TextView

    private lateinit var progressbarViewPagerOfImageLogo : ProgressBar
    private lateinit var progressbarViewPagerOfImageOscar : ProgressBar
    private lateinit var progressbarViewPagerOfImageLera : ProgressBar
    private lateinit var progressbarViewPagerOfImageLexa : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initComponents()
        initDataComponents()
        viewModel.viewModelScope.launch { applyEvents() }
    }

    private fun initComponents() {
        viewModel = ViewModelProvider(this)[AboutViewModel::class.java]
        switchRefreshLayout = findViewById(R.id.swipeRefreshLayoutAbout)
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

        progressbarViewPagerOfImageLogo  = findViewById(R.id.progressbar_view_pager_of_image_logo)
        progressbarViewPagerOfImageOscar = findViewById(R.id.progressbar_view_pager_of_image_oscar)
        progressbarViewPagerOfImageLera  = findViewById(R.id.progressbar_view_pager_of_image_lera)
        progressbarViewPagerOfImageLexa  = findViewById(R.id.progressbar_view_pager_of_image_lexa)
    }

    private fun initDataComponents() {
        setImageWithCircle(R.drawable.mouse_of_apelsinka, findViewById(R.id.mouse_of_apelsinka), this)

        viewModel.handbook = staticHandbook
        liveDataObserveTextWrapper(viewModel.getApelsinkaTitle(), aboutApelsinkaTitle, this)
        liveDataObserveTextWrapper(viewModel.getTextAboutApelsinka(), textViewAboutApelsinka, this)
        liveDataObserveTextWrapper(viewModel.getOscarTitle(), aboutOscarTitle, this)
        liveDataObserveTextWrapper(viewModel.getLeraTitle(), aboutLeraTitle, this)
        liveDataObserveTextWrapper(viewModel.getLexaTitle(), aboutLexaTitle, this)
        liveDataObserveTextWrapper(viewModel.getTextGoodnight(), textViewTextOfGoodnight, this)

        liveDataObserveViewPagerWrapper(viewModel.getImagesOfLogo(), viewPageOfImageLogo, this, this)
        liveDataObserveViewPagerWrapper(viewModel.getImageOfOscar(), viewPageOfImageOscar, this, this)
        liveDataObserveViewPagerWrapper(viewModel.getImageOfLera(), viewPageOfImageLera, this, this)
        liveDataObserveViewPagerWrapper(viewModel.getImageOfLexa(), viewPageOfImageLexa, this, this)

        findViewById<TabLayout>(R.id.tabDots_for_view_pager_of_image_logo).setupWithViewPager(viewPageOfImageLogo)
        findViewById<TabLayout>(R.id.tabDots_for_view_pager_of_image_oscar).setupWithViewPager(viewPageOfImageOscar)
        findViewById<TabLayout>(R.id.tabDots_for_view_pager_of_image_lera).setupWithViewPager(viewPageOfImageLera)
        findViewById<TabLayout>(R.id.tabDots_for_view_pager_of_image_lexa).setupWithViewPager(viewPageOfImageLexa)
    }

    private suspend fun applyEvents() {
        wrapperDisableSwitchLayout(viewPageOfImageLogo, switchRefreshLayout)
        wrapperDisableSwitchLayout(viewPageOfImageOscar, switchRefreshLayout)
        wrapperDisableSwitchLayout(viewPageOfImageLera, switchRefreshLayout)
        wrapperDisableSwitchLayout(viewPageOfImageLexa, switchRefreshLayout)

        wrapperForSwipeOutViewPager(viewPageOfImageLogo,
            { viewModel.nextPicturesLogo() },
            progressbarViewPagerOfImageLogo,
            { ShowToast.show(this@AboutActivity, "Все фотки логотипов загружены") }, viewModel)
        wrapperForSwipeOutViewPager(viewPageOfImageOscar,
            { viewModel.nextPicturesOscar() },
            progressbarViewPagerOfImageOscar,
            { ShowToast.show(this@AboutActivity, "Все фотки Оскара загружены") }, viewModel)
        wrapperForSwipeOutViewPager(viewPageOfImageLera,
            { viewModel.nextPicturesLera() },
            progressbarViewPagerOfImageLera,
            { ShowToast.show(this@AboutActivity, "Все фотки Леры загружены") }, viewModel)
        wrapperForSwipeOutViewPager(viewPageOfImageLexa,
            { viewModel.nextPicturesLexa() },
            progressbarViewPagerOfImageLexa,
            { ShowToast.show(this@AboutActivity, "Все фотки Лёши загружены") }, viewModel)

        wrapperEditTextView(textViewAboutApelsinka, { viewModel.setTextAboutApelsinka(textViewAboutApelsinka.text.toString()) }, this)
        wrapperEditTextView(textViewTextOfGoodnight, { viewModel.setTextGoodnight(textViewTextOfGoodnight.text.toString()) }, this)
        wrapperEditTextView(aboutApelsinkaTitle, { viewModel.setTextAboutApelsinka(aboutApelsinkaTitle.text.toString()) }, this)
        wrapperEditTextView(aboutOscarTitle, { viewModel.setOscarTitle(aboutOscarTitle.text.toString()) }, this)
        wrapperEditTextView(aboutLeraTitle, { viewModel.setLeraTitle(aboutLeraTitle.text.toString()) }, this)
        wrapperEditTextView(aboutLexaTitle, { viewModel.setLexaTitle(aboutLexaTitle.text.toString()) }, this)

        findViewById<LinearLayout>(R.id.layout_textview_about_apelsinka).setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(textViewAboutApelsinka, this@AboutActivity) {
                    viewModel.setTextAboutApelsinka(textViewAboutApelsinka.text.toString())
                }
            }
        })

        switchRefreshLayout.setOnRefreshListener {
            progressbarViewPagerOfImageLogo.visibility = View.VISIBLE
            progressbarViewPagerOfImageOscar.visibility = View.VISIBLE
            progressbarViewPagerOfImageLera.visibility = View.VISIBLE
            progressbarViewPagerOfImageLexa.visibility = View.VISIBLE
            viewModel.viewModelScope.launch {
                viewModel.updateHandbook()
                viewModel.updatePhotos()
                Handler(Looper.getMainLooper()).post {
                    progressbarViewPagerOfImageLogo.visibility =  View.GONE
                    progressbarViewPagerOfImageOscar.visibility = View.GONE
                    progressbarViewPagerOfImageLera.visibility =  View.GONE
                    progressbarViewPagerOfImageLexa.visibility =  View.GONE
                    switchRefreshLayout.isRefreshing = false
                }
            }
        }
    }

}


