package com.example.appstockcontrol_grupo_07.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://valiant-enchantment-production.up.railway.app/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)              // debe terminar en /
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API de usuarios/auth
    val usuarioApi: UsuarioApi = retrofit.create(UsuarioApi::class.java)

    // Si ya tienes una API para Post:
    // val postApi: PostApi = retrofit.create(PostApi::class.java)
}
