package com.example.gift_for_apelsinka.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statements")
data class Statement(
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    val text : String,
    val author : String)
