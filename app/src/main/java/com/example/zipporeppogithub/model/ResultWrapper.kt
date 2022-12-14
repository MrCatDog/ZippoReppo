package com.example.zipporeppogithub.model

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Failure(val error: ErrorEntity) : ResultWrapper<Nothing>()
}
