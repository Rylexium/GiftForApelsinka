package com.example.gift_for_apelsinka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gift_for_apelsinka.db.model.Handbook
import com.example.gift_for_apelsinka.db.model.Statement

@Dao
interface HandbookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHandbook(handbook: Handbook)

    @Query("SELECT * FROM handbook")
    fun readAllHandbook() : LiveData<List<Handbook>>
}