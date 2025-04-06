package net.jandie1505.combattest.game.lobby.constants;

import net.jandie1505.combattest.constants.NamespacedKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LobbyItems {

    static @NotNull ItemStack mapVotingMenuItem() {
        ItemStack item = new ItemStack(Material.MAP);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Map Voting", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_VOTING_MENU, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    static @NotNull ItemStack teamSelectionMenuItem(@Nullable Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(item.getType());

        meta.displayName(Component.text("Team Selection", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_TEAM_SELECTION_MENU, PersistentDataType.BOOLEAN, true);

        if (player != null) {
            meta.setPlayerProfile(player.getPlayerProfile());
        }

        item.setItemMeta(meta);
        return item;
    }

    static @NotNull ItemStack teamSelectionMenuItem() {
        return teamSelectionMenuItem(null);
    }

}
