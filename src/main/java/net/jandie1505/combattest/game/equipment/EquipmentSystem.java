package net.jandie1505.combattest.game.equipment;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.Game;
import net.jandie1505.combattest.game.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This system is an adapted version from the CastleConquest2 equipment system.<br/>
 * It replaces the old CombatTest equipment system and makes it more flexible.
 */
public class EquipmentSystem implements Removable {
    @NotNull private final Game game;
    @NotNull private final Map<Integer, EquipmentData> equipmentMap;
    @NotNull private final Removable removeCondition;

    public EquipmentSystem(@NotNull Game game, @Nullable Removable removeCondition) {
        this.game = game;
        this.equipmentMap = new HashMap<>();
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;

        // Tasks

        this.game.getTaskScheduler().scheduleRepeatingTask(() -> {
            for (Player player : this.game.getOnlinePlayers()) {
                this.clearOtherEquipments(player);
            }
        }, 1, 300, this, "equipment_system_clear");

        this.game.getTaskScheduler().scheduleRepeatingTask(() -> {
            for (Player player : this.game.getOnlinePlayers()) {
                this.givePlayerEquipments(player);
            }
        }, 1, 1, this, "equipment_system_give");

        // Listener

        this.game.registerListener(new InternalListener());
    }

    // ----- ACTIONS -----

    /**
     * Removes equipment from a player which that player is not allowed to have.
     * @param player bukkit player which is not null
     */
    public final void clearOtherEquipments(@NotNull Player player) {

        // get player data

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        // Check if player data is null

        // Check inventory

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);

            // Check if item is allowed

            if (this.isItemAllowed(playerData, item)) {
                continue;
            }

            // Clear item

