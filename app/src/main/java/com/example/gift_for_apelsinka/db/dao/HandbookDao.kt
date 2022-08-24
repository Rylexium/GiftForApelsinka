package com.example.gift_for_apelsinka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gift_for_apelsinka.db.model.Handbook

@Dao
interface HandbookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHandbook(handbook: Handbook)

    @Query("SELECT * FROM handbook")
    suspend fun readAllHandbook() : List<Handbook>

    @Query("Delete from handbook")
    suspend fun clearAllHandbook()
}