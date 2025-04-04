package net.jandie1505.combattest.game.game.listeners;

import net.chaossquad.mclib.MiscUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.jandie1505.combattest.game.game.equipment.EquipmentData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeathListener implements ManagedListener {
    @NotNull private final Game game;

    public DeathListener(@NotNull Game game) {
        this.game = game;
    }

    // ----- EVENTS -----

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!this.game.isPlayerIngame(event.getPlayer())) return;

        Location location = event.getPlayer().getLocation();

        if (location.getY() < -65) {
            location.setY(-65);
        }

        event.setRespawnLocation(location);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) return;

        // Alive status and gamemode

        playerData.setAlive(false);
        player.setGameMode(GameMode.SPECTATOR);

        // Respawn

        if (Boolean.FALSE.equals(this.game.getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN))) {
            this.game.getTaskScheduler().runTaskLater(() -> player.spigot().respawn(), 2, "player_respawn");
        }

        // Get values
        final double playerEquipmentLevel = this.getEquipmentLevelAverage(playerData);
        @Nullable final Killer lastHitKiller = this.getLastHitKiller(player);

        // Reward killer
        if (lastHitKiller != null) this.rewardKiller(lastHitKiller, (int) playerEquipmentLevel);

        // Reward team
        if (lastHitKiller != null) this.rewardTeam(lastHitKiller.playerId());

        // Low Equipment Bonus
        this.giveLowEquipmentBonus(playerData, null, lastHitKiller);

        // Decrease Downgrade Score
        this.decreaseDowngradeScore(playerData, null, lastHitKiller);

        // Equipment downgrade
        this.downgradeEquipment(playerData);

        // Stats
        playerData.incrementDeaths();
    }

    // ----- DEATH ACTIONS -----

    private void rewardKiller(@NotNull Killer killer, int playerEquipmentLevel) {
        final int PAYOUT_KILL = 2000; // TODO: Add config option
        final int LOW_EQUIPMENT_BONUS_KILL = 1000;

        int killerEquipmentLevel = (int) Math.round(this.getEquipmentLevelAverage(killer.playerData()));

        int lowEquipmentBonus = killerEquipmentLevel < playerEquipmentLevel ? LOW_EQUIPMENT_BONUS_KILL * (playerEquipmentLevel - killerEquipmentLevel) : 0;

        int reward = PAYOUT_KILL + lowEquipmentBonus;
        if (reward != 0) killer.playerData().addPoints(reward);
        killer.playerData().incrementKills();

        if (reward > 0) {
            killer.player().sendMessage(Component.empty()
                    .append(Component.text("Player Kill: ", NamedTextColor.GOLD))
                    .append(Component.text("+ " + reward + "P", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                            .hoverEvent(HoverEvent.showText(Component.empty()
                                    .append(Component.text("Base: " + PAYOUT_KILL)).appendNewline()
                                    .append(Component.text("Low Equipment Bonus: " + (lowEquipmentBonus > 0 ? lowEquipmentBonus : "---")))
                            ))
                    )
            );
        }
    }

    private void rewardTeam(@Nullable UUID playerId) {
        final int TEAM_KILL_REWARD = 100; // TODO: Add config option

        if (playerId == null) return;

        PlayerData playerData = this.game.getPlayerData(playerId);
        if (playerData == null) return;
        if (playerData.getTeam() <= 0) return;

        for (Map.Entry<UUID, PlayerData> entry : this.game.getPlayerMap().entrySet()) {
            if (entry.getValue().getTeam() != playerData.getTeam()) continue;
            if (entry.getKey().equals(playerId)) continue;
            entry.getValue().addPoints(TEAM_KILL_REWARD);
        }

    }

    private void decreaseDowngradeScore(@NotNull PlayerData playerData, @Nullable Killer mostDamageKiller, @Nullable Killer lastHitKiller) {
        int playerEquipmentLevel = (int) Math.round(this.getEquipmentLevelAverage(playerData));
        int higherKillerEquipmentLevel = this.getHigherEquipmentLevel(mostDamageKiller, lastHitKiller);

        if (playerEquipmentLevel >= higherKillerEquipmentLevel) {
            playerData.setDowngradeScore(playerData.getDowngradeScore() - 100);
        } else {
            double difference = higherKillerEquipmentLevel - playerEquipmentLevel;
            playerData.setDowngradeScore(playerData.getDowngradeScore() - (100 / (int) Math.max(difference, 2)));
        }

    }

    private void giveLowEquipmentBonus(@NotNull PlayerData playerData, @Nullable Killer mostDamageKiller, @Nullable Killer lastHitKiller) {
        final int PAYOUT_LOW_EQUIPMENT_BONUS_ON_DEATH_WITH_DOWNGRADE = 500; // TODO: Add config option
        final int PAYOUT_LOW_EQUIPMENT_BONUS_ON_DEATH_WITHOUT_DOWNGRADE = 100;

        int playerEquipmentLevel = (int) Math.round(this.getEquipmentLevelAverage(playerData));
        int higherKillerEquipmentLevel = this.getHigherEquipmentLevel(mostDamageKiller, lastHitKiller);

        if (playerEquipmentLevel < higherKillerEquipmentLevel) {

            int deathBonus;

            if (playerData.getDowngradeScore() < 0) {
                deathBonus = PAYOUT_LOW_EQUIPMENT_BONUS_ON_DEATH_WITH_DOWNGRADE;
            } else {
                deathBonus = PAYOUT_LOW_EQUIPMENT_BONUS_ON_DEATH_WITHOUT_DOWNGRADE;
            }

            if (deathBonus != 0) deathBonus = deathBonus * Math.round(higherKillerEquipmentLevel - playerEquipmentLevel);

            if (deathBonus > 0) {
                playerData.addPoints(deathBonus);
            }

        }

    }

    private void downgradeEquipment(@NotNull PlayerData playerData) {

        for (Map.Entry<String, Integer> entry : Map.copyOf(playerData.getEquipments()).entrySet()) {
            EquipmentData data = this.game.getEquipmentSystem().getEquipment(entry.getValue());
            if (data == null) continue;

            int downgradeId = data.downgradeId();
            if (downgradeId <= 0) {
                continue;
            }

            playerData.setEquipment(entry.getKey(), downgradeId);
        }

    }

    // ----- UTILITIES -----

    private @Nullable Killer getLastHitKiller(@NotNull Player player) {

        Player killer = player.getKiller();
        if (killer == null) return null;

        return Killer.create(this.game, killer.getUniqueId());
    }

    /**
     * Get average equipment level of the specified player
     * @param playerData player data
     * @return average equipment level
     */
    private double getEquipmentLevelAverage(PlayerData playerData) {
        List<Double> equipmentLevels = new ArrayList<>();

        Map<String, Integer> equipments = playerData.getEquipments();
        for (Map.Entry<String, Integer> entry : equipments.entrySet()) {

            EquipmentData data = this.game.getEquipmentSystem().getEquipment(entry.getValue());
            if (data == null) continue;

            if (data.equipmentLevel() <= 0) continue;
            equipmentLevels.add((double) data.equipmentLevel());

        }

        return MiscUtils.getAverage(equipmentLevels);
    }

    private int getHigherEquipmentLevel(@Nullable Killer mostDamageKiller, @Nullable Killer lastHitKiller) {
        int mostDamageKillerLevel = mostDamageKiller != null ? (int) Math.round(this.getEquipmentLevelAverage(mostDamageKiller.playerData())) : 0;
        int lastHitKillerLevel = lastHitKiller != null ? (int) Math.round(this.getEquipmentLevelAverage(lastHitKiller.playerData())) : 0;

        return Math.max(mostDamageKillerLevel, lastHitKillerLevel);
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return false;
    }

    // ----- INNER CLASSES -----

    private record Killer(@NotNull UUID playerId, @NotNull Player player, @NotNull PlayerData playerData) {

        public static Killer create(@Nullable UUID playerId, @Nullable Player player, @Nullable PlayerData playerData) {
            if (playerData == null || player == null || playerId == null) return null;
            return new Killer(playerId, player, playerData);
        }

        public static Killer create(@NotNull Game game, @Nullable UUID playerId) {
            if (playerId == null) return null;

            PlayerData playerData = game.getPlayerData(playerId);
            Player player = game.getPlugin().getServer().getPlayer(playerId);

            return create(playerId, player, playerData);
        }

    }

}
