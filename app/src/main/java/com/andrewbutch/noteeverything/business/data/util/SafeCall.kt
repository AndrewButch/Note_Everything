package com.andrewbutch.noteeverything.business.data.util

import com.andrewbutch.noteeverything.business.data.cache.CacheConstants.CACHE_ERROR_TIMEOUT
import com.andrewbutch.noteeverything.business.data.cache.CacheConstants.CACHE_ERROR_UNKNOWN
import com.andrewbutch.noteeverything.business.data.cache.CacheConstants.CACHE_TIMEOUT
import com.andrewbutch.noteeverything.business.data.cache.CacheResult
import com.andrewbutch.noteeverything.business.data.network.NetworkConstants.ERROR_UNKNOWN
import com.andrewbutch.noteeverything.business.data.network.NetworkConstants.NETWORK_ERROR_TIMEOUT
import com.andrewbutch.noteeverything.business.data.network.NetworkConstants.NETWORK_ERROR_UNKNOWN
import com.andrewbutch.noteeverything.business.data.network.NetworkConstants.NETWORK_TIMEOUT
import com.andrewbutch.noteeverything.business.data.network.NetworkResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException

/**
 * Reference: https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
 */

suspend fun <T> safeNetworkCall(
    dispatcher: CoroutineDispatcher,
    networkCall: suspend () -> T?
): NetworkResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(NETWORK_TIMEOUT){
                NetworkResult.Success(networkCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    NetworkResult.Error(code, NETWORK_ERROR_TIMEOUT)
                }
                is IOException -> {
                    NetworkResult.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    NetworkResult.Error(
                        code,
                        errorResponse
                    )
                }
                else -> {
                    NetworkResult.Error(
                        null,
                        NETWORK_ERROR_UNKNOWN
                    )
                }
            }
        }
    }
}

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(CACHE_TIMEOUT){
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {

                is TimeoutCancellationException -> {
                    CacheResult.Error(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    CacheResult.Error(CACHE_ERROR_UNKNOWN)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ERROR_UNKNOWN
    }
}