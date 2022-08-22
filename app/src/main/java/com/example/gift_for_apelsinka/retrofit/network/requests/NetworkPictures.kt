package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.retrofit.Services
import com.example.gift_for_apelsinka.retrofit.network.repo.PicturesRepo
import com.example.gift_for_apelsinka.retrofit.requestmodel.FieldPhotoList
import com.example.gift_for_apelsinka.retrofit.requestmodel.NewTitleById
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetworkPictures : PicturesRepo {
    override suspend fun getAllPictures(): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllPictures())
    }

    override suspend fun setTitlePicture(id : Int, title : String): LinkedTreeMap<*, *> {
        return suspendCoroutine {
            val call = Services.picturesServiceApi?.setTitlePicture(NewTitleById(id, title))
            call?.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    it.resume(response.body() as LinkedTreeMap<*, *>)
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        }
    }

    override suspend fun getAllApelsinkaPicture(): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllApelsinkaPicture())
    }

    override suspend fun getAllOscarPicture(): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllOscarPicture())
    }

    override suspend fun getAllLeraPicture(): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllLeraPicture())
    }

    override suspend fun getAllRylexiumPicture(): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllRylexiumPicture())
    }

    override suspend fun getAllMainPicture(): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllMainPicture())
    }

    override suspend fun getAllLogoPicture(): List<FieldPhoto> {
        return wrapper(Services.picturesServiceApi?.getAllLogoPicture())
    }

    private suspend fun wrapper(callToNetwork : Call<FieldPhotoList>?) : List<FieldPhoto> {
        if(callToNetwork == null) return emptyList()
        return suspendCoroutine {
            callToNetwork.enqueue(object : Callback<FieldPhotoList>{
                override fun onResponse(call: Call<FieldPhotoList>, response: Response<FieldPhotoList>) {
                    it.resume(response.body()!!.getFieldPhotos())
                }

                override fun onFailure(call: Call<FieldPhotoList>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        }
    }
}