package net.jandie1505.combattest.constants;

import org.bukkit.NamespacedKey;

public interface NamespacedKeys {
    String NAMESPACE = "combattest";

    NamespacedKey ITEM_EQUIPMENT_ID = new NamespacedKey(NAMESPACE, "item.equipment.equipment_id");
    NamespacedKey ITEM_EQUIPMENT_ITEM_ID = new NamespacedKey(NAMESPACE, "item.equipment.item_id");
    NamespacedKey ITEM_EQUIPMENT_PRIORITY = new NamespacedKey(NAMESPACE, "item.equipment.priority");

    NamespacedKey ITEM_TRIDENT_WEATHER_MANIPULATION = new NamespacedKey(NAMESPACE, "item.trident.weathermanipulation");
    NamespacedKey ITEM_TRIDENT_RANGED_DAMAGE = new NamespacedKey(NAMESPACE, "item.trident.ranged_damage");
    NamespacedKey ITEM_THROWABLE_DAMAGE = new NamespacedKey(NAMESPACE, "item.throwable.damage");

    NamespacedKey ITEM_PLAYER_MENU = new NamespacedKey(NAMESPACE, "item.player_menu");

}
