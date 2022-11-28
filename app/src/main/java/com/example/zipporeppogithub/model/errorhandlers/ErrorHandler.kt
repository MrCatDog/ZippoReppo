package com.example.zipporeppogithub.model.errorhandlers

import com.example.zipporeppogithub.utils.ErrorEntity

interface ErrorHandler {
    fun handleError(error: Throwable) : ErrorEntity
}