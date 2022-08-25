package com.example.gift_for_apelsinka.activity.photo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.adapter.PhotosAdapter
import kotlinx.coroutines.launch

class PhotosActivity : AppCompatActivity() {
    private lateinit var recv : RecyclerView
    private lateinit var viewModel: PhotosViewModel
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutPhotos)
        viewModel.getPhotosList().observe(this) {
            photosAdapter = PhotosAdapter(this, it, viewModel, recv)
            recv.adapter = photosAdapter
            recv.layoutManager = LinearLayoutManager(this)
        }
    }
    private fun applyEvents() {
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.viewModelScope.launch {
                viewModel.updatePhotosList()
                Handler(Looper.getMainLooper()).post { swipeRefreshLayout.isRefreshing = false }
            }
        }
    }
}