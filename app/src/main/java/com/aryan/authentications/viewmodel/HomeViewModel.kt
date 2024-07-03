package com.aryan.authentications.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.authentications.model.Product
import com.aryan.authentications.respository.ProductRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<Throwable?>()
    val error: LiveData<Throwable?> get() = _error

    private val firebaseCrashlytics = FirebaseCrashlytics.getInstance()

    fun searchProducts(query: String, page: Int, country: String, sortBy: String, productCondition: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.searchProducts(query, page, country, sortBy, productCondition)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _products.postValue(it.data.products)
                        Log.d("HomeViewModel", "Products fetched: ${it.data.products}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("HomeViewModel", "Error response: $errorBody")
                    _error.postValue(Exception("Error fetching products: $errorBody"))
                    firebaseCrashlytics.recordException(Exception("Error fetching products: $errorBody"))
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
                _error.postValue(e)
                firebaseCrashlytics.recordException(e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
