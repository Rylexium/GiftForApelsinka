package com.example.gift_for_apelsinka.db.repo.fieldphoto

import com.example.gift_for_apelsinka.db.dao.FieldPhotoDao
import com.example.gift_for_apelsinka.db.model.FieldPhoto

class FieldPhotoRealization(private val fieldPhotoDao: FieldPhotoDao) : FieldPhotoRepository {
    override suspend fun apelsinkaPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(1)
    }

    override suspend fun oscarPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(2)
    }

    override suspend fun leraPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(3)
    }

    override suspend fun rylexiumPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(4)
    }

    override suspend fun mainPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(5)
    }

    override suspend fun logoPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(6)
    }

    override suspend fun insertFieldPhoto(fieldPhoto: FieldPhoto) {
        fieldPhotoDao.insertFieldPhoto(fieldPhoto)
    }

    override suspend fun updateTitleById(id: Int, newTitle : String) {
        fieldPhotoDao.updateTitleById(id, newTitle)
    }

    override suspend fun deleteAll() {
        fieldPhotoDao.deleteAll()
    }
}