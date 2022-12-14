package com.example.gift_for_apelsinka.activity.photo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.adapter.PhotosAdapter
import com.example.gift_for_apelsinka.cache.colorPrimary
import com.example.gift_for_apelsinka.util.DebugFunctions
import com.example.gift_for_apelsinka.util.IP
import com.example.gift_for_apelsinka.util.dialogs.ShowToast
import com.example.gift_for_apelsinka.util.wrapperNothingHappen
import kotlinx.coroutines.launch


class PhotosActivity : AppCompatActivity() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recv : RecyclerView
    private lateinit var viewModel: PhotosViewModel
    private lateinit var progressBar: ProgressBar
    companion object {
        private var updateFlag : Boolean? = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        DebugFunctions.addDebug("PhotosActivity", "onCreate")

        initComponents()
        applyEvents()
    }

    private fun initComponents() {
        DebugFunctions.addDebug("PhotosActivity","initComponents")
        viewModel = ViewModelProvider(this,
            PhotosViewModelFactory(applicationContext.getSharedPreferences("preference_key", Context.MODE_PRIVATE)))[PhotosViewModel::class.java]
        recv = findViewById(R.id.recycler_view_photos)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutPhotos)
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(colorPrimary)
        progressBar = findViewById(R.id.progress_download_photos)
        viewModel.getPhotosList().observe(this) {
            viewModel.setScrollState(recv.layoutManager?.onSaveInstanceState())
            recv.adapter = PhotosAdapter(this@PhotosActivity, it, viewModel, recv)
            recv.layoutManager = LinearLayoutManager(this)
            (recv.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(viewModel.getScrollState())
        }
    }

    private fun applyEvents() {
        DebugFunctions.addDebug("PhotosActivity","applyEvents")
        ViewCompat.setOnApplyWindowInsetsListener(recv) { _, insets ->
            val systemWindowInsets = insets.systemWindowInsets
            recv.updatePadding(
                left = systemWindowInsets.left,
                bottom = systemWindowInsets.bottom + systemWindowInsets.top,
                right = systemWindowInsets.right
            )
            insets
        }
        recv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var isUpdate = false
            var isShowToastDownload = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {

                    if(!IP.isInternetAvailable(this@PhotosActivity)) {
                        swipeRefreshLayout.isRefreshing = false
                        return
                    }
                    wrapperNothingHappen(this@PhotosActivity) {
                        progressBar.visibility = View.GONE
                        isUpdate = false
                        isShowToastDownload = false
                    }
                    if(progressBar.visibility == View.VISIBLE && !isShowToastDownload) { // ???????? ????????????????
                        isShowToastDownload = true
                        ShowToast.show(this@PhotosActivity, "???????????????? ????????????????????")
                        Handler(Looper.getMainLooper()).postDelayed( { isShowToastDownload = false }, 3000)
                        return
                    }

                    if(isUpdate) return

                    progressBar.visibility = View.VISIBLE
                    viewModel.viewModelScope.launch { //?????????????????? ???? ???????? ????????????????
                        isUpdate = true
                        val flag = viewModel.nextPhotos() //????????????????
                        Handler(Looper.getMainLooper()).post { progressBar.visibility = View.GONE }
                        Handler(Looper.getMainLooper()).postDelayed({ isUpdate = false }, 2_000)
                        if(!flag)
                            Handler(Looper.getMainLooper()).post { ShowToast.show(this@PhotosActivity, "?????????????????? ?????? ????????????????????") }
                    }
                }
            }
        })
        swipeRefreshLayout.setOnRefreshListener {
            if(!IP.isInternetAvailable(this@PhotosActivity)) {
                swipeRefreshLayout.isRefreshing = false
                return@setOnRefreshListener
            }
            wrapperNothingHappen(this) {
                swipeRefreshLayout.isRefreshing = false
            }

            val previousFlag = updateFlag
            updateFlag = null
            viewModel.viewModelScope.launch {
                viewModel.updatePhotosList()
                updateFlag = previousFlag
                Handler(Looper.getMainLooper()).post { swipeRefreshLayout.isRefreshing = false }
            }
        }
    }

    override fun onPause() {
        DebugFunctions.addDebug("PhotosActivity","onPause")
        super.onPause()
    }

    override fun onResume() {
        DebugFunctions.addDebug("PhotosActivity","onResume")
        super.onResume()
    }

    override fun onBackPressed() {
        DebugFunctions.addDebug("PhotosActivity","onBackPressed")
        DebugFunctions.sendReport()
        super.onBackPressed()
    }
    override fun onDestroy() {
        DebugFunctions.addDebug("PhotosActivity","onDestroy")
        DebugFunctions.sendReport()
        super.onDestroy()
    }
}