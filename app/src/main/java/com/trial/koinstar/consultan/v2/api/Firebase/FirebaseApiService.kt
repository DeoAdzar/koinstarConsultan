package com.trial.koinstar.consultan.v2.api.Firebase

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface FirebaseApiService {

    @POST("send")
    fun sendMessage (@HeaderMap headers: HashMap<String, String>, @Body messageBody: String): Call<String>
}