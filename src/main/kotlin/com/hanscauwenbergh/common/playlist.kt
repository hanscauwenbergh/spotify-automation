package com.hanscauwenbergh.common

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.SimplePlaylist
import kotlinx.coroutines.runBlocking

fun SpotifyClientApi.getExistingPlaylistId(name: String) = this
    .getAllPlaylists()
    .singleOrNull { playlist -> playlist.name == name }?.id

fun SpotifyClientApi.getAllPlaylists(): List<SimplePlaylist> {

    val simplePlaylists = mutableListOf<SimplePlaylist>()

    var playlistOffset = 0
    val playlistLimit = 50
    var playlistNext: String?

    do {

        val pagedPlaylists = runBlocking {
            playlists.getClientPlaylists(
                offset = playlistOffset,
                limit = playlistLimit,
            )
        }
        simplePlaylists.addAll(pagedPlaylists.items)

        playlistOffset += playlistLimit
        playlistNext = pagedPlaylists.next
    } while (playlistNext != null)

    return simplePlaylists
}

fun SpotifyClientApi.createPlaylist(name: String, description: String = "") = runBlocking {
    playlists.createClientPlaylist(
        name = name,
        description = description,
        public = false,
        collaborative = false,
    )
}

fun SpotifyClientApi.replacePlaylistTracks(playlistId: String, trackIds: List<String>) = runBlocking {
    playlists.replaceClientPlaylistTracks(
        playlist = playlistId,
        tracks = trackIds.toTypedArray()
    )
}
