package net.jandie1505.combattest.game.lobby.gui;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.jandie1505.combattest.game.lobby.LobbyPlayerData;
import net.jandie1505.combattest.game.lobby.MapData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class LobbyVoteGUI implements InventoryHolder, ManagedListener {
    @NotNull private static final NamespacedKey MAP_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.map_voting.map_id");
    @NotNull private final Lobby lobby;

    public LobbyVoteGUI(@NotNull Lobby lobby) {
        this.lobby = lobby;
        this.lobby.registerListener(this);
    }

    // ----- INVENTORY -----

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, Component.text("Map Voting", NamedTextColor.RED, TextDecoration.STRIKETHROUGH));
    }

    public @NotNull Inventory getInventory(@NotNull final Player player) {

        LobbyPlayerData playerData = this.lobby.getPlayerMap().get(player.getUniqueId());
        if (playerData == null) {
            return this.getInventory();
        }

        List<MapData> maps = this.lobby.getMaps();
        int inventorySize = this.getSize(maps.size());

        Inventory inventory = Bukkit.createInventory(this, inventorySize, Component.text("Map Voting"));

        Iterator<MapData> i = maps.iterator();
        for (int slot = 0; slot < inventorySize; slot++) {
            if (!i.hasNext()) continue;
            MapData map = i.next();

            ItemStack item;
            ItemMeta meta;

            if (playerData.getVote() == map) {
                item = new ItemStack(Material.LIME_TERRACOTTA);
                meta = Bukkit.getItemFactory().getItemMeta(item.getType());
                meta.lore(List.of(Component.text("selected", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
                meta.addEnchant(Enchantment.LOYALTY, 1, true);
            } else {
                item = new ItemStack(Material.GREEN_TERRACOTTA);
                meta = Bukkit.getItemFactory().getItemMeta(item.getType());
                meta.lore(List.of(Component.text("click to vote", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
            }

            meta.displayName(Component.text(map.getName(), NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            meta.addItemFlags(ItemFlag.values());
            meta.getPersistentDataContainer().set(MAP_ID, PersistentDataType.STRING, map.getWorld());

            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        }

        return inventory;
    }

    // ----- EVENT LISTENERS -----

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);

        if (event.getClickedInventory() != event.getInventory()) return; // Click was not in the voting gui
        if (!(event.getWhoClicked() instanceof Player player)) return; // Get player

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        LobbyPlayerData playerData = this.lobby.getPlayerMap().get(player.getUniqueId());
        if (playerData == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String world = meta.getPersistentDataContainer().getOrDefault(MAP_ID, PersistentDataType.STRING, "");
        if (world.isEmpty()) return;

        MapData clickedMap = this.lobby.getMaps().stream()
                .filter(mapData -> mapData.getWorld().equals(world))
                .findFirst().orElse(null);

        if (playerData.getVote() == clickedMap) {
            playerData.setVote(null);
            player.sendRichMessage("<green>You have removed your map vote");
            player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1, 1);
            player.closeInventory();
        } else {

            MapData map = this.lobby.getMaps().stream()
                    .filter(mapData -> mapData.getWorld().equals(world))
                    .findFirst().orElse(null);

            if (map == null) return;

            playerData.setVote(map);
            player.sendRichMessage(
                    "<green>You have voted for <yellow><map_name>",
                    TagResolver.resolver("map_name", Tag.inserting(Component.text(map.getName()))),
                    TagResolver.resolver("map_world", Tag.inserting(Component.text(map.getWorld())))
            );
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
            player.closeInventory();
        }

    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
    }

    // ----- UTILITIES -----

    private int getSize(int amount) {

        if (amount <= 9) {
            return 9;
        }

        if (amount <= 18) {
            return 18;
        }

        if (amount <= 27) {
            return 27;
        }

        if (amount <= 36) {
            return 36;
        }

        if (amount <= 45) {
            return 45;
        }

        return 54;
    }

    // ----- OTHER -----

    public @NotNull Lobby getLobby() {
        return lobby;
    }

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
