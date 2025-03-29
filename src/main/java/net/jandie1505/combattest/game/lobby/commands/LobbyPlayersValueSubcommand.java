package net.jandie1505.combattest.game.lobby.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.jandie1505.combattest.game.lobby.LobbyPlayerData;
import net.jandie1505.combattest.game.lobby.MapData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbyPlayersValueSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Lobby lobby;

    public LobbyPlayersValueSubcommand(@NotNull Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.ADMIN)) {
            sender.sendMessage("§cNo permission");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: [...] value <player> [value [newvalue...]]");
            return true;
        }

        UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);

        if (playerId == null) {
            sender.sendMessage("§cPlayer not found");
            return true;
        }

        LobbyPlayerData playerData = this.lobby.getPlayerMap().get(playerId);

        if (playerData == null) {
            sender.sendMessage("§cPlayer not in lobby");
            return true;
        }

        // GET OR SET VALUES
        if (args.length > 1) {

            try {

                switch (args[1]) {
                    case "team" -> {

                        if (args.length > 2) {
                            playerData.setTeam(Integer.parseInt(args[2]));
                            sender.sendMessage("§aUpdated team to " + playerData.getTeam());
                        } else {
                            sender.sendMessage("§7team: " + playerData.getTeam());
                        }

                    }
                    case "vote" -> {

                        if (args.length > 2) {
                            String value = args[2];

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

                                playerData.setVote(selectedMap);
                                sender.sendRichMessage("<green>Map vote set to " + selectedMap.getName());
                            } else {

                                switch (args[2].toLowerCase()) {
                                    case "null", "clear" -> {
                                        playerData.setVote(null);
                                        sender.sendRichMessage("<green>Vote cleared");
                                    }
                                    default -> sender.sendRichMessage("<red>Unknown subcommand");
                                }

                            }

                        } else {

                            MapData selectedMap = playerData.getVote();
                            if (selectedMap != null) {
                                sender.sendRichMessage("<gray>Vote set to " + selectedMap.getName() + " (" + selectedMap.getWorld() + ")");
                            } else {
                                sender.sendRichMessage("<gray>No vote set");
                            }

                        }

                    }
                    default -> sender.sendMessage("§Invalid value");
                }

            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cIllegal argument");
            }

        } else { // LIST MOST IMPORTANT VALUES

            sender.sendMessage("§7§lPlayer Data:");
            sender.sendMessage("§7team: " + playerData.getTeam());
            sender.sendRichMessage("<gray>vote: " + playerData.getVote());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        return switch (args.length) {
            case 1 -> {

                List<String> players = new ArrayList<>();
                for (UUID playerId : this.lobby.getRegisteredPlayers()) {
                    Player player = this.lobby.getPlugin().getServer().getPlayer(playerId);

                    if (player != null) {
                        players.add(player.getName());
                    } else {
                        players.add(playerId.toString());
                    }
                }

                yield players;

            }
            case 2 -> List.of("team", "vote");
            case 3 -> {
                switch (args[1].toLowerCase()) {
                    case "team" -> {
                        yield List.of("0", "1", "2", "3");
                    }
                    case "vote" -> {
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
            default -> List.of();
        };

    }

    public @NotNull Lobby getLobby() {
        return lobby;
    }
}
