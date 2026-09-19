package net.jandie1505.combattest.game.lobby.commands;

import net.chaossquad.mclib.command.OptionParser;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;

import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LobbyStartCommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public LobbyStartCommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {
        OptionParser.Result args = OptionParser.parse(oldArgs);

        if (!(this.plugin.getGame() instanceof net.jandie1505.combattest.game.lobby.Lobby lobby)) {
            sender.sendRichMessage("<red>There is currently no lobby running.");
            return true;
        }

        if (!Permissions.hasPermission(sender, Permissions.START)) {
            sender.sendRichMessage("<red>You don't have permission to force-start a game.");
            return true;
        }

        boolean ignoreRequirements = false;

        if (args.hasOption("ignore-requirements")) {

            if (Permissions.hasPermission(sender, Permissions.ADMIN)) {
                ignoreRequirements = true;
            } else {
                sender.sendRichMessage("<red>You don't have the permission to ignore the force-start requirements.");
                return true;
            }

        }

        if (ignoreRequirements) {
            lobby.forcestart();
            sender.sendRichMessage("<green>Force-started game (ignoring requirements).");
            return true;
        }

        if (lobby.getOnlinePlayers().size() < 2) {
            sender.sendRichMessage("<red>There must be at least 2 players online to force-start a game.");
            if (Permissions.hasPermission(sender, Permissions.ADMIN)) sender.sendRichMessage("<red>Since you have admin permissions, you can use the --ignore-requirements option to ignore this requirement.");
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return OptionParser.complete(sender, OptionParser.parse(args), null, Set.of("ignore-requirements"), Map.of());
    }

}
