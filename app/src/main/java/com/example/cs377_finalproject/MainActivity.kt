package com.example.cs377_finalproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cs377_finalproject.API.SpotifyAPIHandler
import com.example.cs377_finalproject.API.SpotifyArtist
import com.example.cs377_finalproject.API.SpotifyTrack
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val authRequestCode = 1001
    private val redirectUri = "cs377musicapp://callback"

    private lateinit var authService: AuthorizationService

    private lateinit var greetingText: TextView
    private lateinit var titleText: TextView
    private lateinit var loginStatusText: TextView
    private lateinit var loginButton: MaterialButton
    private lateinit var recentMinutesText: TextView
    private lateinit var averagePopularityText: TextView
    private lateinit var topTrackNameText: TextView
    private lateinit var topTrackArtistText: TextView
    private lateinit var topTracksListText: TextView
    private lateinit var topArtistsListText: TextView

    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = AuthorizationService(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()

        loginButton.setOnClickListener {
            startSpotifyLogin()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }

    private fun bindViews() {
        greetingText = findViewById(R.id.greetingText)
        titleText = findViewById(R.id.titleText)
        loginStatusText = findViewById(R.id.loginStatusText)
        loginButton = findViewById(R.id.loginButton)
        recentMinutesText = findViewById(R.id.recentMinutesText)
        averagePopularityText = findViewById(R.id.averagePopularityText)
        topTrackNameText = findViewById(R.id.topTrackNameText)
        topTrackArtistText = findViewById(R.id.topTrackArtistText)
        topTracksListText = findViewById(R.id.topTracksListText)
        topArtistsListText = findViewById(R.id.topArtistsListText)
    }

    private fun startSpotifyLogin() {
        if (BuildConfig.SPOTIFY_CLIENT_ID == "PASTE_YOUR_SPOTIFY_CLIENT_ID_HERE") {
            Toast.makeText(
                this,
                "Add your Spotify Client ID in local.properties first.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        loginStatusText.text = "Opening Spotify login..."

        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/authorize"),
            Uri.parse("https://accounts.spotify.com/api/token")
        )

        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            BuildConfig.SPOTIFY_CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(redirectUri)
        )
            .setScopes(
                "user-read-private",
                "user-read-email",
                "user-top-read",
                "user-read-recently-played"
            )
            .build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, authRequestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != authRequestCode) return

        if (data == null) {
            loginStatusText.text = "Spotify login finished, but no data was returned."
            return
        }

        val response = AuthorizationResponse.fromIntent(data)
        val exception = AuthorizationException.fromIntent(data)

        if (exception != null) {
            loginStatusText.text =
                "Spotify login failed: ${exception.errorDescription ?: exception.message}"
            return
        }

        if (response == null) {
            loginStatusText.text = "Spotify login finished, but no response was returned."
            return
        }

        loginStatusText.text = "Spotify connected. Getting access token..."

        authService.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, tokenException ->
            if (tokenException != null) {
                runOnUiThread {
                    loginStatusText.text =
                        "Token error: ${tokenException.errorDescription ?: tokenException.message}"
                }
                return@performTokenRequest
            }

            val token = tokenResponse?.accessToken

            if (token == null) {
                runOnUiThread {
                    loginStatusText.text = "Spotify did not return an access token."
                }
                return@performTokenRequest
            }

            accessToken = token

            runOnUiThread {
                loginStatusText.text = "Connected to Spotify. Loading your stats..."
                loginButton.text = "Refresh Spotify Stats"
                loadSpotifyStats(token)
            }
        }
    }

    private fun loadSpotifyStats(token: String) {
        lifecycleScope.launch {
            try {
                val authHeader = "Bearer $token"

                val profileResponse = SpotifyAPIHandler.service.getCurrentUser(authHeader)
                val topTracksResponse = SpotifyAPIHandler.service.getTopTracks(authHeader)
                val topArtistsResponse = SpotifyAPIHandler.service.getTopArtists(authHeader)
                val recentlyPlayedResponse = SpotifyAPIHandler.service.getRecentlyPlayed(authHeader)

                if (!profileResponse.isSuccessful) {
                    val errorText = profileResponse.errorBody()?.string()
                    loginStatusText.text =
                        "Spotify profile request failed.\nCode: ${profileResponse.code()}\n$errorText"
                    return@launch
                }

                if (!topTracksResponse.isSuccessful) {
                    loginStatusText.text = "Spotify top tracks request failed."
                    return@launch
                }

                if (!topArtistsResponse.isSuccessful) {
                    loginStatusText.text = "Spotify top artists request failed."
                    return@launch
                }

                val profile = profileResponse.body()
                val topTracks = topTracksResponse.body()?.items.orEmpty()
                val topArtists = topArtistsResponse.body()?.items.orEmpty()
                val recentTracks = recentlyPlayedResponse.body()?.items?.map { it.track }.orEmpty()

                val displayName = profile?.displayName ?: "Spotify User"

                greetingText.text = "Welcome back, $displayName"
                titleText.text = "Your Spotify Stats"
                loginStatusText.text = "Connected as $displayName"

                updateTopTrackCard(topTracks.firstOrNull())
                updateTopTracksList(topTracks)
                updateTopArtistsList(topArtists)
                updateStatCards(topTracks, recentTracks)

            } catch (exception: Exception) {
                loginStatusText.text = "Something went wrong loading Spotify stats."
                Toast.makeText(
                    this@MainActivity,
                    exception.message ?: "Unknown error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateTopTrackCard(track: SpotifyTrack?) {
        if (track == null) {
            topTrackNameText.text = "No top track found"
            topTrackArtistText.text = "Spotify did not return enough listening history yet."
            return
        }

        topTrackNameText.text = track.name
        topTrackArtistText.text = track.artists.joinToString { it.name }
    }

    private fun updateTopTracksList(tracks: List<SpotifyTrack>) {
        if (tracks.isEmpty()) {
            topTracksListText.text =
                "No top tracks found yet. Listen to more music and try again later."
            return
        }

        topTracksListText.text = tracks.mapIndexed { index, track ->
            val artists = track.artists.joinToString { it.name }
            val duration = formatDuration(track.durationMs)
            "${index + 1}. ${track.name}\n   $artists • $duration"
        }.joinToString("\n\n")
    }

    private fun updateTopArtistsList(artists: List<SpotifyArtist>) {
        if (artists.isEmpty()) {
            topArtistsListText.text = "No top artists found yet."
            return
        }

        val genres = artists
            .flatMap { it.genres.orEmpty() }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(3)
            .joinToString { it.key }
            .ifBlank { "No genre data returned" }

        val artistLines = artists.mapIndexed { index, artist ->
            val popularity = artist.popularity?.let { " • popularity $it" } ?: ""
            "${index + 1}. ${artist.name}$popularity"
        }.joinToString("\n")

        topArtistsListText.text = "Top genres: $genres\n\n$artistLines"
    }

    private fun updateStatCards(topTracks: List<SpotifyTrack>, recentTracks: List<SpotifyTrack>) {
        val recentMinutes = recentTracks.sumOf { it.durationMs } / 60000
        recentMinutesText.text = if (recentTracks.isNotEmpty()) "${recentMinutes}m" else "--"

        val popularityScores = topTracks.mapNotNull { it.popularity }
        averagePopularityText.text = if (popularityScores.isNotEmpty()) {
            popularityScores.average().roundToInt().toString()
        } else {
            "--"
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}