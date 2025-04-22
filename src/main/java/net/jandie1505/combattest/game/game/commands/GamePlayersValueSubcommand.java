package net.jandie1505.combattest.game.game.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.data.PlayerData;
import net.jandie1505.combattest.game.game.equipment.EquipmentItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GamePlayersValueSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final Game game;

    public GamePlayersValueSubcommand(@NotNull Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

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

        PlayerData playerData = this.game.getPlayerData(playerId);

        if (playerData == null) {
            sender.sendMessage("§cPlayer not ingame");
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
                    case "alive" -> {

                        if (args.length > 2) {
                            playerData.setAlive(Boolean.parseBoolean(args[2]));
                            sender.sendMessage("§aUpdated alive to " + playerData.isAlive());
                        } else {
                            sender.sendMessage("§7alive: " + playerData.isAlive());
                        }

                    }
                    case "points" -> {

                        if (args.length > 2) {
                            playerData.setPoints(Integer.parseInt(args[2]));
                            sender.sendMessage("§aUpdated points to " + playerData.getPoints());
                        } else {
                            sender.sendMessage("§7points: " + playerData.getPoints());
                        }

                    }
                    case "kills" -> {

                        if (args.length > 2) {
                            playerData.setKills(Integer.parseInt(args[2]));
                            sender.sendMessage("§aUpdated kills to " + playerData.getKills());
                        } else {
                            sender.sendMessage("§7kills: " + playerData.getKills());
                        }

                    }
                    case "deaths" -> {

                        if (args.length > 2) {
                            playerData.setDeaths(Integer.parseInt(args[2]));
                            sender.sendMessage("§aUpdated deaths to " + playerData.getDeaths());
                        } else {
                            sender.sendMessage("§7deaths: " + playerData.getDeaths());
                        }

                    }
                    case "equipments" -> {

                        if (args.length > 2) {

                            String equipmentType = args[2];

                            if (args.length > 3) {

                                int equipmentId = Integer.parseInt(args[3]);

                                if (equipmentId > 0) {
                                    playerData.setEquipment(equipmentType, equipmentId);
                                    sender.sendMessage("§aEquipped equipment " + equipmentType + " with id " + equipmentId);
                                } else {
                                    playerData.setEquipment(equipmentType, -1);
                                    sender.sendMessage("§aEquipment removed");
                                }

                            } else {
                                sender.sendMessage("§7" + equipmentType + ": " + playerData.getEquipments().get(equipmentType));
                            }

                        } else {

                            sender.sendMessage("§7§lPlayer Equipment:§r\n");

                            Map<String, Integer> equipments = playerData.getEquipments();
                            for (String equipmentType : equipments.keySet()) {
                                sender.sendMessage("§7" + equipmentType + ": " + equipments.get(equipmentType));
                            }

                        }

                    }
                    case "equipmentcountdowns" -> {

                        if (args.length > 3) {

                            EquipmentItem.Identifier identifier = new EquipmentItem.Identifier(Integer.parseInt(args[2]), Integer.parseInt(args[3]));

                            if (args.length > 4) {

                                int equipmentCountdown = Integer.parseInt(args[3]);

                                if (equipmentCountdown > 0) {
                                    playerData.setEquipmentCountdown(identifier, equipmentCountdown);
                                    sender.sendMessage("§aSet equipment countdown for " + identifier + " to " + equipmentCountdown);
                                } else {
                                    playerData.setEquipmentCountdown(identifier, -1);
                                    sender.sendMessage("§aEquipment countdown removed");
                                }

                            } else {
                                sender.sendMessage("§7" + identifier + ": " + playerData.getEquipmentCountdowns().get(identifier));
                            }

                        } else {

                            sender.sendMessage("§7§lPlayer Equipment Countdowns:§r\n");

                            Map<EquipmentItem.Identifier, Integer> equipmentCountdowns = playerData.getEquipmentCountdowns();
                            for (EquipmentItem.Identifier itemId : equipmentCountdowns.keySet()) {
                                sender.sendMessage("§7" + itemId + ": " + equipmentCountdowns.get(itemId));
                            }

                        }

                    }
                    default -> {
                        sender.sendMessage("§Invalid value");
                    }
                }

            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cIllegal argument");
            }

        } else { // LIST MOST IMPORTANT VALUES

            sender.sendMessage("§7§lPlayer Data:");
            sender.sendMessage("§7team: " + playerData.getTeam());
            sender.sendMessage("§7alive: " + playerData.isAlive());
            sender.sendMessage("§7points: " + playerData.getPoints());
            sender.sendMessage("§7kills: " + playerData.getKills());
            sender.sendMessage("§7deaths: " + playerData.getDeaths());

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        return switch (args.length) {
            case 1 -> {

                List<String> players = new ArrayList<>();
                for (UUID playerId : this.game.getRegisteredPlayers()) {
                    Player player = this.game.getPlugin().getServer().getPlayer(playerId);

                    if (player != null) {
                        players.add(player.getName());
                    } else {
                        players.add(playerId.toString());
                    }
                }

                yield players;

            }
            case 2 -> List.of("team", "alive", "points", "kills", "deaths", "equipments", "equipmentcountdowns");
            default -> List.of();
        };

    }

    public @NotNull Game getGame() {
        return game;
    }
}
