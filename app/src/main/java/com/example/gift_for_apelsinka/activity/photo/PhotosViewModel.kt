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
        println("index : $index")
        println("id : ${list?.get(index)?.id}")
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
            FieldPhoto(16, sharedPreferences.all["16"].toString(), R.drawable.main_ksixa16),
            FieldPhoto(17, sharedPreferences.all["17"].toString(), R.drawable.main_ksixa17),
            FieldPhoto(18, sharedPreferences.all["18"].toString(), R.drawable.main_ksixa18),
            FieldPhoto(19, sharedPreferences.all["19"].toString(), R.drawable.main_ksixa19),
            FieldPhoto(20, sharedPreferences.all["20"].toString(), R.drawable.main_ksixa20),
            FieldPhoto(21, sharedPreferences.all["21"].toString(), R.drawable.main_ksixa21),
            FieldPhoto(22, sharedPreferences.all["22"].toString(), R.drawable.main_ksixa22),
            FieldPhoto(23, sharedPreferences.all["23"].toString(), R.drawable.main_ksixa23),
            FieldPhoto(24, sharedPreferences.all["24"].toString(), R.drawable.main_ksixa24),
            FieldPhoto(25, sharedPreferences.all["25"].toString(), R.drawable.main_ksixa25),
            FieldPhoto(26, sharedPreferences.all["26"].toString(), R.drawable.main_ksixa26),
            FieldPhoto(27, sharedPreferences.all["27"].toString(), R.drawable.main_ksixa27),
            FieldPhoto(28, sharedPreferences.all["28"].toString(), R.drawable.main_ksixa28),
            FieldPhoto(29, sharedPreferences.all["29"].toString(), R.drawable.main_ksixa29),
            FieldPhoto(30, sharedPreferences.all["30"].toString(), R.drawable.main_ksixa30),
            FieldPhoto(31, sharedPreferences.all["31"].toString(), R.drawable.main_ksixa31),
            FieldPhoto(32, sharedPreferences.all["32"].toString(), R.drawable.main_ksixa32),
            FieldPhoto(33, sharedPreferences.all["33"].toString(), R.drawable.main_ksixa33),
            FieldPhoto(34, sharedPreferences.all["34"].toString(), R.drawable.main_ksixa34),
            FieldPhoto(35, sharedPreferences.all["35"].toString(), R.drawable.main_ksixa35),
            FieldPhoto(36, sharedPreferences.all["36"].toString(), R.drawable.main_ksixa36),
            FieldPhoto(37, sharedPreferences.getString("37", "Мини-Ксюша").toString(), R.drawable.mini_ksixa),
            FieldPhoto(38, sharedPreferences.getString("38", "КИБОРГ-УБИЙЦА... АААААА!!!!").toString(), R.drawable.murder_ksixa),
            FieldPhoto(39, sharedPreferences.getString("39", "ПОПААВС!!!").toString(), R.drawable.popados))
            .shuffled()
        liveDataPhotosList.value = list
        return liveDataPhotosList
    }
}