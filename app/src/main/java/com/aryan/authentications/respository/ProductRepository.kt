package com.aryan.authentications.respository

import com.aryan.authentications.model.ProductResponse
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun searchProducts(
        query: String,
        page: Int,
        country: String,
        sortBy: String,
        productCondition: String
    ): Response<ProductResponse> {
        return apiService.searchProducts(query, page, country, sortBy, productCondition)
    }
}
