package net.jandie1505.combattest.game.lobby.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.lobby.Lobby;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyRandomTeamsSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Lobby lobby;

    public LobbyRandomTeamsSubcommand(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;

        this.lobby.randomTeams();
        sender.sendRichMessage("<green>All players were assigned to random teams.");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
