package net.jandie1505.combattest.game.lobby.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.jandie1505.combattest.game.lobby.LobbyPlayerData;
import net.jandie1505.combattest.game.lobby.MapData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LobbyVoteCommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public LobbyVoteCommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>This command can only be executed by a player");
            return true;
        }

        if (!(this.plugin.getGame() instanceof Lobby lobby)) {
            sender.sendRichMessage("<red>No game running");
            return true;
        }

        if (args.length < 1) {

            Component component = Component.empty()
                    .append(Component.text("Available Maps: ", NamedTextColor.GOLD));

            Iterator<MapData> i = lobby.getMaps().iterator();
            while (i.hasNext()) {
                MapData map = i.next();
                component = component.append(Component.text(map.getName(), NamedTextColor.GOLD));
                if (i.hasNext()) component = component.append(Component.text(", ", NamedTextColor.GOLD));
            }

            return true;
        }

        LobbyPlayerData playerData = lobby.getPlayerMap().get(player.getUniqueId());
        if (playerData == null) {
            sender.sendRichMessage("<red>You are not ingame");
            return true;
        }

        String name = args[0];
        for (int i = 1; i < args.length; i++) {
            name = name + " " + args[i];
        }

        if (name.equalsIgnoreCase("clear") || name.equalsIgnoreCase("null")) {
            playerData.setVote(null);
            player.sendRichMessage("<green>You have cleared your vote");
            return true;
        }

        final String nameCopy = name;

        MapData map = lobby.getMaps().stream()
                .filter(mapData -> mapData.getName().equals(nameCopy))
                .findFirst().orElse(null);

        if (map == null) {
            sender.sendRichMessage("<red>This map does not exist");
            return true;
        }

        playerData.setVote(map);
        player.sendRichMessage(
                "<green>You have voted for <yellow><map_name>",
                TagResolver.resolver("map_name", Tag.inserting(Component.text(map.getName()))),
                TagResolver.resolver("map_world", Tag.inserting(Component.text(map.getWorld())))
        );
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(this.plugin.getGame() instanceof Lobby lobby)) return List.of();

        if (args.length == 1) {
            List<String> mapNames = new ArrayList<>();
            mapNames.add("clear");
            mapNames.addAll(lobby.getMaps().stream().map(MapData::getName).toList());
            return mapNames;
        }

        return List.of();
    }
}
