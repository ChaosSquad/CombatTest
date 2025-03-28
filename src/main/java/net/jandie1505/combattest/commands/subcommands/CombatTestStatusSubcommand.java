package net.jandie1505.combattest.commands.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.base.GamePart;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.endlobby.Endlobby;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.lobby.Lobby;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CombatTestStatusSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public CombatTestStatusSubcommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        GamePart game = this.plugin.getGame();

        if (game instanceof Lobby) {
            sender.sendRichMessage("<gray>Game status: LOBBY");
        } else if (game instanceof Game) {
            sender.sendRichMessage("<gray>Game status: INGAME");
        } else if (game instanceof Endlobby) {
            sender.sendRichMessage("<gray>Game status: ENDLOBBY");
        } else if (game == null) {
            sender.sendRichMessage("<gray>No game running");
        } else {
            sender.sendRichMessage("<red>Unknown game running. Stop with /combattest stop.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

}
