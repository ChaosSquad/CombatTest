package net.jandie1505.combattest.commands.subcommands.players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.constants.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GamePlayersAddSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final GamePart game;

    public GamePlayersAddSubcommand(@NotNull GamePart game) {
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

        Player player = PlayerUtils.getPlayerFromString(args[0]);
        if (player == null) {
            sender.sendRichMessage("<red>Player not found");
            return true;
        }

        boolean success = game.addPlayer(player);
        if (success) {
            sender.sendRichMessage("<green>Player added");
        } else {
            sender.sendRichMessage("<red>Failed to add player");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return Bukkit.getOnlinePlayers().stream()
                .filter(player -> !this.game.getPlayers().contains(player.getUniqueId()))
                .map(Player::getName)
                .toList();
        return List.of();
    }

}
