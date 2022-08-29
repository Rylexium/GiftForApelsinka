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
import com.example.gift_for_apelsinka.cache.defaultListOfMainPictures
import com.example.gift_for_apelsinka.cache.defaultPhotosApelsinka
import com.example.gift_for_apelsinka.db.*
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkPictures
import com.example.gift_for_apelsinka.util.wrapperNextPictures
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*

class PhotosViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private var liveDataPhotosList: MutableLiveData<List<FieldPhoto>> = MutableLiveData()
    private var isUpdating = false
    companion object {
        private var state: Parcelable? = null
        private var pageOfApelsinka = 0
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
        val picturesApelsinka = NetworkPictures.getAllApelsinkaPicture(0, 100)
        deletePicturesApelsinkaFromDB()
        savePicturesToDB(picturesApelsinka)

        val res = liveDataPhotosList.value as MutableList
        val db = pictureRealization.apelsinkaPicture()
        res.addAll(db)
        pageOfApelsinka = db.size / 10

        liveDataPhotosList.value = res.distinct()
        isUpdating = false
        return liveDataPhotosList.value
    }

    suspend fun nextPhotos() : Boolean {
        var picturesFromNetwork : List<FieldPhoto>

        var picturesFromBD : List<FieldPhoto>

        while(true) {
            picturesFromNetwork = NetworkPictures.getAllApelsinkaPicture(pageOfApelsinka)
            val previosSize = pictureRealization.apelsinkaPicture().size
            savePicturesToDB(picturesFromNetwork.shuffled())
            picturesFromBD = pictureRealization.apelsinkaPicture()

            if(picturesFromNetwork.size == 10)
                pageOfApelsinka += 1

            if(previosSize != picturesFromBD.size) break //таких нет, надо отобразить
            if(picturesFromNetwork.size < 10) break //пришло меньше 10 -> это конец
        }
        if(picturesFromNetwork.isEmpty()) return false

        val result = liveDataPhotosList.value as MutableList

        if(picturesFromNetwork.map { it.id }.subtract(picturesFromBD.map { it.id }.toSet()).isNotEmpty() ||
            result.subtract(picturesFromNetwork.toSet()).size == defaultPhotosApelsinka(sharedPreferences).size) {
            result.addAll(picturesFromBD)
            liveDataPhotosList.value = result.distinct()
        }
        if(picturesFromNetwork.size < 10) return false
        return true
    }
}