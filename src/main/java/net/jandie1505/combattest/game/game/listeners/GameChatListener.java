package net.jandie1505.combattest.game.game.listeners;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameChatListener implements ManagedListener {
    @NotNull private final Game game;

    public GameChatListener(@NotNull Game game) {
        this.game = game;
    }

    // ----- JOIN/LEAVE -----

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (!this.game.isPlayerIngame(event.getPlayer())) {
            event.joinMessage(null);
            return;
        }

        event.joinMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("reconnected", NamedTextColor.YELLOW))
        );

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (!this.game.isPlayerIngame(event.getPlayer())) {
            event.quitMessage(null);
            return;
        }

        event.quitMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("disconnected", NamedTextColor.YELLOW))
        );

    }

    // ----- CHAT -----

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        event.viewers().clear();
        event.viewers().add(this.getGame().getPlugin().getServer().getConsoleSender());

        PlayerData playerData = this.getGame().getPlayerData(event.getPlayer());

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (playerData != null) {
            // INGAME CHAT

            // Chat type (global / team)
            if (playerData.getTeam() <= 0 || message.startsWith("@all ") || message.startsWith("@everyone ") || message.startsWith("@shout ") || message.startsWith("@global ")) {
                // GLOBAL INGAME CHAT

                // Check if global chat is enabled
                /*
                if (!this.getGame().getConfig().optBoolean(ConquestConfigKeys.GLOBAL_CHAT, false)) {
                    event.getPlayer().sendMessage(Component.text("Global chat is disabled", NamedTextColor.RED));
                    event.setCancelled(true);
                    return;
                }

                 */

                // Remove @all string
                if (message.startsWith("@everyone ")) {
                    message = message.substring(10);
                } else if (message.startsWith("@shout ")) {
                    message = message.substring(7);
                } else if (message.startsWith("@global ")) {
                    message = message.substring(8);
                } else if (message.startsWith("@all ")) {
                    message = message.substring(5);
                }

                // Viewers
                event.viewers().addAll(this.getGame().getPlugin().getServer().getOnlinePlayers());

                // Format
                event.renderer((source, sourceDisplayName, msg, viewer) -> {

                    @Nullable PlayerData viewerData = viewer instanceof Player player ? this.game.getPlayerData(player) : null;

                    // Team name
                    Component teamName;
                    if (playerData.getTeam() > 0) {
                        teamName = Component.text("Team " + playerData.getTeam());
                    } else {
                        teamName = Component.text("No team");
                    }

                    NamedTextColor teamColor;
                    if (event.getPlayer() == viewer || (viewerData != null && viewerData.getTeam() > 0 && viewerData.getTeam() == playerData.getTeam())) {
                        teamColor = NamedTextColor.GREEN;
                    } else {
                        teamColor = NamedTextColor.RED;
                    }

                    teamName = teamName.color(teamColor);

                    return Component.empty()
                            .append(Component.text("[").color(NamedTextColor.GRAY))
                            .append(Component.text("Global").color(NamedTextColor.GOLD))
                            .append(Component.text("]").color(NamedTextColor.GRAY))
                            .appendSpace()
                            .append(Component.text("[").color(NamedTextColor.GRAY))
                            .append(teamName)
                            .append(Component.text("]").color(NamedTextColor.GRAY))
                            .appendSpace()
                            .append(event.getPlayer().displayName()).color(teamColor)
                            .append(Component.text(": ").color(NamedTextColor.GRAY))
                            .append(Component.empty().append(msg).color(NamedTextColor.GRAY));
                });
            } else {
                // TEAM CHAT

                // Viewers
                event.viewers().addAll(this.getGame().getPlugin().getServer().getOnlinePlayers().stream()
                        .filter(player -> {
                            PlayerData otherPlayerData = this.getGame().getPlayerData(player);
                            if (otherPlayerData == null) return false;
                            return otherPlayerData.getTeam() == playerData.getTeam();
                        })
                        .toList()
                );

                Component displayName = Component.empty()
                        .append(Component.text("[").color(NamedTextColor.GRAY))
                        .append(Component.text("Team " + playerData.getTeam(), NamedTextColor.GREEN))
                        .append(Component.text("]").color(NamedTextColor.GRAY))
                        .appendSpace()
                        .append(event.getPlayer().displayName().color(NamedTextColor.GREEN))
                        .append(Component.text(": ").color(NamedTextColor.GRAY));

                // Format
                event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, msg) -> Component.empty()
                        .append(displayName)
                        .append(Component.empty().append(msg).color(NamedTextColor.GRAY)))
                );

            }

        } else {
            // SPECTATOR CHAT

            // Viewers
            event.viewers().addAll(this.getGame().getPlugin().getServer().getOnlinePlayers().stream()
                    .filter(player -> this.getGame().getPlayerData(player) == null)
                    .toList()
            );

            // Format
            event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, msg) -> Component.empty()
                    .append(Component.text("[Spectator] ").color(NamedTextColor.GRAY))
                    .append(sourceDisplayName.color(NamedTextColor.GRAY))
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(Component.empty().append(msg).color(NamedTextColor.GRAY)))
            );

        }

        // BYPASSING PLAYERS

        for (Player player : this.getGame().getPlugin().getServer().getOnlinePlayers().stream()
                .filter(player -> this.getGame().getPlugin().isPlayerBypassing(player.getUniqueId()))
                .toList()
        ) {
            event.viewers().add(player);
        }

        // SET MESSAGE

        Component builtMessage;
        if (Permissions.hasPermission(event.getPlayer(), Permissions.CHAT_FORMATTING)) {
            builtMessage = MiniMessage.miniMessage().deserialize(message);
        } else {
            builtMessage = Component.text(message);
        }

        event.message(builtMessage);
    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return game;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
