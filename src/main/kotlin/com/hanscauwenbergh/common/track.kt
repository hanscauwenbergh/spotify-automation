package com.hanscauwenbergh.common

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.SavedTrack
import com.adamratzman.spotify.models.Track
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

fun SpotifyClientApi.getLatestSavedTracks(maxAmount: Int): List<SavedTrack> {

    val savedTracks = mutableListOf<SavedTrack>()

    var trackOffset = 0
    val trackLimit = minOf(maxAmount, 50)
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
    } while (trackNext != null && savedTracks.size < maxAmount)

    return savedTracks
}

fun SpotifyClientApi.getAllSavedTracks() = getLatestSavedTracks(Int.MAX_VALUE)

fun SpotifyClientApi.getTracksWithAudioFeatures(tracksToGetAudioFeaturesFor: List<Track>) = runBlocking {

    val trackIds = tracksToGetAudioFeaturesFor.map { track -> track.id }

    val tracksAudioFeatures = trackIds
        .windowed(
            size = 100,
            step = 100,
            partialWindows = true,
        )
        .flatMap { windowedTrackIds ->
            tracks.getAudioFeatures(*windowedTrackIds.toTypedArray())
        }

    return@runBlocking tracksToGetAudioFeaturesFor.zip(tracksAudioFeatures)
}

private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

fun calculateDaysBetweenSaving(savedTrack1: SavedTrack, savedTrack2: SavedTrack): Int {

    val track1AddedAt = format.parse(savedTrack1.addedAt)
    val track2AddedAt = format.parse(savedTrack2.addedAt)

    val diffInMilliseconds = track1AddedAt.time - track2AddedAt.time
    return TimeUnit.MILLISECONDS.toDays(diffInMilliseconds).toInt()
}
