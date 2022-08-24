package com.example.gift_for_apelsinka.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "field_photo")
data class FieldPhoto(
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    val picture : String,
    var title : String?,
    val belongs : Int)
