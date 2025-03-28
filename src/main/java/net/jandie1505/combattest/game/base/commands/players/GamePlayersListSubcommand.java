package net.jandie1505.combattest.game.base.commands.players;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.game.base.GamePart;
import net.jandie1505.combattest.constants.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GamePlayersListSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final GamePart game;

    public GamePlayersListSubcommand(@NotNull GamePart game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        List<UUID> players = game.getPlayers();

        Component list = Component.empty()
                .append(Component.text("Ingame players:", NamedTextColor.GOLD)).appendSpace();

        Iterator<UUID> i = players.iterator();

        if (i.hasNext()) {

            while (i.hasNext()) {
                UUID playerId = i.next();
                OfflinePlayer p = Bukkit.getOfflinePlayer(playerId);

                if (p.getName() != null) {
                    list = list.append(Component.text(p.getName() + " (" + p.getUniqueId() + ")", NamedTextColor.GRAY));
                } else {
                    list = list.append(Component.text(p.getUniqueId().toString(), NamedTextColor.GRAY));
                }

                if (i.hasNext()) {
                    list = list.append(Component.text(", ", NamedTextColor.GOLD));
                }
            }

        } else {
            list = list.append(Component.text("---", NamedTextColor.RED));
        }

        sender.sendMessage(list);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
