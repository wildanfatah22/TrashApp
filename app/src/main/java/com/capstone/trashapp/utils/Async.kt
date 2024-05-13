package com.capstone.trashapp.utils

sealed class Async<out R> private constructor() {
    data class Success<out T>(val data: T) : Async<T>()
    data class Error(val error: String) : Async<Nothing>()
    data object Loading : Async<Nothing>()
    data object Empty : Async<Nothing>()
}