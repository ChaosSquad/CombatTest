package net.jandie1505.combattest.commands.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SettingsSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public SettingsSubcommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "autostart" -> {

                    if (args.length > 1) {
                        this.plugin.setAutostartNewGame(Boolean.parseBoolean(args[1]));
                        sender.sendRichMessage("<green>Updated autostart setting to " + this.plugin.isAutostartNewGame());
                    } else {
                        sender.sendRichMessage("<gold>Autostart: " + this.plugin.isAutostartNewGame());
                    }

                }
                case "single_server" -> {

                    if (args.length > 1) {
                        sender.sendRichMessage("<red>Setting cannot be changed on runtime");
                    } else {
                        sender.sendRichMessage("<gold>Single Server Mode: " + this.plugin.isSingleServer());
                    }

                }
                case "cloud_system_mode" -> {

                    if (args.length > 1) {
                        sender.sendRichMessage("<red>Setting cannot be changed on runtime");
                    } else {
                        sender.sendRichMessage("<gold>Cloud System Mode: " + this.plugin.isCloudSystemMode());
                    }

                }
                default -> sender.sendRichMessage("<red>Unknown setting");
            }
        } else {
            sender.sendRichMessage("<gold>Settings");
            sender.sendRichMessage("<gold>Autostart: " + this.plugin.isAutostartNewGame());
            sender.sendRichMessage("<gold>Single Server: " + this.plugin.isSingleServer());
            sender.sendRichMessage("<gold>Cloud System Mode: " + this.plugin.isCloudSystemMode());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> List.of("autostart", "single_server", "cloud_system_mode");
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "autostart" -> {
                        yield List.of("true", "false");
                    }
                    default -> {
                        yield List.of();
                    }
                }
            }
            default -> List.of();
        };
    }
}
