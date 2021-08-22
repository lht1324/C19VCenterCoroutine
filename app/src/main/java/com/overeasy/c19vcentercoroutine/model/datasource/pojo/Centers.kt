package com.overeasy.c19vcentercoroutine.model.datasource.pojo

data class Centers(
    val page: Int,
    val perPage: Int,
    val totalCount: Int,
    val currentCount: Int,
    val matchCount: Int,
    val data: ArrayList<Center>
)
