package com.example.gift_for_apelsinka.activity.photo

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gift_for_apelsinka.activity.main.MainViewModel
import com.example.gift_for_apelsinka.cache.defaultPhotosApelsinka
import com.example.gift_for_apelsinka.db.*
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkPictures
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*

class PhotosViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private var liveDataPhotosList: MutableLiveData<List<FieldPhoto>> = MutableLiveData()
    private var isUpdating = false
    companion object {
        private var state: Parcelable? = null
        val KEY_PHOTOS = "PAGE_PHOTOS_APELSINKA"
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
            setScrollState(recv.layoutManager?.onSaveInstanceState())
            list?.get(index)?.title = title
            liveDataPhotosList.value = list!!
            (recv.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(getScrollState())
            recv.adapter?.notifyDataSetChanged()
        }
        if (!hasDB)
            sharedPreferences.edit()
                .putString(list?.get(index)?.id.toString(), title).apply()
        else
            pictureRealization.updateTitleById(id, title)
            NetworkPictures.setTitlePicture(id, title)
    }

    fun getPhotosList(): LiveData<List<FieldPhoto>> = runBlocking {
        if(liveDataPhotosList.value != null) return@runBlocking liveDataPhotosList
        val list = defaultPhotosApelsinka(sharedPreferences)
        val task = async { return@async pictureRealization.apelsinkaPicture() }
        if(task.await().isNotEmpty())
            list.addAll(task.getCompleted())

        liveDataPhotosList.value = list.distinct().shuffled(Random())
        return@runBlocking liveDataPhotosList
    }

    suspend fun updatePhotosList(): List<FieldPhoto>? {
        if(isUpdating) return null
        isUpdating = true
        val picturesApelsinka = NetworkPictures.getAllApelsinkaPicture(0)
        deletePicturesApelsinkaFromDB()
        savePicturesToDB(picturesApelsinka)

        val res = liveDataPhotosList.value as MutableList
        val db = pictureRealization.apelsinkaPicture()
        res.addAll(db)

        liveDataPhotosList.value = res.distinct()
        isUpdating = false
        return liveDataPhotosList.value
    }

    suspend fun nextPhotos() : Boolean {
        var page = sharedPreferences.getInt(KEY_PHOTOS, 0)
        var pictureFromNetwork: List<FieldPhoto>

        while(true) {
            pictureFromNetwork = NetworkPictures.getAllApelsinkaPicture(page)
            val previosSize = pictureRealization.apelsinkaPicture().size
            savePicturesToDB(pictureFromNetwork)
            val nowSize = pictureRealization.apelsinkaPicture().size

            if(pictureFromNetwork.size == 10)
                page += 1

            if(previosSize == nowSize) break //таких нет, надо отобразить
            if(pictureFromNetwork.size < 10) break //пришло меньше 10 -> это конец
        }
        if(pictureFromNetwork.isEmpty()) return false
        sharedPreferences.edit().putInt(KEY_PHOTOS, page).apply()

        val result = liveDataPhotosList.value as MutableList

        result.addAll(pictureRealization.apelsinkaPicture())

        liveDataPhotosList.value = result.distinct()
        if(pictureFromNetwork.size < 10) return false
        return true
    }
}