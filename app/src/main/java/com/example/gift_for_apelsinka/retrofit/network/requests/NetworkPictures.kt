package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.retrofit.network.repo.PicturesRepo
import com.google.gson.internal.LinkedTreeMap

object NetworkPictures : PicturesRepo {
    override suspend fun getAllPictures(): List<FieldPhoto> {
        TODO("Not yet implemented")
    }

    override suspend fun setTitlePicture(): LinkedTreeMap<*, *> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllApelsinkaPicture(): List<FieldPhoto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllOscarPicture(): List<FieldPhoto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllLeraPicture(): List<FieldPhoto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllRylexiumPicture(): List<FieldPhoto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMainPicture(): List<FieldPhoto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllLogoPicture(): List<FieldPhoto> {
        TODO("Not yet implemented")
    }

}