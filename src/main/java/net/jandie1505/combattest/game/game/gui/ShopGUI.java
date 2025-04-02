package net.jandie1505.combattest.game.game.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.chaossquad.mclib.ItemUtils;
import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShopGUI implements InventoryHolder, ManagedListener {
    @NotNull private static final NamespacedKey BACK_BUTTON = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.shop.back_button");
    @NotNull private static final NamespacedKey SHOP_ITEM_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.shop.item_id");
    @NotNull private final Game game;
    @NotNull private final Removable removeCondition;
    @NotNull private final List<ShopItem> items;

    public ShopGUI(@NotNull Game game, @Nullable Removable removeCondition) {
        this.game = game;
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;
        this.items = new ArrayList<>();
        this.game.registerListener(this);
    }

    // ----- INVENTORY -----

    @Override
    public @NotNull Inventory getInventory() {
        if (!this.game.isListenerRegistered(this)) {
            return Bukkit.createInventory(this, 9, Component.text("Shop", NamedTextColor.RED, TextDecoration.STRIKETHROUGH));
        }

        Iterator<ShopItem> itemIterator = List.copyOf(this.items).iterator();
        int itemIndex = 0;

        Inventory inventory = Bukkit.createInventory(this, this.getSize(), Component.text("Item Shop", NamedTextColor.GOLD));

        for (int slot = 0; slot < inventory.getSize(); slot++) {

            if (slot == 0) {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text("Back to main menu", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                meta.lore(List.of(Component.text("Click to go back to main menu.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
                meta.addItemFlags(ItemFlag.values());
                meta.getPersistentDataContainer().set(BACK_BUTTON, PersistentDataType.BOOLEAN, true);
                ItemUtils.setCustomHeadForSkullMeta((SkullMeta) meta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZlYmFhNDFkMWQ0MDVlYjZiNjA4NDViYjlhYzcyNGFmNzBlODVlYWM4YTk2YTU1NDRiOWUyM2FkNmM5NmM2MiJ9fX0=");
                item.setItemMeta(meta);
                inventory.setItem(slot, item);
                continue;
            }

            if (this.isItemSlot(slot, inventory.getSize())) {

                if (itemIterator.hasNext()) {
                    ShopItem shopItem = itemIterator.next();

                    ItemStack item = shopItem.item().clone();
                    ItemMeta meta = item.getItemMeta() != null ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
                    meta.getPersistentDataContainer().set(SHOP_ITEM_ID, PersistentDataType.INTEGER, itemIndex);
                    meta.lore(this.updateLore(meta.lore(), shopItem.price()));
                    item.setItemMeta(meta);
                    inventory.setItem(slot, item);

                    itemIndex++;
                }

            } else {
                ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta fillerMeta = fillerItem.getItemMeta();
                fillerMeta.displayName(Component.text(" "));
                fillerMeta.addItemFlags(ItemFlag.values());
                fillerItem.setItemMeta(fillerMeta);
                inventory.setItem(slot, fillerItem);
            }

        }

        return inventory;
    }

    private int getSize() {

        if (this.items.size() <= 7) {
            return 27;
        }

        if (this.items.size() <= 14) {
            return 36;
        }

        if (this.items.size() <= 21) {
            return 45;
        }

        return 54;
    }

    private boolean isItemSlot(int slot, int size) {

        if (size <= 27) {
            return (slot > 9 && slot < 17);
        }

        if (size <= 36) {
            return (slot > 9 && slot < 17) || (slot > 18 && slot < 26);
        }

        if (size <= 45) {
            return (slot > 9 && slot < 17) || (slot > 18 && slot < 26) || (slot > 27 && slot < 35);
        }

        return (slot > 9 && slot < 17) || (slot > 18 && slot < 26) || (slot > 27 && slot < 35) || (slot > 36 && slot < 44);
    }

    private @NotNull List<Component> updateLore(@Nullable List<Component> lore, int price) {
        lore = new ArrayList<>(lore != null ? lore : List.of());
        lore.addFirst(Component.text("Price: " + price + "P", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        return lore;
    }

    // ----- EVENTS -----

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);

        if (event.getClickedInventory() != event.getInventory()) return; // Click was not in the upgrade gui
        if (!(event.getWhoClicked() instanceof Player player)) return; // Get player

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (meta.getPersistentDataContainer().getOrDefault(BACK_BUTTON, PersistentDataType.BOOLEAN, false)) {
            player.openInventory(this.game.getPlayerMainGUI().getInventory());
            player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        } else {
            int itemId = meta.getPersistentDataContainer().getOrDefault(SHOP_ITEM_ID, PersistentDataType.INTEGER, -1);

            if (itemId < 0 || itemId >= this.items.size()) {
                return;
            }

            ShopItem shopItem = this.items.get(itemId);

            if (!this.hasFreeSlots(player.getInventory(), shopItem.item())) {
                player.sendRichMessage("<red>You don't have enough space in your inventory!");
                player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 0.5F, 0.0F);
                return;
            }

            if (playerData.getPoints() >= shopItem.price()) {
                playerData.setPoints(playerData.getPoints() - shopItem.price());
                player.getInventory().addItem(shopItem.item().clone());
                player.sendRichMessage(
                        "<green>Successfully purchased <aqua><item_name><reset><green> for <aqua><price>P<green>!",
                        TagResolver.resolver("item_name", Tag.inserting(shopItem.item().displayName())),
                        TagResolver.resolver("price", Tag.inserting(Component.text(shopItem.price())))
                );
                player.playSound(player.getLocation().clone(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            } else {
                player.sendRichMessage("<red>You don't have enough points to buy this item!");
                player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 0.5F, 0F);
            }

        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
    }

    private boolean hasFreeSlots(@NotNull PlayerInventory inventory, @NotNull ItemStack checkedItem) {

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);

            // There is a free slot in the inventory
            if (item == null || item.getType() == Material.AIR) {
                return true;
            }

            // Check if the current slot is the item we want to add
            if (!item.isSimilar(checkedItem)) {
                continue;
            }

            // If the item is the same we want to add and there is still space on the stack, there is space
            if (item.getAmount() < item.getType().getMaxStackSize()) {
                return true;
            }

        }

        return false;
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        try {
            return this.removeCondition.toBeRemoved();
        } catch (Exception e) {
            return true;
        }
    }

    public @NotNull Game getGame() {
        return this.game;
    }

    public @NotNull Removable getRemoveCondition() {
        return this.removeCondition;
    }

    public @NotNull List<ShopItem> getItems() {
        return this.items;
    }

    // ----- ITEM -----

    /**
     * Represents an item in the shop.
     * @param item item
     * @param price price
     */
    public record ShopItem(@NotNull ItemStack item, int price) {

        public @NotNull ItemStack item() {
            return this.item.clone();
        }

    }

}
