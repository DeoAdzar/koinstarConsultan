package com.trial.koinstar.consultan.v2.api


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BaseApiService {
    // ================= AUTH ===================
    @FormUrlEncoded
    @POST("auth/login/usert")
    fun login (
        @Field("email") email:String,
        @Field("password") password:String
    ): Call<ResponseBody>

    @GET("auth/logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("auth/resetPasswordLink")
    fun forgetPassword (
        @Field("email") email:String,
    ): Call<ResponseBody>
    // ================= AUTH ===================

    // ================= PROFILE ===================
    @GET("profile")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("profile/update")
    fun updateProfile (
        @Header("Authorization") token: String,
        @Field("email") email:String,
        @Field("name") name:String,
        @Field("agency_origin") agency_origin:String,
        @Field("profession") profession:String,
        @Field("num_hp") phone:String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("profile/status/update")
    fun updateStatus (
        @Header("Authorization") token: String,
        @Field("status") status:String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("profile/password/update")
    fun updatePasswordProfile (
        @Header("Authorization") token: String,
        @Field("current_password") current:String,
        @Field("new_password") new:String,
        @Field("confirm_password") confirm:String,
    ): Call<ResponseBody>

    @Multipart
    @POST("profile/photo/update")
    fun updateImageProfile (
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
    ): Call<ResponseBody>
    // ================= PROFILE ===================

}