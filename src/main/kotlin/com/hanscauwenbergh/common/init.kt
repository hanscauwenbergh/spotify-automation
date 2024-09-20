package com.hanscauwenbergh.common

import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.SpotifyUserAuthorization
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyClientApi
import kotlinx.coroutines.runBlocking

val scopes = arrayOf(
    SpotifyScope.UserLibraryRead,
    SpotifyScope.PlaylistReadPrivate,
    SpotifyScope.PlaylistModifyPrivate,
)

fun initializeApi(
    clientId: String,
    clientSecret: String,
    redirectUri: String,
    accessToken: String,
    refreshToken: String,
    scopes: Array<SpotifyScope>
) = runBlocking {
    spotifyClientApi(
        clientId = clientId,
        clientSecret = clientSecret,
        redirectUri = redirectUri,
        token = Token(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = 0,
            tokenType = "Bearer",
        ),
//        authorization = SpotifyUserAuthorization(
//            token = Token(
//                accessToken = accessToken,
//                tokenType = "Bearer",
//                expiresIn = 0,
//                refreshToken = refreshToken,
//                scopeString = scopes.joinToString(" "),
//            )
//        ),
    ).build()
}
