package com.example.cs377_finalproject.API

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("v1/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<SpotifyUserProfile>

    @GET("v1/me/top/tracks")
    suspend fun getTopTracks(
        @Header("Authorization") authorization: String,
        @Query("time_range") timeRange: String = "short_term",
        @Query("limit") limit: Int = 5
    ): Response<TopTracksResponse>

    @GET("v1/me/top/artists")
    suspend fun getTopArtists(
        @Header("Authorization") authorization: String,
        @Query("time_range") timeRange: String = "short_term",
        @Query("limit") limit: Int = 5
    ): Response<TopArtistsResponse>

    @GET("v1/me/player/recently-played")
    suspend fun getRecentlyPlayed(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 50
    ): Response<RecentlyPlayedResponse>
}