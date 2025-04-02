package net.jandie1505.combattest.game.game.constants;

import net.jandie1505.combattest.constants.NamespacedKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class OtherItems {

    private OtherItems() {}

    public static ItemStack getPlayerMenuItem() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .append(Component.text("Player Menu", NamedTextColor.GOLD)).appendSpace()
                .append(Component.text("(right-click)", NamedTextColor.GRAY))
        );
        meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_PLAYER_MENU, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

}
