package com.example.gift_for_apelsinka.retrofit.requestmodel.response.pictures

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Sort__1 {
    @SerializedName("sorted")
    @Expose
    var sorted: Boolean? = null

    @SerializedName("unsorted")
    @Expose
    var unsorted: Boolean? = null

    @SerializedName("empty")
    @Expose
    var empty: Boolean? = null
}