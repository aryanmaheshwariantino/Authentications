package com.aryan.authentications.respository

import com.aryan.authentications.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("country") country: String,
        @Query("sort_by") sortBy: String,
        @Query("product_condition") productCondition: String
    ): Response<ProductResponse>

}

