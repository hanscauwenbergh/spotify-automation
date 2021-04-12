@file:DependsOn("com.adamratzman:spotify-api-kotlin-jvm:3.6.01")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

import com.adamratzman.spotify.*
import com.adamratzman.spotify.models.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

val (clientId, clientSecret, redirectUri) = args
val authorizationUrl = getSpotifyAuthorizationUrl(
    scopes = arrayOf(
        SpotifyScope.PLAYLIST_READ_PRIVATE,
        SpotifyScope.PLAYLIST_MODIFY_PRIVATE,
        SpotifyScope.USER_LIBRARY_READ,
    ), // TODO: share scopes with other script by importing from common file
    clientId = clientId,
    redirectUri = redirectUri
)

println("Authorization URL: $authorizationUrl")
val authorizationCode = readLine()!!
// TODO: request and parse response to print out accessToken and refreshToken to input into other script