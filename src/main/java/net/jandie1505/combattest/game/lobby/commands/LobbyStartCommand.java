package net.jandie1505.combattest.game.lobby.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.lobby.Lobby;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyStartCommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public LobbyStartCommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.START)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        if (!(this.plugin.getGame() instanceof Lobby lobby)) {
            sender.sendRichMessage("<red>Not in a lobby");
            return true;
        }

        lobby.forcestart();
        sender.sendRichMessage("<green>You have started the lobby");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
