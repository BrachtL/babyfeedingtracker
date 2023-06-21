package com.example.babyfeedingtrackermvvm.model

data class FeedingDataResponse (
    val average06: Float,
    val average12: Float,
    val average24: Float,
    val averageAllTime: Float,
    val message: String,
    val usernameArray: List<String>,
    val timeArray: List<String>,
    val amountArray: List<Int>,
    val colorArray: List<String>
)