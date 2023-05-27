package net.jandie1505.combattest.lobby;

import net.jandie1505.combattest.ItemStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.UUID;

public class LobbyMenu implements InventoryHolder {
    private final Lobby lobby;
    private final UUID playerId;
    private int page;

    public LobbyMenu(Lobby lobby, UUID playerId) {
        this.lobby = lobby;
        this.playerId = playerId;
    }

    @Override
    public Inventory getInventory() {

        switch (this.page) {
            case 0:
                return this.getSelectionMenu();
            case 1:
                return this.getVotingMenu();
            case 2:
                return this.getTeamMenu();
            default:
                return Bukkit.createInventory(this, 27, "§c§mLobby Menu");
        }

    }

    private Inventory getSelectionMenu() {
        Inventory inventory = Bukkit.createInventory(this, 27, "§6§lLobby Menu");

        inventory.setItem(12, ItemStorage.getLobbyVoteButton());
        inventory.setItem(14, ItemStorage.getLobbyTeamsButton());

        return inventory;
    }

    public Inventory getVotingMenu() {
        int inventorySize = ((this.lobby.getMaps().size() / 9) + 1) * 9;

        if (inventorySize > 54) {
            inventorySize = 54;
        }

        if (inventorySize < 27) {
            inventorySize = 27;
        }

        Inventory inventory = Bukkit.createInventory(this, inventorySize, "§6§lMap Voting");
        LobbyPlayerData playerData = this.lobby.getPlayerMap().get(this.playerId);

        inventory.setItem(0, ItemStorage.getBackButton());

        if (playerData == null) {
            return inventory;
        }

        int slot = 1;
        for (MapData map : this.lobby.getMaps()) {

            if (slot >= inventory.getSize()) {
                break;
            }

            inventory.setItem(slot, ItemStorage.getLobbyVoteMapButton(map.getName(), map.getWorld(), map == playerData.getVote()));

            slot++;
        }

        return inventory;
    }

    public Inventory getTeamMenu() {
        List<Integer> teams = this.lobby.getTeams();

        int inventorySize = ((teams.size() / 9) + 1) * 9;

        if (inventorySize > 54) {
            inventorySize = 54;
        }

        if (inventorySize < 27) {
            inventorySize = 27;
        }

        Inventory inventory = Bukkit.createInventory(this, inventorySize, "§6§lMap Voting");
        LobbyPlayerData playerData = this.lobby.getPlayerMap().get(this.playerId);

        inventory.setItem(0, ItemStorage.getBackButton());

        if (playerData == null) {
            return inventory;
        }

        if (playerData.getTeam() > 0) {
            inventory.setItem(1, ItemStorage.getLobbyTeamSelectionLeaveTeamButton());
        } else {
            inventory.setItem(1, ItemStorage.getLobbyTeamSelectionCreateTeamButton());
        }

        int slot = 2;
        for (Integer teamId : teams) {

            if (slot >= inventory.getSize()) {
                break;
            }

            inventory.setItem(slot, ItemStorage.getLobbyTeamSelectionButton(teamId, playerData.getTeam() == teamId));

            slot++;
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
