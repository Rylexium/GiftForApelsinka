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
import com.example.gift_for_apelsinka.activity.photo.PhotosViewModel
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.DialogEditText.editTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotosAdapter(
    private val context: Context,
    private val photos: List<FieldPhoto>,
    private val viewModel: PhotosViewModel,
    private val recv: RecyclerView)
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
    private fun isNumeric(str: String) = str.all { it in '0'..'9' }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val newList = photos[position]

        holder.setIsRecyclable(false) // answer RecyclerView showing wrong data when scrolling

        holder.title.text = if(newList.title == "null") "" else newList.title
        holder.title.visibility = if(newList.title == "null" || newList.title == "") View.GONE else View.VISIBLE

        var image: Any?
        CoroutineScope(Dispatchers.IO).launch {
            image = newList.picture
            image = if(isNumeric(newList.picture)) newList.picture.toInt()
                    else ConvertClass.convertStringToBitmap(newList.picture)

            Handler(Looper.getMainLooper()).post {
                Glide.with(context)
                    .load(image)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(holder.photo)
            }
        }

        val hasDB = !isNumeric(newList.picture)
        holder.photo.setOnLongClickListener {
            editTextView(holder.title, context) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.changePhotoAtIndex(position, newList.id, holder.title.text.toString(), hasDB, this@PhotosAdapter.recv)
                }
            }
            true
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemCount(): Int {
        return photos.size
    }
}