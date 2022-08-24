package com.example.gift_for_apelsinka.db.repo.fieldphoto

import androidx.lifecycle.LiveData
import com.example.gift_for_apelsinka.db.model.FieldPhoto

interface FieldPhotoRepository {
    suspend fun apelsinkaPicture() : List<FieldPhoto>
    suspend fun oscarPicture() : List<FieldPhoto>
    suspend fun leraPicture() : List<FieldPhoto>
    suspend fun rylexiumPicture() : List<FieldPhoto>
    suspend fun mainPicture() : List<FieldPhoto>
    suspend fun logoPicture() : List<FieldPhoto>

    suspend fun insertFieldPhoto(fieldPhoto: FieldPhoto)
    suspend fun updateTitleById(id : Int, newTitle : String)
    suspend fun deleteAll()
}