package com.example.gift_for_apelsinka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.model.Statements

@Dao
interface FieldPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFieldPhoto(fieldPhoto: FieldPhoto)

    @Query("UPDATE field_photo SET title=:title WHERE id=:id")
    suspend fun updateTitleById(id : Int, title : String)

    @Query("SELECT * FROM field_photo where belongs=:belongs")
    suspend fun readAllFieldPhotoByBelongs(belongs : Int) : List<FieldPhoto>

    @Query("DELETE FROM field_photo WHERE belongs=:belongs")
    suspend fun deleteAllFieldPhotoByBelongs(belongs: Int)

    @Query("Delete FROM field_photo")
    suspend fun deleteAll()
}