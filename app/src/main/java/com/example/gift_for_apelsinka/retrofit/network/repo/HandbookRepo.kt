package com.example.gift_for_apelsinka.retrofit.network.repo

import com.example.gift_for_apelsinka.db.model.Handbook
import com.google.gson.internal.LinkedTreeMap

interface HandbookRepo {
    suspend fun getHandbook() : Map<String, String>
    suspend fun getValueByKey(key : String) : Handbook?
    suspend fun postHandbook(key: String, value : String) : LinkedTreeMap<*, *>
}