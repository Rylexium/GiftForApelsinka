package com.example.gift_for_apelsinka.activity.photo.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.PhotosViewModel
import com.example.gift_for_apelsinka.activity.photo.model.FieldPhoto
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.DialogEditText
import com.example.gift_for_apelsinka.util.DialogEditText.editTextView
import com.example.gift_for_apelsinka.util.InitView.setImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotosAdapter(
    private val context: Context,
    private val photos: List<FieldPhoto>,
    private val viewModel : PhotosViewModel,
    private val recv : RecyclerView)
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

        holder.title.text = if(newList.title == "null") "" else newList.title
        holder.title.visibility = if(newList.title == "null" || newList.title == "") View.GONE else View.VISIBLE

        setImage(newList.drawable as Int, holder.photo, context)
        var image: Any?
        CoroutineScope(Dispatchers.IO).launch {
            image = if (newList.drawable is Int) newList.drawable
                    else ConvertClass.convertStringToBitmap(newList.drawable as String)
            Handler(Looper.getMainLooper()).post {
                Glide.with(context)
                    .load(image)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(holder.photo)
            }
        }

        holder.photo.setOnLongClickListener {
            editTextView(holder.title, context) {
                viewModel.setScrollState(recv.layoutManager?.onSaveInstanceState())
                viewModel.changePhotoAtIndex(position, holder.title.text.toString())
                (recv.layoutManager as LinearLayoutManager)
                    .onRestoreInstanceState(viewModel.getScrollState())
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}