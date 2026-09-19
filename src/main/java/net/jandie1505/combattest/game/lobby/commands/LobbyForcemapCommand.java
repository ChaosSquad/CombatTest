package net.jandie1505.combattest.game.lobby.commands;

import net.chaossquad.mclib.command.OptionParser;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.config.ConfigKeys;
import net.jandie1505.combattest.constants.Permissions;
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

import java.util.*;

public class LobbyForcemapCommand implements TabCompletingCommandExecutor {
    @NotNull private final CombatTest plugin;

    public LobbyForcemapCommand(@NotNull CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] argsOld) {
        var args = OptionParser.parse(argsOld);

        if (!Permissions.hasPermission(sender, Permissions.FORCE_MAP)) {
            sender.sendRichMessage("<red>No permission.");
            return true;
        }

        if (!(this.plugin.getGame() instanceof Lobby lobby)) {
            sender.sendRichMessage("<red>No game running");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>This command can only be executed by a player");
            return true;
        }

        if (args.args().length < 1) {

            Component component = Component.empty()
                    .append(Component.text("Available Maps: ", NamedTextColor.GOLD));

            Iterator<MapData> i = lobby.getMaps().iterator();
            while (i.hasNext()) {
                MapData map = i.next();
                component = component.append(Component.text(map.getName(), NamedTextColor.GOLD));
                if (i.hasNext()) component = component.append(Component.text(", ", NamedTextColor.GOLD));
            }

            sender.sendMessage(component);
            return true;
        }

        boolean ignoreRestrictions = false;
        if (args.hasOption("ignore-restrictions")) {
            if (Permissions.hasPermission(sender, Permissions.ADMIN)) {
                ignoreRestrictions = true;
            } else {
                sender.sendRichMessage("<red>You don't have the permission to ignore the forcemap restrictions.");
                return true;
            }
        }

        String name = args.args()[0];
        for (int i = 1; i < args.args().length; i++) {
            name = name + " " + args.args()[i];
        }

        if (name.equalsIgnoreCase("clear") || name.equalsIgnoreCase("null")) {

            if (!ignoreRestrictions) {
                sender.sendRichMessage("<red>You can't unset the map.");
                if (Permissions.hasPermission(sender, Permissions.ADMIN)) sender.sendRichMessage("<red>You can ignore this restriction by using the option --ignore-restrictions.");
                return true;
            }

            lobby.selectMap(null);
            player.sendRichMessage("<green>You have unset the map.");
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

        if (lobby.getSelectedMap() != null && !ignoreRestrictions) {
            sender.sendRichMessage("<red>You can't change an already set map.");
            if (Permissions.hasPermission(sender, Permissions.ADMIN)) sender.sendRichMessage("<red>You can ignore this restriction by using the option --ignore-restrictions.");
            return true;
        }

        lobby.selectMap(map);
        player.sendRichMessage(
                "<green>You have selected the map <yellow><map_name>",
                TagResolver.resolver("map_name", Tag.inserting(Component.text(map.getName()))),
                TagResolver.resolver("map_world", Tag.inserting(Component.text(map.getWorld())))
        );
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] oldArgs) {
        if (!(this.plugin.getGame() instanceof Lobby lobby)) return List.of();
        return OptionParser.complete(
                s,
                OptionParser.parse(oldArgs),
                (sender, args) -> {

                    if (args.args().length == 1) {
                        List<String> mapNames = new ArrayList<>();
                        mapNames.add("clear");
                        mapNames.addAll(lobby.getMaps().stream().map(MapData::getName).toList());
                        return mapNames;
                    }

                    return List.of();

                },
                Set.of("ignore-restrictions"),
                Map.of()
        );
    }

}
