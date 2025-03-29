package net.jandie1505.combattest.game.lobby.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.jandie1505.combattest.game.lobby.MapData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LobbyValueSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Lobby lobby;

    public LobbyValueSubcommand(@NotNull Lobby lobby) {
        this.lobby = lobby;
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
                            this.lobby.setTime(Integer.parseInt(args[1]));
                            sender.sendRichMessage("<green>Time updated to " + this.lobby.getTime());
                        } else {
                            sender.sendRichMessage("<gold>Time: " + this.lobby.getTime());
                        }

                    }
                    case "map" -> {

                        if (args.length > 1) {
                            String value = args[1];

                            if (value.startsWith("w:")){
                                value = value.substring(2);

                                MapData selectedMap = null;
                                for (MapData mapData : this.lobby.getMaps()) {
                                    if (mapData.getWorld().equals(value)) {
                                        selectedMap = mapData;
                                    }
                                }

                                if (selectedMap == null) {
                                    sender.sendRichMessage("<red>Map not found");
                                    return true;
                                }

                                this.lobby.selectMap(selectedMap);
                                sender.sendRichMessage("<green>Map vote set to " + selectedMap.getName());
                            } else {

                                switch (args[1].toLowerCase()) {
                                    case "null", "clear" -> {
                                        this.lobby.selectMap(null);
                                        sender.sendRichMessage("<green>Map cleared");
                                    }
                                    default -> sender.sendRichMessage("<red>Unknown subcommand");
                                }

                            }

                        } else {

                            MapData selectedMap = lobby.getSelectedMap();
                            if (selectedMap != null) {
                                sender.sendRichMessage("<gray>Vote set to " + selectedMap.getName() + " (" + selectedMap.getWorld() + ")");
                            } else {
                                sender.sendRichMessage("<gray>No vote set");
                            }

                        }

                    }
                    default -> sender.sendRichMessage("<red>Available values: time");
                }

            } else {
                sender.sendRichMessage("<gold>Values:");
                sender.sendRichMessage("<gold>Time: " + this.lobby.getTime());
                sender.sendRichMessage("<gold>Map: " + this.lobby.getSelectedMap());
            }

        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Illegal argument");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        return switch (args.length) {
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "map" -> {
                        List<String> complete = new ArrayList<>();
                        for (MapData map : this.lobby.getMaps()) {
                            complete.add("w:" + map.getWorld());
                        }
                        complete.add("clear");
                        yield complete;
                    }
                    default -> {
                        yield List.of();
                    }
                }
            }
            case 1 -> List.of("time", "map");
            default -> List.of();
        };
    }
}
