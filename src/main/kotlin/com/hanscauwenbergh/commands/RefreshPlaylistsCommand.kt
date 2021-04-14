package com.hanscauwenbergh.commands

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.SavedTrack
import com.adamratzman.spotify.models.SimplePlaylist
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hanscauwenbergh.common.*
import common.*
import kotlinx.coroutines.runBlocking

class RefreshPlaylistsCommand : CliktCommand(name = "RefreshPlaylists") {

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

        val playlists = api.getAllPlaylists()

        api.getAllSavedTracks()
            .filterOutTracksAddedInBatch()
            .splitIfMoreThanDaysInBetweenAddedAt()
            .mergeSmallSelections()
            .splitBigSelections()
            .print()
            .getRefreshedPlaylistSelections()
            .save(playlists, api)
    }

    private fun List<SavedTrack>.filterOutTracksAddedInBatch() = filterNot {
        it.addedAt.startsWith("2019-06-05") ||
                it.addedAt.startsWith("2019-06-04") ||
                it.addedAt.startsWith("2019-06-03") ||
                it.addedAt.startsWith("2019-06-01") ||
                it.addedAt.startsWith("2017-08-13")
    }

    private fun List<SavedTrack>.splitIfMoreThanDaysInBetweenAddedAt() = groupConsecutiveBy { savedTrack1, savedTrack2 ->

        val daysBetweenAdding = calculateDaysBetweenAdding(savedTrack1, savedTrack2)

        return@groupConsecutiveBy daysBetweenAdding <= 7
    }

    private fun List<List<SavedTrack>>.mergeSmallSelections(): List<List<SavedTrack>> {

        val savedSelections = this.toMutableList()

        // join smaller groups (look at neighbours, join with smallest)
        var index = 0
        while (index < savedSelections.size && savedSelections.size > 1) {

            val currentSelection = savedSelections[index]
            if (currentSelection.size < 20) {

                val previousNeighbor = savedSelections.getOrNull(index - 1)
                val nextNeighbor = savedSelections.getOrNull(index + 1)

                if (nextNeighbor == null || (previousNeighbor != null && previousNeighbor.size < nextNeighbor.size)) {

                    // join with previous neighbor
                    savedSelections[index - 1] = (savedSelections[index - 1] + currentSelection) as MutableList<SavedTrack>
                    savedSelections.removeAt(index)
                    index--
                } else {

                    // join with next neighbor
                    savedSelections[index] = (currentSelection + savedSelections[index + 1]) as MutableList<SavedTrack>
                    savedSelections.removeAt(index + 1)
                    index--
                }
            } else {
                index++
            }
        }

        return savedSelections
    }

    private fun List<List<SavedTrack>>.splitBigSelections(): List<List<SavedTrack>> {

        // divide groups bigger than 40, all groups should contain 20-40 songs in the end (prefer as small groups as possible)

        return this.flatMap {

            if (it.size > 40) {

                // split up into selections of 20-40 songs (prefer as small groups as possible)

                val amountOfSubselections = it.size / 20
                var remainder = it.size % 20

                val indicesToSplit = (0..it.size step 20).toMutableList()

                var shiftAll = remainder / (indicesToSplit.size - 1)
                for (i in 1 until indicesToSplit.size) {
                    indicesToSplit[i] = indicesToSplit[i] + shiftAll
                    shiftAll = shiftAll + (remainder / (indicesToSplit.size - 1))
                }

                remainder %= (indicesToSplit.size - 1)
                var currentIndex = 1
                while (remainder > 0) {

                    for (i in currentIndex until indicesToSplit.size) {
                        indicesToSplit[i]++
                    }
                    currentIndex++
                    remainder--
                }

                // 0, 20, 40, 60, 80, 100
                // 0, 21, 42, 63, 83, 103

                // divide in groups of size 20 and spread remainder evenly across groups
                // 41 (mod 20 == 1) -> 20, 21
                // 59 (mod 20 == 19) -> NOT 20, 20, 19 but 30, 29

                // alternative: look for biggest date differences and try to split that way
                // (searching algorithm, brute-force splitting 20-40 chunks and maximizing total summed date difference)

                val split = indicesToSplit
                    .zipWithNext()
                    .map { (fromIndex, toIndex) ->
                        it.subList(fromIndex, toIndex)
                    }

                split

            } else {
                listOf(it)
            }
        }
    }

    private fun List<List<SavedTrack>>.print(): List<List<SavedTrack>> {

        this.forEach {

            it.forEach { track ->
                println("${track.track.name} - ${track.track.artists.joinToString(", ") { artist -> artist.name }}")
            }
            println("\n")
        }

        return this
    }

    private fun List<List<SavedTrack>>.getRefreshedPlaylistSelections(): List<PlaylistSelection> {

        val selectionsTrackIds = this
            .map { selection ->
                selection.map { savedTrack ->
                    savedTrack.track.id
                }.toTypedArray()
            }

        return listOf(
            PlaylistSelection(
                playlistName = "Recent selection",
                playlistDescription = "TODO",
                selectedTrackIds = selectionsTrackIds.first(),
            ),
            PlaylistSelection(
                playlistName = "Random selection",
                playlistDescription = "TODO",
                selectedTrackIds = selectionsTrackIds.drop(5).random(),
            ),
        )
    }

    private fun List<PlaylistSelection>.save(playlists: List<SimplePlaylist>, api: SpotifyClientApi) = forEach { playListSelection ->

        val playlistId = playlists
            .singleOrNull { playlist -> playlist.name == playListSelection.playlistName }
            ?.id
            ?: runBlocking {
                api.playlists.createClientPlaylist(
                    name = playListSelection.playlistName,
                    description = playListSelection.playlistDescription,
                    public = false,
                    collaborative = false,
                ).id
            }

        runBlocking {
            api.playlists.replaceClientPlaylistTracks(
                playlist = playlistId,
                tracks = playListSelection.selectedTrackIds
            )
        }
    }

    private data class PlaylistSelection(
        val playlistName: String,
        val playlistDescription: String,
        val selectedTrackIds: Array<String>,
    )

    private fun <T> Iterable<T>.groupConsecutiveBy(groupIdentifier: (T, T) -> Boolean) =
        if (!this.any())
            emptyList()
        else this
            .drop(1)
            .fold(mutableListOf(mutableListOf(this.first()))) { groups, t ->
                groups.last().apply {
                    if (groupIdentifier.invoke(last(), t)) {
                        add(t)
                    } else {
                        groups.add(mutableListOf(t))
                    }
                }
                groups
            }
}
