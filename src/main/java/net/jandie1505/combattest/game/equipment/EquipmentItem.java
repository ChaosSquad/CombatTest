package net.jandie1505.combattest.game.equipment;

import net.jandie1505.combattest.constants.NamespacedKeys;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a single item.<br/>
 * Contains values:<br/>
 * - Item: {@link EquipmentItem#item(Identifier)}<br/>
 * - Slot: {@link EquipmentItem#slot()}<br/>
 * - Priority: {@link EquipmentItem#priority()}<br/>
 * - Countdown: {@link EquipmentItem#countdown()}
 */
public class EquipmentItem {
    @NotNull private final ItemStack item;
    @NotNull private final EquipmentSlot slot;
    private final int priority;
    private final int countdown;

    /**
     * Read class description.
     * @param item {@link EquipmentItem#item(Identifier)}
     * @param slot {@link EquipmentItem#slot()}
     * @param priority {@link EquipmentItem#priority()}
     * @param countdown {@link EquipmentItem#countdown()}
     */
    public EquipmentItem(@NotNull ItemStack item, @NotNull EquipmentSlot slot, int priority, int countdown) {
        this.item = item.clone();
        this.slot = slot;
        this.priority = priority;
        this.countdown = countdown;
    }

    // ----- VALUES -----

    /**
     * The item that should be given to the player.<br/>
     * This method returns an item which is ready to be given to the player.
     * @param identifier item identifier (for writing it onto the item's {@link org.bukkit.persistence.PersistentDataContainer}).
     * @return item
     */
    public final @NotNull ItemStack item(@NotNull Identifier identifier) {
        ItemStack item = this.item.clone();

        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_EQUIPMENT_ID, PersistentDataType.INTEGER, identifier.equipmentId());
        meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_EQUIPMENT_ITEM_ID, PersistentDataType.INTEGER, identifier.itemId());
        meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_EQUIPMENT_PRIORITY, PersistentDataType.INTEGER, this.priority);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * The slot type where the item should be placed.<br/>
     * {@link EquipmentSlot#HAND} means that the item is placed in the normal inventory.<br/>
     * The other slots are for the specific slots like offhand or armor slots.<br/>
     * When the item slot is set to the main inventory, the priority is disabled.
     * @return equipment slot
     */
    public final @NotNull EquipmentSlot slot() {
        return slot;
    }

    /**
     * Priority when multiple items should be placed on the same slot.<br/>
     * The item with the higher priority "wins".<br/>
     * If there are two items with the same priority, a random item "wins".<br/>
     * The priority system is disabled when the item is not placed on a specific slot ({@link EquipmentSlot#HAND}).
     * @return priority
     */
    public final int priority() {
        return priority;
    }

    /**
     * Item countdown.<br/>
     * The system will wait the specified amount of ticks before giving the item to this player again.<br/>
     * The countdown is saved in the countdowns map with the equipment id as key.
     * @return countdown
     */
    public final int countdown() {
        return countdown;
    }

    /**
     * Returns the saved item stack.
     * @deprecated This item should not be given to a player. Use {@link EquipmentItem#item(Identifier)} for that.
     * @return saved item
     */
    @Deprecated
    public final @NotNull ItemStack getRawItem() {
        return item.clone();
    }

    // ----- BUILDERS -----

    public static EquipmentItem defaultSlot(@NotNull ItemStack item) {
        return new EquipmentItem(item, EquipmentSlot.HAND, 0, 0);
    }

    public static EquipmentItem defaultWithCountdown(@NotNull ItemStack item, int countdown) {
        return new EquipmentItem(item, EquipmentSlot.HAND, 0, countdown);
    }

    // ----- IDENTIFIER -----

    /**
     * Returns the item identifier of the specified item.<br/>
     * If the item is not an equipment item, null is returned.
     * @param item item to get info from
     * @return equipment item identifier (or null if not an equipment item)
     */
    public static Identifier getIdentifierFromItem(@Nullable ItemStack item) {
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        int id = meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_EQUIPMENT_ID, PersistentDataType.INTEGER, -1);
        if (id < 1) return null;

        int itemId = meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_EQUIPMENT_ITEM_ID, PersistentDataType.INTEGER, -1);
        if (itemId < 0) return new Identifier(id, -1);

        return new Identifier(id, itemId);
    }

    public record Identifier(int equipmentId, int itemId) {

        @Override
        public String toString() {
            return "[" + equipmentId + ":" + itemId + "]";
        }

    }

}
