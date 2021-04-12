@file:DependsOn("com.adamratzman:spotify-api-kotlin-jvm:3.6.01")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

import com.adamratzman.spotify.*
import com.adamratzman.spotify.models.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

val (clientId, clientSecret, redirectUri, accessToken, refreshToken) = args
val api = initializeApi(
    clientId = clientId,
    clientSecret = clientSecret,
    redirectUri = redirectUri,
    accessToken = accessToken,
    refreshToken = refreshToken,
    scopes = listOf(
        SpotifyScope.PLAYLIST_READ_PRIVATE,
        SpotifyScope.PLAYLIST_MODIFY_PRIVATE,
        SpotifyScope.USER_LIBRARY_READ,
    ),
)

val recentSelectionPlaylistName = "Recent selection"
val minNumberOfTracks = 20
val maxNumberOfTracks = 40

val recentlySavedTracks = api.getRecentlySavedTracks(maxAmount = maxNumberOfTracks)

val maxSecondsBetweenAdding = recentlySavedTracks
    .subList(minNumberOfTracks, maxNumberOfTracks)
    .zipWithNext { savedTrack1, savedTrack2 ->
        calculateSecondsBetweenAdding(savedTrack1, savedTrack2)
    }
    .max()

val selectedRecentTrackIds = recentlySavedTracks
    .zipWithNext()
    .dropLastWhile { (savedTrack1, savedTrack2) ->
        calculateSecondsBetweenAdding(savedTrack1, savedTrack2) != maxSecondsBetweenAdding
    }
    .map { (savedTrack1, _) -> savedTrack1.track.id }

val recentSelectionPlaylistId = api.getExistingPlaylistId(name = recentSelectionPlaylistName)
    ?: api.createPlaylist(name = recentSelectionPlaylistName).id

api.replacePlaylistTracks(
    playlistId = recentSelectionPlaylistId,
    trackIds = selectedRecentTrackIds,
)


fun initializeApi(
    clientId: String,
    clientSecret: String,
    redirectUri: String,
    accessToken: String,
    refreshToken: String,
    scopes: List<SpotifyScope>
) = runBlocking {
    spotifyClientApi(
        clientId = clientId,
        clientSecret = clientSecret,
        redirectUri = redirectUri,
        authorization = SpotifyUserAuthorization(
            token = Token(
                accessToken = accessToken,
                tokenType = "Bearer",
                expiresIn = 0,
                refreshToken = refreshToken,
                scopeString = scopes.joinToString(" "),
            )
        ),
    ) {
        automaticRefresh = true
    }.build()
}

fun SpotifyClientApi.getRecentlySavedTracks(maxAmount: Int): List<SavedTrack> {

    val savedTracks = mutableListOf<SavedTrack>()

    var trackOffset = 0
    val trackLimit = 50
    var trackNext: String?

    do {
        val pagedSavedTracks = runBlocking {
            library.getSavedTracks(
                offset = trackOffset,
                limit = trackLimit,
            )
        }
        savedTracks.addAll(pagedSavedTracks.items)

        trackOffset += trackLimit
        trackNext = pagedSavedTracks.next
    } while (savedTracks.size >= maxAmount || trackNext != null)

    return savedTracks
}

val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

fun calculateSecondsBetweenAdding(savedTrack1: SavedTrack, savedTrack2: SavedTrack): Long {

    val track1AddedAt = format.parse(savedTrack1.addedAt)
    val track2AddedAt = format.parse(savedTrack2.addedAt)

    return (track1AddedAt.time - track2AddedAt.time) / 1000
}

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
