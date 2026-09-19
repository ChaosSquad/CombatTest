package net.jandie1505.combattest.game.game.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GamePointsCommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public GamePointsCommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>This command can only be executed by a player");
            return true;
        }

        if (!(this.plugin.getGame() instanceof Game game)) {
            sender.sendRichMessage("<red>No game running");
            return true;
        }

        var playerData = game.getPlayerData(player);
        if (playerData == null) {
            sender.sendRichMessage("<red>You are not ingame");
            return true;
        }

        player.sendRichMessage("<gold>Your points: " + playerData.getPoints());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
