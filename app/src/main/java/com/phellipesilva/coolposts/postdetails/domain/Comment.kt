package com.phellipesilva.coolposts.postdetails.domain

data class Comment(
    val id: Int,
    val userEmail: String,
    val body: String
)