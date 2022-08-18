package com.example.gift_for_apelsinka.activity.photo.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.model.FieldPhoto
import com.example.gift_for_apelsinka.util.ConvertClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotosAdapter(
    private val context: Context,
    private val photos: List<FieldPhoto>)
    : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    inner class PhotosViewHolder(v : View) : RecyclerView.ViewHolder(v) {
        val photo = v.findViewById<ImageView>(R.id.photo_of_apelsinka)
        val title = v.findViewById<TextView>(R.id.title_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.field_of_photo, parent, false)

        return PhotosViewHolder(v)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val newList = photos[position]
        holder.title.text = newList.body

        var image: Any?
        CoroutineScope(Dispatchers.IO).launch {
            image = if (newList.drawable is Int) context.resources.getDrawable(newList.drawable)
                    else ConvertClass.convertStringToBitmap(newList.drawable as String)
            Handler(Looper.getMainLooper()).post {
                Glide.with(context)
                    .load(image)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(holder.photo)
            }
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}