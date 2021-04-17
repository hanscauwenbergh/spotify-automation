package com.hanscauwenbergh.common

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.SavedTrack
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

fun SpotifyClientApi.getAllSavedTracks() = getLatestSavedTracks(Integer.MAX_VALUE)

fun SpotifyClientApi.getLatestSavedTracks(maxAmount: Int): List<SavedTrack> {

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
    } while (trackNext != null && savedTracks.size < maxAmount)

    return savedTracks
}

private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

fun calculateDaysBetweenSaving(savedTrack1: SavedTrack, savedTrack2: SavedTrack): Int {

    val track1AddedAt = format.parse(savedTrack1.addedAt)
    val track2AddedAt = format.parse(savedTrack2.addedAt)

    val diffInMilliseconds = track1AddedAt.time - track2AddedAt.time
    return TimeUnit.MILLISECONDS.toDays(diffInMilliseconds).toInt()
}
