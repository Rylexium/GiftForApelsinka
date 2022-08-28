package com.example.gift_for_apelsinka.retrofit.requestmodel.response.pictures

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Pageable {
    @SerializedName("sort")
    @Expose
    var sort: Sort? = null

    @SerializedName("pageNumber")
    @Expose
    var pageNumber: Int? = null

    @SerializedName("pageSize")
    @Expose
    var pageSize: Int? = null

    @SerializedName("offset")
    @Expose
    var offset: Int? = null

    @SerializedName("paged")
    @Expose
    var paged: Boolean? = null

    @SerializedName("unpaged")
    @Expose
    var unpaged: Boolean? = null
}