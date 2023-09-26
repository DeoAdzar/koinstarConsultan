package com.trial.koinstar.consultan.v2.api.Firebase

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FirebaseClient {
    companion object{
        private lateinit var retrofit: Retrofit

        fun getClient(): Retrofit {
            retrofit = Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }
    }
}