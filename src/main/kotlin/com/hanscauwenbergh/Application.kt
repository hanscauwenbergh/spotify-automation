package com.hanscauwenbergh

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.hanscauwenbergh.commands.GetAuthorizationCodeCommand
import com.hanscauwenbergh.commands.RefreshPlaylistsCommand
import com.hanscauwenbergh.commands.RefreshRecentSelectionCommand

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