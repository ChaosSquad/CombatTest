package net.jandie1505.combattest.commands.subcommands.players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.constants.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GamePlayersRemoveSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final GamePart game;

    public GamePlayersRemoveSubcommand(@NotNull GamePart game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        if (args.length < 1) {
            sender.sendRichMessage("<red>Usage: /combattest players remove <player>");
            return true;
        }

        UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);
        if (playerId == null) {
            sender.sendRichMessage("<red>Player not found");
            return true;
        }

        boolean success = game.removePlayer(playerId);
        if (success) {
            sender.sendRichMessage("<green>Player removed");
        } else {
            sender.sendRichMessage("<red>Failed to remove player");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return this.game.getPlayers().stream()
                .map(uuid -> Bukkit.getPlayer(uuid))
                .filter(Objects::nonNull)
                .map(Player::getName)
                .toList();
        return List.of();
    }

}
