package com.example.cs377_finalproject.API

import com.google.gson.annotations.SerializedName

data class SpotifyUserProfile(
    val id: String,
    @SerializedName("display_name") val displayName: String?,
    val email: String?,
    val country: String?,
    val images: List<SpotifyImage>?
)

data class SpotifyImage(
    val url: String,
    val height: Int?,
    val width: Int?
)

data class TopTracksResponse(
    val items: List<SpotifyTrack>
)

data class TopArtistsResponse(
    val items: List<SpotifyArtist>
)

data class RecentlyPlayedResponse(
    val items: List<RecentlyPlayedItem>
)

data class RecentlyPlayedItem(
    val track: SpotifyTrack,
    @SerializedName("played_at") val playedAt: String
)

data class SpotifyTrack(
    val id: String,
    val name: String,
    val popularity: Int?,
    @SerializedName("duration_ms") val durationMs: Long,
    val artists: List<SpotifyArtistSimple>,
    val album: SpotifyAlbum?
)

data class SpotifyArtistSimple(
    val id: String,
    val name: String
)

data class SpotifyArtist(
    val id: String,
    val name: String,
    val popularity: Int?,
    val genres: List<String>?,
    val images: List<SpotifyImage>?
)

data class SpotifyAlbum(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>?
)