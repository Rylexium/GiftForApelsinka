package com.example.gift_for_apelsinka.retrofit.model

import com.example.gift_for_apelsinka.db.model.Statements
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StatementsList {
    @SerializedName("statements")
    @Expose
    private var statements : List<Statements> = listOf()

    fun getStatements() : List<Statements> = statements
}