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
            FieldPhoto(1, sharedPreferences.all["1"].toString(), R.drawable.main_ksixa1),
            FieldPhoto(2, sharedPreferences.all["2"].toString(), R.drawable.main_ksixa2),
            FieldPhoto(3, sharedPreferences.all["3"].toString(), R.drawable.main_ksixa3),
            FieldPhoto(4, sharedPreferences.all["4"].toString(), R.drawable.main_ksixa4),
            FieldPhoto(5, sharedPreferences.all["5"].toString(), R.drawable.main_ksixa5),
            FieldPhoto(6, sharedPreferences.all["6"].toString(), R.drawable.main_ksixa6),
            FieldPhoto(7, sharedPreferences.all["7"].toString(), R.drawable.main_ksixa7),
            FieldPhoto(8, sharedPreferences.all["8"].toString(), R.drawable.main_ksixa8),
            FieldPhoto(9, sharedPreferences.all["9"].toString(), R.drawable.main_ksixa9),
            FieldPhoto(10, sharedPreferences.all["10"].toString(), R.drawable.main_ksixa10),
            FieldPhoto(11, sharedPreferences.all["11"].toString(), R.drawable.main_ksixa11),
            FieldPhoto(12, sharedPreferences.all["12"].toString(), R.drawable.main_ksixa12),
            FieldPhoto(13, sharedPreferences.all["13"].toString(), R.drawable.main_ksixa13),
            FieldPhoto(14, sharedPreferences.all["14"].toString(), R.drawable.main_ksixa14),
            FieldPhoto(15, sharedPreferences.all["15"].toString(), R.drawable.main_ksixa15),
            FieldPhoto(16, sharedPreferences.all["16"].toString(), R.drawable.main_ksixa16))
            .shuffled()
        liveDataPhotosList.value = list
        return liveDataPhotosList
    }
}