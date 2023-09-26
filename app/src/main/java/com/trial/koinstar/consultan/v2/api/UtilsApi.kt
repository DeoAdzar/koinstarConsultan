package com.trial.koinstar.consultan.v2.api

class UtilsApi {
    companion object {
//        val BASE_URL = "https://poksma.ae-technoo.com/public/api/"
        val BASE_URL = "https://koinstar.ae-technoo.com/public/api/"

        fun getApiService() : BaseApiService {
            return RetrofitClient.getClient(BASE_URL).create(BaseApiService::class.java)
        }
    }

}