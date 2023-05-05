package net.jandie1505.combattest.game;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {
    private final UUID playerId;
    private int meleeEquipment;
    private int rangedEquipment;
    private int armorEquipment;
    private boolean alive;
    private int respawntimer;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.meleeEquipment = 0;
        this.alive = false;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public int getMeleeEquipment() {
        return meleeEquipment;
    }

    public void setMeleeEquipment(int meleeEquipment) {
        this.meleeEquipment = meleeEquipment;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getRespawntimer() {
        return respawntimer;
    }

    public void setRespawntimer(int respawntimer) {
        this.respawntimer = respawntimer;
    }
}
