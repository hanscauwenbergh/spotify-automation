import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import commands.GetAuthorizationCodeCommand
import commands.RefreshPlaylistsCommand
import commands.RefreshRecentSelectionCommand

fun main(vararg args: String) = SpotifyAutomation()
    .subcommands(
        GetAuthorizationCodeCommand(),
        RefreshPlaylistsCommand(),
        RefreshRecentSelectionCommand()
    )
    .main(args)

class SpotifyAutomation : CliktCommand() {
    override fun run() {}
}
