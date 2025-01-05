package com.auto.artist.ui

sealed class UiState {
    data object EMPTY : UiState()
    data object LOADING : UiState()
    data class READY<T>(val data: T) : UiState()
    data object ERROR : UiState()

    fun isEmpty() = this is EMPTY
    fun isLoading() = this is LOADING
    fun isReady() = this is READY<*>
    fun isError() = this is ERROR

}