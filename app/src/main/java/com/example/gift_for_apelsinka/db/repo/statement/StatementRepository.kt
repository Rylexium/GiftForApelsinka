package com.example.gift_for_apelsinka.db.repo.statement

import androidx.lifecycle.LiveData
import com.example.gift_for_apelsinka.db.model.Statements

interface StatementRepository {
    suspend fun getAll(): List<Statements>
    suspend fun insertStatement(statement: Statements)
    suspend fun clearStatement()
}