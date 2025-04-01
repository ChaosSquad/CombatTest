package net.jandie1505.combattest.game.game.gui;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.jandie1505.combattest.game.game.equipment.EquipmentData;
import net.jandie1505.combattest.game.game.equipment.EquipmentItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The GUI responsible for upgrading or resetting player equipment.
 */
public class EquipmentUpgradeGUI implements InventoryHolder, ManagedListener {
    @NotNull private static final NamespacedKey EQUIPMENT_CATEGORY = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.equipment.equipment.category");
    @NotNull private static final NamespacedKey EQUIPMENT_ID = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.equipment.equipment.id");
    @NotNull private static final NamespacedKey RESET_BUTTON = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.equipment.reset_button");
    @NotNull private final Game game;
    @NotNull private final Removable removeCondition;

    public EquipmentUpgradeGUI(@NotNull Game game, @Nullable Removable removeCondition) {
        this.game = game;
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;
        this.game.registerListener(this);
    }

    // ----- INVENTORY HOLDER -----

    /**
     * This method cannot open the real Equipment GUI, because it does not accept parameters.<br/>
     * It just returns an empty inventory with an error message.
     * @return empty inventory with error message
     */
    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 9, Component.text("Equipment GUI: Error", NamedTextColor.DARK_RED, TextDecoration.STRIKETHROUGH).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
    }

    // ----- GUI BUILDER -----

    /**
     * Opens the upgrade gui from a specific equipment.
     * @param equipmentType equipment type (the key in the PlayerData equipment map)
     * @param equipmentId The equipment the player currently has
     * @return the upgrade gui
     */
    public Inventory getUpgradeGUI(@NotNull String equipmentType, int equipmentId) {
        if (!this.game.isListenerRegistered(this)) return this.getInventory();

        Inventory inventory = this.game.getPlugin().getServer().createInventory(this, 54, Component.text("Equipment Upgrades", NamedTextColor.GRAY));

        EquipmentData equipmentData = this.game.getEquipmentSystem().getEquipment(equipmentId);

        if (equipmentData == null) {
            return this.getInventory();
        }

        ItemStack equipmentItem = null;

        if (!equipmentData.items().isEmpty()) {
            equipmentItem = equipmentData.items().getFirst().item(new EquipmentItem.Identifier(-1, -1)); // Equipment system would clear menu item when player obtains it
        }

        if (equipmentItem == null) {
            ItemStack noItem = new ItemStack(Material.BARRIER);
            ItemMeta noItemMeta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(noItem.getType());

            noItemMeta.setDisplayName("§r§7No item");
            noItemMeta.addItemFlags(ItemFlag.values());

            noItem.setItemMeta(noItemMeta);

            equipmentItem = noItem.clone();
        }

        this.updateName(equipmentItem, Component.text("(current item)", NamedTextColor.GOLD));

        inventory.setItem(13, equipmentItem);

        List<ItemStack> upgradeItems = new ArrayList<>();

        for (Integer upgradeId : equipmentData.upgradeIds()) {
            EquipmentData upgradeData = this.game.getEquipmentSystem().getEquipment(upgradeId);

            if (upgradeData == null) {
                upgradeItems.add(new ItemStack(Material.BARRIER));
                continue;
            }

            ItemStack upgradeItem = null;

            if (!upgradeData.items().isEmpty()) {
                upgradeItem = upgradeData.items().getFirst().item(new EquipmentItem.Identifier(-1, -1)); // Equipment system would clear menu item when player obtains it
            }

            if (upgradeItem == null) {
                continue;
            }

            this.updateName(upgradeItem, Component.text("(click to upgrade for " + upgradeData.price() + "P)", NamedTextColor.GREEN));
            this.updateEquipmentId(upgradeItem, equipmentType, upgradeId);

            upgradeItems.add(upgradeItem);

        }

        switch (upgradeItems.size()) {
            case 1 -> inventory.setItem(40, upgradeItems.get(0));
            case 2 -> {
                inventory.setItem(37, upgradeItems.get(0));
                inventory.setItem(43, upgradeItems.get(1));
            }
            case 3 -> {
                inventory.setItem(37, upgradeItems.get(0));
                inventory.setItem(40, upgradeItems.get(1));
                inventory.setItem(43, upgradeItems.get(2));
            }
            default -> {
                ItemStack noItem = new ItemStack(Material.BARRIER);
                ItemMeta noItemMeta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(noItem.getType());

                noItemMeta.displayName(Component.text("No item", NamedTextColor.RED));
                noItemMeta.addItemFlags(ItemFlag.values());

                noItem.setItemMeta(noItemMeta);

                inventory.setItem(40, new ItemStack(Material.BARRIER));
            }
        }

        ItemStack resetItem = new ItemStack(Material.TNT);
        ItemMeta resetItemMeta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(resetItem.getType());

        resetItemMeta.displayName(Component.text("Reset item", NamedTextColor.RED, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        resetItemMeta.lore(List.of(Component.text("Click here to reset your equipment.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        resetItemMeta.getPersistentDataContainer().set(RESET_BUTTON, PersistentDataType.BOOLEAN, true);
        resetItemMeta.getPersistentDataContainer().set(EQUIPMENT_CATEGORY, PersistentDataType.STRING, equipmentType);

        resetItem.setItemMeta(resetItemMeta);

        inventory.setItem(8, resetItem);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) continue;

            item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta itemMeta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(item.getType());

            itemMeta.displayName(Component.text(" "));
            itemMeta.addItemFlags(ItemFlag.values());

            item.setItemMeta(itemMeta);

            inventory.setItem(i, item);
        }

        return inventory;
    }

    private void updateName(@Nullable ItemStack item, @NotNull Component info) {
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        Component originalDisplayName = meta.displayName();
        Component displayName = Component.empty().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .append(originalDisplayName != null ? originalDisplayName : Component.text(item.getType().name()));

        displayName = displayName.appendSpace().append(info);

        meta.displayName(displayName);
        item.setItemMeta(meta);
    }

    private void updateEquipmentId(ItemStack item, @NotNull String type, int equipmentId) {

        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            meta = this.game.getPlugin().getServer().getItemFactory().getItemMeta(item.getType());
        }

        meta.getPersistentDataContainer().set(EQUIPMENT_CATEGORY, PersistentDataType.STRING, type); // Sets the equipment category to let the event listener know which equipment category has to be set to the new item.
        meta.getPersistentDataContainer().set(EQUIPMENT_ID, PersistentDataType.INTEGER, equipmentId); // Sets the equipment id to let the event listener know to which value the equipment category has to be changed to.

        item.setItemMeta(meta);

    }

    // ----- EVENT LISTENER -----

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
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

        String equipmentType;
        try {
            equipmentType = meta.getPersistentDataContainer().get(EQUIPMENT_CATEGORY, PersistentDataType.STRING);
        } catch (Exception e) {
            equipmentType = null;
        }

        boolean isResetButton = meta.getPersistentDataContainer().getOrDefault(RESET_BUTTON, PersistentDataType.BOOLEAN, false);
        if (isResetButton) {
            this.onResetButtonClick(player, playerData, equipmentType);
        } else {
            int equipmentId = meta.getPersistentDataContainer().getOrDefault(EQUIPMENT_ID, PersistentDataType.INTEGER, -1);
            this.onUpgradeItemClick(player, playerData, equipmentType, equipmentId);
        }

    }

    private void onResetButtonClick(@NotNull final Player player, @NotNull final PlayerData playerData, @Nullable final String type) {
        final int price = 10000;

        if (type == null) {
            player.sendRichMessage("<red>An internal error occurred.");
            return;
        }

        if (playerData.getPoints() >= price) {

            playerData.setPoints(playerData.getPoints() - price);

            playerData.setEquipment(type, -1);

            player.sendRichMessage("<green>You have reset your equipment successfully.");

        } else {
            player.closeInventory();
            player.sendRichMessage("<red>You don't have enough points to reset your equipment!");
            player.playSound(player.getLocation().clone(), Sound.ENTITY_ITEM_PICKUP, SoundCategory.RECORDS, 1, 0);
        }

    }

    private void onUpgradeItemClick(@NotNull final Player player, @NotNull final PlayerData playerData, @Nullable final String type, int equipmentId) {

        if (type == null || equipmentId <= 0) {
            player.sendRichMessage("<red>An internal error occurred.");
            return;
        }

        EquipmentData equipmentData = this.game.getEquipmentSystem().getEquipment(equipmentId);
        if (equipmentData == null || equipmentData.price() < 0) {
            player.sendRichMessage("<red>An internal error occurred.");
            return;
        }

        if (playerData.getPoints() >= equipmentData.price()) {
            playerData.setPoints(playerData.getPoints() - equipmentData.price());
            playerData.setEquipment(type, equipmentId);
            player.sendRichMessage("<green>Equipment successfully upgraded to <aqua><equipment_name><green>!", TagResolver.resolver("equipment_name", Tag.inserting(Component.text(equipmentData.name()))));

            int updatedEquipmentId = playerData.getEquipment(type);
            player.openInventory(this.getUpgradeGUI(type, updatedEquipmentId));
        } else {
            player.closeInventory();
            player.sendRichMessage("<red>You don't have enough points to upgrade!");
        }

    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
    }

    // ----- OTHER -----

    public @NotNull Game getGame() {
        return this.game;
    }

    @Override
    public boolean toBeRemoved() {
        try {
            return removeCondition.toBeRemoved();
        } catch (Exception e) {
            return true;
        }
    }

}
