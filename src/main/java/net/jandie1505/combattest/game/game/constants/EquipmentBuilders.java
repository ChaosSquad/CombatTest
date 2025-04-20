package net.jandie1505.combattest.game.game.constants;

import net.jandie1505.combattest.constants.NamespacedKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class EquipmentBuilders {
    private static final Component CLEARED_COMPONENT = Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);

    private EquipmentBuilders() {}

    /**
     * Build a melee weapon.
     * @param name item name
     * @param material material
     * @param attackDamage attack damage
     * @param attackSpeed attack speed
     * @param enchantments enchantments
     * @return item
     */
    public static ItemStack meleeWeaponBuilder(@NotNull Component name, @NotNull Material material, double attackDamage, double attackSpeed, @NotNull Map<Enchantment, Integer> enchantments) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
        meta.displayName(CLEARED_COMPONENT.append(name));
        List<Component> lore = new ArrayList<>();
        meta.addItemFlags(ItemFlag.values());
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (attackDamage >= 0) {
            lore.add(CLEARED_COMPONENT.append(Component.text("Damage: " + attackDamage, NamedTextColor.GRAY)));
            attackDamage = attackDamage - 1;
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(Attribute.ATTACK_DAMAGE.getKey(), attackDamage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        }

        if (attackSpeed >= 0) {
            lore.add(Component.text("§7Speed: " + attackSpeed, NamedTextColor.GRAY));
            attackSpeed = attackSpeed - 3.5;
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(Attribute.ATTACK_SPEED.getKey(), attackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        }

        for (Enchantment enchantment : enchantments.keySet()) {
            int level = enchantments.get(enchantment);

            if (level < 0) {
                continue;
            }

            meta.addEnchant(enchantment, level, true);

        }

        meta.lore(lore);

        meta.setUnbreakable(true);

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack potionBuilder(@NotNull Component name, double damageAmount, double cooldown, double intensityModifier) {

        ItemStack item = new ItemStack(Material.SPLASH_POTION);

        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.SPLASH_POTION);

        // VISUALS

        meta.displayName(CLEARED_COMPONENT.append(name));
        meta.addItemFlags(ItemFlag.values());
        meta.setColor(Color.fromRGB(4459017));

        // EFFECTS

        if (intensityModifier >= 0.0) {
            meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_POTION_INTENSITY_MODIFIER, PersistentDataType.DOUBLE, intensityModifier);
        }

        if (damageAmount > 0) {
            meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_POTION_INSTANT_DAMAGE_CUSTOM_VALUE, PersistentDataType.DOUBLE, damageAmount);
        }

        // LORE

        List<Component> lore = new ArrayList<>();

        if (cooldown > 0) {
            lore.add(CLEARED_COMPONENT.append(Component.text("Cooldown: " + cooldown + "s", NamedTextColor.GRAY)));
        }

        if (intensityModifier >= 0) {
            double percentage = intensityModifier * 100.0;
            lore.add(CLEARED_COMPONENT.append(Component.text("Radius: " + Math.round(percentage) + "%", NamedTextColor.GRAY)));
        }

        meta.lore(lore);

        // OTHER STUFF

        meta.setUnbreakable(true);

        // RETURN

        item.setItemMeta(meta);
        return item;

    }

    public static ItemStack shieldBuilder(@NotNull Component name, int durability) {
        ItemStack item = new ItemStack(Material.SHIELD);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.SHIELD);

        meta.displayName(CLEARED_COMPONENT.append(name));
        meta.lore(List.of(CLEARED_COMPONENT.append(Component.text("Press F to reload shield", NamedTextColor.GRAY))));

        Damageable damageable = (Damageable) meta;
        damageable.setMaxDamage(durability);
        damageable.setDamage(0);

        item.setItemMeta(meta);
        return item;

    }

    /**
     * Builds a bow.
     * @param name item name
     * @param power power
     * @param punch punch
     * @param flame flame
     * @param infinity infinity
     * @return item
     */
    public static ItemStack bowBuilder(@NotNull Component name, int power, int punch, int flame, boolean infinity) {

        ItemStack item = new ItemStack(Material.BOW);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(CLEARED_COMPONENT.append(name));

        if (power > 0) {
            meta.addEnchant(Enchantment.POWER, power, true);
        }

        if (punch > 0) {
            meta.addEnchant(Enchantment.PUNCH, punch, true);
        }

        if (flame > 0) {
            meta.addEnchant(Enchantment.FLAME, flame, true);
        }

        if (infinity) {
            meta.addEnchant(Enchantment.INFINITY, 1, true);
        }

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Build a crossbow.
     * @param name item name
     * @param quickCharge quick charge level
     * @param piercing piercing level
     * @param multiShot multishot level
     * @param rocketLevel rocket level (only for text)
     * @return item
     */
    public static ItemStack crossbowBuilder(@NotNull Component name, int quickCharge, int piercing, int multiShot, int rocketLevel) {

        ItemStack item = new ItemStack(Material.CROSSBOW);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(CLEARED_COMPONENT.append(name));

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
            meta.lore(List.of(Component.text("Ammunition: Rockets Lvl. " + rocketLevel, NamedTextColor.GRAY)));
        }

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Build a trident.
     * @param name item name
     * @param meleeDamage enable melee damage
     * @param loyalty loyalty enchantment
     * @param channeling channeling enchantment
     * @param riptide riptide enchantment
     * @param rangedDamage ranged damage
     * @param weatherManipulationText show weather manipulation ability text
     * @return item
     */
    public static ItemStack tridentBuilder(@NotNull Component name, boolean meleeDamage, int loyalty, int channeling, int riptide, double rangedDamage, boolean weatherManipulationText) {

        ItemStack item = new ItemStack(Material.TRIDENT);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(CLEARED_COMPONENT.append(name));

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

        List<Component> lore = new ArrayList<>();

        lore.add(CLEARED_COMPONENT.append(Component.text("Ranged Damage: " + rangedDamage, NamedTextColor.GRAY)));

        if (weatherManipulationText) {
            meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_TRIDENT_WEATHER_MANIPULATION, PersistentDataType.BOOLEAN, true);
            lore.add(CLEARED_COMPONENT.append(Component.text("50% chance of starting a thunderstorm", NamedTextColor.GRAY)));
            lore.add(CLEARED_COMPONENT.append(Component.text(" when hitting a player", NamedTextColor.GRAY)));
        }

        if (!meleeDamage) {
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(Attribute.ATTACK_DAMAGE.getKey(), 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
            lore.add(CLEARED_COMPONENT.append(Component.text("Melee damage disabled", NamedTextColor.GRAY)));
        }

        meta.lore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        if (rangedDamage >= 0) {
            meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_TRIDENT_RANGED_DAMAGE, PersistentDataType.DOUBLE, rangedDamage);
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Builds an armor.
     * @param name item name
     * @param material armor type
     * @param armor armor points
     * @param toughness armor toughness points
     * @param protection protection enchantment level
     * @return item
     */
    public static ItemStack armorBuilder(@NotNull Component name, @NotNull Material material, int armor, int toughness, int protection, int knockbackResistance) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

        meta.displayName(CLEARED_COMPONENT.append(name));

        List<Component> lore = new ArrayList<>();
        lore.add(CLEARED_COMPONENT.append(Component.text("Armor: " + armor, NamedTextColor.GRAY)));
        lore.add(CLEARED_COMPONENT.append(Component.text("Toughness: " + toughness, NamedTextColor.GRAY)));
        lore.add(CLEARED_COMPONENT.append(Component.text("Protection: " + protection, NamedTextColor.GRAY)));
        if (knockbackResistance > 0) lore.add(CLEARED_COMPONENT.append(Component.text("Knockback Resistance: " + knockbackResistance + "%", NamedTextColor.GRAY)));
        meta.lore(lore);

        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(Attribute.ARMOR.getKey(), armor, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(Attribute.ARMOR_TOUGHNESS.getKey(), toughness, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));
        if (knockbackResistance > 0) meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(Attribute.KNOCKBACK_RESISTANCE.getKey(), knockbackResistance, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ARMOR));

        if (protection > 0) {
            meta.addEnchant(Enchantment.PROTECTION, protection, true);
        }

        if (meta instanceof ArmorMeta armorMeta && !(meta instanceof LeatherArmorMeta)) {
            armorMeta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.WARD));
        }

        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack armorBuilder(@NotNull Component name, @NotNull Material material, int armor, int toughness, int protection) {
        return armorBuilder(name, material, armor, toughness, protection, 0);
    }

    /**
     * Builds a decorative armor without any protection.
     * @param material armor type
     * @param glint item glint effect (if item should look like it is enchanted)
     * @return item
     */
    public static ItemStack decorativeArmorBuilder(@NotNull Material material, boolean glint) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(CLEARED_COMPONENT.append(Component.text("Armor part", NamedTextColor.GRAY)));
        meta.lore(List.of(CLEARED_COMPONENT.append(Component.text("See helmet for armor details.", NamedTextColor.GRAY))));
        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(Attribute.ARMOR.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ARMOR));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(Attribute.ARMOR_TOUGHNESS.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ARMOR));

        if (glint) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        }

        if (meta instanceof ArmorMeta armorMeta && !(meta instanceof LeatherArmorMeta)) {
            armorMeta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.WARD));
        }

        item.setItemMeta(meta);

        return item;
    }

}
