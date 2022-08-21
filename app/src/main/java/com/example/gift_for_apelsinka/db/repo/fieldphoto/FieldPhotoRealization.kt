package com.example.gift_for_apelsinka.db.repo.fieldphoto

import androidx.lifecycle.LiveData
import com.example.gift_for_apelsinka.db.dao.FieldPhotoDao
import com.example.gift_for_apelsinka.db.model.FieldPhoto

class FieldPhotoRealization(private val fieldPhotoDao: FieldPhotoDao) : FieldPhotoRepository {
    override val apelsinkaPicture: LiveData<List<FieldPhoto>>
        get()  = fieldPhotoDao.readAllFieldPhotoByBelongs(1)

    override val oscarPicture: LiveData<List<FieldPhoto>>
        get() = fieldPhotoDao.readAllFieldPhotoByBelongs(2)

    override val leraPicture: LiveData<List<FieldPhoto>>
        get() = fieldPhotoDao.readAllFieldPhotoByBelongs(3)

    override val rylexiumPicture: LiveData<List<FieldPhoto>>
        get() = fieldPhotoDao.readAllFieldPhotoByBelongs(4)

    override val mainPicture: LiveData<List<FieldPhoto>>
        get() = fieldPhotoDao.readAllFieldPhotoByBelongs(5)

    override val logoPicture: LiveData<List<FieldPhoto>>
        get() = fieldPhotoDao.readAllFieldPhotoByBelongs(6)

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