package com.example.gift_for_apelsinka.retrofit.requestmodel.response.pictures

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FieldPhotoList {
    @SerializedName("picture")
    @Expose
    var picture: Picture? = null

    fun getFieldPhotos() : List<FieldPhoto>? = picture?.content
}