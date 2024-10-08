package com.hanscauwenbergh.commands

import com.adamratzman.spotify.models.Track
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hanscauwenbergh.common.*

class RefreshRecentSelectionCommand : CliktCommand(name = "RefreshRecentSelection") {

    private val minNumberOfTracks = 20
    private val maxNumberOfTracks = 40

    private val recentSelectionPlaylistName = "Recent selection"
    private val recentSelectionPlaylistDescription = "Selection of latest saved library tracks (min. ${minNumberOfTracks}, max. ${maxNumberOfTracks})"

    private val clientId: String by option(help = "Client ID").required()
    private val clientSecret: String by option(help = "Client Secret").required()
    private val redirectUri: String by option(help = "Redirect URI").required()
    private val accessToken: String by option(help = "Access Token").required()
    private val refreshToken: String by option(help = "Refresh Token").required()

    override fun run() {

        val api = initializeApi(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            accessToken = accessToken,
            refreshToken = refreshToken,
            scopes = scopes,
        )

        val latestSavedTracks = api.getLatestSavedTracks(maxAmount = maxNumberOfTracks)

        val maxDaysBetweenSaving = latestSavedTracks
            .subList(minNumberOfTracks, maxNumberOfTracks)
            .zipWithNext { savedTrack1, savedTrack2 ->
                DaysBetweenSavedTracks(savedTrack1.track, savedTrack2.track, calculateDaysBetweenSaving(savedTrack1, savedTrack2))
            }
            .maxBy { it.daysBetweenSaving }

        val selectedLatestTracks = latestSavedTracks
            .takeWhile { savedTrack ->
                savedTrack.track.id != maxDaysBetweenSaving.savedTrack2.id
            }
            .map { savedTrack -> savedTrack.track }

        val recentSelectionPlaylistId = api.getExistingPlaylistId(name = recentSelectionPlaylistName)
            ?: api.createPlaylist(name = recentSelectionPlaylistName, description = recentSelectionPlaylistDescription).id

        api.replacePlaylistTracks(
            playlistId = recentSelectionPlaylistId,
            tracks = selectedLatestTracks,
        )
    }
}

data class DaysBetweenSavedTracks(
    val savedTrack1: Track,
    val savedTrack2: Track,
    val daysBetweenSaving: Int,
)
