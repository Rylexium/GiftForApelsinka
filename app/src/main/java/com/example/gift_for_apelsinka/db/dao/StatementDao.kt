package com.example.gift_for_apelsinka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gift_for_apelsinka.db.model.Statement

@Dao
interface StatementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatement(statement: Statement)

    @Query("SELECT * FROM statements")
    fun readAllStatements() : LiveData<List<Statement>>
}