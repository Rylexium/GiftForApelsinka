package com.example.gift_for_apelsinka.retrofit.network.repo

import com.example.gift_for_apelsinka.db.model.Statements

interface StatementsRepo {
    suspend fun getStatements(page : Int, size : Int) : List<Statements>
    suspend fun getStatements(page : Int) : List<Statements>
}