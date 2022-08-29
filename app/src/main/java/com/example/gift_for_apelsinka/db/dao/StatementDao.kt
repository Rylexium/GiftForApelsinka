package com.example.gift_for_apelsinka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gift_for_apelsinka.db.model.Statements

@Dao
interface StatementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatement(statement: Statements)

    @Query("SELECT * FROM statements")
    suspend fun readAllStatements() : List<Statements>

    @Query("DELETE from statements")
    suspend fun deleteAll()

    @Query("Delete from statements where id=:id")
    suspend fun deleteById(id : Int)
}