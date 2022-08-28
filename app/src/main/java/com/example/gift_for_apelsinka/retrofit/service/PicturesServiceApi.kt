package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.retrofit.requestmodel.response.pictures.FieldPhotoList
import com.example.gift_for_apelsinka.retrofit.requestmodel.NewTitleById
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PicturesServiceApi {
    @GET("pictures")
    fun getAllPictures(@Query("page") page : Int, @Query("size") size : Int): Call<FieldPhotoList>

    @POST("pictures")
    fun setTitlePicture(@Body newTitleById: NewTitleById) : Call<Any>

    @GET("pictures/apelsinka")
    fun getAllApelsinkaPicture(@Query("page") page : Int, @Query("size") size : Int) : Call<FieldPhotoList>

    @GET("pictures/oscar")
    fun getAllOscarPicture(@Query("page") page : Int, @Query("size") size : Int) : Call<FieldPhotoList>

    @GET("pictures/lera")
    fun getAllLeraPicture(@Query("page") page : Int, @Query("size") size : Int) : Call<FieldPhotoList>

    @GET("pictures/rylexium")
    fun getAllRylexiumPicture(@Query("page") page : Int, @Query("size") size : Int) : Call<FieldPhotoList>

    @GET("pictures/main")
    fun getAllMainPicture(@Query("page") page : Int, @Query("size") size : Int) : Call<FieldPhotoList>

    @GET("pictures/logo")
    fun getAllLogoPicture(@Query("page") page : Int, @Query("size") size : Int) : Call<FieldPhotoList>

    @GET("pictures/apelsinka")
    fun getAllApelsinkaPicture(@Query("page") page : Int) : Call<FieldPhotoList>

    @GET("pictures/oscar")
    fun getAllOscarPicture(@Query("page") page : Int) : Call<FieldPhotoList>

    @GET("pictures/lera")
    fun getAllLeraPicture(@Query("page") page : Int) : Call<FieldPhotoList>

    @GET("pictures/rylexium")
    fun getAllRylexiumPicture(@Query("page") page : Int) : Call<FieldPhotoList>

    @GET("pictures/main")
    fun getAllMainPicture(@Query("page") page : Int) : Call<FieldPhotoList>

    @GET("pictures/logo")
    fun getAllLogoPicture(@Query("page") page : Int) : Call<FieldPhotoList>

}