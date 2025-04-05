package net.jandie1505.combattest.game.lobby.gui;

import net.chaossquad.mclib.ItemUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.jandie1505.combattest.game.lobby.LobbyPlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LobbyTeamSelectionGUI implements InventoryHolder, ManagedListener {
    @NotNull private static final NamespacedKey LEAVE_BUTTON = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.team_selection.leave");
    @NotNull private static final NamespacedKey CREATE_BUTTON = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.team_selection.create");
    @NotNull private static final NamespacedKey TEAM_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.team_selection.team");
    @NotNull private final Lobby lobby;

    public LobbyTeamSelectionGUI(@NotNull Lobby lobby) {
        this.lobby = lobby;
        this.lobby.registerListener(this);
    }

    // ----- INVENTORY -----

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, Component.text("Team Selection", NamedTextColor.RED, TextDecoration.STRIKETHROUGH));
    }

    public @NotNull Inventory getInventory(@NotNull Player player) {

        LobbyPlayerData playerData = this.lobby.getPlayerMap().get(player.getUniqueId());
        if (playerData == null) {
            return this.getInventory();
        }

        List<Integer> teams = this.lobby.getTeams();

        Inventory inventory = Bukkit.createInventory(this, this.getSize(teams.size()), Component.text("Team Selection"));

        // Join/Leave Button

        if (playerData.getTeam() > 0) {
            ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

            meta.displayName(Component.text("❌ Quit Team", NamedTextColor.RED).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            meta.getPersistentDataContainer().set(LEAVE_BUTTON, PersistentDataType.BOOLEAN, true);
            meta.addItemFlags(ItemFlag.values());

            item.setItemMeta(meta);
            inventory.setItem(0, item);
        } else {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

            meta.displayName(Component.text("➕ Create Team", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            ItemUtils.setCustomHeadForSkullMeta((SkullMeta) meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQzOGQwOGJkMDQwNWMwNWY0N2VhODZkNjY2NDM0MzRmZGQyZThjNDZmZjFlNmY4ODJiYjliZjg5MWM3ZDNhNSJ9fX0=");
            meta.getPersistentDataContainer().set(CREATE_BUTTON, PersistentDataType.BOOLEAN, true);
            meta.addItemFlags(ItemFlag.values());

            item.setItemMeta(meta);
            inventory.setItem(0, item);
        }

        Iterator<Integer> i = teams.iterator();
        for (int slot = 1; slot < inventory.getSize(); slot++) {
            if (!i.hasNext()) break;
            int team = i.next();

            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(item.getType());

            meta.displayName(Component.text("Team " + team, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            meta.lore(List.of(Component.text("Click to join", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
            meta.getPersistentDataContainer().set(TEAM_ID, PersistentDataType.INTEGER, team);
            meta.addItemFlags(ItemFlag.values());

            for (Map.Entry<UUID, LobbyPlayerData> entry : this.lobby.getPlayerMap().entrySet()) {
                if (entry.getValue().getTeam() != team) continue;

                Player firstFoundPlayer = Bukkit.getPlayer(entry.getKey());
                if (firstFoundPlayer == null) continue;

                meta.setPlayerProfile(firstFoundPlayer.getPlayerProfile());
                break;
            }

            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        }

        return inventory;
    }

    // ----- EVENT LISTENERS -----

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
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

        if (meta.getPersistentDataContainer().getOrDefault(LEAVE_BUTTON, PersistentDataType.BOOLEAN, false)) {
            playerData.setTeam(0);
            player.sendRichMessage("<green>You have left your team!");
            player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1, 1);
            player.closeInventory();
        } else if (meta.getPersistentDataContainer().getOrDefault(CREATE_BUTTON, PersistentDataType.BOOLEAN, false)) {

            List<Integer> teams = this.lobby.getTeams();
            for (int value = 1; value <= 100; value++) {
                if (teams.contains(value)) continue;
                playerData.setTeam(value);
                player.sendRichMessage("<green>You have created a new team!");
                player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1, 1);
                player.closeInventory();
                break;
            }

        } else {

            int team = meta.getPersistentDataContainer().getOrDefault(TEAM_ID, PersistentDataType.INTEGER, 0);
            if (team <= 0) return;

            if (playerData.getTeam() == team) {
                player.sendRichMessage("<red>You are already member of this team!");
            } else {
                playerData.setTeam(team);
                player.sendRichMessage("<green>You have joined team <yellow>" + team);
            }

            player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1, 1);
            player.closeInventory();

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
    }

    // ----- UTILITIES -----

    private int getSize(int amount) {

        if (amount <= 8) {
            return 9;
        }

        if (amount <= 17) {
            return 18;
        }

        if (amount <= 26) {
            return 27;
        }

        if (amount <= 35) {
            return 36;
        }

        if (amount <= 44) {
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
