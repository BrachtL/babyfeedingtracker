package com.example.babyfeedingtrackermvvm.listener

interface APIListener<T> {
    fun onSuccess(result: T)
    fun onFailure(message: String)
}