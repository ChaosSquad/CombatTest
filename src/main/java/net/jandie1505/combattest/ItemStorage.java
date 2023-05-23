package net.jandie1505.combattest;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStorage {
    public static final String MENU_ITEM = "M";
    public static final String HOTBAR_ITEM = "H";
    public static final String EQUIPMENT_MELEE = "EM";
    public static final String EQUIPMENT_RANGED = "ER";
    public static final String EQUIPMENT_ARMOR = "EA";
    public static final String SHOP_ITEM = "EU";
    public static final String EQUIPMENT_OFFHAND = "EO";
    private static final Map<Integer, ItemStack> MELEE_ITEMS;
    private static final Map<Integer, ItemStack> RANGED_ITEMS;
    private static final Map<Integer, ItemStack> ARMOR_ITEMS;
    private static final Map<Integer, ItemStack> OFFHAND_ITEMS;
    private static final Map<Integer, ItemStack> SHOP_ITEMS;

    static {

        // MELEE ITEMS

        Map<Integer, ItemStack> meleeItemsInit = new HashMap<>();

        {
            ItemStack item = new ItemStack(Material.STONE_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STONE_SWORD);

            meta.setDisplayName("Crappy Sword");
            meta.setLore(List.of("EM0", "At least better than fighting with fists", "Damage: 5"));
            meta.addItemFlags(ItemFlag.values());
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(0, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword");
            meta.setLore(List.of("EM100", "Damage: 6"));
            meta.addItemFlags(ItemFlag.values());
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(100, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword +");
            meta.setLore(List.of("EM101", "Damage: 7"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(101, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword ++");
            meta.setLore(List.of("EM102", "Damage: 7.5"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(102, item);
        }

        {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword");
            meta.setLore(List.of("EM1100", "Damage: 8"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1100, item);
        }

        {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword +");
            meta.setLore(List.of("EM1101", "Damage: 8.5"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1101, item);
        }

        {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword ++");
            meta.setLore(List.of("EM1102", "Damage: 9"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1102, item);
        }

        {
            ItemStack item = new ItemStack(Material.NETHERITE_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.NETHERITE_SWORD);

            meta.setDisplayName("Netherite Sword");
            meta.setLore(List.of("EM1103", "Damage: 10"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1103, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword");
            meta.setLore(List.of("EM1200", "Damage: 7", "Fire Aspect I"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1200, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword +");
            meta.setLore(List.of("EM1201", "Damage: 7", "Fire Aspect II"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1201, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword ++");
            meta.setLore(List.of("EM1202", "Damage: 7", "Fire Aspect III"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 3, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1202, item);
        }

        {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Fire Sword +++");
            meta.setLore(List.of("EM1203", "Damage: 8", "Fire Aspect III"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 3, true);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1203, item);
            
        }

        meleeItemsInit.put(200, axeBuilder("Iron Axe", Material.IRON_AXE, 8, 0.7, 200));
        meleeItemsInit.put(201, axeBuilder("Iron Axe +", Material.IRON_AXE, 8, 0.9, 201));
        meleeItemsInit.put(202, axeBuilder("Iron Axe ++", Material.IRON_AXE, 9, 0.9, 202));
        meleeItemsInit.put(1300, axeBuilder("Heavy Axe", Material.DIAMOND_AXE, 10, 0.5, 1300));
        meleeItemsInit.put(1301, axeBuilder("Heavy Axe +", Material.DIAMOND_AXE, 10.5, 0.5, 1301));
        meleeItemsInit.put(1302, axeBuilder("Heavy Axe ++", Material.DIAMOND_AXE, 11, 0.5, 1302));
        meleeItemsInit.put(1303, axeBuilder("Heavy Axe +++", Material.NETHERITE_AXE, 12, 0.5, 1303));
        meleeItemsInit.put(1400, axeBuilder("Light Axe", Material.IRON_AXE, 9, 1, 1400));
        meleeItemsInit.put(1401, axeBuilder("Light Axe +", Material.IRON_AXE, 9.5, 1, 1401));
        meleeItemsInit.put(1402, axeBuilder("Light Axe ++", Material.IRON_AXE, 9.5, 1.1, 1402));
        meleeItemsInit.put(1403, axeBuilder("Light Axe +++", Material.DIAMOND_AXE, 10, 1.1, 1403));

        meleeItemsInit.put(300, potionBuilder("Damage Potion", 1, 4, 300));
        meleeItemsInit.put(301, potionBuilder("Damage Potion +", 1, 3.5, 301));
        meleeItemsInit.put(302, potionBuilder("Damage Potion ++", 1, 3, 302));
        meleeItemsInit.put(1500, potionBuilder("Fast Damage Potion", 1, 2.5, 1500));
        meleeItemsInit.put(1501, potionBuilder("Fast Damage Potion +", 1, 2, 1501));
        meleeItemsInit.put(1502, potionBuilder("Fast Damage Potion ++", 1, 1.5, 1502));
        meleeItemsInit.put(1503, potionBuilder("Fast Damage Potion +++", 1, 1, 1503));
        meleeItemsInit.put(1600, potionBuilder("Heavy Damage Potion", 2, 4, 1600));
        meleeItemsInit.put(1601, potionBuilder("Heavy Damage Potion +", 2, 3.5, 1601));
        meleeItemsInit.put(1602, potionBuilder("Heavy Damage Potion ++", 2, 3, 1602));
        meleeItemsInit.put(1603, potionBuilder("Heavy Damage Potion +++", 2, 2.5, 1603));

        MELEE_ITEMS = Map.copyOf(meleeItemsInit);

        // RANGED ITEMS

        Map<Integer, ItemStack> rangedItemsInit = new HashMap<>();

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Default Bow");
            meta.setLore(List.of("ER100"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            rangedItemsInit.put(100, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Default Bow +");
            meta.setLore(List.of("ER101"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(101, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Powerful Bow");
            meta.setLore(List.of("ER1100"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1100, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Powerful Bow +");
            meta.setLore(List.of("ER1101"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1101, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Most Powerful Bow");
            meta.setLore(List.of("ER1102"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 3, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1102, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Misc Bow");
            meta.setLore(List.of("ER1200"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1200, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Misc Bow +");
            meta.setLore(List.of("ER1201"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1201, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Misc Bow ++");
            meta.setLore(List.of("ER1202"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1202, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Default Crossbow");
            meta.setLore(List.of("ER200"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            rangedItemsInit.put(200, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Default Crossbow +");
            meta.setLore(List.of("ER201"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(201, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Rocket Launcher Crossbow");
            meta.setLore(List.of("ER1300", "Ammunition: Rocket Tier 1"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1300, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Rocket Launcher Crossbow +");
            meta.setLore(List.of("ER1301", "Ammunition: Rocket Tier 2"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1301, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Rocket Launcher Crossbow ++");
            meta.setLore(List.of("ER1302", "Ammunition: Rocket Tier 3"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1302, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Advanced Crossbow");
            meta.setLore(List.of("ER1400"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 2, true);
            meta.addEnchant(Enchantment.PIERCING, 2, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1400, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Advanced Crossbow +");
            meta.setLore(List.of("ER1401"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 3, true);
            meta.addEnchant(Enchantment.PIERCING, 3, true);
            meta.addEnchant(Enchantment.MULTISHOT, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1401, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Advanced Crossbow ++");
            meta.setLore(List.of("ER1402"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 2, true);
            meta.addEnchant(Enchantment.PIERCING, 4, true);
            meta.addEnchant(Enchantment.MULTISHOT, 1, true);

            item.setItemMeta(meta);

            rangedItemsInit.put(1402, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Trident");
            meta.setLore(List.of("ER300", "Melee damage disabled"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 1, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(300, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Trident +");
            meta.setLore(List.of("ER301", "Melee damage disabled"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 2, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(301, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Riptide Trident");
            meta.setLore(List.of("ER1500", "Melee damage disabled"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 2, true);
            meta.addEnchant(Enchantment.RIPTIDE, 2, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1500, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Riptide Trident +");
            meta.setLore(List.of("ER1501", "Melee damage disabled"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 2, true);
            meta.addEnchant(Enchantment.RIPTIDE, 3, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1501, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Riptide Trident ++");
            meta.setLore(List.of("ER1502", "Melee damage disabled"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 2, true);
            meta.addEnchant(Enchantment.RIPTIDE, 4, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1502, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Extra-loyal Trident");
            meta.setLore(List.of("ER1600", "Melee damage disabled"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 3, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1600, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Lightning Trident");
            meta.setLore(List.of("ER1601", "Melee damage disabled"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 3, true);
            meta.addEnchant(Enchantment.CHANNELING, 1, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1601, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Weather-manipulating Trident");
            meta.setLore(List.of("ER1602", "20 % Chance of starting thunderstorms", "Melee damage disabled"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 3, true);
            meta.addEnchant(Enchantment.CHANNELING, 1, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1602, item);
        }

        RANGED_ITEMS = Map.copyOf(rangedItemsInit);

        // ARMOR ITEMS

        Map<Integer, ItemStack> armorItemsInit = new HashMap<>();

        armorItemsInit.put(0, armorBuilder("Default Armor", Material.LEATHER_HELMET, 0, 0, 0, 0));
        armorItemsInit.put(100, armorBuilder("Chainmail Armor", Material.CHAINMAIL_HELMET, 5, 0, 0, 100));
        armorItemsInit.put(101, armorBuilder("Chainmail Armor +", Material.CHAINMAIL_HELMET, 9, 0, 0, 101));
        armorItemsInit.put(102, armorBuilder("Chainmail Armor ++", Material.CHAINMAIL_HELMET, 12, 0, 0, 102));
        armorItemsInit.put(1100, armorBuilder("Heavy Armor", Material.DIAMOND_HELMET, 15, 5, 0, 1100));
        armorItemsInit.put(1101, armorBuilder("Heavy Armor +", Material.DIAMOND_HELMET, 17, 7, 0, 1101));
        armorItemsInit.put(1102, armorBuilder("Heavy Armor ++", Material.DIAMOND_HELMET, 18, 8, 0, 1102));
        armorItemsInit.put(1103, armorBuilder("Heavy Armor +++", Material.NETHERITE_HELMET, 20, 10, 0, 1103));
        armorItemsInit.put(1200, armorBuilder("Multi-Defense Armor", Material.IRON_HELMET, 0, 0, 10, 1200));
        armorItemsInit.put(1201, armorBuilder("Multi-Defense Armor +", Material.IRON_HELMET, 0, 0, 12, 1201));
        armorItemsInit.put(1202, armorBuilder("Multi-Defense Armor ++", Material.IRON_HELMET, 0, 0, 13, 1202));
        armorItemsInit.put(1203, armorBuilder("Multi-Defense Armor +++", Material.GOLDEN_HELMET, 0, 0, 15, 1203));

        ARMOR_ITEMS = Map.copyOf(armorItemsInit);

        // OFFHAND ITEMS

        Map<Integer, ItemStack> offhandItemsInit = new HashMap<>();

        offhandItemsInit.put(9000, getRocketLauncherAmmo("Rocket Tier 1", 1, 9000));
        offhandItemsInit.put(9001, getRocketLauncherAmmo("Rocket Tier 2", 3, 9001));
        offhandItemsInit.put(9002, getRocketLauncherAmmo("Rocket Tier 3", 5, 9002));

        offhandItemsInit.put(100, shieldBuilder("Chainmail Armor Shield", 6, 100));
        offhandItemsInit.put(101, shieldBuilder("Chainmail Armor + Shield", 13, 101));
        offhandItemsInit.put(102, shieldBuilder("Chainmail Armor ++ Shield", 20, 102));
        offhandItemsInit.put(1100, shieldBuilder("Heavy Armor Shield", 24, 1100));
        offhandItemsInit.put(1101, shieldBuilder("Heavy Armor + Shield", 28, 1101));
        offhandItemsInit.put(1102, shieldBuilder("Heavy Armor ++ Shield", 33, 1102));
        offhandItemsInit.put(1103, shieldBuilder("Heavy Armor +++ Shield", 38, 1103));
        offhandItemsInit.put(1200, shieldBuilder("Multi-Defense Armor Shield", 28, 1200));
        offhandItemsInit.put(1201, shieldBuilder("Multi-Defense Armor + Shield", 36, 1201));
        offhandItemsInit.put(1202, shieldBuilder("Multi-Defense Armor ++ Shield", 45, 1202));
        offhandItemsInit.put(1203, shieldBuilder("Multi-Defense Armor +++ Shield", 55, 1203));

        OFFHAND_ITEMS = Map.copyOf(offhandItemsInit);

        // SHOP ITEMS

        Map<Integer, ItemStack> shopItemsInit = new HashMap<>();

        {
            ItemStack item = new ItemStack(Material.ARROW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.ARROW);

            meta.setDisplayName("Default Arrow");
            meta.setLore(List.of("EU100", "Price: 50"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

            item.setItemMeta(meta);

            shopItemsInit.put(100, item);
        }

        {
            ItemStack item = new ItemStack(Material.TIPPED_ARROW);

            PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.TIPPED_ARROW);

            meta.setDisplayName("Damage Arrow");
            meta.setLore(List.of("EU101", "For an extra bit of damage", "(Recommended to combine with Powerful", "Bow for mass destruction)", "Price: 2000"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setColor(Color.fromRGB(4459017));
            meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 0), false);

            item.setItemMeta(meta);

            shopItemsInit.put(101, item);
        }

        {
            ItemStack item = new ItemStack(Material.TIPPED_ARROW);

            PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.TIPPED_ARROW);

            meta.setDisplayName("Poisonous Arrow");
            meta.setLore(List.of("EU102", "Price: 2500"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setColor(Color.GREEN);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 600, 0), false);

            item.setItemMeta(meta);

            shopItemsInit.put(102, item);
        }

        {
            ItemStack item = new ItemStack(Material.TIPPED_ARROW);

            PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.TIPPED_ARROW);

            meta.setDisplayName("Slowness Arrow");
            meta.setLore(List.of("EU103", "Price: 500"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setColor(Color.GRAY);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 600, 0), false);

            item.setItemMeta(meta);

            shopItemsInit.put(103, item);
        }

        {
            ItemStack item = new ItemStack(Material.POTION);

            PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);

            meta.setDisplayName("Medikit 2000");
            meta.setLore(List.of("EU104", "Small injury?", "Headache?", "Arm cut off?", "Medikit 2000 is there for you!", "Price: 250"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setColor(Color.RED);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0), false);

            item.setItemMeta(meta);

            shopItemsInit.put(104, item);
        }

        {
            ItemStack item = new ItemStack(Material.SPLASH_POTION);

            PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.SPLASH_POTION);

            meta.setDisplayName("Throwable Vodka");
            meta.setLore(List.of("EU105", "No description needed :)", "100% alcohol", "Price: 20000"));
            meta.addItemFlags(ItemFlag.values());
            meta.setColor(Color.fromRGB(14737632));
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 1200, 1), false);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 1200, 1), false);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 0), false);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.DARKNESS, 2400, 0), false);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 200, 2), false);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0), false);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 0), false);

            item.setItemMeta(meta);

            shopItemsInit.put(105, item);
        }

        {
            ItemStack item = new ItemStack(Material.MILK_BUCKET);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.MILK_BUCKET);

            meta.setDisplayName("Decontamination Drink");
            meta.setLore(List.of("EU106", "Recommended to consumers of Throwable Vodka", "Price: 5000"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

            item.setItemMeta(meta);

            shopItemsInit.put(106, item);
        }

        SHOP_ITEMS = Map.copyOf(shopItemsInit);

    }

    public static boolean hasId(ItemStack item, String prefix, int id) {

        if (item.getItemMeta() == null) {
            return false;
        }

        if (item.getItemMeta().getLore() == null) {
            return false;
        }

        if (item.getItemMeta().getLore().size() < 1) {
            return false;
        }

        return item.getItemMeta().getLore().get(0).equals(prefix + id);
    }

    public static String getIdPrefix(ItemStack item) {

        if (item.getItemMeta() == null) {
            return "";
        }

        if (item.getItemMeta().getLore() == null) {
            return "";
        }

        if (item.getItemMeta().getLore().size() < 1) {
            return "";
        }

        return item.getItemMeta().getLore().get(0).replaceAll("[^A-Z]", "");
    }

    public static int getId(ItemStack item) {

        if (item.getItemMeta() == null) {
            return -1;
        }

        if (item.getItemMeta().getLore() == null) {
            return -1;
        }

        if (item.getItemMeta().getLore().size() < 1) {
            return -1;
        }

        try {
            return Integer.parseInt(item.getItemMeta().getLore().get(0).replaceAll("[^0-9]", ""));
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    public static ItemStack getBackButton() {

        ItemStack item = new ItemStack(Material.BARRIER);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BARRIER);

        meta.setDisplayName("§c§lBACK");
        meta.setLore(List.of("M0"));

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack getDisplayItem(String text, Material material) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

        meta.setDisplayName(text);
        meta.setLore(List.of("M1"));
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack buildInventoryButton(Material material, String name, List<String> l, int id) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

        if (name != null) {
            meta.setDisplayName(name);
        }

        List<String> lore = new ArrayList<>();
        lore.add("M" + id);
        if (l != null) {
            lore.addAll(l);
        }
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack getPlayerMenuButton() {

        ItemStack item = new ItemStack(Material.CHEST);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CHEST);

        meta.setDisplayName("§a§lOpen Player Menu §r§a(right click)");
        meta.setLore(List.of("H0"));

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack getMeleeButton() {
        return buildInventoryButton(Material.NETHERITE_SWORD, "Melee Weapons", null, 2);
    }

    public static ItemStack getRangedButton() {
        return buildInventoryButton(Material.BOW, "Ranged Weapons", null, 3);
    }

    public static ItemStack getArmorButton() {
        return buildInventoryButton(Material.IRON_CHESTPLATE, "Armor", null, 4);
    }

    public static ItemStack getItemShopButton() {
        return buildInventoryButton(Material.SPECTRAL_ARROW, "Item Shop", null, 5);
    }

    public static ItemStack getResetButton() {
        return buildInventoryButton(Material.TNT, "§4§lReset", List.of("Price: 10000"), 6);
    }

    public static ItemStack getLobbyVoteButton() {
        return buildInventoryButton(Material.FILLED_MAP, "§r§6Map voting", null, 7);
    }

    public static ItemStack getLobbyTeamsButton() {
        return buildInventoryButton(Material.PLAYER_HEAD, "§r§6Team Selection", null, 8);
    }

    public static ItemStack getLobbyVoteMapButton(String mapName, String worldName, boolean selected) {
        String colorCode;

        if (selected) {
            colorCode = "§r§a";
        } else {
            colorCode = "§r§6";
        }

        ItemStack itemStack = buildInventoryButton(Material.GREEN_TERRACOTTA, colorCode + mapName, null, 9);

        ItemMeta meta = itemStack.getItemMeta();

        List<String> lore = meta.getLore();
        lore.add(worldName);
        meta.setLore(lore);

        if (selected) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static ItemStack getMelee(int id) {
        return MELEE_ITEMS.get(id);
    }

    public static Integer getMeleeReverse(ItemStack item) {

        if(item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
            return null;
        }

        for (Integer id : Map.copyOf(MELEE_ITEMS).keySet()) {
            ItemStack itemStack = MELEE_ITEMS.get(id);

            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
                continue;
            }

            if (itemStack.getItemMeta().getLore().get(0).equals(item.getItemMeta().getLore().get(0))) {
                return id;
            }
        }

        return null;
    }

    public static int getMeleePrice(int id) {

        // Is item specialized
        if (id >= 1000) {

            // Is melee item in final level (no more upgrades)
            if ((id % 10) >= 3) {

                return 5000;

            } else if ((id % 10) == 0) {

                return 7500;

            } else {

                return 2500;

            }

        } else {

            // Is no item set (or default item set) (= melee level 0)
            // If not, is item ready for specialisation
            if (id == 0) {

                return 0;

            } else if ((id % 10) == 0) {

                return 2500;

            } else {

                return 1250;

            }

        }

    }

    public static ItemStack getRanged(int id) {
        return RANGED_ITEMS.get(id);
    }

    public static Integer getRangedReverse(ItemStack item) {

        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
            return null;
        }

        for (Integer id : Map.copyOf(RANGED_ITEMS).keySet()) {
            ItemStack itemStack = RANGED_ITEMS.get(id);

            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
                continue;
            }

            if (itemStack.getItemMeta().getLore().get(0).equals(item.getItemMeta().getLore().get(0))) {
                return id;
            }
        }

        return null;
    }

    public static int getRangedPrice(int id) {

        // Is item specialized
        if (id >= 1000) {

            // Is melee item in final level (no more upgrades)
            if ((id % 10) >= 2) {

                return 5000;

            } else if ((id % 10) == 0) {

                return 7500;

            } else {

                return 2500;

            }

        } else {

            // Is no item set (or default item set) (= melee level 0)
            // If not, is item ready for specialisation
            if (id == 0) {

                return 0;

            } else if ((id % 10) == 0) {

                return 2500;

            } else {

                return 1250;

            }

        }

    }

    public static ItemStack getArmor(int id) {
        return ARMOR_ITEMS.get(id);
    }

    public static Integer getArmorReverse(ItemStack item) {

        if(item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
            return null;
        }

        for (Integer id : Map.copyOf(ARMOR_ITEMS).keySet()) {
            ItemStack itemStack = ARMOR_ITEMS.get(id);

            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
                continue;
            }

            if (itemStack.getItemMeta().getLore().get(0).equals(item.getItemMeta().getLore().get(0))) {
                return id;
            }
        }

        return null;
    }

    public static int getArmorPrice(int id) {

        // Is item specialized
        if (id >= 1000) {

            // Is melee item in final level (no more upgrades)
            if ((id % 10) >= 3) {

                return 5000;

            } else if ((id % 10) == 0) {

                return 7500;

            } else {

                return 2500;

            }

        } else {

            // Is no item set (or default item set) (= melee level 0)
            // If not, is item ready for specialisation
            if (id == 0) {

                return 0;

            } else if ((id % 10) == 0) {

                return 2500;

            } else {

                return 1250;

            }

        }

    }

    public static ItemStack getArmorChestplate() {
        return armorBuilder("Armor Part (SEE HELMET)", Material.LEATHER_CHESTPLATE, 0, 0, 0, 10);
    }

    public static ItemStack getArmorLeggings() {
        return armorBuilder("Armor Part (SEE HELMET)", Material.LEATHER_LEGGINGS, 0, 0, 0, 10);
    }

    public static ItemStack getArmorBoots() {
        return armorBuilder("Armor Part (SEE HELMET)", Material.LEATHER_BOOTS, 0, 0, 0, 10);
    }

    public static ItemStack getOffhandEquipment(int id) {
        return OFFHAND_ITEMS.get(id);
    }

    public static Integer getOffhandEquipmentReverse(ItemStack item) {

        if(item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
            return null;
        }

        for (Integer id : Map.copyOf(OFFHAND_ITEMS).keySet()) {
            ItemStack itemStack = OFFHAND_ITEMS.get(id);

            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
                continue;
            }

            if (itemStack.getItemMeta().getLore().get(0).equals(item.getItemMeta().getLore().get(0))) {
                return id;
            }
        }

        return null;
    }

    public static ItemStack getShopItem(int id) {
        return SHOP_ITEMS.get(id);
    }

    public static Integer getShopItemReverse(ItemStack item) {

        if(item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
            return null;
        }

        for (Integer id : Map.copyOf(SHOP_ITEMS).keySet()) {
            ItemStack itemStack = SHOP_ITEMS.get(id);

            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
                continue;
            }

            if (itemStack.getItemMeta().getLore().get(0).equals(item.getItemMeta().getLore().get(0))) {
                return id;
            }
        }

        return null;
    }

    public static int getShopItemPrice(int id) {

        switch (id) {
            case 100:
                return 50;
            case 101:
                return 2000;
            case 102:
                return 2500;
            case 103:
                return 500;
            case 104:
                return 250;
            case 105:
                return 20000;
            case 106:
                return 5000;
            default:
                return 0;
        }

    }

    private static ItemStack axeBuilder(String name, Material material, double attackDamage, double attackSpeed, int id) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName(name);
        meta.setLore(List.of("EM" + id, "Damage: " + attackDamage, "Speed: " + attackSpeed));
        meta.addItemFlags(ItemFlag.values());

        attackDamage = attackDamage - 1;
        attackSpeed = attackSpeed - 3.5;

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", attackDamage, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attack_speed", attackSpeed, AttributeModifier.Operation.ADD_NUMBER));

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;

    }

    private static ItemStack potionBuilder(String name, int level, double cooldown, int id) {

        ItemStack item = new ItemStack(Material.SPLASH_POTION);

        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.SPLASH_POTION);

        meta.setDisplayName(name);
        meta.setLore(List.of("EM" + id, "Damage: " + (level * 6), "Cooldown: " + cooldown));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        meta.setColor(Color.fromRGB(4459017));

        if (level > 0) {

            level = level - 1;

            meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, level), true);

        }

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack armorBuilder(String name, Material material, int armor, int toughness, int protection, int id) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

        meta.setDisplayName(name);
        meta.setLore(List.of("EA" + id, "Armor: " + armor, "Toughness: " + toughness, "Protection: " + protection));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", armor, AttributeModifier.Operation.ADD_NUMBER));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier("generic.armor_toughness", toughness, AttributeModifier.Operation.ADD_NUMBER));

        if (protection > 0) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection, true);
        }

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack getRocketLauncherAmmo(String name, int fireworkStars, int id) {

        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);

        FireworkMeta meta = (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);

        meta.setDisplayName(name);
        meta.setLore(List.of(EQUIPMENT_OFFHAND + id));
        meta.addItemFlags(ItemFlag.values());

        meta.setPower(fireworkStars * 2);

        for (int i = 0; i < fireworkStars; i++) {
            meta.addEffect(FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());
        }

        item.setItemMeta(meta);

        return item;

    }

    public static boolean isArmor(ItemStack itemStack) {

        if (itemStack == null) {
            return false;
        }

        String typeNameString = itemStack.getType().name();
        return typeNameString.contains("HELMET") || typeNameString.contains("CHESTPLATE") || typeNameString.contains("LEGGINGS") || typeNameString.contains("BOOTS");

    }

    public static ItemStack shieldBuilder(String name, int durability, int id) {

        ItemStack item = new ItemStack(Material.SHIELD);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.SHIELD);

        meta.setDisplayName(name);
        meta.setLore(List.of(EQUIPMENT_OFFHAND + id, "Press F to reload shield"));

        item.setItemMeta(meta);

        if (durability < 336) {
            item.setDurability((short) (336 - durability));
        }

        return item;

    }

}
