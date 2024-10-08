package com.hanscauwenbergh.commands

import com.adamratzman.spotify.getSpotifyAuthorizationUrl
import com.fasterxml.jackson.annotation.JsonAlias
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.hanscauwenbergh.common.scopes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.runBlocking

class GetTokensCommand : CliktCommand(name = "GetTokens") {

    private val clientId: String by option(help = "Client ID").required()
    private val clientSecret: String by option(help = "Client Secret").required()
    private val redirectUri: String by option(help = "Redirect URI").required()

    override fun run() {
        val authorizationUrl = getSpotifyAuthorizationUrl(
            scopes = scopes,
            clientId = clientId,
            redirectUri = redirectUri
        )

        println("Authorization URL: $authorizationUrl")
        print("Enter code: ")
        val authorizationCode = readLine()!!

        val client = HttpClient {
            install(ContentNegotiation) {
                jackson() // Jackson serializer setup
            }
        }

        val response: AuthorizationResponseBody = runBlocking {
            client.submitForm(
                url = "https://accounts.spotify.com/api/token",
                formParameters = Parameters.build {
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                    append("redirect_uri", redirectUri)
                    append("grant_type", "authorization_code")
                    append("code", authorizationCode)
                }
            ).body()
        }

        with(response) {
            println("Access token: $accessToken")
            println("Refresh token: $refreshToken")
        }

        client.close()
    }

    private data class AuthorizationResponseBody(
        @JsonAlias("access_token") val accessToken: String,
        @JsonAlias("token_type") val tokenType: String,
        @JsonAlias("scope") val scope: String,
        @JsonAlias("expires_in") val expiresIn: Int,
        @JsonAlias("refresh_token") val refreshToken: String,
    )
}
