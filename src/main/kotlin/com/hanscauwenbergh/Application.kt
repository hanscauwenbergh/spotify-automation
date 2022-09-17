package com.hanscauwenbergh

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.hanscauwenbergh.commands.GetTokensCommand
import com.hanscauwenbergh.commands.RefreshMorningSelectionCommand
import com.hanscauwenbergh.commands.RefreshRecentSelectionCommand

fun main(vararg args: String) = SpotifyAutomation()
    .subcommands(
        GetTokensCommand(),
        RefreshRecentSelectionCommand(),
        RefreshMorningSelectionCommand(),
    )
    .main(args)

class SpotifyAutomation : CliktCommand() {
    override fun run() {}
}
