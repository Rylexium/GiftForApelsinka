package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.retrofit.CallbackWithRetry
import com.example.gift_for_apelsinka.retrofit.Services
import com.example.gift_for_apelsinka.retrofit.network.repo.PicturesRepo
import com.example.gift_for_apelsinka.retrofit.requestmodel.response.pictures.FieldPhotoList
import com.example.gift_for_apelsinka.retrofit.requestmodel.NewTitleById
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetworkPictures : PicturesRepo {
    override suspend fun getAllPictures(page : Int, size : Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllPictures(page, size))
    }

    override suspend fun setTitlePicture(id : Int, title : String): LinkedTreeMap<*, *> {
        return suspendCoroutine {
            val call = Services.picturesServiceApi?.setTitlePicture(NewTitleById(id, title))
            call?.enqueue(object : CallbackWithRetry<Any>(call) {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    it.resume(response.body() as LinkedTreeMap<*, *>)
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getAllApelsinkaPicture(page : Int, size : Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllApelsinkaPicture(page, size))
    }

    override suspend fun getAllApelsinkaPicture(page: Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllApelsinkaPicture(page))
    }

    override suspend fun getAllOscarPicture(page : Int, size : Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllOscarPicture(page, size))
    }

    override suspend fun getAllOscarPicture(page: Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllOscarPicture(page))
    }

    override suspend fun getAllLeraPicture(page : Int, size : Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllLeraPicture(page, size))
    }

    override suspend fun getAllLeraPicture(page: Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllLeraPicture(page))
    }

    override suspend fun getAllRylexiumPicture(page : Int, size : Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllRylexiumPicture(page, size))
    }

    override suspend fun getAllRylexiumPicture(page: Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllRylexiumPicture(page))
    }

    override suspend fun getAllMainPicture(page : Int, size : Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllMainPicture(page, size))
    }

    override suspend fun getAllMainPicture(page: Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllMainPicture(page))
    }

    override suspend fun getAllLogoPicture(page : Int, size : Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllLogoPicture(page, size))
    }

    override suspend fun getAllLogoPicture(page: Int): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllLogoPicture(page))
    }

    private suspend fun wrapper(callToNetwork : Call<FieldPhotoList>?) : List<FieldPhoto> {
        if(callToNetwork == null) return emptyList()
        return suspendCoroutine {
            callToNetwork.enqueue(object : Callback<FieldPhotoList> {
                override fun onResponse(call: Call<FieldPhotoList>, response: Response<FieldPhotoList>) {
                    it.resume(response.body()!!.getFieldPhotos() ?: listOf())
                }

                override fun onFailure(call: Call<FieldPhotoList>, t: Throwable) {
                }
            })
        }
    }
}