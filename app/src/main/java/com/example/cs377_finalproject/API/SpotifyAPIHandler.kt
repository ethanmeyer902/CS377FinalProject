package com.example.cs377_finalproject.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpotifyAPIHandler {
    /* variables needed to help call the Spotify API
        provided by Spotify for Developers
     */
    private val clientID = "b7967c719d1c4665a67daf121c1c0743"
    private val clientSecret = "f360c3dfa71a4a72bb00024043584e98"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val spotifyService = retrofit.create(SpotifyApiService::class.java)
}