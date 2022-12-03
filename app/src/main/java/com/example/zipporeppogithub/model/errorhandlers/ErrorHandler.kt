package com.example.zipporeppogithub.model.errorhandlers

import com.example.zipporeppogithub.model.ErrorEntity

interface ErrorHandler {
    fun handleError(error: Throwable) : ErrorEntity
}