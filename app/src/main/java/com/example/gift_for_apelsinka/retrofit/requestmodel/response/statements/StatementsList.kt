package com.example.gift_for_apelsinka.retrofit.requestmodel.response.statements

import com.example.gift_for_apelsinka.db.model.Statements
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StatementsList {
    @SerializedName("statements")
    @Expose
    private var statementJSON : StatementJSON? = null

    fun getStatements() : List<Statements>? = statementJSON?.content
}