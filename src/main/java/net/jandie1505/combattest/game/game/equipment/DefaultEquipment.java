package net.jandie1505.combattest.game.game.equipment;

import net.jandie1505.combattest.constants.NamespacedKeys;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultEquipment {

    private DefaultEquipment() {}

    public static Map<Integer, EquipmentData> getEquipment() {
        Map<Integer, EquipmentData> equipmentMap = new HashMap<>();

        equipmentMap.put(1000, new EquipmentData("Crappy Sword", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Crappy Sword", Material.STONE_SWORD, 5, -1, Map.of()))), 0, List.of(1100, 1200, 1300), 0, 0));

        equipmentMap.put(1100, new EquipmentData("Iron Sword", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Iron Sword", Material.IRON_SWORD, 6, -1, Map.of()))), 1250, List.of(1101), 0, 1)); // Iron Sword
        equipmentMap.put(1101, new EquipmentData("Iron Sword +", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Iron Sword +", Material.IRON_SWORD, 7, -1, Map.of()))), 1250, List.of(1102), 1100, 2)); // Iron Sword +
        equipmentMap.put(1102, new EquipmentData("Iron Sword ++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Iron Sword ++", Material.IRON_SWORD, 7.5, -1, Map.of()))), 1250, List.of(1110, 1120), 1101, 3)); // Iron Sword ++
        equipmentMap.put(1110, new EquipmentData("Damage Sword", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Damage Sword", Material.DIAMOND_SWORD, 8, -1, Map.of()))), 2500, List.of(1111), 0, 4)); // Damage Sword
        equipmentMap.put(1111, new EquipmentData("Damage Sword +", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Damage Sword +", Material.DIAMOND_SWORD, 8.5, -1, Map.of()))), 2500, List.of(1112), 1110, 5)); // Damage Sword +
        equipmentMap.put(1112, new EquipmentData("Damage Sword ++" , List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Damage Sword ++", Material.DIAMOND_SWORD, 9, -1, Map.of()))), 2500, List.of(1113), 1111, 6)); // Damage Sword ++
        equipmentMap.put(1113, new EquipmentData("Damage Sword +++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Damage Sword +++", Material.NETHERITE_SWORD, 10, -1, Map.of()))), 5000, List.of(), 1112, 7)); // Damage Sword +++
        equipmentMap.put(1120, new EquipmentData("Fire Sword", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Fire Sword", Material.GOLDEN_SWORD, 5, -1, Map.of(Enchantment.FIRE_ASPECT, 1)))), 2500, List.of(1121), 0, 4)); // Fire Sword
        equipmentMap.put(1121, new EquipmentData("Fire Sword +", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Fire Sword +", Material.GOLDEN_SWORD, 5.5, -1, Map.of(Enchantment.FIRE_ASPECT, 1)))), 2500, List.of(1122), 1120, 5)); // Fire Sword +
        equipmentMap.put(1122, new EquipmentData("Fire Sword ++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Fire Sword ++", Material.GOLDEN_SWORD, 5.5, -1, Map.of(Enchantment.FIRE_ASPECT, 2)))), 2500, List.of(1123), 1121, 6)); // Fire Sword ++
        equipmentMap.put(1123, new EquipmentData("Fire Sword +++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Fire Sword +++", Material.GOLDEN_SWORD, 6, -1, Map.of(Enchantment.FIRE_ASPECT, 2)))), 5000, List.of(), 1122, 7)); // Fire Sword +++

        equipmentMap.put(1200, new EquipmentData("Iron Axe", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Iron Axe", Material.IRON_AXE, 8, 0.7, Map.of()))), 1250, List.of(1201), 0, 1)); // Iron Axe
        equipmentMap.put(1201, new EquipmentData("Iron Axe +", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Iron Axe +", Material.IRON_AXE, 8, 0.9, Map.of()))), 1250, List.of(1202), 1200, 2)); // Iron Axe +
        equipmentMap.put(1202, new EquipmentData("Iron Axe ++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Iron Axe ++", Material.IRON_AXE, 9, 0.9, Map.of()))), 1250, List.of(1210, 1220), 1201, 3)); // Iron Axe ++
        equipmentMap.put(1210, new EquipmentData("Heavy Axe", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Heavy Axe", Material.DIAMOND_AXE, 10, 0.5, Map.of()))), 2500, List.of(1211), 0, 4)); // Heavy Axe
        equipmentMap.put(1211, new EquipmentData("Heavy Axe +", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Heavy Axe +", Material.DIAMOND_AXE, 10.5, 0.5, Map.of()))), 2500, List.of(1212), 1210, 5)); // Heavy Axe +
        equipmentMap.put(1212, new EquipmentData("Heavy Axe ++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Heavy Axe ++", Material.DIAMOND_AXE, 11, 0.5, Map.of()))), 2500, List.of(1213), 1211, 6)); // Heavy Axe ++
        equipmentMap.put(1213, new EquipmentData("Heavy Axe +++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Heavy Axe +++", Material.NETHERITE_AXE, 12, 0.5, Map.of()))), 5000, List.of(), 1212, 7)); // Heavy Axe +++
        equipmentMap.put(1220, new EquipmentData("Light Axe", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Light Axe", Material.GOLDEN_AXE, 9, 1, Map.of()))), 2500, List.of(1221), 0, 4)); // Light Axe
        equipmentMap.put(1221, new EquipmentData("Light Axe +", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Light Axe +", Material.GOLDEN_AXE, 9.5, 1, Map.of()))), 2500, List.of(1222), 1220, 5)); // Light Axe +
        equipmentMap.put(1222, new EquipmentData("Light Axe ++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Light Axe ++", Material.GOLDEN_AXE, 9.5, 1.1, Map.of()))), 2500, List.of(1223), 1221, 6)); // Light Axe ++
        equipmentMap.put(1223, new EquipmentData("Light Axe +++", List.of(EquipmentItem.defaultSlot(meleeWeaponBuilder("Light Axe +++", Material.GOLDEN_AXE, 10, 1.1, Map.of()))), 5000, List.of(), 1222, 7)); // Light Axe +++

        equipmentMap.put(1300, new EquipmentData("Damage Potion", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Damage Potion", 1, 4), 60)), 1250, List.of(1301), 0, 1)); // Damage Potion
        equipmentMap.put(1301, new EquipmentData("Damage Potion +", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Damage Potion +", 1, 3.5), 55)), 1250, List.of(1302), 1300, 2)); // Damage Potion +
        equipmentMap.put(1302, new EquipmentData("Damage Potion ++", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Damage Potion ++", 1, 3), 50)), 1250, List.of(1310, 1320), 1301, 3)); // Damage Potion ++
        equipmentMap.put(1310, new EquipmentData("Fast Damage Potion", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Fast Damage Potion", 1, 2.5), 40)), 2500, List.of(1311), 0, 4)); // Fast Damage Potion
        equipmentMap.put(1311, new EquipmentData("Fast Damage Potion +", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Fast Damage Potion +", 1, 2), 30)), 2500, List.of(1312), 1310, 5)); // Fast Damage Potion +
        equipmentMap.put(1312, new EquipmentData("Fast Damage Potion ++", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Fast Damage Potion ++", 1, 1.5), 25)), 2500, List.of(1313), 1311, 6)); // Fast Damage Potion ++
        equipmentMap.put(1313, new EquipmentData("Fast Damage Potion +++", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Fast Damage Potion +++", 1, 1), 20)), 5000, List.of(), 1312, 7)); // Fast Damage Potion +++
        equipmentMap.put(1320, new EquipmentData("Heavy Damage Potion", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Heavy Damage Potion", 2, 4), 60)), 2500, List.of(1321), 0, 4)); // Heavy Damage Potion
        equipmentMap.put(1321, new EquipmentData("Heavy Damage Potion +", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Heavy Damage Potion +", 2, 3.5), 50)), 2500, List.of(1322), 1320, 5)); // Heavy Damage Potion +
        equipmentMap.put(1322, new EquipmentData("Heavy Damage Potion ++", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Heavy Damage Potion ++", 2, 3), 45)), 2500, List.of(1323), 1321, 6)); // Heavy Damage Potion ++
        equipmentMap.put(1323, new EquipmentData("Heavy Damage Potion +++", List.of(EquipmentItem.defaultWithCountdown(potionBuilder("Heavy Damage Potion +++", 2, 2.5), 40)), 5000, List.of(), 1322, 7)); // Heavy Damage Potion +++

        // RANGED

        equipmentMap.put(2000, new EquipmentData("Default Ranged", List.of(), 0, List.of(2100, 2200, 2300), 0, 0)); // Default (No item)

        equipmentMap.put(2100, new EquipmentData("Default Bow", List.of(EquipmentItem.defaultSlot(bowBuilder("Default Bow", 0, 0, 0, 0))), 1250, List.of(2101), 0, 1)); // Default Bow
        equipmentMap.put(2101, new EquipmentData("Default Bow +", List.of(EquipmentItem.defaultSlot(bowBuilder("Default Bow +", 0, 1, 0, 0))), 1250, List.of(2110, 2120), 2100, 2)); // Default Bow +
        equipmentMap.put(2110, new EquipmentData("Powerful Bow", List.of(EquipmentItem.defaultSlot(bowBuilder("Powerful Bow", 1, 0, 0, 0))), 2500, List.of(2111), 0, 4)); // Powerful Bow
        equipmentMap.put(2111, new EquipmentData("Powerful Bow +", List.of(EquipmentItem.defaultSlot(bowBuilder("Powerful Bow +", 2, 0, 0, 0))), 2500, List.of(2112), 2110, 6)); // Powerful Bow +
        equipmentMap.put(2112, new EquipmentData("Powerful Bow ++", List.of(EquipmentItem.defaultSlot(bowBuilder("Most Powerful Pow", 3, 0, 0, 0))), 5000, List.of(), 2111, 7)); // Powerful Bow ++
        equipmentMap.put(2120, new EquipmentData("Misc Bow", List.of(EquipmentItem.defaultSlot(bowBuilder("Misc Bow", 0, 2, 0, 0))), 2500, List.of(2121), 0, 4)); // Misc Bow
        equipmentMap.put(2121, new EquipmentData("Misc Bow +", List.of(EquipmentItem.defaultSlot(bowBuilder("Misc Bow +", 0, 2, 1, 0))), 2500, List.of(2122), 2120, 6)); // Misc Bow +
        equipmentMap.put(2122, new EquipmentData("Misc Bow ++", List.of(EquipmentItem.defaultSlot(bowBuilder("Misc Bow ++", 0, 2, 1, 1))), 5000, List.of(), 2121, 7)); // Misc Bow ++

        equipmentMap.put(2200, new EquipmentData("Default Crossbow", List.of(EquipmentItem.defaultSlot(crossbowBuilder("Default Crossbow", 0, 0, 0, 0))), 1250, List.of(2201), 0, 1)); // Default Crossbow
        equipmentMap.put(2201, new EquipmentData("Default Crossbow +", List.of(EquipmentItem.defaultSlot(crossbowBuilder("Default Crossbow +", 1, 0, 0, 0))), 1250, List.of(2210, 2220), 2200, 2)); // Default Crossbow +
        equipmentMap.put(2210, new EquipmentData("Rocket Launcher Crossbow", List.of(
                EquipmentItem.defaultSlot(crossbowBuilder("Rocket Launcher Crossbow", 1, 0, 0, 1)),
                new EquipmentItem(rocketBuilder("Rocket Tier 1", 1), EquipmentSlot.OFF_HAND, 1, 1)), 2500, List.of(2211), 0, 4)
        ); // Rocket Launcher Crossbow
        equipmentMap.put(2211, new EquipmentData("Rocket Launcher Crossbow +", List.of(
                EquipmentItem.defaultSlot(crossbowBuilder("Rocket Launcher Crossbow +", 1, 0, 0, 2)),
                new EquipmentItem(rocketBuilder("Rocket Tier 2", 2), EquipmentSlot.OFF_HAND, 1, 1)
        ), 2500, List.of(2212), 2210, 6)); // Rocket Launcher Crossbow +
        equipmentMap.put(2212, new EquipmentData("Rocket Launcher Crossbow ++", List.of(
                EquipmentItem.defaultSlot(crossbowBuilder("Rocket Launcher Crossbow ++", 1, 0, 0, 3)),
                new EquipmentItem(rocketBuilder("Rocket Tier 3", 4), EquipmentSlot.OFF_HAND, 1, 1)
        ), 5000, List.of(), 2211, 7)); // Rocket Launcher Crossbow ++
        equipmentMap.put(2220, new EquipmentData("Advanced Crossbow", List.of(EquipmentItem.defaultSlot(crossbowBuilder("Advanced Crossbow", 2, 1, 0, 0))), 2500, List.of(2221), 0, 4)); // Advanced Crossbow
        equipmentMap.put(2221, new EquipmentData("Advanced Crossbow +", List.of(EquipmentItem.defaultSlot(crossbowBuilder("Advanced Crossbow +", 2, 2, 0, 0))), 2500, List.of(2222), 2220, 6)); // Advanced Crossbow +
        equipmentMap.put(2222, new EquipmentData("Advanced Crossbow ++", List.of(EquipmentItem.defaultSlot(crossbowBuilder("Advanced Crossbow ++", 2, 2, 1, 0))), 5000, List.of(), 2221, 7)); // Advanced Crossbow ++

        equipmentMap.put(2300, new EquipmentData("Default Trident", List.of(EquipmentItem.defaultSlot(tridentBuilder("Default Trident", false, 1, 0, 0, 2d, false))), 1250, List.of(2301), 0, 1)); // Default Trident
        equipmentMap.put(2301, new EquipmentData("Default Trident +", List.of(EquipmentItem.defaultSlot(tridentBuilder("Default Trident +", false, 2, 0, 0, 4d, false))), 1250, List.of(2310, 2320), 2300, 2)); // Default Trident +
        equipmentMap.put(2310, new EquipmentData("Riptide Trident", List.of(EquipmentItem.defaultWithCountdown(tridentBuilder("Riptide Trident", false, 1, 0, 2, null, false), 4*20)), 2500, List.of(2311), 0, 4)); // Riptide Trident
        equipmentMap.put(2311, new EquipmentData("Riptide Trident +", List.of(EquipmentItem.defaultWithCountdown(tridentBuilder("Riptide Trident +", false, 1, 0, 3, null, false), 4*20)), 2500, List.of(2312), 2310, 6)); // Riptide Trident +
        equipmentMap.put(2312, new EquipmentData("Riptide Trident ++", List.of(EquipmentItem.defaultWithCountdown(tridentBuilder("Riptide Trident ++", true, 1, 0, 4, null, false), 4*20)), 5000, List.of(), 2311, 7)); // Riptide Trident ++
        equipmentMap.put(2320, new EquipmentData("Extra-loyal Trident", List.of(EquipmentItem.defaultSlot(tridentBuilder("Extra-loyal Trident", false, 3, 0, 0, null, false))), 2500, List.of(2321), 0, 4)); // Extra-loyal Trident
        equipmentMap.put(2321, new EquipmentData("Lightning Trident", List.of(EquipmentItem.defaultSlot(tridentBuilder("Lightning Trident", false, 3, 1, 0, null, false))), 2500, List.of(2322), 2320, 6)); // Lightning Trident
        equipmentMap.put(2322, new EquipmentData("Weather-manipulating Lightning Trident", List.of(EquipmentItem.defaultSlot(tridentBuilder("Weather-manipulating lightning Trident", false, 3, 8, 0, null, true))), 5000, List.of(), 2321, 7)); // Weather-manipulating Lightning Trident

        // ARMOR

        ItemStack decorativeChainmailChestplate = decorativeArmorBuilder(Material.CHAINMAIL_CHESTPLATE, false);
        ItemStack decorativeChainmailLeggings = decorativeArmorBuilder(Material.CHAINMAIL_LEGGINGS, false);
        ItemStack decorativeChainmailBoots = decorativeArmorBuilder(Material.CHAINMAIL_BOOTS, false);
        ItemStack decorativeDiamondChestplate = decorativeArmorBuilder(Material.DIAMOND_CHESTPLATE, false);
        ItemStack decorativeDiamondLeggings = decorativeArmorBuilder(Material.DIAMOND_LEGGINGS, false);
        ItemStack decorativeDiamondBoots = decorativeArmorBuilder(Material.DIAMOND_BOOTS, false);
        ItemStack decorativeNetheriteChestplate = decorativeArmorBuilder(Material.NETHERITE_CHESTPLATE, false);
        ItemStack decorativeNetheriteLeggings = decorativeArmorBuilder(Material.NETHERITE_LEGGINGS, false);
        ItemStack decorativeNetheriteBoots = decorativeArmorBuilder(Material.NETHERITE_BOOTS, false);
        ItemStack decorativeIronChestplate = decorativeArmorBuilder(Material.IRON_CHESTPLATE, true);
        ItemStack decorativeIronLeggings = decorativeArmorBuilder(Material.IRON_LEGGINGS, true);
        ItemStack decorativeIronBoots = decorativeArmorBuilder(Material.IRON_BOOTS, true);
        ItemStack decorativeGoldChestplate = decorativeArmorBuilder(Material.GOLDEN_CHESTPLATE, true);
        ItemStack decorativeGoldLeggings = decorativeArmorBuilder(Material.GOLDEN_LEGGINGS, true);
        ItemStack decorativeGoldBoots = decorativeArmorBuilder(Material.GOLDEN_BOOTS, true);
        ItemStack decorativeLeatherChestplate = decorativeArmorBuilder(Material.LEATHER_CHESTPLATE, false);
        ItemStack decorativeLeatherLeggings = decorativeArmorBuilder(Material.LEATHER_LEGGINGS, false);
        ItemStack decorativeLeatherBoots = decorativeArmorBuilder(Material.LEATHER_BOOTS, false);

        equipmentMap.put(3000, new EquipmentData("Default Armor", List.of(
                new EquipmentItem(armorBuilder("Default Armor", Material.LEATHER_HELMET, 0, 0, 0), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(decorativeLeatherChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeLeatherLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeLeatherBoots, EquipmentSlot.FEET, 0, 0)
        ), 0, List.of(3100), 0, 0)); // Default Armor

        equipmentMap.put(3100, new EquipmentData("Chainmail Armor", List.of(
                new EquipmentItem(armorBuilder("Chainmail Armor", Material.CHAINMAIL_HELMET, 5, 0, 0), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Chainmail Armor Shield", 6), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeChainmailChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeChainmailLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeChainmailBoots, EquipmentSlot.FEET, 0, 0)
        ), 1250, List.of(3101), 0, 1)); // Chainmail Armor
        equipmentMap.put(3101, new EquipmentData("Chainmail Armor +", List.of(
                new EquipmentItem(armorBuilder("Chainmail Armor +", Material.CHAINMAIL_HELMET, 9, 0, 0), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Chainmail Armor + Shield", 13), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeChainmailChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeChainmailLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeChainmailBoots, EquipmentSlot.FEET, 0, 0)
        ), 1250, List.of(3102), 3100, 2)); // Chainmail Armor +
        equipmentMap.put(3102, new EquipmentData("Chainmail Armor ++", List.of(
                new EquipmentItem(armorBuilder("Chainmail Armor ++", Material.CHAINMAIL_HELMET, 12, 0, 0), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Chainmail Armor ++ Shield", 20), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeChainmailChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeChainmailLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeChainmailBoots, EquipmentSlot.FEET, 0, 0)
        ), 1250, List.of(3110, 3120), 3101, 3)); // Chainmail Armor ++

        equipmentMap.put(3110, new EquipmentData("Heavy Armor", List.of(
                new EquipmentItem(armorBuilder("Heavy Armor", Material.DIAMOND_HELMET, 15, 5, 0), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Heavy Armor Shield", 24), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeDiamondChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeDiamondLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeDiamondBoots, EquipmentSlot.FEET, 0, 0)
        ), 2500, List.of(3111), 0, 4)); // Heavy Armor
        equipmentMap.put(3111, new EquipmentData("Heavy Armor +", List.of(
                new EquipmentItem(armorBuilder("Heavy Armor +", Material.DIAMOND_HELMET, 17, 7, 0), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Heavy Armor + Shield", 28), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeDiamondChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeDiamondLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeDiamondBoots, EquipmentSlot.FEET, 0, 0)
        ), 2500, List.of(3112), 3110, 5)); // Heavy Armor +
        equipmentMap.put(3112, new EquipmentData("Heavy Armor ++", List.of(
                new EquipmentItem(armorBuilder("Heavy Armor ++", Material.DIAMOND_HELMET, 18, 8, 0), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Heavy Armor ++ Shield", 33), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeDiamondChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeDiamondLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeDiamondBoots, EquipmentSlot.FEET, 0, 0)
        ), 2500, List.of(3113), 3111, 6)); // Heavy Armor ++
        equipmentMap.put(3113, new EquipmentData("Heavy Armor +++", List.of(
                new EquipmentItem(armorBuilder("Heavy Armor +++", Material.NETHERITE_HELMET, 20, 10, 0), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Heavy Armor +++ Shield", 38), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeNetheriteChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeNetheriteLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeNetheriteBoots, EquipmentSlot.FEET, 0, 0)
        ), 2500, List.of(), 3112, 7)); // Heavy Armor +++

        equipmentMap.put(3120, new EquipmentData("Multi-Defence Armor", List.of(
                new EquipmentItem(armorBuilder("Multi-Defense Armor", Material.IRON_HELMET, 0, 0, 10), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Multi-Defense Armor Shield", 28), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeIronChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeIronLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeIronBoots, EquipmentSlot.FEET, 0, 0)
        ), 2500, List.of(3121), 0, 4)); // Multi-Defence Armor
        equipmentMap.put(3121, new EquipmentData("Multi-Defence Armor +", List.of(
                new EquipmentItem(armorBuilder("Multi-Defense Armor +", Material.IRON_HELMET, 0, 0, 12), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Multi-Defense Armor + Shield", 36), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeIronChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeIronLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeIronBoots, EquipmentSlot.FEET, 0, 0)
        ), 2500, List.of(3122), 3120, 5)); // Multi-Defence Armor +
        equipmentMap.put(3122, new EquipmentData("Multi-Defence Armor ++", List.of(
                new EquipmentItem(armorBuilder("Multi-Defense Armor ++", Material.IRON_HELMET, 0, 0, 13), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Multi-Defense Armor ++ Shield", 45), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeIronChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeIronLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeIronBoots, EquipmentSlot.FEET, 0, 0)
        ), 2500, List.of(3123), 3121, 6)); // Multi-Defence Armor ++
        equipmentMap.put(3123, new EquipmentData("Multi-Defence Armor +++", List.of(
                new EquipmentItem(armorBuilder("Multi-Defense Armor +++", Material.GOLDEN_HELMET, 0, 0, 15), EquipmentSlot.HEAD, 0, 0),
                new EquipmentItem(shieldBuilder("Multi-Defense Armor +++ Shield", 55), EquipmentSlot.OFF_HAND, 0, 10*20),
                new EquipmentItem(decorativeGoldChestplate, EquipmentSlot.CHEST, 0, 0),
                new EquipmentItem(decorativeGoldLeggings, EquipmentSlot.LEGS, 0, 0),
                new EquipmentItem(decorativeGoldBoots, EquipmentSlot.FEET, 0, 0)
        ), 2500, List.of(), 3122, 7)); // Multi-Defence Armor +++

        // RETURN

        return equipmentMap;
    }

    private static ItemStack meleeWeaponBuilder(String name, Material material, double attackDamage, double attackSpeed, Map<Enchantment, Integer> enchantments) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName("§r" + name);
        List<String> lore = new ArrayList<>();
        meta.addItemFlags(ItemFlag.values());
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (attackDamage >= 0) {
            lore.add("§7Damage: " + attackDamage);
            attackDamage = attackDamage - 1;
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(NamespacedKey.minecraft("generic.attack_damage"), attackDamage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        }

        if (attackSpeed >= 0) {
            lore.add("§7Speed: " + attackSpeed);
            attackSpeed = attackSpeed - 3.5;
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(NamespacedKey.minecraft("generic.attack_speed"), attackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        }

        for (Enchantment enchantment : enchantments.keySet()) {
            int level = enchantments.get(enchantment);

            if (level < 0) {
                continue;
            }

            meta.addEnchant(enchantment, level, true);

        }

        meta.setLore(lore);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;

    }

    private static ItemStack potionBuilder(String name, int level, double cooldown) {

        ItemStack item = new ItemStack(Material.SPLASH_POTION);

        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.SPLASH_POTION);

        meta.setDisplayName("§r" + name);
        meta.setLore(List.of("Cooldown: " + cooldown));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        meta.setColor(Color.fromRGB(4459017));

        if (level > 0) {

            level = level - 1;

            meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, level), true);

        }

        item.setItemMeta(meta);

        return item;

    }

    private static ItemStack bowBuilder(String name, int power, int punch, int flame, int infinity) {

        ItemStack item = new ItemStack(Material.BOW);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.setDisplayName("§r" + name);

        if (power > 0) {
            meta.addEnchant(Enchantment.POWER, power, true);
        }

        if (punch > 0) {
            meta.addEnchant(Enchantment.PUNCH, punch, true);
        }

        if (flame > 0) {
            meta.addEnchant(Enchantment.FLAME, flame, true);
        }

        if (infinity > 0) {
            meta.addEnchant(Enchantment.INFINITY, infinity, true);
        }

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack crossbowBuilder(String name, int quickCharge, int piercing, int multiShot, int rocketLevel) {

        ItemStack item = new ItemStack(Material.CROSSBOW);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.setDisplayName("§r" + name);

        if (quickCharge > 0) {
            meta.addEnchant(Enchantment.QUICK_CHARGE, quickCharge, true);
        }

        if (piercing > 0) {
            meta.addEnchant(Enchantment.PIERCING, piercing, true);
        }

        if (multiShot > 0) {
            meta.addEnchant(Enchantment.MULTISHOT, multiShot, true);
        }

        if (rocketLevel > 0) {
            meta.setLore(List.of("§r§7Ammunition: Rockets Lvl. " + rocketLevel));
        }

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack tridentBuilder(String name, boolean meleeDamage, int loyalty, int channeling, int riptide, @Nullable Double rangedDamage, boolean weatherManipulationText) {

        ItemStack item = new ItemStack(Material.TRIDENT);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.setDisplayName("§r" + name);

        if (loyalty > 0) {
            meta.addEnchant(Enchantment.LOYALTY, loyalty, true);
        } else {
            meta.addEnchant(Enchantment.LOYALTY, 1, true);
        }

        if (channeling > 0) {
            meta.addEnchant(Enchantment.CHANNELING, channeling, true);
        }

        if (riptide > 0) {
            meta.addEnchant(Enchantment.RIPTIDE, riptide, true);
        }

        List<String> lore = new ArrayList<>();

        lore.add("§7Ranged Damage: " + rangedDamage);

        if (rangedDamage != null) {
            meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_TRIDENT_RANGED_DAMAGE, PersistentDataType.DOUBLE, rangedDamage);
        }

        if (weatherManipulationText) {
            meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_TRIDENT_WEATHER_MANIPULATION, PersistentDataType.BOOLEAN, true);
            lore.add("§750% chance of starting a thunderstorm");
            lore.add(" §7when hitting a player");
        }

        if (!meleeDamage) {
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(NamespacedKey.minecraft("generic.attack_damage"), 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
            lore.add("§7Melee damage disabled");
        }

        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        //if (rangedDamage >= 0) {
        //    meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_THROWABLE_DAMAGE, PersistentDataType.DOUBLE, rangedDamage);
        //}

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack armorBuilder(String name, Material material, int armor, int toughness, int protection) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

        meta.setDisplayName("§r" + name);
        meta.setLore(List.of("§r§7Armor: " + armor, "§r§7Toughness: " + toughness, "§r§7Protection: " + protection));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(NamespacedKey.minecraft("generic.armor"), armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(NamespacedKey.minecraft("generic.armor_toughness"), toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));

        if (protection > 0) {
            meta.addEnchant(Enchantment.PROTECTION, protection, true);
        }

        if (meta instanceof ArmorMeta armorMeta && !(meta instanceof LeatherArmorMeta)) {
            armorMeta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.WARD));
        }

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack decorativeArmorBuilder(Material material, boolean glint) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.setDisplayName("§r§7Armor part");
        meta.setLore(List.of("§r§7See helmet for armor details."));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(NamespacedKey.minecraft("generic.armor"), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ARMOR));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(NamespacedKey.minecraft("generic.armor_toughness"), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ARMOR));

        if (glint) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        }
        if (meta instanceof ArmorMeta armorMeta && !(meta instanceof LeatherArmorMeta)) {
            armorMeta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.WARD));
        }

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack shieldBuilder(String name, int durability) {

        ItemStack item = new ItemStack(Material.SHIELD);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.SHIELD);

        meta.setDisplayName(name);
        meta.setLore(List.of("§r§7Press F to reload shield"));

        item.setItemMeta(meta);

        if (durability < 336) {
            item.setDurability((short) (336 - durability));
        }

        return item;

    }

    public static ItemStack rocketBuilder(String name, int fireworkStars) {

        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);

        FireworkMeta meta = (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);

        meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.values());

        meta.setPower(fireworkStars * 2);

        for (int i = 0; i < fireworkStars; i++) {
            meta.addEffect(FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());
        }

        meta.setLore(List.of("§r§7Firework Stars: " + meta.getEffects().size(), "§r§7Power: " + meta.getPower()));

        item.setItemMeta(meta);

        return item;

    }

}
