package com.example.zipporeppogithub.model

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
    sealed class ExtError : ErrorEntity() {
        object Permission : ExtError()
        object Common: ExtError()
    }
    object Cancel : ErrorEntity()
    object UnknownError : ErrorEntity()
}
