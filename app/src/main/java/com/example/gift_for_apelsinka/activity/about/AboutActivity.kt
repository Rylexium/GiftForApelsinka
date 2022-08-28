package com.example.gift_for_apelsinka.activity.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.about.adapter.ImageViewOfPersonPageAdapter
import com.example.gift_for_apelsinka.cache.staticHandbook
import com.example.gift_for_apelsinka.util.DialogEditText.editTextView
import com.example.gift_for_apelsinka.util.DoubleClickListener
import com.example.gift_for_apelsinka.util.InitView.enableDisableSwipeRefresh
import com.example.gift_for_apelsinka.util.InitView.initViewPager
import com.example.gift_for_apelsinka.util.InitView.setImageWithCircle
import com.example.gift_for_apelsinka.util.views.ImageViewPager
import kotlinx.coroutines.launch

class AboutActivity : AppCompatActivity() {
    private lateinit var switchRefreshLayout : SwipeRefreshLayout
    private lateinit var viewModel: AboutViewModel
    private lateinit var viewPageOfImageOscar : ImageViewPager
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
        initDataComponents()
        applyEvents()
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
    }

    private fun initDataComponents() {
        setImageWithCircle(R.drawable.mouse_of_apelsinka, findViewById(R.id.mouse_of_apelsinka), this)

        viewModel.handbook = staticHandbook
        liveDataObserveTextWrapper(viewModel.getApelsinkaTitle(), aboutApelsinkaTitle)
        liveDataObserveTextWrapper(viewModel.getTextAboutApelsinka(), textViewAboutApelsinka)
        liveDataObserveTextWrapper(viewModel.getOscarTitle(), aboutOscarTitle)
        liveDataObserveTextWrapper(viewModel.getLeraTitle(), aboutLeraTitle)
        liveDataObserveTextWrapper(viewModel.getLexaTitle(), aboutLexaTitle)
        liveDataObserveTextWrapper(viewModel.getTextGoodnight(), textViewTextOfGoodnight)

        liveDataObserveViewPagerWrapper(viewModel.getImagesOfLogo(), viewPageOfImageLogo)
        liveDataObserveViewPagerWrapper(viewModel.getImageOfOscar(), viewPageOfImageOscar)
        liveDataObserveViewPagerWrapper(viewModel.getImageOfLera(), viewPageOfImageLera)
        liveDataObserveViewPagerWrapper(viewModel.getImageOfLexa(), viewPageOfImageLexa)
    }

    private fun liveDataObserveTextWrapper(liveData: MutableLiveData<String>, textView: TextView) {
        liveData.observe(this) {
            textView.text = it
        }
    }
    private fun liveDataObserveViewPagerWrapper(liveData: MutableLiveData<List<Any>>, viewPager: ViewPager) {
        liveData.observe(this) {
            initViewPager(viewPager, 65, ImageViewOfPersonPageAdapter(this, it))
        }
    }

    private fun applyEvents() {
        viewPageOfImageLogo.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, v: Float, i1: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                enableDisableSwipeRefresh(switchRefreshLayout, state == ViewPager.SCROLL_STATE_IDLE)
            }
        })
        viewPageOfImageOscar.setOnSwipeOutListener(object : ImageViewPager.OnSwipeOutListener {
            override fun onSwipeOutAtStart() {
                Log.e("oscar", "start")
            }

            override fun onSwipeOutAtEnd() {
                Log.e("oscar", "start")
            }

        })
        viewPageOfImageOscar.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, v: Float, i1: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                enableDisableSwipeRefresh(switchRefreshLayout, state == ViewPager.SCROLL_STATE_IDLE)
            }
        })
        viewPageOfImageLera.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, v: Float, i1: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                enableDisableSwipeRefresh(switchRefreshLayout, state == ViewPager.SCROLL_STATE_IDLE)
            }
        })
        viewPageOfImageLexa.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, v: Float, i1: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {
                enableDisableSwipeRefresh(switchRefreshLayout, state == ViewPager.SCROLL_STATE_IDLE)
            }
        })
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
        switchRefreshLayout.setOnRefreshListener {
            viewModel.viewModelScope.launch {
                viewModel.updateHandbook()
                viewModel.updatePhotos()
                Handler(Looper.getMainLooper()).post { switchRefreshLayout.isRefreshing = false }
            }
        }
    }
}


