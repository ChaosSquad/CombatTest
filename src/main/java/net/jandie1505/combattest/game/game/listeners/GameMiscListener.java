package net.jandie1505.combattest.game.game.listeners;

import net.chaossquad.mclib.MiscUtils;
import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.combattracking.CombatTracker;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Random;

public class GameMiscListener implements ManagedListener {
    @NotNull private final Game game;

    public GameMiscListener(@NotNull Game game) {
        this.game = game;
    }

    // ----- LISTENERS -----

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (this.game.getPlugin().isPlayerBypassing(player)) return;

        // Spectators can't get damage
        PlayerData playerData = this.game.getPlayerData(player);
        if (playerData == null) {
            event.setCancelled(true);
            return;
        }

        if (event instanceof EntityDamageByEntityEvent byEntityEvent) {
            if (!(PlayerUtils.getRealDamager(byEntityEvent.getDamager()) instanceof Player damager)) return;
            if (this.game.getPlugin().isPlayerBypassing(damager)) return;

            // Spectators can't damage ingame players
            PlayerData damagerData = this.game.getPlayerData(damager);
            if (damagerData == null) {
                event.setCancelled(true);
                return;
            }

            // Players in the same team can't damage each other
            if (playerData.getTeam() > 0 && playerData.getTeam() == damagerData.getTeam()) {
                event.setCancelled(true);
                return;
            }

            CombatTracker tracker = this.game.getCombatTracker();
            tracker.onPlayerDamage(player.getUniqueId(), damager.getUniqueId(), event.getFinalDamage());
        }

    }

    @EventHandler
    public void onEntityMoveForWorldBorder(PlayerMoveEvent event) {
        if (!this.game.isEnableBorder()) return;
        if (this.game.getPlugin().isPlayerBypassing(event.getPlayer())) return;

        PlayerData playerData = this.game.getPlayerData(event.getPlayer());
        if (playerData == null) return;

        if (playerData.isAlive()) {

            if (this.game.isInBorders(event.getTo())) return;
            event.setCancelled(true);

            // If the player trying to leave the area, prevent it
            // IF the player has already left the area, "kill" them.
            if (this.game.isInBorders(event.getFrom())) {
                event.getPlayer().showTitle(Title.title(
                        Component.text("\uD83D\uDEB7", NamedTextColor.RED),
                        Component.text("Don't leave the game area!", NamedTextColor.RED),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)
                ));
                event.getPlayer().playSound(event.getPlayer().getLocation().clone(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.RECORDS, 1, 0);
            } else {
                playerData.setRespawntimer(10);
                playerData.setAlive(false);
                event.getPlayer().sendRichMessage("<red>You have been killed for leaving the game area!");
            }

        } else {

            // Block moving in spectator, only looking around is allowed
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
                return;
            }

        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityForResettingNoPvPTimer(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            PlayerData playerData = this.game.getPlayerData(player);
            if (playerData != null) playerData.setNoPvpTimer(0);
        }

        if (event.getDamager() instanceof Player damager) {
            PlayerData damagerData = this.game.getPlayerData(damager);
            if (damagerData != null) {
                damagerData.setNoPvpTimer(0);
                damagerData.setPoints(damagerData.getPoints() + (5 * (int) event.getDamage()));
            }
        }

    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!this.game.isPlayerIngame(player)) return;
        if (event.getFoodLevel() >= player.getFoodLevel()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRiptide(PlayerRiptideEvent event) {
        PlayerData data = this.game.getPlayerData(event.getPlayer());
        if (data == null) return;

        data.setHasUsedTrident(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHitForWeatherManipulationAbility(ProjectileHitEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Trident trident)) return;
        if (WorldUtils.getWeather(this.game.getWorld()) == WorldUtils.WeatherType.THUNDER) return;

        if (!(trident.getShooter() instanceof Player player)) return;
        if (!this.game.isPlayerIngame(player)) return;

        ItemStack item = trident.getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (!meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_TRIDENT_WEATHER_MANIPULATION, PersistentDataType.BOOLEAN, false)) return;

        int randomValue = new Random().nextInt(100);
        if (randomValue >= 50) return;

        WorldUtils.setWeather(this.game.getWorld(), WorldUtils.WeatherType.THUNDER);

        player.showTitle(Title.title(
                Component.text("\uD83D\uDD31", NamedTextColor.AQUA),
                Component.text("Weather Manipulation activated", NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2), Duration.ofMillis(250))
        ));
        player.playSound(player.getLocation().clone(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, SoundCategory.RECORDS, 1.0F, 1.0F);
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return false;
    }

}
