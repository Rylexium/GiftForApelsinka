package com.example.gift_for_apelsinka.retrofit.network.repo

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.google.gson.internal.LinkedTreeMap

interface PicturesRepo {
    suspend fun getAllPictures(page : Int, size : Int) : List<FieldPhoto>
    suspend fun setTitlePicture(id : Int, title : String) : LinkedTreeMap<*, *>

    suspend fun getAllApelsinkaPicture(page : Int, size : Int) : List<FieldPhoto>
    suspend fun getAllOscarPicture(page : Int, size : Int) : List<FieldPhoto>
    suspend fun getAllLeraPicture(page : Int, size : Int) : List<FieldPhoto>
    suspend fun getAllRylexiumPicture(page : Int, size : Int) : List<FieldPhoto>
    suspend fun getAllMainPicture(page : Int, size : Int) : List<FieldPhoto>
    suspend fun getAllLogoPicture(page : Int, size : Int) : List<FieldPhoto>


    suspend fun getAllApelsinkaPicture(page : Int) : List<FieldPhoto>
    suspend fun getAllOscarPicture(page : Int) : List<FieldPhoto>
    suspend fun getAllLeraPicture(page : Int) : List<FieldPhoto>
    suspend fun getAllRylexiumPicture(page : Int) : List<FieldPhoto>
    suspend fun getAllMainPicture(page : Int) : List<FieldPhoto>
    suspend fun getAllLogoPicture(page : Int) : List<FieldPhoto>
}