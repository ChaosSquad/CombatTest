package net.jandie1505.combattest.constants;

import org.bukkit.NamespacedKey;

public interface NamespacedKeys {
    String NAMESPACE = "combattest";

    NamespacedKey ITEM_EQUIPMENT_ID = new NamespacedKey(NAMESPACE, "item.equipment.equipment_id");
    NamespacedKey ITEM_EQUIPMENT_ITEM_ID = new NamespacedKey(NAMESPACE, "item.equipment.item_id");
    NamespacedKey ITEM_EQUIPMENT_PRIORITY = new NamespacedKey(NAMESPACE, "item.equipment.priority");

    NamespacedKey ITEM_TRIDENT_WEATHER_MANIPULATION = new NamespacedKey(NAMESPACE, "item.trident.weathermanipulation");
    NamespacedKey ITEM_TRIDENT_RANGED_DAMAGE = new NamespacedKey(NAMESPACE, "item.trident.ranged_damage");

    NamespacedKey ITEM_PLAYER_MENU = new NamespacedKey(NAMESPACE, "item.player_menu");
    NamespacedKey ITEM_VOTING_MENU = new NamespacedKey(NAMESPACE, "item.voting_menu");
    NamespacedKey ITEM_TEAM_SELECTION_MENU = new NamespacedKey(NAMESPACE, "item.team_selection_menu");

    NamespacedKey ITEM_POTION_INTENSITY_MODIFIER = new NamespacedKey(NAMESPACE, "item.modifier.splash_potion.intensity");
    NamespacedKey ITEM_POTION_INSTANT_DAMAGE_CUSTOM_VALUE = new NamespacedKey(NAMESPACE, "item.modifier.splash_potion.instant_damage");

}
