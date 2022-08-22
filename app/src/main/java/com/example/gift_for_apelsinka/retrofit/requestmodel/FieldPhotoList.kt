package com.example.gift_for_apelsinka.retrofit.requestmodel

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FieldPhotoList {
    @SerializedName("picture")
    @Expose
    private var list : List<FieldPhoto> = listOf()

    fun getFieldPhotos() : List<FieldPhoto> = list
}