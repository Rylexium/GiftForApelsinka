package com.example.gift_for_apelsinka.retrofit.requestmodel

import com.example.gift_for_apelsinka.db.model.Handbook
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HandbookList {
    @SerializedName("handbook")
    @Expose
    private var handbook : List<Handbook> = listOf()

    fun getHandbook() : List<Handbook> = handbook
}