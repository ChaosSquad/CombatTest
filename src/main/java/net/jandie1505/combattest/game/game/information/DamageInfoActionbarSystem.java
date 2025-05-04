package net.jandie1505.combattest.game.game.information;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.combattest.game.game.Game;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class DamageInfoActionbarSystem implements ManagedListener, Removable {
    @NotNull private final Game game;
    @NotNull private final Removable removable;
    @NotNull private final Map<Player, DamageInfo> damages;

    public DamageInfoActionbarSystem(@NotNull Game game, @NotNull Removable removable) {
        this.game = game;
        this.removable = removable;
        this.damages = new HashMap<>();

        this.game.getTaskScheduler().scheduleRepeatingTask(this::task, 1, 20, this, "damage_info_actionbar");
        this.game.registerListener(this);
    }

    // ----- EVENT -----

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (this.game.isPlayerIngame(player)) {
            this.takeDamage(player, event.getFinalDamage());
        }

        if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            Entity damager;

            if (damageByEntityEvent.getDamager() instanceof Player damagerPlayer) {
                damager = damagerPlayer;
            } else if (damageByEntityEvent.getDamager() instanceof Projectile projectile) {

                if (projectile.getShooter() instanceof Player damagerPlayer) {
                    damager = damagerPlayer;
                } else {
                    damager = null;
                }

            } else {
                damager = null;
            }

            if (!(damager instanceof Player damagerPlayer) || !this.game.isPlayerIngame(damagerPlayer)) return;

            this.dealDamage(damagerPlayer, event.getFinalDamage());
        }

    }

    // ----- TASKS -----

    private void task() {
        for (Map.Entry<Player, DamageInfo> entry : Map.copyOf(this.damages).entrySet()) {

            if (!entry.getKey().isOnline()) {
                this.damages.remove(entry.getKey());
                continue;
            }

            if (!this.game.isPlayerIngame(entry.getKey())) {
                this.damages.remove(entry.getKey());
                continue;
            }

            if (entry.getValue().countdown > 0) {
                this.showActionBar(entry.getKey(), entry.getValue());
                entry.getValue().countdown--;
            } else {
                this.damages.remove(entry.getKey());
            }

        }
    }

    // ----- UTILITIES -----

    private void dealDamage(Player player, double damage) {
        DamageInfo info = this.getOrCreateDamageInfo(player);

        info.damageDealt += damage;
        info.countdown = 5;

        this.showActionBar(player, info);
    }

    private void takeDamage(Player player, double damage) {
        DamageInfo info = this.getOrCreateDamageInfo(player);

        info.damageTaken += damage;
        info.countdown = 5;

        this.showActionBar(player, info);
    }

    private String formatDamage(double damage) {
        return new DecimalFormat("0.00").format(damage);
    }

    private DamageInfo getOrCreateDamageInfo(Player player) {
        return this.damages.computeIfAbsent(player, k -> new DamageInfo());
    }

    private void showActionBar(Player player, DamageInfo info) {
        this.game.getPlugin().getActionBarManager().sendActionBarMessage(player,
                "damage_display",
                20,
                Component.empty()
                        .append(Component.text("\uD83D\uDDE1" + formatDamage(info.damageDealt), NamedTextColor.DARK_GREEN))
                        .appendSpace()
                        .append(Component.text("\uD83D\uDC94" + formatDamage(info.damageTaken), NamedTextColor.DARK_RED))
        );
    }

    // ----- OTHER -----

    @Override
    public boolean toBeRemoved() {
        return this.removable.toBeRemoved();
    }

    public @NotNull final Game getGame() {
        return game;
    }

    private static final class DamageInfo {
        int countdown = 5;
        double damageDealt = 0;
        double damageTaken = 0;
    }

}
