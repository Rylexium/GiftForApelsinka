package com.example.gift_for_apelsinka.db.repo.handbook

import com.example.gift_for_apelsinka.db.dao.HandbookDao
import com.example.gift_for_apelsinka.db.model.Handbook

class HandbookRealization(private val handbookDao: HandbookDao) : HandbookRepository {
    override val allHandbook: MutableMap<String, String>
        get() {
            val list = handbookDao.readAllHandbook().value
            val mutableMap = mutableMapOf<String, String>()
            for((key, value) in list!!)
                mutableMap[key] = value
            return mutableMap
        }

    override suspend fun insertHandbook(handbook: Handbook) {
        handbookDao.insertHandbook(handbook)
    }

    override suspend fun clearHandbook() {
        handbookDao.clearAllHandbook()
    }

}