package net.jandie1505.combattest.game;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

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
            default:
                return Bukkit.createInventory(this, 27, "§aPlayer Menu");
        }

    }

    private Inventory getMainMenu() {

        Inventory inventory = Bukkit.createInventory(this, 27, "§aMain Menu");

        inventory.setItem(12, Equipment.getMeleeButton());
        inventory.setItem(13, Equipment.getRangedButton());
        inventory.setItem(14, Equipment.getArmorButton());

        return inventory;

    }

    private Inventory getMeleeMenu() {

        Inventory inventory = Bukkit.createInventory(this, 27, "§aMelee Weapons");

        PlayerData playerData = this.game.getPlayerMap().get(playerId);

        if (playerData == null) {
            return Bukkit.createInventory(this, 27, "§aMelee Weapons");
        }

        // Is item specialized
        if (playerData.getMeleeEquipment() >= 1000) {

            // Is melee item in final level (no more upgrades)
            if ((playerData.getMeleeEquipment() % 10) >= 3) {

                inventory.setItem(13, Equipment.getMelee(playerData.getMeleeEquipment()));

            } else {

                inventory.setItem(17, Equipment.getBackButton());

                inventory.setItem(22, Equipment.getMelee(playerData.getMeleeEquipment()));

                inventory.setItem(4, Equipment.getMelee(playerData.getMeleeEquipment() + 1));

            }

        } else {

            // Is no item set (or default item set) (= melee level 0)
            // If not, is item ready for specialisation
            if (playerData.getMeleeEquipment() == 0) {

                inventory.setItem(17, Equipment.getBackButton());

                inventory.setItem(22, Equipment.getMelee(0));

                inventory.setItem(3, Equipment.getMelee(100));
                inventory.setItem(4, Equipment.getMelee(200));
                inventory.setItem(5, Equipment.getMelee(300));

            } else if ((playerData.getMeleeEquipment() % 10) >= 2) {

                inventory.setItem(17, Equipment.getBackButton());

                switch (playerData.getMeleeEquipment()) {
                    case 102:
                        inventory.setItem(22, Equipment.getMelee(102));
                        inventory.setItem(3, Equipment.getMelee(1100));
                        inventory.setItem(5, Equipment.getMelee(1200));
                        break;
                    case 202:
                        inventory.setItem(22, Equipment.getMelee(202));
                        inventory.setItem(3, Equipment.getMelee(1300));
                        inventory.setItem(5, Equipment.getMelee(1400));
                        break;
                    case 302:
                        inventory.setItem(22, Equipment.getMelee(302));
                        inventory.setItem(3, Equipment.getMelee(1500));
                        inventory.setItem(5, Equipment.getMelee(1600));
                        break;
                    default:
                        break;
                }

            } else {

                inventory.setItem(17, Equipment.getBackButton());

                inventory.setItem(22, Equipment.getMelee(playerData.getMeleeEquipment()));

                inventory.setItem(4, Equipment.getMelee(playerData.getMeleeEquipment() + 1));

            }

        }

        return inventory;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
