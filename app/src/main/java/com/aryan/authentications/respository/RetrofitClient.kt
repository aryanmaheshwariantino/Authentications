package com.aryan.authentications.respository

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://real-time-amazon-data.p.rapidapi.com/"

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("x-rapidapi-key", "6d0d9971e2msh999581d6f834ef4p1059a3jsn176cfc5b31ed") // Replace with your actual API key
                    .addHeader("x-rapidapi-host", "real-time-amazon-data.p.rapidapi.com")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
