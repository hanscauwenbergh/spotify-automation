package common

import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.SpotifyUserAuthorization
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyClientApi
import kotlinx.coroutines.runBlocking

val scopes = arrayOf(
    SpotifyScope.PLAYLIST_READ_PRIVATE,
    SpotifyScope.PLAYLIST_MODIFY_PRIVATE,
    SpotifyScope.USER_LIBRARY_READ
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
