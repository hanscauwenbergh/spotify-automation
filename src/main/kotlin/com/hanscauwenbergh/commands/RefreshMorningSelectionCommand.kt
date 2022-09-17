package com.hanscauwenbergh.commands

import com.adamratzman.spotify.models.AudioFeatures
import com.adamratzman.spotify.models.Track
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hanscauwenbergh.common.*
import com.hanscauwenbergh.filter.FilterRuleParser

class RefreshMorningSelectionCommand : CliktCommand(name = "RefreshMorningSelection") {

    private val morningSelectionPlaylistName = "Morning selection"

    private val clientId: String by option(help = "Client ID").required()
    private val clientSecret: String by option(help = "Client Secret").required()
    private val redirectUri: String by option(help = "Redirect URI").required()
    private val accessToken: String by option(help = "Access Token").required()
    private val refreshToken: String by option(help = "Refresh Token").required()

    private val filterRuleParser = FilterRuleParser()

    override fun run() {

        val api = initializeApi(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            accessToken = accessToken,
            refreshToken = refreshToken,
            scopes = scopes,
        )

        val morningSelectionPlaylist = api.getExistingPlaylist(name = morningSelectionPlaylistName)
        if (morningSelectionPlaylist == null) {
            return
        }

        val description = morningSelectionPlaylist.description
        if (description == null) {
            return
        }

        val filterRules = filterRuleParser.parseFilterRules(description)

        val tracks = api
            .getAllSavedTracks()
            .map { savedTrack -> savedTrack.track }

        val tracksWithAudioFeatures = api.getTracksWithAudioFeatures(tracks)

        val selectedTracks = tracksWithAudioFeatures
            .filter { (_, audioFeatures) ->
                audioFeatures != null && filterRules.none { filterRule -> filterRule.filtersOut(audioFeatures) }
            }

//        printInfo(selectedTracks)

        api.replacePlaylistTracks(
            playlistId = morningSelectionPlaylist.id,
            trackIds = selectedTracks.map { (track, _) -> track.id },
        )
    }

    private fun printInfo(selectedTracks: List<Pair<Track, AudioFeatures?>>) {

        println("Found ${selectedTracks.size} tracks for current filter criteria")
        println()

        selectedTracks.forEachIndexed { index, (track, audioFeatures) ->

            println("${index + 1}. ${track.artists.joinToString(", ") { artist -> artist.name }} - ${track.name}")
            if (audioFeatures != null) {
                with(audioFeatures) {
                    println("acousticness=$acousticness, danceability=$danceability, energy=$energy, instrumentalness=$instrumentalness, liveness=$liveness, loudness=$loudness, mode=$mode, speechiness=$speechiness, tempo=$tempo, timeSignature=$timeSignature, valence=$valence")
                }
            }
            println()
        }
    }
}
