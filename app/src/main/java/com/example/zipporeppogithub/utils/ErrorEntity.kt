package com.example.zipporeppogithub.utils

sealed class ErrorEntity {
    sealed class ApiError : ErrorEntity() {
        object Network : ApiError()
        object NotFound : ApiError()
        object AccessDenied : ApiError()
        object ServiceUnavailable : ApiError()
    }

    sealed class DBError : ErrorEntity() {
        object NoPermission : DBError()
        object Common: DBError()
    }
    object Cancel : ErrorEntity()
    object UnknownError : ErrorEntity()
}