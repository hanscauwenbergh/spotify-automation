package commands

import com.adamratzman.spotify.getSpotifyAuthorizationUrl
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import common.scopes

class GetAuthorizationCodeCommand : CliktCommand(name = "GetAuthorizationCode") {

    private val clientId: String by option(help = "Client ID").required()
    private val redirectUri: String by option(help = "Redirect URI").required()

    override fun run() {
        val authorizationUrl = getSpotifyAuthorizationUrl(
            scopes = scopes,
            clientId = clientId,
            redirectUri = redirectUri
        )

        println("Authorization URL: $authorizationUrl")
    }
}
