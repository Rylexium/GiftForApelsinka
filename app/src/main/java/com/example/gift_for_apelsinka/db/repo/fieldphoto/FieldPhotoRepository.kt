package com.example.gift_for_apelsinka.db.repo.fieldphoto

import androidx.lifecycle.LiveData
import com.example.gift_for_apelsinka.db.model.FieldPhoto

interface FieldPhotoRepository {
    val apelsinkaPicture : LiveData<List<FieldPhoto>>
    val oscarPicture : LiveData<List<FieldPhoto>>
    val leraPicture : LiveData<List<FieldPhoto>>
    val rylexiumPicture : LiveData<List<FieldPhoto>>
    val mainPicture : LiveData<List<FieldPhoto>>
    val logoPicture : LiveData<List<FieldPhoto>>

    suspend fun insertFieldPhoto(fieldPhoto: FieldPhoto)
    suspend fun updateTitleById(id : Int, newTitle : String)
    suspend fun deleteAll()
}