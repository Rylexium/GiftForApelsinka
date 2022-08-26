package com.example.gift_for_apelsinka.db.repo.fieldphoto

import com.example.gift_for_apelsinka.db.dao.FieldPhotoDao
import com.example.gift_for_apelsinka.db.model.FieldPhoto

class FieldPhotoRealization(private val fieldPhotoDao: FieldPhotoDao) : FieldPhotoRepository {
    private val APELSINKA = 1
    private val OSCAR = 2
    private val LERA = 3
    private val RYLELXIUM = 4
    private val MAIN = 5
    private val LOGO = 6

    override suspend fun apelsinkaPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(APELSINKA)
    }

    override suspend fun oscarPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(OSCAR)
    }

    override suspend fun leraPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(LERA)
    }

    override suspend fun rylexiumPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(RYLELXIUM)
    }

    override suspend fun mainPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(MAIN)
    }

    override suspend fun logoPicture(): List<FieldPhoto> {
        return fieldPhotoDao.readAllFieldPhotoByBelongs(LOGO)
    }

    override suspend fun insertFieldPhoto(fieldPhoto: FieldPhoto) {
        fieldPhotoDao.insertFieldPhoto(fieldPhoto)
    }

    override suspend fun updateTitleById(id: Int, newTitle : String) {
        fieldPhotoDao.updateTitleById(id, newTitle)
    }

    override suspend fun deleteApelsinkaPicture() {
        fieldPhotoDao.deleteAllFieldPhotoByBelongs(APELSINKA)
    }

    override suspend fun deleteOscarPicture() {
        fieldPhotoDao.deleteAllFieldPhotoByBelongs(OSCAR)
    }

    override suspend fun deleteLeraPicture() {
        fieldPhotoDao.deleteAllFieldPhotoByBelongs(LERA)
    }

    override suspend fun deleteRylexiumPicture() {
        fieldPhotoDao.deleteAllFieldPhotoByBelongs(RYLELXIUM)
    }

    override suspend fun deleteMainPicture() {
        fieldPhotoDao.deleteAllFieldPhotoByBelongs(MAIN)
    }

    override suspend fun deleteLogoPicture() {
        fieldPhotoDao.deleteAllFieldPhotoByBelongs(LOGO)
    }

    override suspend fun deleteAll() {
        fieldPhotoDao.deleteAll()
    }
}