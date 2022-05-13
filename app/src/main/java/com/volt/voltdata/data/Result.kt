package com.volt.voltdata.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class ConnectionError(val connection: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is ConnectionError -> "ConnectionError[exception=$connection]"
            is Error -> "Error[exception=$exception]"
        }
    }
}