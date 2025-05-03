package net.jandie1505.combattest.game.game.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GamePayCommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public GamePayCommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>This command can only be executed by a player");
            return true;
        }

        if (!(this.plugin.getGame() instanceof Game game)) {
            sender.sendRichMessage("<red>No game running");
            return true;
        }

        if (args.length != 2) {
            sender.sendRichMessage("<red>Usage: [...] pay <player> <amount>");
            return true;
        }

        PlayerData senderData = game.getPlayerData(player);
        if (senderData == null) {
            sender.sendRichMessage("<red>You are not ingame");
            return true;
        }

        Player receiver = PlayerUtils.getPlayerFromString(args[0]);
        if (receiver == null) {
            sender.sendRichMessage("<red>Player not found");
            return true;
        }

        PlayerData receiverData = game.getPlayerData(receiver);
        if (receiverData == null) {
            sender.sendRichMessage("<red>Player not found");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Invalid amount");
            return true;
        }

        if (amount <= 0) {
            sender.sendRichMessage("<red>You can't steal money from other players");
            return true;
        }

        if (senderData.getPoints() < amount) {
            sender.sendRichMessage("<red>You don't have enough points");
            return true;
        }

        senderData.setPoints(senderData.getPoints() - amount);
        receiverData.setPoints(receiverData.getPoints() + amount);
        player.sendRichMessage("<green>You have successfully paid " + amount + " points to " + receiver.getName());
        receiver.sendRichMessage("<aqua>You have received " + amount + " points from " + sender.getName());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}
