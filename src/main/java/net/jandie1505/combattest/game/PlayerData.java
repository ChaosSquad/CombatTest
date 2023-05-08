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
    private int points;
    private int regenerationCooldown;
    private int kills;
    private int deaths;
    private double potionTimer;
    private boolean hasTrident;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;

        this.meleeEquipment = 0;
        this.rangedEquipment = 0;
        this.armorEquipment = 0;

        this.alive = false;
        this.respawntimer = 0;

        this.points = 0;

        this.regenerationCooldown = 0;

        this.potionTimer = 0;

        this.hasTrident = false;
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

    public int getRangedEquipment() {
        return rangedEquipment;
    }

    public void setRangedEquipment(int rangedEquipment) {
        this.rangedEquipment = rangedEquipment;
    }

    public int getArmorEquipment() {
        return armorEquipment;
    }

    public void setArmorEquipment(int armorEquipment) {
        this.armorEquipment = armorEquipment;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRegenerationCooldown() {
        return regenerationCooldown;
    }

    public void setRegenerationCooldown(int regenerationCooldown) {
        this.regenerationCooldown = regenerationCooldown;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public double getPotionTimer() {
        return potionTimer;
    }

    public void setPotionTimer(double potionTimer) {
        this.potionTimer = potionTimer;
    }

    public boolean hasTrident() {
        return hasTrident;
    }

    public void setHasTrident(boolean hasTrident) {
        this.hasTrident = hasTrident;
    }

    public static int getEqupmentCompareLevel(int level) {

        if (level > 1000) {

            return 3 + (level % 1000);

        } else {

            if (level == 0) {
                return 0;
            } else {
                return 1 + (level % 100);
            }

        }

    }

    public static int compareEquipmentLevels(PlayerData data, PlayerData compareData) {

        int p1Melee = getEqupmentCompareLevel(data.getMeleeEquipment());
        int p2Melee = getEqupmentCompareLevel(compareData.getMeleeEquipment());
        int p1Ranged = getEqupmentCompareLevel(data.getRangedEquipment());
        int p2Ranged = getEqupmentCompareLevel(compareData.getRangedEquipment());
        int p1Armor = getEqupmentCompareLevel(data.getArmorEquipment());
        int p2Armor = getEqupmentCompareLevel(compareData.getArmorEquipment());

        int p1All = p1Melee + p1Ranged + p1Armor;
        p1All = p1All / 3;

        int p2All = p2Melee + p2Ranged + p2Armor;
        p2All = p2All / 3;

        return (p1All - p2All);

    }
}
