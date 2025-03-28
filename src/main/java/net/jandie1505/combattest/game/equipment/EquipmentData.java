package net.jandie1505.combattest.game.equipment;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Equipment data.<br/>
 * Stores the equipment items and the equipment name.
 */
public class EquipmentData {
    @NotNull private final String name;
    @NotNull private final List<EquipmentItem> items;
    private final int price;
    @NotNull private final List<Integer> upgradeIds;
    private final int downgradeId;
    private final int equipmentLevel;

    /**
     * Read class description.
     * @param name {@link EquipmentData#name()}
     * @param items {@link EquipmentData#items()}
     */
    public EquipmentData(@NotNull String name, @NotNull List<EquipmentItem> items, int price, List<Integer> upgradeIds, int downgradeId, int equipmentLevel) {
        this.name = name;
        this.items = List.copyOf(items);
        this.price = price;
        this.upgradeIds = List.copyOf(upgradeIds);
        this.downgradeId = downgradeId;
        this.equipmentLevel = equipmentLevel;
    }

    /**
     * Equipment name that is displayed to the players.
     * @return equipment name
     */
    public final String name() {
        return this.name;
    }

    /**
     * Items that will be given to the players when they have this equipment.
     * @return equipment items.
     */
    public final List<EquipmentItem> items() {
        return this.items;
    }

    /**
     * Returns the upgrade price of the equipment.
     * @return upgrade price
     */
    public final int price() {
        return this.price;
    }

    /**
     * Returns a list of the ids the equipment can be upgraded to,
     * @return upgrade ids
     */
    public final List<Integer> upgradeIds() {
        return Collections.unmodifiableList(this.upgradeIds);
    }

    /**
     * Returns the id the equipment downgrades to when the equipment holder is downgraded.
     * @return downgrade id
     */
    public final int downgradeId() {
        return this.downgradeId;
    }

    /**
     * Returns the equipment level.<br/>
     * It is used to compare the strength of different equipments.
     * @return equipment level
     */
    public final int equipmentLevel() {
        return this.equipmentLevel;
    }

}
