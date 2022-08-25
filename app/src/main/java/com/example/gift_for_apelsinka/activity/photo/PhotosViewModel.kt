package com.example.gift_for_apelsinka.activity.photo

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gift_for_apelsinka.cache.defaultPhotosApelsinka
import com.example.gift_for_apelsinka.db.deleteAll
import com.example.gift_for_apelsinka.db.deletePicturesApelsinkaFromDB
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.pictureRealization
import com.example.gift_for_apelsinka.db.savePicturesToDB
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkPictures
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class PhotosViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private var liveDataPhotosList: MutableLiveData<List<FieldPhoto>> = MutableLiveData()

    companion object {
        private var state: Parcelable? = null
    }
    fun getScrollState(): Parcelable? {
        return state
    }

    fun setScrollState(stateValue: Parcelable?) {
        state = stateValue
    }

    suspend fun changePhotoAtIndex(index : Int, id : Int, title : String, hasDB : Boolean, recv : RecyclerView) {
        val list = liveDataPhotosList.value
        Handler(Looper.getMainLooper()).post {
            list?.get(index)?.title = title
            liveDataPhotosList.value = list!!
            (recv.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(getScrollState())
        }
        if (hasDB)
            sharedPreferences.edit()
                .putString(list?.get(index)?.id.toString(), title).apply()
        else
            pictureRealization.updateTitleById(id, title)
            NetworkPictures.setTitlePicture(id, title)
    }

    fun getPhotosList(): LiveData<List<FieldPhoto>> = runBlocking {
        if(liveDataPhotosList.value != null) return@runBlocking liveDataPhotosList
        val list = defaultPhotosApelsinka(sharedPreferences)
        val task = async { pictureRealization.apelsinkaPicture() }
        if(task.await().isNotEmpty())
            list.addAll(task.getCompleted())

        liveDataPhotosList.value = list.distinct()
        return@runBlocking liveDataPhotosList
    }

    suspend fun updatePhotosList(): List<FieldPhoto>? {
        val picturesApelsinka = NetworkPictures.getAllApelsinkaPicture()
        deletePicturesApelsinkaFromDB()
        savePicturesToDB(picturesApelsinka)

        val res = liveDataPhotosList.value as MutableList
        val db = pictureRealization.apelsinkaPicture()
        res.addAll(db)

        liveDataPhotosList.value = res.distinct()
        return liveDataPhotosList.value
    }
}