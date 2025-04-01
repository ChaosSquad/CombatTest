package net.jandie1505.combattest.game.game.gui;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerMainGUI implements InventoryHolder, ManagedListener {
    @NotNull private static final NamespacedKey BUTTON_TYPE = new NamespacedKey(NamespacedKeys.NAMESPACE, "gui.player_menu.button_type");
    @NotNull private final Game game;
    @NotNull private final Removable removeCondition;

    public PlayerMainGUI(@NotNull Game game, @Nullable Removable removeCondition) {
        this.game = game;
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;
        this.game.registerListener(this);
    }

    // ----- INVENTORY -----

    @Override
    public @NotNull Inventory getInventory() {
        if (!this.game.isListenerRegistered(this)) return Bukkit.createInventory(this, 9, Component.text("Player Menu", NamedTextColor.DARK_RED, TextDecoration.STRIKETHROUGH).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        Inventory inventory = Bukkit.createInventory(this, 27, Component.text("Player Menu", NamedTextColor.GOLD));

        // MELEE BUTTON
        ItemStack melee = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meleeMeta = melee.getItemMeta();
        meleeMeta.displayName(Component.text("Melee Weapons", NamedTextColor.AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meleeMeta.lore(List.of(Component.text("Here you can upgrade your melee weapon!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        meleeMeta.addItemFlags(ItemFlag.values());
        meleeMeta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, ButtonType.MELEE);
        melee.setItemMeta(meleeMeta);
        inventory.setItem(10, melee);

        // Ranged Button
        ItemStack ranged = new ItemStack(Material.BOW);
        ItemMeta rangedMeta = ranged.getItemMeta();
        rangedMeta.displayName(Component.text("Ranged Weapons", NamedTextColor.AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        rangedMeta.lore(List.of(Component.text("Here you can upgrade your ranged weapon!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        rangedMeta.addItemFlags(ItemFlag.values());
        rangedMeta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, ButtonType.RANGED);
        ranged.setItemMeta(rangedMeta);
        inventory.setItem(12, ranged);

        // Armor Button
        ItemStack armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta armorMeta = armor.getItemMeta();
        armorMeta.displayName(Component.text("Armor", NamedTextColor.AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        armorMeta.lore(List.of(Component.text("Here you can upgrade your armor!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        armorMeta.addItemFlags(ItemFlag.values());
        armorMeta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, ButtonType.ARMOR);
        armor.setItemMeta(armorMeta);
        inventory.setItem(14, armor);

        // Item Shop Button
        ItemStack itemShop = new ItemStack(Material.NETHER_STAR);
        ItemMeta itemShopMeta = itemShop.getItemMeta();
        itemShopMeta.displayName(Component.text("Item Shop", NamedTextColor.AQUA, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        itemShopMeta.lore(List.of(Component.text("Here you can buy some special items!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        itemShopMeta.addItemFlags(ItemFlag.values());
        itemShopMeta.getPersistentDataContainer().set(BUTTON_TYPE, PersistentDataType.STRING, ButtonType.ITEM_SHOP);
        itemShop.setItemMeta(itemShopMeta);
        inventory.setItem(16, itemShop);

        // Filler Item
        ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerItemMeta = fillerItem.getItemMeta();
        fillerItemMeta.displayName(Component.text(" "));
        fillerItemMeta.addItemFlags(ItemFlag.values());
        fillerItem.setItemMeta(fillerItemMeta);
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() != Material.AIR) continue;
            inventory.setItem(slot, fillerItem.clone());
        }

        return inventory;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);

        if (event.getClickedInventory() != event.getInventory()) return; // Click was not in the upgrade gui
        if (!(event.getWhoClicked() instanceof Player player)) return; // Get player

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String buttonType = meta.getPersistentDataContainer().getOrDefault(BUTTON_TYPE, PersistentDataType.STRING, "");
        switch (buttonType) {
            case ButtonType.MELEE -> {
                player.openInventory(this.game.getEquipmentUpgradeGUI().getUpgradeGUI("melee", playerData.getEquipment("melee")));
                player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            }
            case ButtonType.RANGED -> {
                player.openInventory(this.game.getEquipmentUpgradeGUI().getUpgradeGUI("ranged", playerData.getEquipment("ranged")));
                player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            }
            case ButtonType.ARMOR -> {
                player.openInventory(this.game.getEquipmentUpgradeGUI().getUpgradeGUI("armor", playerData.getEquipment("armor")));
                player.playSound(player.getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            }
            case ButtonType.ITEM_SHOP -> {
                player.closeInventory();
                player.sendRichMessage("<red>Currently not supported");
            }
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() != this) return;
        event.setCancelled(true);
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        try {
            return this.removeCondition.toBeRemoved();
        } catch (Exception e) {
            return true;
        }
    }

    private interface ButtonType {
        String MELEE = "melee";
        String RANGED = "ranged";
        String ARMOR = "armor";
        String ITEM_SHOP = "item_shop";
    }

}
