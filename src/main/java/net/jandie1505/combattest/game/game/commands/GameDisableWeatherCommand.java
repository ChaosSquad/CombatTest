package net.jandie1505.combattest.game.game.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameDisableWeatherCommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public GameDisableWeatherCommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(this.plugin.getGame() instanceof Game game)) {
            sender.sendRichMessage("<red>No game running.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>This command can only be executed by a player.");
            return true;
        }

        var playerData = game.getPlayerData(player);
        if (playerData == null) {
            sender.sendMessage("§cYou are not ingame.");
            return true;
        }

        playerData.setWeatherDisabled(!playerData.isWeatherDisabled());

        if (playerData.isWeatherDisabled()) {
            sender.sendRichMessage("<green>Rain and thunder disabled.");
        } else {
            sender.sendRichMessage("<green>Switched back to global weather.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
