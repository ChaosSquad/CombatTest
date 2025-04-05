package net.jandie1505.combattest.game.game.listeners;

import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.game.Game;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameInventoryListener implements ManagedListener {
    @NotNull private final Game game;

    public GameInventoryListener(@NotNull Game game) {
        this.game = game;
    }

    // ----- MENU ITEM -----

    @EventHandler
    public void onInventoryClickForPlayerMenuItem(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!this.game.isPlayerIngame(player)) return;
        if (!this.isPlayerMenuItem(event.getCurrentItem())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDragForPlayerMenuItem(@NotNull InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!this.game.isPlayerIngame(player)) return;
        if (event.getNewItems().values().stream().noneMatch(this::isPlayerMenuItem)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onOffhandSwapForMenuItem(@NotNull PlayerSwapHandItemsEvent event) {
        if (!this.game.isPlayerIngame(event.getPlayer())) return;
        if (!this.isPlayerMenuItem(event.getOffHandItem())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractForMenuItem(@NotNull PlayerInteractEvent event) {
        if (!this.game.isPlayerIngame(event.getPlayer())) return;
        if (!this.isPlayerMenuItem(event.getItem())) return;

        event.setCancelled(true);

        if (event.getAction().isRightClick()) {
            event.getPlayer().openInventory(this.game.getPlayerMainGUI().getInventory());
            event.getPlayer().playSound(event.getPlayer().getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onPlayerDropItemForMenuItem(@NotNull PlayerDropItemEvent event) {
        if (!this.game.isPlayerIngame(event.getPlayer())) return;
        if (!this.isPlayerMenuItem(event.getItemDrop().getItemStack())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeathForMenuItemRemoval(@NotNull PlayerDeathEvent event) {
        event.getDrops().removeIf(item -> item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_PLAYER_MENU, PersistentDataType.BOOLEAN, false));
    }

    // ----- UTILITIES -----

    private boolean isPlayerMenuItem(@Nullable ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_PLAYER_MENU, PersistentDataType.BOOLEAN, false);
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
