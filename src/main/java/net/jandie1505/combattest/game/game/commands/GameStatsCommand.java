package net.jandie1505.combattest.game.game.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GameStatsCommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public GameStatsCommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(this.plugin.getGame() instanceof Game game)) {
            sender.sendRichMessage("<red>No game running");
            return true;
        }

        Player target;
        if (args.length > 0) {

            target = PlayerUtils.getPlayerFromString(args[0]);

            if (target == null) {
                sender.sendRichMessage("<red>Player not found.");
                return true;
            }

        } else {

            if (!(sender instanceof Player player)) {
                sender.sendRichMessage("<red>Usage: /stats <player>");
                return true;
            }

            target = player;
        }

        var targetData = game.getPlayerData(target);
        if (targetData == null) {
            sender.sendRichMessage("<red>Player not found."); // Same es player really not found, to prevent players trying to find invisible / not ingame players.
            return true;
        }

        sender.sendMessage(Component.empty()
                .append(Component.text("Stats of ", NamedTextColor.GOLD))
                .append(Component.empty().append(target.displayName()).color(NamedTextColor.AQUA))
                .append(Component.text(":", NamedTextColor.GOLD))
        );
        sender.sendRichMessage("<gold> - Kills: <green>" + targetData.getKills());
        sender.sendRichMessage("<gold> - Deaths: <red>" + targetData.getDeaths());
        sender.sendRichMessage("<gold> - K/D: <aqua>" + PlayerData.getKD(targetData.getKills(), targetData.getDeaths()));

        var team = targetData.getTeam();
        if (team > 0) {
            sender.sendRichMessage("<gold>Stats of <aqua>Team " + team + "<gold>:");
            sender.sendRichMessage("<gold> - Kills: <green>" + game.getTeamKills(team));
            sender.sendRichMessage("<gold> - Deaths: <red>" + game.getTeamDeaths(team));
            sender.sendRichMessage("<gold> - K/D: <red>" + PlayerData.getKD(game.getTeamKills(team), game.getTeamDeaths(team)));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(this.plugin.getGame() instanceof Game game)) return List.of();
        if (args.length == 1) return game.getOnlinePlayers().stream().map(Player::getName).toList();
        return List.of();
    }
}
