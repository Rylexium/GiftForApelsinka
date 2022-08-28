package com.example.gift_for_apelsinka.retrofit.requestmodel.response.pictures

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Content {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("picture")
    @Expose
    var picture: String? = null

    @SerializedName("title")
    @Expose
    var title: Any? = null

    @SerializedName("belongs")
    @Expose
    var belongs: Int? = null
}