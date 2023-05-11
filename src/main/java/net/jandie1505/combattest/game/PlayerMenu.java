package net.jandie1505.combattest.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class PlayerMenu implements InventoryHolder {
    private final Game game;
    private final UUID playerId;
    private int page;

    public PlayerMenu(Game game, UUID playerId) {
        this.game = game;
        this.playerId = playerId;
        this.page = 0;
    }

    @Override
    public Inventory getInventory() {

        switch (page) {
            case 0:
                return this.getMainMenu();
            case 1:
                return this.getMeleeMenu();
            case 2:
                return this.getRangedMenu();
            case 3:
                return this.getArmorMenu();
            case 4:
                return this.getItemShop();
            default:
                return Bukkit.createInventory(this, 27, "§c§mPlayer Menu");
        }

    }

    private Inventory getMainMenu() {

        Inventory inventory = Bukkit.createInventory(this, 27, "§6§lMain Menu");

        inventory.setItem(12, ItemStorage.getMeleeButton());
        inventory.setItem(13, ItemStorage.getRangedButton());
        inventory.setItem(14, ItemStorage.getArmorButton());
        inventory.setItem(22, ItemStorage.getItemShopButton());

        PlayerData playerData = this.game.getPlayerMap().get(playerId);

        if (playerData != null) {
            inventory.setItem(8, ItemStorage.getDisplayItem("§a§lKills: §r§a" + playerData.getKills(), Material.DIAMOND_SWORD));
            inventory.setItem(17, ItemStorage.getDisplayItem("§a§lDeaths: §r§a" + playerData.getDeaths(), Material.SKELETON_SKULL));
            inventory.setItem(26, ItemStorage.getDisplayItem("§a§lPoints: §r§a" + playerData.getPoints(), Material.EMERALD));
        }

        return inventory;

    }

    private Inventory getMeleeMenu() {

        Inventory inventory = Bukkit.createInventory(this, 27, "§6§lMelee Weapons");

        PlayerData playerData = this.game.getPlayerMap().get(playerId);

        if (playerData == null) {
            return Bukkit.createInventory(this, 27, "§c§mMelee Weapons");
        }

        inventory.setItem(0, ItemStorage.getBackButton());
        inventory.setItem(8, ItemStorage.getDisplayItem("§a§lPoints: §r§a" + playerData.getPoints(), Material.EMERALD));
        inventory.setItem(26, ItemStorage.getResetButton());

        // Is item specialized
        if (playerData.getMeleeEquipment() >= 1000) {

            // Is melee item in final level (no more upgrades)
            if ((playerData.getMeleeEquipment() % 10) >= 3) {

                inventory.setItem(13, ItemStorage.getMelee(playerData.getMeleeEquipment()));

            } else {

                inventory.setItem(4, ItemStorage.getMelee(playerData.getMeleeEquipment()));

                inventory.setItem(22, ItemStorage.getMelee(playerData.getMeleeEquipment() + 1));

            }

        } else {

            // Is no item set (or default item set) (= melee level 0)
            // If not, is item ready for specialisation
            if (playerData.getMeleeEquipment() == 0) {

                inventory.setItem(4, ItemStorage.getMelee(0));

                inventory.setItem(21, ItemStorage.getMelee(100));
                inventory.setItem(22, ItemStorage.getMelee(200));
                inventory.setItem(23, ItemStorage.getMelee(300));

            } else if ((playerData.getMeleeEquipment() % 10) >= 2) {

                switch (playerData.getMeleeEquipment()) {
                    case 102:
                        inventory.setItem(4, ItemStorage.getMelee(102));
                        inventory.setItem(21, ItemStorage.getMelee(1100));
                        inventory.setItem(23, ItemStorage.getMelee(1200));
                        break;
                    case 202:
                        inventory.setItem(4, ItemStorage.getMelee(202));
                        inventory.setItem(21, ItemStorage.getMelee(1300));
                        inventory.setItem(23, ItemStorage.getMelee(1400));
                        break;
                    case 302:
                        inventory.setItem(4, ItemStorage.getMelee(302));
                        inventory.setItem(21, ItemStorage.getMelee(1500));
                        inventory.setItem(23, ItemStorage.getMelee(1600));
                        break;
                    default:
                        break;
                }

            } else {

                inventory.setItem(4, ItemStorage.getMelee(playerData.getMeleeEquipment()));

                inventory.setItem(22, ItemStorage.getMelee(playerData.getMeleeEquipment() + 1));

            }

        }

        return inventory;
    }

    private Inventory getRangedMenu() {
        Inventory inventory = Bukkit.createInventory(this, 27, "§6§lRanged Weapons");

        PlayerData playerData = this.game.getPlayerMap().get(playerId);

        if (playerData == null) {
            return Bukkit.createInventory(this, 27, "§c§mRanged Weapons");
        }

        inventory.setItem(0, ItemStorage.getBackButton());
        inventory.setItem(8, ItemStorage.getDisplayItem("§a§lPoints: §r§a" + playerData.getPoints(), Material.EMERALD));
        inventory.setItem(26, ItemStorage.getResetButton());

        // Is item specialized
        if (playerData.getRangedEquipment() >= 1000) {

            // Is Ranged item in final level (no more upgrades)
            if ((playerData.getRangedEquipment() % 10) >= 2) {

                inventory.setItem(13, ItemStorage.getRanged(playerData.getRangedEquipment()));

            } else {

                inventory.setItem(4, ItemStorage.getRanged(playerData.getRangedEquipment()));

                inventory.setItem(22, ItemStorage.getRanged(playerData.getRangedEquipment() + 1));

            }

        } else {

            // Is no item set (or default item set) (= Ranged level 0)
            // If not, is item ready for specialisation
            if (playerData.getRangedEquipment() == 0) {

                inventory.setItem(4, ItemStorage.getRanged(0));

                inventory.setItem(21, ItemStorage.getRanged(100));
                inventory.setItem(22, ItemStorage.getRanged(200));
                inventory.setItem(23, ItemStorage.getRanged(300));

            } else if ((playerData.getRangedEquipment() % 10) >= 1) {

                switch (playerData.getRangedEquipment()) {
                    case 101:
                        inventory.setItem(4, ItemStorage.getRanged(101));
                        inventory.setItem(21, ItemStorage.getRanged(1100));
                        inventory.setItem(23, ItemStorage.getRanged(1200));
                        break;
                    case 201:
                        inventory.setItem(4, ItemStorage.getRanged(201));
                        inventory.setItem(21, ItemStorage.getRanged(1300));
                        inventory.setItem(23, ItemStorage.getRanged(1400));
                        break;
                    case 301:
                        inventory.setItem(4, ItemStorage.getRanged(301));
                        inventory.setItem(21, ItemStorage.getRanged(1500));
                        inventory.setItem(23, ItemStorage.getRanged(1600));
                        break;
                    default:
                        break;
                }

            } else {

                inventory.setItem(4, ItemStorage.getRanged(playerData.getRangedEquipment()));

                inventory.setItem(22, ItemStorage.getRanged(playerData.getRangedEquipment() + 1));

            }

        }

        return inventory;
    }

    public Inventory getArmorMenu() {
        Inventory inventory = Bukkit.createInventory(this, 27, "§6§lArmor");

        PlayerData playerData = this.game.getPlayerMap().get(playerId);

        if (playerData == null) {
            return Bukkit.createInventory(this, 27, "§c§mArmor");
        }

        inventory.setItem(0, ItemStorage.getBackButton());
        inventory.setItem(8, ItemStorage.getDisplayItem("§a§lPoints: §r§a" + playerData.getPoints(), Material.EMERALD));
        inventory.setItem(26, ItemStorage.getResetButton());

        // Is item specialized
        if (playerData.getArmorEquipment() >= 1000) {

            // Is Armor item in final level (no more upgrades)
            if ((playerData.getArmorEquipment() % 10) >= 3) {

                inventory.setItem(13, ItemStorage.getArmor(playerData.getArmorEquipment()));

            } else {

                inventory.setItem(4, ItemStorage.getArmor(playerData.getArmorEquipment()));

                inventory.setItem(22, ItemStorage.getArmor(playerData.getArmorEquipment() + 1));

            }

        } else {

            // Is no item set (or default item set) (= Armor level 0)
            // If not, is item ready for specialisation
            if (playerData.getArmorEquipment() == 0) {

                inventory.setItem(4, ItemStorage.getArmor(0));

                inventory.setItem(22, ItemStorage.getArmor(100));

            } else if ((playerData.getArmorEquipment() % 10) >= 2) {

                if (playerData.getArmorEquipment() == 102) {
                    inventory.setItem(4, ItemStorage.getArmor(101));
                    inventory.setItem(21, ItemStorage.getArmor(1100));
                    inventory.setItem(23, ItemStorage.getArmor(1200));
                }

            } else {

                inventory.setItem(4, ItemStorage.getArmor(playerData.getArmorEquipment()));

                inventory.setItem(22, ItemStorage.getArmor(playerData.getArmorEquipment() + 1));

            }

        }

        return inventory;
    }

    public Inventory getItemShop() {
        Inventory inventory = Bukkit.createInventory(this, 27, "§6§lItemShop");

        PlayerData playerData = this.game.getPlayerMap().get(playerId);

        if (playerData == null) {
            return Bukkit.createInventory(this, 27, "§c§mItemShop");
        }

        inventory.setItem(0, ItemStorage.getBackButton());
        inventory.setItem(8, ItemStorage.getDisplayItem("§a§lPoints: §r§a" + playerData.getPoints(), Material.EMERALD));

        inventory.setItem(10, ItemStorage.getShopItem(100));
        inventory.setItem(11, ItemStorage.getShopItem(101));
        inventory.setItem(12, ItemStorage.getShopItem(102));
        inventory.setItem(13, ItemStorage.getShopItem(103));
        inventory.setItem(14, ItemStorage.getShopItem(104));
        inventory.setItem(15, ItemStorage.getShopItem(105));
        inventory.setItem(16, ItemStorage.getShopItem(106));

        return inventory;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
