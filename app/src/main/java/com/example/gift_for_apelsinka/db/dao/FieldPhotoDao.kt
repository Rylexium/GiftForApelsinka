package com.example.gift_for_apelsinka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.model.Statement

@Dao
interface FieldPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFieldPhoto(fieldPhoto: FieldPhoto)

    @Query("SELECT * FROM field_photo where belongs=:belongs")
    fun readAllFieldPhotoByBelongs(belongs : Int) : LiveData<List<FieldPhoto>>
}