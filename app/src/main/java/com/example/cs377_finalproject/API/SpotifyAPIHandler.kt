package com.example.cs377_finalproject.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpotifyAPIHandler {
    /* variables needed to help call the Spotify API
        provided by Spotify for Developers
     */
    // private val clientID = ""
    // private val clientSecret = ""
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val spotifyService = retrofit.create(SpotifyApiService::class.java)
}