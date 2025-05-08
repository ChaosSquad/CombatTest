package net.jandie1505.combattest.game.game.combat;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.game.Game;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class AdaptiveArmorSystem implements ManagedListener {
    @NotNull private final Game game;
    @NotNull private final Removable removeCondition;

    public AdaptiveArmorSystem(@NotNull Game game, @Nullable Removable removeCondition) {
        this.game = game;
        this.removeCondition = removeCondition != null ? removeCondition : () -> false;

        this.game.registerListener(this);
        this.game.getTaskScheduler().scheduleRepeatingTask(this::task, 1, 20, this, "adaptive_armor_system");
    }

    // ----- EVENTS -----

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        // Get item

        ItemStack item = player.getInventory().getHelmet();
        if (item == null) return;

        // Get values

        ArmorData data = ArmorData.get(item);
        if (data == null) return;

        double current = data.current();
        if (data.current() <= 0) return; // Check if shield is available

        double intensity = Math.min(data.intensity(), 1.0);
        if (intensity <= 0) return;

        // Update damage

        final double originalDamage = event.getDamage();
        double damageAbsorbed = originalDamage * intensity;

        current = current - damageAbsorbed; // Update shield durability
        if (current < 0) {
            // This removes absorbed damage for damage the shield no longer can block because it's broken there.
            // For example, if the shield normally blocks 10 damage, but the shield durability is 5, the shield will only absorb 5 damage.
            damageAbsorbed = damageAbsorbed + current;
            current = 0;
        }

        // Update values

        event.setDamage(originalDamage - damageAbsorbed);
        ArmorData.put(item, data.updatedCurrent(current));

        // Play sound
        player.getWorld().playSound(player.getLocation().clone(), Sound.BLOCK_CHAIN_BREAK, 1.25f, 1.25f);
        this.showActionBar(player);

    }

    // ----- TASKS -----

    private void task() {

        for (Player player : this.game.getOnlinePlayers()) {
            ArmorData data = ArmorData.get(player.getInventory().getHelmet());
            if (data == null) continue;
            if (!data.isValid()) continue;

            if (data.regenerationRate() > 0.0 && data.current() < data.maximum() && this.game.getNoDamageTracker().getNoDamageTicks(player) > 15) {
                ArmorData updated = data.updatedCurrent(Math.min(data.current() + data.regenerationRate(), data.maximum()));
                ArmorData.put(player.getInventory().getHelmet(), updated);
            }

            this.showActionBar(player);
        }

    }

    // ----- UTILITIES -----

    private void showActionBar(@NotNull Player player) {

        ArmorData data = ArmorData.get(player.getInventory().getHelmet());
        if (data == null) return;

        DecimalFormat df = new DecimalFormat("0.##");
        Component text = Component.text(
                "\uD83D\uDEE1" + df.format(data.current()) + "/" + df.format(data.maximum()),
                this.getShieldColor(data)
        );

        this.game.getPlugin().getActionBarManager().sendActionBarMessage(player, "adaptive_armor_shield", 21, text);
    }

    private @NotNull TextColor getShieldColor(@NotNull ArmorData data) {
        if (data.current() <= 0) return NamedTextColor.RED;

        double shieldPercentage = data.current() / data.maximum();
        if (shieldPercentage >= 0.66) {
            return NamedTextColor.GREEN;
        } else if (shieldPercentage >= 0.50) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.GOLD;
        }
    }

    // ----- DATA -----

    public record ArmorData(double current, double maximum, double intensity, double regenerationRate) {

        public boolean isValid() {
            return maximum >= 0.0 && intensity >= 0.0 && this.regenerationRate >= 0.0;
        }

        public static @Nullable ArmorData get(@Nullable ItemStack item) {
            if (item == null) return null;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return null;

            // Get values

            double current = meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_SHIELD_CURRENT, PersistentDataType.DOUBLE, 0.0);
            final double maximum = meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_SHIELD_MAXIMUM, PersistentDataType.DOUBLE, -1.0);
            final double intensity = meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_INTENSITY, PersistentDataType.DOUBLE, -1.0);
            final double regenerationRate = meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_REGENERATION_RATE, PersistentDataType.DOUBLE, -1.0);

            ArmorData data = new ArmorData(current, maximum, intensity, regenerationRate);
            if (!data.isValid()) return null;
            return data;
        }

        public static void put(@Nullable ItemStack item, @Nullable ArmorData data) {
            if (item == null) return;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            // Set values

            if (data != null && data.isValid()) {
                meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_SHIELD_CURRENT, PersistentDataType.DOUBLE, data.current());
                meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_SHIELD_MAXIMUM, PersistentDataType.DOUBLE, data.maximum());
                meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_INTENSITY, PersistentDataType.DOUBLE, data.intensity());
                meta.getPersistentDataContainer().set(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_REGENERATION_RATE, PersistentDataType.DOUBLE, data.regenerationRate());
            } else {
                meta.getPersistentDataContainer().remove(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_SHIELD_CURRENT);
                meta.getPersistentDataContainer().remove(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_SHIELD_MAXIMUM);
                meta.getPersistentDataContainer().remove(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_INTENSITY);
                meta.getPersistentDataContainer().remove(NamespacedKeys.ITEM_ADAPTIVE_ARMOR_REGENERATION_RATE);
            }

            // Set meta
            item.setItemMeta(meta);
        }

        public @NotNull ArmorData updatedCurrent(double current) {
            return new ArmorData(current, this.maximum(), this.intensity(), this.regenerationRate());
        }

    }

    // ----- OTHER -----

    public final @NotNull Game getGame() {
        return game;
    }

    @Override
    public boolean toBeRemoved() {
        try {
            return this.removeCondition.toBeRemoved();
        } catch (Exception e) {
            return true;
        }
    }
}
