package net.jandie1505.combattest.game;

import net.jandie1505.combattest.game.equipment.EquipmentItem;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class PlayerData {
    private final UUID playerId;
    private final Map<String, Integer> equipments;
    private final Map<EquipmentItem.Identifier, Integer> equipmentCountdowns;
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
    private int tridentTimer;
    private boolean hasUsedTrident;
    private int shieldTimer;
    private int team;
    private Scoreboard scoreboard;
    private int noPvpTimer;
    private boolean weatherDisabled;
    private int rewardPoints;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;

        this.equipments = new HashMap<>();
        this.equipmentCountdowns = new HashMap<>();

        this.meleeEquipment = 0;
        this.rangedEquipment = 0;
        this.armorEquipment = 0;

        this.alive = false;
        this.respawntimer = 0;

        this.points = 0;

        this.regenerationCooldown = 0;

        this.potionTimer = 0;
        this.tridentTimer = 0;
        this.hasUsedTrident = false;
        this.shieldTimer = 0;

        this.team = 0;

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        this.noPvpTimer = 0;

        this.weatherDisabled = false;

        this.rewardPoints = 0;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    @Deprecated(forRemoval = true)
    public int getMeleeEquipment() {
        return meleeEquipment;
    }

    @Deprecated(forRemoval = true)
    public void setMeleeEquipment(int meleeEquipment) {
        this.meleeEquipment = meleeEquipment;
    }

    @Deprecated(forRemoval = true)
    public int getRangedEquipment() {
        return rangedEquipment;
    }

    @Deprecated(forRemoval = true)
    public void setRangedEquipment(int rangedEquipment) {
        this.rangedEquipment = rangedEquipment;
    }

    @Deprecated(forRemoval = true)
    public int getArmorEquipment() {
        return armorEquipment;
    }

    @Deprecated(forRemoval = true)
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

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        if (team >= 0) {
            this.team = team;
        }
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void resetScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public int getNoPvpTimer() {
        return noPvpTimer;
    }

    public void setNoPvpTimer(int noPvpTimer) {
        this.noPvpTimer = noPvpTimer;
    }

    public boolean isWeatherDisabled() {
        return weatherDisabled;
    }

    public void setWeatherDisabled(boolean weatherDisabled) {
        this.weatherDisabled = weatherDisabled;
    }

    public int getTridentTimer() {
        return tridentTimer;
    }

    public void setTridentTimer(int tridentTimer) {
        this.tridentTimer = tridentTimer;
    }

    public boolean hasUsedTrident() {
        return hasUsedTrident;
    }

    public void setHasUsedTrident(boolean hasUsedTrident) {
        this.hasUsedTrident = hasUsedTrident;
    }

    public int getShieldTimer() {
        return shieldTimer;
    }

    public void setShieldTimer(int shieldTimer) {
        this.shieldTimer = shieldTimer;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    // ----- EQUIPMENT -----

    /**
     * Get a map of all equipments a player has
     * @return equipment map
     */
    public Map<String, Integer> getEquipments() {
        return Map.copyOf(this.equipments);
    }

    /**
     * Get a specific equipment for a player
     * @param specifier equipment specifier.
     * @return equipment id
     */
    public int getEquipment(String specifier) {
        Integer value = this.equipments.get(specifier);
        if (value == null) return -1;
        return value;
    }

    /**
     * Set a specific equipment for a player
     * @param specifier equipment specifier
     * @param id equipment id (0 or lower to remove)
     */
    public void setEquipment(String specifier, int id) {
        if (specifier == null) return;

        if (id > 0) {
            this.equipments.put(specifier, id);
        } else {
            this.equipments.remove(specifier);
        }

    }

    /**
     * Clear equipment of a player
     */
    public void resetEquipment() {
        this.equipments.clear();
    }

    /**
     * Check if a player has the specified equipment.
     * @param id equipment id
     * @return true when the player has it
     */
    public boolean hasEquipment(int id) {
        return this.equipments.containsValue(id);
    }

    /**
     * Reset all equipment countdowns.
     * Should be called when the player respawns.
     */
    public void resetEquipmentCountdowns() {
        this.equipmentCountdowns.clear();
    }

    /**
     * Get an equipment countdown.
     * Returns -1 if no the countdown does not exist.
     * @param identifier identifier of the equipment item
     * @return countdown (-1 if not exist)
     */
    public int getEquipmentCountdown(EquipmentItem.Identifier identifier) {
        return Objects.requireNonNullElse(this.equipmentCountdowns.get(identifier), -1);
    }

    /**
     * Set an equipment countdown for a specific equipment id.
     * If the countdown is set to a negative value, it will be removed.
     * @param identifier identifier of the equipment item
     * @param countdown count down (positive or 0 for setting, negative value for removing)
     */
    public void setEquipmentCountdown(EquipmentItem.Identifier identifier, int countdown) {

        if (identifier == null) {
            throw new IllegalArgumentException("equipment id must be higher than 0");
        }

        if (countdown < 0) {
            this.equipmentCountdowns.remove(identifier);
            return;
        }

        this.equipmentCountdowns.put(identifier, countdown);
    }

    public Map<EquipmentItem.Identifier, Integer> getEquipmentCountdowns() {
        return Map.copyOf(this.equipmentCountdowns);
    }

    // ----- STATIC -----

    public static int getEquipmentCompareLevel(int level) {

        if (level > 1000) {

            return 3 + (level % 10);

        } else {

            if (level == 0) {
                return 0;
            } else {
                return 1 + (level % 10);
            }

        }

    }

    public static double compareEquipmentLevels(PlayerData data, PlayerData compareData) {

        /*
        double p1Melee = getEquipmentCompareLevel(data.getMeleeEquipment());
        double p2Melee = getEquipmentCompareLevel(compareData.getMeleeEquipment());
        double p1Ranged = getEquipmentCompareLevel(data.getRangedEquipment());
        double p2Ranged = getEquipmentCompareLevel(compareData.getRangedEquipment());
        double p1Armor = getEquipmentCompareLevel(data.getArmorEquipment());
        double p2Armor = getEquipmentCompareLevel(compareData.getArmorEquipment());

        double p1All = p1Melee + p1Ranged + p1Armor;
        p1All = p1All / 3;

        double p2All = p2Melee + p2Ranged + p2Armor;
        p2All = p2All / 3;

        return (p1All - p2All);

         */

        return 0;
    }

    public static double getKD(int k, int d) {

        double kills = k;
        double deaths = d;

        if (deaths == 0) {
            return kills;
        }

        double kd = kills / deaths;

        return Math.round(kd * 100.0) / 100.0;
    }

    public static Comparator<PlayerData> getKillsComparator() {
        return Comparator.comparingInt(PlayerData::getKills);
    }

    public static Comparator<PlayerData> getDeathsComparator() {
        return Comparator.comparingInt(PlayerData::getDeaths);
    }

    public static Comparator<PlayerData> getKDComparator() {
        return Comparator.comparingDouble(o -> PlayerData.getKD(o.getKills(), o.getDeaths()));
    }
}
