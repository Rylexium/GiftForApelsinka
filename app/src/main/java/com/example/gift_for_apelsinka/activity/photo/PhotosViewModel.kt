package com.example.gift_for_apelsinka.activity.photo

import android.content.SharedPreferences
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.model.FieldPhoto

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

    fun changePhotoAtIndex(index : Int, title : String) {
        val list = liveDataPhotosList.value
        list?.get(index)?.title = title
        sharedPreferences.edit()
            .putString(list?.get(index)?.id.toString(), title).apply()
        liveDataPhotosList.value = list
    }

    fun getPhotosList(): LiveData<List<FieldPhoto>> {
        if(liveDataPhotosList.value != null) return liveDataPhotosList
        val list = listOf(
            FieldPhoto(1,  R.drawable.main_ksixa1,  sharedPreferences.all["1"].toString(), 0),
            FieldPhoto(2,  R.drawable.main_ksixa2,  sharedPreferences.all["2"].toString(), 0),
            FieldPhoto(3,  R.drawable.main_ksixa3,  sharedPreferences.all["3"].toString(), 0),
            FieldPhoto(4,  R.drawable.main_ksixa4,  sharedPreferences.all["4"].toString(), 0),
            FieldPhoto(5,  R.drawable.main_ksixa5,  sharedPreferences.all["5"].toString(), 0),
            FieldPhoto(6,  R.drawable.main_ksixa6,  sharedPreferences.all["6"].toString(), 0),
            FieldPhoto(7,  R.drawable.main_ksixa7,  sharedPreferences.all["7"].toString(), 0),
            FieldPhoto(8,  R.drawable.main_ksixa8,  sharedPreferences.all["8"].toString(), 0),
            FieldPhoto(9,  R.drawable.main_ksixa9,  sharedPreferences.all["9"].toString(), 0),
            FieldPhoto(10, R.drawable.main_ksixa10, sharedPreferences.all["10"].toString(), 0),
            FieldPhoto(11, R.drawable.main_ksixa11, sharedPreferences.all["11"].toString(), 0),
            FieldPhoto(12, R.drawable.main_ksixa12, sharedPreferences.all["12"].toString(), 0),
            FieldPhoto(13, R.drawable.main_ksixa13, sharedPreferences.all["13"].toString(), 0),
            FieldPhoto(14, R.drawable.main_ksixa14, sharedPreferences.all["14"].toString(), 0),
            FieldPhoto(15, R.drawable.main_ksixa15, sharedPreferences.all["15"].toString(), 0),
            FieldPhoto(16, R.drawable.main_ksixa16, sharedPreferences.all["16"].toString(), 0)
        )
            .shuffled()
        liveDataPhotosList.value = list
        return liveDataPhotosList
    }
}