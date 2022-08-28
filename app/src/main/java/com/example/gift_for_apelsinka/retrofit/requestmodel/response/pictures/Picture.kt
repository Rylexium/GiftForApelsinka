package com.example.gift_for_apelsinka.retrofit.requestmodel.response.pictures

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Picture {
    @SerializedName("content")
    @Expose
    var content: List<FieldPhoto> = listOf()

    @SerializedName("pageable")
    @Expose
    var pageable: Pageable? = null

    @SerializedName("last")
    @Expose
    var last: Boolean? = null

    @SerializedName("totalPages")
    @Expose
    var totalPages: Int? = null

    @SerializedName("totalElements")
    @Expose
    var totalElements: Int? = null

    @SerializedName("sort")
    @Expose
    var sort: Sort__1? = null

    @SerializedName("first")
    @Expose
    var first: Boolean? = null

    @SerializedName("number")
    @Expose
    var number: Int? = null

    @SerializedName("numberOfElements")
    @Expose
    var numberOfElements: Int? = null

    @SerializedName("size")
    @Expose
    var size: Int? = null

    @SerializedName("empty")
    @Expose
    var empty: Boolean? = null
}