package net.jandie1505.combattest.game.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class GameValueSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GameValueSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) {
            sender.sendMessage("§cNo permission");
            return true;
        }

        try {

            if (args.length > 0) {

                switch (args[0].toLowerCase()) {
                    case "time" -> {

                        if (args.length > 1) {
                            this.game.setTime(Integer.parseInt(args[1]));
                            sender.sendRichMessage("<green>Time updated to " + this.game.getTime());
                        } else {
                            sender.sendRichMessage("<gold>Time: " + this.game.getTime());
                        }

                    }
                    default -> sender.sendRichMessage("<red>Available values: time");
                }

            } else {
                sender.sendRichMessage("<gold>Values:");
                sender.sendRichMessage("<gold>Time: " + this.game.getTime());
            }

        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Illegal argument");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return List.of("time");
        return List.of();
    }
}
