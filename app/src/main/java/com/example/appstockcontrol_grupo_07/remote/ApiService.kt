package com.example.appstockcontrol_grupo_07.remote

import com.example.appstockcontrol_grupo_07.model.Post
import retrofit2.http.GET

interface ApiService {

    @GET("/post")
    suspend fun getPosts(): List<Post>

}