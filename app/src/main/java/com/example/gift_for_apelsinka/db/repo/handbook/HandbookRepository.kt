package com.example.gift_for_apelsinka.db.repo.handbook

import com.example.gift_for_apelsinka.db.model.Handbook

interface HandbookRepository {
    val allHandbook : MutableMap<String, String>
    suspend fun insertHandbook(handbook: Handbook)
    suspend fun clearHandbook()
}