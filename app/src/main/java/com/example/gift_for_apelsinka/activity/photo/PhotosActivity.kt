package com.example.gift_for_apelsinka.activity.photo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.adapter.PhotosAdapter

class PhotosActivity : AppCompatActivity() {
    private lateinit var recv : RecyclerView
    private lateinit var viewModel: PhotosViewModel
    private lateinit var photosAdapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)
        initComponents()
    }

    override fun onResume() {
        super.onResume()
        if (recv.layoutManager != null)
            (recv.layoutManager as LinearLayoutManager) //скролим до нужного момента
                .onRestoreInstanceState(viewModel.getScrollState())
    }

    override fun onPause() {
        super.onPause()
        viewModel.setScrollState(recv.layoutManager?.onSaveInstanceState())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", viewModel.getScrollState().toString())
        viewModel.setScrollState(recv.layoutManager?.onSaveInstanceState())
    }
    private fun initComponents() {
        viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]
        recv = findViewById(R.id.recycler_view_photos)

        viewModel.getPhotosList().observe(this) {
            photosAdapter = PhotosAdapter(this, it)
            recv.adapter = photosAdapter
            recv.layoutManager = LinearLayoutManager(this)
            (recv.layoutManager as LinearLayoutManager) //скролим до нужного момента
                .onRestoreInstanceState(viewModel.getScrollState())
        }

    }


}