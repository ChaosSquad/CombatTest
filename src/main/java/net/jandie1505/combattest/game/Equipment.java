package net.jandie1505.combattest.game;

import net.jandie1505.combattest.CombatTest;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public final class Equipment {

    public static ItemStack getBackButton() {

        ItemStack item = new ItemStack(Material.BARRIER);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BARRIER);

        meta.setDisplayName("BACK");

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
        if (id == 0) {

            ItemStack item = new ItemStack(Material.STONE_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.STONE_SWORD);

            meta.setDisplayName("Crappy Sword");
            meta.setLore(List.of("At least better than fighting with fists", "Damage: 5", "0"));
            meta.addItemFlags(ItemFlag.values());
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 100) {

            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword");
            meta.setLore(List.of("Damage: 6", "100"));
            meta.addItemFlags(ItemFlag.values());
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 101) {

            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword +");
            meta.setLore(List.of("Damage: 7", "101"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 102) {

            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Iron Sword ++");
            meta.setLore(List.of("Damage: 7.5", "102"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 2, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 1100) {

            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword");
            meta.setLore(List.of("Damage: 8", "1100"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;
            
        } else if (id == 1101) {

            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword +");
            meta.setLore(List.of("Damage: 8.5", "1101"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 2, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 1102) {

            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Diamond Sword ++");
            meta.setLore(List.of("Damage: 9", "1102"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 3, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;
            
        } else if (id == 1103) {

            ItemStack item = new ItemStack(Material.NETHERITE_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.NETHERITE_SWORD);

            meta.setDisplayName("Netherite Sword");
            meta.setLore(List.of("Damage: 10", "1103"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 3, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;
            
        } else if (id == 1200) {

            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword");
            meta.setLore(List.of("Damage: 7", "Fire Aspect I", "1200"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 1, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 1201) {

            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword +");
            meta.setLore(List.of("Damage: 7", "Fire Aspect II", "1201"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 2, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 1202) {

            ItemStack item = new ItemStack(Material.IRON_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.IRON_SWORD);

            meta.setDisplayName("Fire Sword ++");
            meta.setLore(List.of("Damage: 7", "Fire Aspect III", "1202"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 3, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 1203) {

            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_SWORD);

            meta.setDisplayName("Fire Sword +++");
            meta.setLore(List.of("Damage: 8", "Fire Aspect III", "1202"));
            meta.addItemFlags(ItemFlag.values());
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 3, false);
            meta.setUnbreakable(true);

            item.setItemMeta(meta);

            return item;

        } else if (id == 200) {
            return axeBuilder("Iron Axe", Material.IRON_AXE, 8, 0.5, 200);
        } else if (id == 201) {
            return axeBuilder("Iron Axe +", Material.IRON_AXE, 8, 0.7, 201);
        } else if (id == 202) {
            return axeBuilder("Iron Axe ++", Material.IRON_AXE, 9, 0.7, 202);
        } else if (id == 1300) {
            return axeBuilder("Heavy Axe", Material.DIAMOND_AXE, 10, 0.3, 1300);
        } else if (id == 1301) {
            return axeBuilder("Heavy Axe +", Material.DIAMOND_AXE, 10.5, 0.3, 1301);
        } else if (id == 1302) {
            return axeBuilder("Heavy Axe ++", Material.DIAMOND_AXE, 11, 0.3, 1302);
        } else if (id == 1303) {
            return axeBuilder("Heavy Axe +++", Material.NETHERITE_AXE, 12, 0.3, 1303);
        } else if (id == 1400) {
            return axeBuilder("Light Axe", Material.IRON_AXE, 9, 0.8, 1400);
        } else if (id == 1401) {
            return axeBuilder("Light Axe +", Material.IRON_AXE, 9.5, 0.8, 1401);
        } else if (id == 1402) {
            return axeBuilder("Light Axe ++", Material.IRON_AXE, 9.5, 0.9, 1402);
        } else if (id == 1403) {
            return axeBuilder("Light Axe +++", Material.DIAMOND_AXE, 10, 0.9, 1403);
        } else if (id == 300) {
            return potionBuilder("Damage Potion", 1, 6, 300);
        } else if (id == 301) {
            return potionBuilder("Damage Potion +", 1, 5, 301);
        } else if (id == 302) {
            return potionBuilder("Damage Potion ++", 1, 4, 302);
        } else if (id == 1500) {
            return potionBuilder("Fast Damage Potion", 1, 3, 1500);
        } else if (id == 1501) {
            return potionBuilder("Fast Damage Potion +", 1, 2, 1501);
        } else if (id == 1502) {
            return potionBuilder("Fast Damage Potion ++", 1, 1, 1502);
        } else if (id == 1503) {
            return potionBuilder("Fast Damage Potion +++", 1, 0.5, 1503);
        } else if (id == 1600) {
            return potionBuilder("Heavy Damage Potion", 2, 6, 1600);
        } else if (id == 1601) {
            return potionBuilder("Heavy Damage Potion +", 2, 5, 1601);
        } else if (id == 1602) {
            return potionBuilder("Heavy Damage Potion ++", 2, 4, 1602);
        } else if (id == 1603) {
            return potionBuilder("Heavy Damage Potion +++", 2, 3, 1603);
        } else {
            return new ItemStack(Material.AIR);
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

    private Equipment() {}
}