            player.getInventory().setItem(i, new ItemStack(Material.AIR));

        }

    }

    /**
     * Gives the player all their equipments.
     * @param player player
     */
    public void givePlayerEquipments(@NotNull Player player) {
        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;
        if (!playerData.isAlive()) return;

        Map<String, Integer> equipments = playerData.getEquipments();
        for (String specifier : equipments.keySet()) {
            Integer equipmentId = equipments.get(specifier);
            if (equipmentId == null) continue;
            this.givePlayerEquipment(player, equipmentId);
        }
    }

    /**
     * Gives a player equipment.
     * @param player bukkit player which is not null
     */
    public final void givePlayerEquipment(@NotNull Player player, int equipmentId) {

        // get player data

        PlayerData playerData = this.game.getPlayerData(player);

        // Check if player data is null

        if (playerData == null) {
            return;
        }

        // Check if no item is selected

        if (equipmentId < 1) {
            return;
        }

        // Get equipment data

        EquipmentData equipmentData = this.equipmentMap.get(equipmentId);

        // Check if equipment does exist

        if (equipmentData == null) {
            return;
        }

        // Handle all equipment items

        for (int itemId = 0; itemId < equipmentData.items().size(); itemId++) {
            EquipmentItem equipmentItem = equipmentData.items().get(itemId);
            EquipmentItem.Identifier identifier = new EquipmentItem.Identifier(equipmentId, itemId);

            // Check if the item exists (if the player already has the item)

            boolean itemAlreadyExists = false;
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);

                EquipmentItem.Identifier currentIdentifier = EquipmentItem.getIdentifierFromItem(itemStack);
                if (currentIdentifier == null) continue;

                if (currentIdentifier.equipmentId() != equipmentId) continue;
                if (currentIdentifier.itemId() != itemId) continue;

                itemAlreadyExists = true;
                break;
            }

            if (itemAlreadyExists) {
                continue;
            }

            EquipmentItem.Identifier itemOnCursorIdentifier = EquipmentItem.getIdentifierFromItem(player.getItemOnCursor());
            if (itemOnCursorIdentifier != null && itemOnCursorIdentifier.equipmentId() == equipmentId && itemOnCursorIdentifier.itemId() == itemId) {
                continue;
            }

            // Check for item priority

            if (equipmentItem.slot() != EquipmentSlot.HAND) {
                ItemStack existingItem = null;

                switch (equipmentItem.slot()) {
                    case OFF_HAND -> existingItem = player.getInventory().getItemInOffHand();
                    case HEAD -> existingItem = player.getInventory().getHelmet();
                    case CHEST -> existingItem = player.getInventory().getChestplate();
                    case LEGS -> existingItem = player.getInventory().getLeggings();
                    case FEET -> existingItem = player.getInventory().getBoots();
                }

                if (existingItem != null && existingItem.getItemMeta() != null && existingItem.getItemMeta().getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_EQUIPMENT_PRIORITY, PersistentDataType.INTEGER, 0) >= equipmentItem.priority()) {
                    continue;
                }

            }

            // Check if item has countdown
            // If no countdown is set (countdown < 0), set countdown to maximum and don't give item.
            // If a countdown exists (countdown > 0), count down and don't give item.
            // If the countdown has expired (countdown == 0), remove the countdown and give item.

            if (equipmentItem.countdown() > 0) {
                int countdown = playerData.getEquipmentCountdown(identifier);

                if (countdown > 0) {
                    playerData.setEquipmentCountdown(identifier, countdown - 1);
                    continue;
                }

                if (countdown < 0) {
                    playerData.setEquipmentCountdown(identifier, equipmentItem.countdown());
                    continue;
                }

                playerData.setEquipmentCountdown(identifier, -1);

            }

            // Get item

            ItemStack item = equipmentItem.item(identifier);

            // Check if item is a trident with loyalty
            // If true, the trident will be not given to the player when the trident of that player still exists as an entity

            if (item.getType() == Material.TRIDENT && item.getItemMeta() != null && item.getItemMeta().getEnchants().containsKey(Enchantment.LOYALTY)) {

                boolean tridentContinue = false;
                for (Trident trident : this.game.getWorld().getEntitiesByClass(Trident.class)) {

                    if (trident == null) {
                        continue;
                    }

                    if (trident.getShooter() != player) {
                        continue;
                    }

                    EquipmentItem.Identifier tridentIdentifier = EquipmentItem.getIdentifierFromItem(trident.getItemStack());

                    if (tridentIdentifier == null) {
                        continue;
                    }

                    if (!tridentIdentifier.equals(identifier)) {
                        continue;
                    }

                    tridentContinue = true;
                    break;
                }

                if (tridentContinue) continue;

            }

            // Add information to item lore

            this.onGivePlayerEquipmentItemReadyToBeGiven(identifier, equipmentData, equipmentItem, item, player, playerData);

            // Clear the old item before giving the new item

            this.clearOtherEquipments(player);

            // Give the new item

            switch (equipmentItem.slot()) {
                case OFF_HAND -> player.getInventory().setItemInOffHand(item);
                case HEAD -> player.getInventory().setHelmet(item);
                case CHEST -> player.getInventory().setChestplate(item);
                case LEGS -> player.getInventory().setLeggings(item);
                case FEET -> player.getInventory().setBoots(item);
                case HAND -> player.getInventory().addItem(item);
            }

        }

    }

    /**
     * Called when an item in {@link EquipmentSystem#givePlayerEquipment(Player, int)} is ready to be given to the player.
     * @param identifier identifier (equipment id and item id)
     * @param equipmentData equipment data
     * @param equipmentItem equipment item
     * @param item the item that is given to the player
     * @param player player the item is given to
     * @param playerData player data
     */
    @ApiStatus.OverrideOnly
    public void onGivePlayerEquipmentItemReadyToBeGiven(@NotNull EquipmentItem.Identifier identifier, @NotNull EquipmentData equipmentData, @NotNull EquipmentItem equipmentItem, @NotNull ItemStack item, @NotNull Player player, @NotNull PlayerData playerData) {}

    // ----- CHECKS -----

    /**
     * Checks if the item is allowed with the specified player data.
     * @param playerData player data
     * @param item item
     * @return true if item is allowed
     */
    private boolean isItemAllowed(@NotNull PlayerData playerData, @Nullable ItemStack item) {

        if (item == null) {
            return true;
        }

        // Get the equipment ids of the item

        EquipmentItem.Identifier equipmentIdentifier = EquipmentItem.getIdentifierFromItem(item);
        if (equipmentIdentifier == null) return true;

        // Check if player is allowed to have this equipment

        return playerData.hasEquipment(equipmentIdentifier.equipmentId());
    }

    // ----- OTHER -----

    public final @NotNull Game getGame() {
        return game;
    }

    public final @NotNull Map<Integer, EquipmentData> getEquipmentMap() {
        return equipmentMap;
    }

    public final @Nullable EquipmentData getEquipment(int equipmentId) {
        if (equipmentId < 1) return null;
        return this.equipmentMap.get(equipmentId);
    }

    @Override
    public boolean toBeRemoved() {
        return this.removeCondition.toBeRemoved();
    }

    // ----- LISTENER -----

    public class InternalListener implements ManagedListener {

        /**
         * Prevent players from dropping equipment items.
         */
        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent event) {
            if (EquipmentSystem.this.getGame().getPlugin().isPlayerBypassing(event.getPlayer())) return;
            if (EquipmentItem.getIdentifierFromItem(event.getItemDrop().getItemStack()) == null) return;
            event.setCancelled(true);
        }

        @EventHandler
        public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("You cannot modify your offhand slot", NamedTextColor.RED));
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {

            // Handle other inventories (prevent doing anything with equipment items in other inventories)
            if (event.getInventory().getHolder() != event.getWhoClicked()) {

                // Prevent putting the item into other inventories
                EquipmentItem.Identifier identifier = EquipmentItem.getIdentifierFromItem(event.getCurrentItem());
                if (identifier != null) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(Component.text("You cannot move equipment to other inventories", NamedTextColor.RED));
                }

                return;
            }

            // Handle player inventory

            EquipmentItem.Identifier equipmentIdentifier = EquipmentItem.getIdentifierFromItem(event.getCurrentItem());

            // Prevents modifying the offhand slot
            if (event.getSlot() == 40) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(Component.text("You cannot modify your offhand slot", NamedTextColor.RED));
                return;
            }

            // Prevents modifying the armor slots
            if (event.getSlot() == 36 || event.getSlot() == 37 || event.getSlot() == 38 || event.getSlot() == 39) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(Component.text("You cannot modify your armor slots", NamedTextColor.RED));
                return;
            }

            // Prevents modifying the offhand slot using swap hands key
            if (event.getClick() == ClickType.SWAP_OFFHAND) {
                event.setCancelled(true);
                return;
            }

            // Prevents dropping equipment
            if (event.getClick() == ClickType.DROP && equipmentIdentifier != null) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(Component.text("You cannot drop your equipment", NamedTextColor.RED));
                return;
            }

            // Handle cursor item
            EquipmentItem.Identifier cursorItemIdentifier = EquipmentItem.getIdentifierFromItem(event.getCursor());
            if (cursorItemIdentifier != null) {

                // Prevent dropping the item by moving it outside the inventory
                if (event.getClickedInventory() == null) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(Component.text("You cannot drop your equipment", NamedTextColor.RED));
                    return;
                }

                if (event.getSlotType() == InventoryType.SlotType.CRAFTING || event.getSlotType() == InventoryType.SlotType.RESULT) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(Component.text("You cannot put equipment items to your crafting slots", NamedTextColor.RED));
                    return;
                }

            }

        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent event) {

            if (event.getInventory().getHolder() != event.getWhoClicked()) {

                // Prevent putting the item into other inventories
                EquipmentItem.Identifier identifier = EquipmentItem.getIdentifierFromItem(event.getOldCursor());
                if (identifier != null) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(Component.text("You cannot drag equipment to other inventories", NamedTextColor.RED));
                }

                return;
            }

            // Handle player inventory

            // Prevents modifying the offhand slot using inventory drag
            if (event.getInventorySlots().contains(40)) {
                event.setCancelled(true);
                return;
            }

            // Prevents modifying the armor slots using inventory drag
            if (event.getInventorySlots().contains(36) || event.getInventorySlots().contains(37) || event.getInventorySlots().contains(38) || event.getInventorySlots().contains(39)) {
                event.setCancelled(true);
                return;
            }

            // Handle cursor
            EquipmentItem.Identifier equipmentIdentifier = EquipmentItem.getIdentifierFromItem(event.getOldCursor());
            if (equipmentIdentifier != null) {

                // Block Crafting
                if (event.getRawSlots().contains(0) || event.getRawSlots().contains(1) || event.getRawSlots().contains(2) || event.getRawSlots().contains(3) || event.getRawSlots().contains(4)) {
                    event.setCancelled(true);
                    return;
                }

            }

        }

        @Override
        public boolean toBeRemoved() {
            return EquipmentSystem.this.toBeRemoved();
        }
    }

}
