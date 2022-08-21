package com.example.gift_for_apelsinka.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "handbook")
data class Handbook(
    @PrimaryKey(autoGenerate = false)
    val key : String,
    val value : String)
