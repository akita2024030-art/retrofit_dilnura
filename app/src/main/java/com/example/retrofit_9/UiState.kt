package com.example.retrofit_9

import java.io.IOException
import retrofit2.HttpException

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(
        val message: String,
        val isNetworkError: Boolean = false,
        val isHttpError: Boolean = false,
        val httpCode: Int = 0
    ) : UiState<Nothing>
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): UiState<T> {
    return try {
        UiState.Success(apiCall())
    } catch (e: IOException) {
        UiState.Error("No network", isNetworkError = true)
    } catch (e: HttpException) {
        val code = e.code()
        UiState.Error("Server error: $code", isHttpError = true, httpCode = code)
    } catch (e: Exception) {
        UiState.Error(e.localizedMessage ?: "Unknown error")
    }
}
