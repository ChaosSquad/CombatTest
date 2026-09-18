package net.jandie1505.combattest.commands.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.storage.DataStorageEditorCommand;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CombatTestConfigEditorSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public CombatTestConfigEditorSubcommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return true;
        return DataStorageEditorCommand.onCommand(this.plugin.config(), sender, label, args);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) return List.of();
        return DataStorageEditorCommand.onTabComplete(this.plugin.config(), sender, args);
    }

}
