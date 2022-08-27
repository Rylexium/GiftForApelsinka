package com.example.gift_for_apelsinka.activity.photo

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.adapter.PhotosAdapter
import com.example.gift_for_apelsinka.util.ShowToast
import kotlinx.coroutines.launch


class PhotosActivity : AppCompatActivity() {
    private lateinit var switchRefreshLayout: SwipeRefreshLayout
    private lateinit var recv : RecyclerView
    private lateinit var viewModel: PhotosViewModel
    private lateinit var progressBar: ProgressBar
    companion object {
        private var updateFlag : Boolean? = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)
        initComponents()
        applyEvents()
    }

    private fun initComponents() {
        viewModel = ViewModelProvider(this,
            PhotosViewModelFactory(applicationContext.getSharedPreferences("preference_key", Context.MODE_PRIVATE)))[PhotosViewModel::class.java]
        recv = findViewById(R.id.recycler_view_photos)
        switchRefreshLayout = findViewById(R.id.swipeRefreshLayoutPhotos)
        progressBar = findViewById(R.id.progress_download_photos)
        viewModel.getPhotosList().observe(this) {
            viewModel.setScrollState(recv.layoutManager?.onSaveInstanceState())
            recv.adapter = PhotosAdapter(applicationContext, it, viewModel, recv)
            recv.layoutManager = LinearLayoutManager(this)
            (recv.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(viewModel.getScrollState())
        }
    }

    private fun applyEvents() {
        recv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if(progressBar.visibility == View.VISIBLE) {
                        ShowToast.show(this@PhotosActivity, "Загружаю фотографии")
                        return
                    }
                    when(updateFlag) {
                        true -> {
                            progressBar.visibility = View.VISIBLE
                            updateFlag = false
                            viewModel.viewModelScope.launch {
                                viewModel.updatePhotosList()
                                progressBar.visibility = View.GONE
                            }
                        }
                        false -> {
                            ShowToast.show(this@PhotosActivity, "Загружены все фотографии")
                            updateFlag = null
                            Handler(Looper.getMainLooper()).postDelayed({
                                updateFlag = false
                            }, 1_000)
                        }
                        else -> {}
                    }
                }
            }
        })
        switchRefreshLayout.setOnRefreshListener {
            val previousFlag = updateFlag
            updateFlag = null
            viewModel.viewModelScope.launch {
                viewModel.updatePhotosList()
                updateFlag = previousFlag
                Handler(Looper.getMainLooper()).post { switchRefreshLayout.isRefreshing = false }
            }
        }
    }
}