package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import common.*

class RefreshRecentSelectionCommand : CliktCommand(name = "RefreshRecentSelection") {

    private val recentSelectionPlaylistName = "Recent selection"
    private val minNumberOfTracks = 20
    private val maxNumberOfTracks = 40

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

        val recentlySavedTracks = api.getRecentlySavedTracks(maxAmount = maxNumberOfTracks)

        val maxDaysBetweenAdding = recentlySavedTracks
            .subList(minNumberOfTracks, maxNumberOfTracks)
            .zipWithNext { savedTrack1, savedTrack2 ->
                calculateDaysBetweenAdding(savedTrack1, savedTrack2)
            }
            .maxOrNull()

        val selectedRecentTrackIds = recentlySavedTracks
            .zipWithNext()
            .dropLastWhile { (savedTrack1, savedTrack2) ->
                calculateDaysBetweenAdding(savedTrack1, savedTrack2) != maxDaysBetweenAdding
            }
            .map { (savedTrack1, _) -> savedTrack1.track.id }

        val recentSelectionPlaylistId = api.getExistingPlaylistId(name = recentSelectionPlaylistName)
            ?: api.createPlaylist(name = recentSelectionPlaylistName).id

        api.replacePlaylistTracks(
            playlistId = recentSelectionPlaylistId,
            trackIds = selectedRecentTrackIds,
        )
    }
}
