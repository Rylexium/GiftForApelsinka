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
import com.example.gift_for_apelsinka.cache.colorPrimary
import com.example.gift_for_apelsinka.cache.staticHandbook
import com.example.gift_for_apelsinka.util.*
import com.example.gift_for_apelsinka.util.InitView.setImageWithCircle
import com.example.gift_for_apelsinka.util.dialogs.DialogEditText.editTextView
import com.example.gift_for_apelsinka.util.dialogs.ShowToast
import com.example.gift_for_apelsinka.util.listener.DoubleClickListener
import com.example.gift_for_apelsinka.util.views.ImageViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class AboutActivity : AppCompatActivity() {
    private lateinit var swipeRefreshLayout : SwipeRefreshLayout
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
        DebugFunctions.addDebug("AboutActivity","onCreate")
        initComponents()
        initDataComponents()
        viewModel.viewModelScope.launch { applyEvents() }
    }

    private fun initComponents() {
        DebugFunctions.addDebug("AboutActivity","onCreate")
        viewModel = ViewModelProvider(this)[AboutViewModel::class.java]
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutAbout)
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
        DebugFunctions.addDebug("AboutActivity","initDataComponents")
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
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorPrimary)
    }

    private suspend fun applyEvents() {
        DebugFunctions.addDebug("AboutActivity","applyEvents")
        wrapperDisableSwitchLayout(viewPageOfImageLogo, swipeRefreshLayout)
        wrapperDisableSwitchLayout(viewPageOfImageOscar, swipeRefreshLayout)
        wrapperDisableSwitchLayout(viewPageOfImageLera, swipeRefreshLayout)
        wrapperDisableSwitchLayout(viewPageOfImageLexa, swipeRefreshLayout)

        wrapperForSwipeOutViewPager(viewPageOfImageLogo,
            { viewModel.nextPicturesLogo(this) },
            progressbarViewPagerOfImageLogo,
            { ShowToast.show(this@AboutActivity, "Все фотки логотипов загружены") }, viewModel, this)
        wrapperForSwipeOutViewPager(viewPageOfImageOscar,
            { viewModel.nextPicturesOscar(this) },
            progressbarViewPagerOfImageOscar,
            { ShowToast.show(this@AboutActivity, "Все фотки Оскара загружены") }, viewModel, this)
        wrapperForSwipeOutViewPager(viewPageOfImageLera,
            { viewModel.nextPicturesLera(this) },
            progressbarViewPagerOfImageLera,
            { ShowToast.show(this@AboutActivity, "Все фотки Леры загружены") }, viewModel, this)
        wrapperForSwipeOutViewPager(viewPageOfImageLexa,
            { viewModel.nextPicturesLexa(this) },
            progressbarViewPagerOfImageLexa,
            { ShowToast.show(this@AboutActivity, "Все фотки Лёши загружены") }, viewModel, this)

        wrapperEditTextView(textViewAboutApelsinka, { viewModel.setTextAboutApelsinka(textViewAboutApelsinka.text.toString()) }, this)
        wrapperEditTextView(textViewTextOfGoodnight, { viewModel.setTextGoodnight(textViewTextOfGoodnight.text.toString()) }, this)
        wrapperEditTextView(aboutApelsinkaTitle, { viewModel.setTextAboutApelsinka(aboutApelsinkaTitle.text.toString()) }, this)
        wrapperEditTextView(aboutOscarTitle, { viewModel.setOscarTitle(aboutOscarTitle.text.toString()) }, this)
        wrapperEditTextView(aboutLeraTitle, { viewModel.setLeraTitle(aboutLeraTitle.text.toString()) }, this)
        wrapperEditTextView(aboutLexaTitle, { viewModel.setLexaTitle(aboutLexaTitle.text.toString()) }, this)

        findViewById<LinearLayout>(R.id.layout_textview_about_apelsinka).setOnClickListener(object : DoubleClickListener(){
            override fun onDoubleClick() {
                editTextView(textViewAboutApelsinka, applicationContext) {
                    viewModel.setTextAboutApelsinka(textViewAboutApelsinka.text.toString())
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            if(!IP.isInternetAvailable(this@AboutActivity)) {
                swipeRefreshLayout.isRefreshing = false
                return@setOnRefreshListener
            }

            wrapperNothingHappen(this) {
                progressbarViewPagerOfImageLogo.visibility =  View.GONE
                progressbarViewPagerOfImageOscar.visibility = View.GONE
                progressbarViewPagerOfImageLera.visibility =  View.GONE
                progressbarViewPagerOfImageLexa.visibility =  View.GONE
                swipeRefreshLayout.isRefreshing = false
            }

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
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    override fun onPause() {
        DebugFunctions.addDebug("AboutActivity","onPause")
        super.onPause()
    }

    override fun onResume() {
        DebugFunctions.addDebug("AboutActivity","onResume")
        super.onResume()
    }

    override fun onBackPressed() {
        DebugFunctions.addDebug("AboutActivity","onBackPressed")
        DebugFunctions.sendReport()
        super.onBackPressed()
    }
    override fun onDestroy() {
        DebugFunctions.addDebug("AboutActivity","onDestroy")
        DebugFunctions.sendReport()
        super.onDestroy()
    }
}


