package net.jandie1505.combattest.game.lobby.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyMapsSubommand implements TabCompletingCommandExecutor {
    @NotNull private final Lobby lobby;

    public LobbyMapsSubommand(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        sender.sendRichMessage("<gold>Available maps:");
        this.lobby.getMaps().forEach(map -> {
            sender.sendMessage(Component.text(" - ", NamedTextColor.GOLD).append(Component.text(map.getName(), NamedTextColor.GOLD).append(Component.text("(" + map.getWorld() + ")", NamedTextColor.GOLD))));
        });

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
