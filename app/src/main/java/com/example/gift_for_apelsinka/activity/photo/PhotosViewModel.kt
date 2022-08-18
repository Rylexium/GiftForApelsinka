package com.example.gift_for_apelsinka.activity.photo

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.activity.photo.model.FieldPhoto

class PhotosViewModel : ViewModel() {
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

    fun getPhotosList(): LiveData<List<FieldPhoto>> {
        if(liveDataPhotosList.value != null) return liveDataPhotosList
        val list = listOf(
            FieldPhoto(1, "fas", R.drawable.developer),
            FieldPhoto(2, "fafdasfs", R.drawable.apelsinka),
            FieldPhoto(3, "dasg", R.drawable.cat3),
            FieldPhoto(4, "sd", R.drawable.cat1),
            FieldPhoto(5, "fasgasdf", R.drawable.developer),
            FieldPhoto(6, "z124", R.drawable.apelsinka),
            FieldPhoto(7, "42w1", R.drawable.apelsinka),
            FieldPhoto(8, "fa532s", R.drawable.cat3),
            FieldPhoto(9, "532", R.drawable.cat3)
        )
        liveDataPhotosList.value = list
        return liveDataPhotosList
    }
}