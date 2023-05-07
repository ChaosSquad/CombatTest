package net.jandie1505.combattest.game;

import net.jandie1505.combattest.CombatTest;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStorage {
    private static final Map<Integer, ItemStack> MELEE_ITEMS;
    private static final Map<Integer, ItemStack> RANGED_ITEMS;

    static {

        // MELEE ITEMS

        Map<Integer, ItemStack> meleeItemsInit = new HashMap<>();

        {
            ItemStack item = new ItemStack(Material.STONE_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STONE_SWORD);

            meta.setDisplayName("Crappy Sword");
            meta.setLore(List.of("At least better than fighting with fists", "Damage: 5", "0"));
            meta.addItemFlags(ItemFlag.values());
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(0, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword");
            meta.setLore(List.of("Damage: 6", "100"));
            meta.addItemFlags(ItemFlag.values());
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(100, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword +");
            meta.setLore(List.of("Damage: 7", "101"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(101, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword ++");
            meta.setLore(List.of("Damage: 7.5", "102"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 2, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(102, item);
        }

        {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword");
            meta.setLore(List.of("Damage: 8", "1100"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1100, item);
        }

        {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword +");
            meta.setLore(List.of("Damage: 8.5", "1101"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 2, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1101, item);
        }

        {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword ++");
            meta.setLore(List.of("Damage: 9", "1102"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 3, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1102, item);
        }

        {
            ItemStack item = new ItemStack(Material.NETHERITE_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.NETHERITE_SWORD);

            meta.setDisplayName("Netherite Sword");
            meta.setLore(List.of("Damage: 10", "1103"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 3, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1103, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword");
            meta.setLore(List.of("Damage: 7", "Fire Aspect I", "1200"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 1, false);
            meta.setUnbreakable(true);

            meleeItemsInit.put(1200, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword +");
            meta.setLore(List.of("Damage: 7", "Fire Aspect II", "1201"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 2, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1201, item);
        }

        {
            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword ++");
            meta.setLore(List.of("Damage: 7", "Fire Aspect III", "1202"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 3, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1202, item);
        }

        {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Fire Sword +++");
            meta.setLore(List.of("Damage: 8", "Fire Aspect III", "1202"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 3, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            meleeItemsInit.put(1203, item);
            
        }

        meleeItemsInit.put(200, axeBuilder("Iron Axe", Material.IRON_AXE, 8, 0.5, 200));
        meleeItemsInit.put(201, axeBuilder("Iron Axe +", Material.IRON_AXE, 8, 0.7, 201));
        meleeItemsInit.put(202, axeBuilder("Iron Axe ++", Material.IRON_AXE, 9, 0.7, 202));
        meleeItemsInit.put(1300, axeBuilder("Heavy Axe", Material.DIAMOND_AXE, 10, 0.3, 1300));
        meleeItemsInit.put(1301, axeBuilder("Heavy Axe +", Material.DIAMOND_AXE, 10.5, 0.3, 1301));
        meleeItemsInit.put(1302, axeBuilder("Heavy Axe ++", Material.DIAMOND_AXE, 11, 0.3, 1302));
        meleeItemsInit.put(1303, axeBuilder("Heavy Axe +++", Material.NETHERITE_AXE, 12, 0.3, 1303));
        meleeItemsInit.put(1400, axeBuilder("Light Axe", Material.IRON_AXE, 9, 0.8, 1400));
        meleeItemsInit.put(1401, axeBuilder("Light Axe +", Material.IRON_AXE, 9.5, 0.8, 1401));
        meleeItemsInit.put(1402, axeBuilder("Light Axe ++", Material.IRON_AXE, 9.5, 0.9, 1402));
        meleeItemsInit.put(1403, axeBuilder("Light Axe +++", Material.DIAMOND_AXE, 10, 0.9, 1403));

        meleeItemsInit.put(300, potionBuilder("Damage Potion", 1, 6, 300));
        meleeItemsInit.put(301, potionBuilder("Damage Potion +", 1, 5, 301));
        meleeItemsInit.put(302, potionBuilder("Damage Potion ++", 1, 4, 302));
        meleeItemsInit.put(1500, potionBuilder("Fast Damage Potion", 1, 3, 1500));
        meleeItemsInit.put(1501, potionBuilder("Fast Damage Potion +", 1, 2, 1501));
        meleeItemsInit.put(1502, potionBuilder("Fast Damage Potion ++", 1, 1, 1502));
        meleeItemsInit.put(1503, potionBuilder("Fast Damage Potion +++", 1, 0.5, 1503));
        meleeItemsInit.put(1600, potionBuilder("Heavy Damage Potion", 2, 6, 1600));
        meleeItemsInit.put(1601, potionBuilder("Heavy Damage Potion +", 2, 5, 1601));
        meleeItemsInit.put(1602, potionBuilder("Heavy Damage Potion ++", 2, 4, 1602));
        meleeItemsInit.put(1603, potionBuilder("Heavy Damage Potion +++", 2, 3, 1603));

        MELEE_ITEMS = Map.copyOf(meleeItemsInit);

        // RANGED ITEMS

        Map<Integer, ItemStack> rangedItemsInit = new HashMap<>();

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Default Bow");
            meta.setLore(List.of("100"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            rangedItemsInit.put(100, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Default Bow +");
            meta.setLore(List.of("101"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 1, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(101, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Powerful Bow");
            meta.setLore(List.of("1100"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1100, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Powerful Bow +");
            meta.setLore(List.of("1101"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 3, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1101, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Most Powerful Bow");
            meta.setLore(List.of("1102"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1102, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Misc Bow");
            meta.setLore(List.of("1200"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1200, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Misc Bow +");
            meta.setLore(List.of("1201"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, false);
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1201, item);
        }

        {
            ItemStack item = new ItemStack(Material.BOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);

            meta.setDisplayName("Misc Bow ++");
            meta.setLore(List.of("1202"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, false);
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1202, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Default Crossbow");
            meta.setLore(List.of("200"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            rangedItemsInit.put(200, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Default Crossbow +");
            meta.setLore(List.of("201"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(201, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Multishot Crossbow");
            meta.setLore(List.of("1300"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, false);
            meta.addEnchant(Enchantment.MULTISHOT, 1, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1300, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Rocket Launcher Crossbow");
            meta.setLore(List.of("Ammunition: Rockets", "1301"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1301, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Rocket Launcher Crossbow +");
            meta.setLore(List.of("Ammunition: Rockets", "1302"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 1, false);
            meta.addEnchant(Enchantment.MULTISHOT, 1, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1302, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Piercing Crossbow");
            meta.setLore(List.of("1400"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 2, false);
            meta.addEnchant(Enchantment.PIERCING, 2, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1400, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Piercing Crossbow +");
            meta.setLore(List.of("1401"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 2, false);
            meta.addEnchant(Enchantment.PIERCING, 3, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1401, item);
        }

        {
            ItemStack item = new ItemStack(Material.CROSSBOW);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CROSSBOW);

            meta.setDisplayName("Piercing Crossbow ++");
            meta.setLore(List.of("1402"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.QUICK_CHARGE, 2, false);
            meta.addEnchant(Enchantment.PIERCING, 4, false);

            item.setItemMeta(meta);

            rangedItemsInit.put(1402, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Trident");
            meta.setLore(List.of("Melee damage disabled", "300"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 1, false);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(300, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Trident +");
            meta.setLore(List.of("Melee damage disabled", "301"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 2, false);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(301, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Riptide Trident");
            meta.setLore(List.of("Melee damage disabled", "1500"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 2, false);
            meta.addEnchant(Enchantment.RIPTIDE, 2, false);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1500, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Riptide Trident +");
            meta.setLore(List.of("Melee damage disabled", "1501"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 2, false);
            meta.addEnchant(Enchantment.RIPTIDE, 3, false);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1501, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Riptide Trident ++");
            meta.setLore(List.of("Melee damage disabled", "1502"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 2, false);
            meta.addEnchant(Enchantment.RIPTIDE, 4, false);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1502, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Extra-loyal Trident");
            meta.setLore(List.of("Melee damage disabled", "1600"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 3, false);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1600, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Lightning Trident");
            meta.setLore(List.of("Melee damage disabled", "1601"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 3, false);
            meta.addEnchant(Enchantment.CHANNELING, 1, false);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1601, item);
        }

        {
            ItemStack item = new ItemStack(Material.TRIDENT);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.TRIDENT);

            meta.setDisplayName("Weather-manipulating Trident");
            meta.setLore(List.of("Melee damage disabled", "1602"));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.LOYALTY, 3, false);
            meta.addEnchant(Enchantment.CHANNELING, 1, false);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", 0, AttributeModifier.Operation.ADD_NUMBER));

            item.setItemMeta(meta);

            rangedItemsInit.put(1602, item);
        }

        RANGED_ITEMS = Map.copyOf(rangedItemsInit);

    }

    public static ItemStack getBackButton() {

        ItemStack item = new ItemStack(Material.BARRIER);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BARRIER);

        meta.setDisplayName("§c§lBACK");

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack getDisplayItem(String text, Material material) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

        meta.setDisplayName(text);
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack getPlayerMenuButton() {

        ItemStack item = new ItemStack(Material.CHEST);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.CHEST);

        meta.setDisplayName("§a§lOpen Player Menu §r§a(right click)");

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack getMeleeButton() {
        return CombatTest.buildInventoryButton(Material.NETHERITE_SWORD, "Melee Weapons", null, 0);
    }

    public static ItemStack getRangedButton() {
        return CombatTest.buildInventoryButton(Material.BOW, "Ranged Weapons", null, 1);
    }

    public static ItemStack getArmorButton() {
        return CombatTest.buildInventoryButton(Material.IRON_CHESTPLATE, "Armor", null, 2);
    }

    public static ItemStack getMelee(int id) {
        return MELEE_ITEMS.get(id);
    }

    public static Integer getMeleeReverse(ItemStack item) {

        for (Integer id : Map.copyOf(MELEE_ITEMS).keySet()) {
            ItemStack itemStack = MELEE_ITEMS.get(id);

            if (itemStack.isSimilar(item)) {
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

                return 10000;

            } else if ((id % 10) == 0) {

                return 10000;

            } else {

                return 5000;

            }

        } else {

            // Is no item set (or default item set) (= melee level 0)
            // If not, is item ready for specialisation
            if (id == 0) {

                return 0;

            } else if ((id % 10) == 0) {

                return 5000;

            } else {

                return 2500;

            }

        }

    }

    public static ItemStack getRanged(int id) {
        return RANGED_ITEMS.get(id);
    }

    public static Integer getRangedReverse(ItemStack item) {

        for (Integer id : Map.copyOf(RANGED_ITEMS).keySet()) {
            ItemStack itemStack = RANGED_ITEMS.get(id);

            if (itemStack.isSimilar(item)) {
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

                return 10000;

            } else if ((id % 10) == 0) {

                return 10000;

            } else {

                return 5000;

            }

        } else {

            // Is no item set (or default item set) (= melee level 0)
            // If not, is item ready for specialisation
            if (id == 0) {

                return 0;

            } else if ((id % 10) == 0) {

                return 5000;

            } else {

                return 2500;

            }

        }

    }

    private static ItemStack axeBuilder(String name, Material material, double attackDamage, double attackSpeed, int id) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName(name);
        meta.setLore(List.of("Damage: " + attackDamage, "Speed: " + attackSpeed, String.valueOf(id)));
        meta.addItemFlags(ItemFlag.values());

        attackDamage = attackSpeed - 1;
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

        meta.setDisplayName("Damage Potion");
        meta.setLore(List.of("Damage: " + (level * 6), "Cooldown: " + cooldown, String.valueOf(id)));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        meta.setColor(Color.fromRGB(4459017));

        if (level > 0) {

            level = level - 1;

            meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, level), false);

        }

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack getRocketLauncherAmmo() {

        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);

        FireworkMeta meta = (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);

        meta.setDisplayName("Ammunition for Rocket Launcher Crossbow");
        meta.addItemFlags(ItemFlag.values());

        meta.addEffect(FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());
        meta.addEffect(FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());
        meta.addEffect(FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());
        meta.addEffect(FireworkEffect.builder().withColor(Color.BLACK).with(FireworkEffect.Type.BALL).build());

        item.setItemMeta(meta);

        return item;

    }
}
