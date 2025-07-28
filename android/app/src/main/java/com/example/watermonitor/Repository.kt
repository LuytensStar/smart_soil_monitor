package com.example.watermonitor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class SensorState(
    val brightness: Int,
    val moisture: Int,
    val ts: Long
)

private interface Api {
    @GET("data")
    suspend fun getState(): SensorState
}


class Repository(baseUrl: String) {


    private val api: Api

    init {
        val log = HttpLoggingInterceptor().apply {

            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(log)
            .build()

        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }


    suspend fun latest(): SensorState =
        withContext(Dispatchers.IO) {
            api.getState()
        }
}
