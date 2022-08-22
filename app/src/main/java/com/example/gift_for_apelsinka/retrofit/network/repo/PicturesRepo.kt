package com.example.gift_for_apelsinka.retrofit.network.repo

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.google.gson.internal.LinkedTreeMap

interface PicturesRepo {
    suspend fun getAllPictures() : List<FieldPhoto>
    suspend fun setTitlePicture() : LinkedTreeMap<*, *>
    suspend fun getAllApelsinkaPicture() : List<FieldPhoto>
    suspend fun getAllOscarPicture() : List<FieldPhoto>
    suspend fun getAllLeraPicture() : List<FieldPhoto>
    suspend fun getAllRylexiumPicture() : List<FieldPhoto>
    suspend fun getAllMainPicture() : List<FieldPhoto>
    suspend fun getAllLogoPicture() : List<FieldPhoto>
}