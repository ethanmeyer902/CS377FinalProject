package com.example.cs377_finalproject.API

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/me/top/tracks")
    suspend fun getTopTracks(
        @Header("Authorization") token: String,
        @Query("time_range") timeRange: String = "medium_term",
        @Query("limit") limit: Int = 10
    ): Response<TopTracksResponse>
}