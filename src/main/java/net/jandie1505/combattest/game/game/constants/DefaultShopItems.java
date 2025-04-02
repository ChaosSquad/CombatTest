package net.jandie1505.combattest.game.game.constants;

import net.jandie1505.combattest.game.game.gui.ShopGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public final class DefaultShopItems {

    private DefaultShopItems() {}

    public static List<ShopGUI.ShopItem> getShopItems() {
        List<ShopGUI.ShopItem> items = new ArrayList<>();

        items.add(new ShopGUI.ShopItem(getDefaultArrow(), 50));
        items.add(new ShopGUI.ShopItem(getSpectralArrow(), 50));
        items.add(new ShopGUI.ShopItem(getDamageArrow(), 2000));
        items.add(new ShopGUI.ShopItem(getPoisonousArrow(), 2500));
        items.add(new ShopGUI.ShopItem(getSlownessArrow(), 500));
        items.add(new ShopGUI.ShopItem(getMedikit(), 250));
        items.add(new ShopGUI.ShopItem(getGoldenApple(), 500));
        items.add(new ShopGUI.ShopItem(getVodka(), 10));
        items.add(new ShopGUI.ShopItem(getThrowableVodka(), 10000));
        items.add(new ShopGUI.ShopItem(getVodkaArrow(), 20000));
        items.add(new ShopGUI.ShopItem(getGreenBull(), 5000));
        items.add(new ShopGUI.ShopItem(getThrowableGreenBull(), 20000));
        items.add(new ShopGUI.ShopItem(getDecontaminationDrink(), 5000));
        items.add(new ShopGUI.ShopItem(getEnchantedGoldenApple(), 50000));

        return items;
    }

    private static ItemStack getDefaultArrow() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.ARROW);

        meta.displayName(Component.text("Default Arrow").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("The original!", NamedTextColor.GRAY),
                Component.text("Without any special features.", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getSpectralArrow() {
        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Illuminated Arrow").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Makes your enemies glow!", NamedTextColor.GRAY),
                Component.text("Not compatible with infinity bows.", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);
        return item;
    }


    private static ItemStack getDamageArrow() {
        ItemStack item = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.TIPPED_ARROW);

        meta.displayName(Component.text("Arrow: Enhanced Damage Edition").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Hurts even more!", NamedTextColor.GRAY),
                Component.text("Use with a powerful bow for mass destruction!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        meta.setColor(Color.fromRGB(4459017));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getPoisonousArrow() {
        ItemStack item = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.TIPPED_ARROW);

        meta.displayName(Component.text("Poisonous Arrow").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Poison your enemies!", NamedTextColor.GRAY),
                Component.text("They will have fun!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        meta.setColor(Color.fromRGB(4459017));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 600, 0), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getSlownessArrow() {
        ItemStack item = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.TIPPED_ARROW);

        meta.displayName(Component.text("Slowness Arrow").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Slows down your enemies!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        meta.setColor(Color.fromRGB(4459017));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 0), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getMedikit() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);

        meta.displayName(Component.text("Medikit").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Small injury? Headache? Arm cut off?", NamedTextColor.GRAY),
                Component.text("The medikit is here for you!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        meta.setColor(Color.RED);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 0), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getGoldenApple() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Very healthy apple").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Heals you more than a medikit!", NamedTextColor.GRAY),
                Component.text("Only for privately insured people.", NamedTextColor.GRAY)
        )));

        item.setItemMeta(meta);
        return new ItemStack(Material.GOLDEN_APPLE);
    }

    private static ItemStack getVodka() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Vodka").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Alcohol: 100%", NamedTextColor.GRAY),
                Component.text("Have some fun!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.values());
        meta.setColor(Color.fromRGB(14737632));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, 1), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.NAUSEA, 1200, 1), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.DARKNESS, 2400, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 200, 2), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getThrowableVodka() {
        ItemStack item = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Throwable Vodka").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Alcohol: 100%", NamedTextColor.GRAY),
                Component.text("Let's give other players some fun!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.values());
        meta.setColor(Color.fromRGB(14737632));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, 1), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.NAUSEA, 1200, 1), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.DARKNESS, 2400, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 200, 2), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getVodkaArrow() {
        ItemStack item = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Vodka Arrow").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Alcohol: 100%", NamedTextColor.GRAY),
                Component.text("Don't let people run away from your fun!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.values());
        meta.setColor(Color.fromRGB(14737632));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, 1), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.NAUSEA, 1200, 1), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.DARKNESS, 2400, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 200, 2), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0), false);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getGreenBull() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Green Bull").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Lends Wings!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.values());
        meta.setColor(Color.fromRGB(14737632));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 60*20, 1), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getThrowableGreenBull() {
        ItemStack item = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Throwable Green Bull").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Lends wings to other players!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.values());
        meta.setColor(Color.fromRGB(14737632));
        meta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 60*20, 1), false);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getDecontaminationDrink() {
        ItemStack item = new ItemStack(Material.MILK_BUCKET);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Decontamination Drink").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("If you have enough of that annoying potion effects.", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getEnchantedGoldenApple() {
        ItemStack item = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("GIVE ME SUPERPOWERS!").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(unformattedLore(List.of(
                Component.text("Gives you superpowers!", NamedTextColor.GRAY)
        )));
        meta.addItemFlags(ItemFlag.values());

        item.setItemMeta(meta);
        return item;
    }

    // ----- UTILITIES -----

    private static List<Component> unformattedLore(List<Component> lore) {
        List<Component> copiedLore = new ArrayList<>();

        for (Component component : lore) {
            copiedLore.add(Component.empty().color(TextColor.color(0)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).append(component));
        }

        return copiedLore;
    }

}
