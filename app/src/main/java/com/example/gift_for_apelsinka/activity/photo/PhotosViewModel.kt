package com.example.gift_for_apelsinka.activity.photo

import android.content.SharedPreferences
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
            FieldPhoto(1, "", R.drawable.main_ksixa),
            FieldPhoto(2, "", R.drawable.main_ksixa1),
            FieldPhoto(3, "", R.drawable.main_ksixa2),
            FieldPhoto(4, "", R.drawable.main_ksixa3),
            FieldPhoto(5, "", R.drawable.main_ksixa4),
            FieldPhoto(6, "", R.drawable.main_ksixa5),
            FieldPhoto(7, "", R.drawable.main_ksixa6),
            FieldPhoto(8, "", R.drawable.main_ksixa7),
            FieldPhoto(9, "", R.drawable.main_ksixa8),
            FieldPhoto(10, "", R.drawable.main_ksixa9),
            FieldPhoto(11, "", R.drawable.main_ksixa10),
            FieldPhoto(12, "", R.drawable.main_ksixa11),
            FieldPhoto(13, "", R.drawable.main_ksixa12),
            FieldPhoto(14, "", R.drawable.main_ksixa13),
            FieldPhoto(15, "", R.drawable.main_ksixa14),
            FieldPhoto(16, "", R.drawable.main_ksixa15),
            FieldPhoto(17, "", R.drawable.main_ksixa16),
            FieldPhoto(18, "", R.drawable.main_ksixa17),
            FieldPhoto(19, "", R.drawable.main_ksixa18),
            FieldPhoto(20, "", R.drawable.main_ksixa19),
            FieldPhoto(21, "", R.drawable.main_ksixa20),
            FieldPhoto(22, "", R.drawable.main_ksixa21),
            FieldPhoto(23, "", R.drawable.main_ksixa22),
            FieldPhoto(24, "", R.drawable.main_ksixa23),
            FieldPhoto(25, "", R.drawable.main_ksixa24),
            FieldPhoto(26, "", R.drawable.main_ksixa25),
            FieldPhoto(27, "", R.drawable.main_ksixa26),
            FieldPhoto(28, "", R.drawable.main_ksixa27),
            FieldPhoto(29, "", R.drawable.main_ksixa28),
            FieldPhoto(30, "", R.drawable.main_ksixa29),
            FieldPhoto(31, "", R.drawable.main_ksixa30),
            FieldPhoto(32, "", R.drawable.main_ksixa31),
            FieldPhoto(33, "", R.drawable.main_ksixa32),
            FieldPhoto(34, "", R.drawable.main_ksixa33),
            FieldPhoto(35, "", R.drawable.main_ksixa34),
            FieldPhoto(36, "", R.drawable.main_ksixa35),
            FieldPhoto(37, "Мини-Ксюша", R.drawable.mini_ksixa),
            FieldPhoto(38, "КИБОРГ-УБИЙЦА... АААААА!!!!", R.drawable.murder_ksixa),
            FieldPhoto(39, "ПОПААВС!!!", R.drawable.popados))
            .shuffled()
        liveDataPhotosList.value = list
        return liveDataPhotosList
    }
}