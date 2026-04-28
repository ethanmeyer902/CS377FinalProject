package com.example.cs377_finalproject.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpotifyAPIHandler {
    val service: SpotifyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApiService::class.java)
    }
}