package net.jandie1505.combattest.game.base.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.config.ConfigKeys;
import net.jandie1505.combattest.game.base.GamePart;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameLeaveSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final GamePart game;

    public GameLeaveSubcommand(@NotNull GamePart game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>This command can only be used by players.");
            return true;
        }

        if (this.game.getPlugin().config().optBoolean(ConfigKeys.SINGLE_SERVER_MODE, false)) {
            sender.sendRichMessage("<red>This command is not available in single-server mode.");
            return true;
        }

        if (!this.game.getRegisteredPlayers().contains(player.getUniqueId())) {
            sender.sendRichMessage("<red>You are currently not ingame.");
            return true;
        }

        this.game.removePlayer(player.getUniqueId());
        player.sendRichMessage("<green>You successfully have left the game.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
