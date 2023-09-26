package com.trial.koinstar.consultan.v2.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object{
        private lateinit var retrofit: Retrofit
        lateinit var mApiService: BaseApiService

        fun getClient(baseUrl:String): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(level = HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = okhttp3.OkHttpClient.Builder().addInterceptor(interceptor).build()
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            mApiService = retrofit.create(BaseApiService::class.java)
            return retrofit
        }
    }
}