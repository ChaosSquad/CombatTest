package net.jandie1505.combattest.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.game.Game;
import net.jandie1505.combattest.game.PlayerData;
import net.jandie1505.combattest.game.equipment.EquipmentItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated temporary solution for testing, will be integrated into CombatTest command
 */
@Deprecated
public class CombatTestEquipmentSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public CombatTestEquipmentSubcommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        GamePart gamePart = this.plugin.getGame();
        if (!(gamePart instanceof Game game)) {
            sender.sendRichMessage("<red>Game not running");
            return true;
        }

        UUID playerId = PlayerUtils.getPlayerUUIDFromString(args[0]);

        if (playerId == null) {
            sender.sendMessage("§cPlayer not found");
            return true;
        }

        PlayerData playerData = game.getPlayerData(playerId);

        if (playerData == null) {
            sender.sendMessage("§cPlayer not ingame");
            return true;
        }

        if (args.length < 2) {
            sender.sendRichMessage("<red>Failure");
            return true;
        }

        switch (args[1]) {
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
            default -> sender.sendRichMessage("<red>Unknown subcommand");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length ==1) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        if (args.length == 2) return List.of("equipments", "equipmentcountdowns");
        return List.of();
    }

}
