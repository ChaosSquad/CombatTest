package net.jandie1505.combattest.game.game.listeners;

import net.chaossquad.mclib.executable.ManagedListener;
import net.chaossquad.mclib.misc.Removable;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.game.Game;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SplashPotionModifierListener implements ManagedListener {
    @NotNull private final Game game;
    @NotNull private final Removable removable;

    public SplashPotionModifierListener(@NotNull Game game, @Nullable Removable removable) {
        this.game = game;
        this.removable = removable != null ? removable : () -> false;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        this.changeSplashPotionIntensity(event);
        this.applyCustomDamage(event);
    }

    private void changeSplashPotionIntensity(PotionSplashEvent event) {
        double radiusReductionFactor = event.getPotion().getPotionMeta().getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_POTION_INTENSITY_MODIFIER, PersistentDataType.DOUBLE, -1.0);
        if (radiusReductionFactor < 0.0) return;

        event.getAffectedEntities().forEach(entity -> {

            // Prevent division by zero
            if (radiusReductionFactor == 0.0) {
                event.setIntensity(entity, 0.0);
                return;
            }

            double originalIntensity = event.getIntensity(entity);

            // Calculate new intensity based on the reduced radius.
            // The formula is based on the assumption that intensity is linear to distance.
            // New intensity = oldIntensity ^ (1 / radiusReductionFactor).
            // This conversion corresponds to a "compression" of the effective range.
            double adjustedIntensity = Math.pow(originalIntensity, 1.0 / radiusReductionFactor);

            adjustedIntensity = Math.min(1.0, adjustedIntensity);

            // Set new intensity
            event.setIntensity(entity, adjustedIntensity);
        });

    }

    private void applyCustomDamage(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();

        // DAMAGE AMOUNT

        final double baseDamage = potion.getPotionMeta().getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_POTION_INSTANT_DAMAGE_CUSTOM_VALUE, PersistentDataType.DOUBLE, -1.0);
        if (baseDamage <= 0.0) return;

        // DAMAGE SOURCE

        final DamageSource.Builder builder = DamageSource.builder(DamageType.MAGIC)
                .withDamageLocation(potion.getLocation().clone())
                .withDirectEntity(potion);

        if (potion.getShooter() instanceof Entity thrower) {
            builder.withCausingEntity(thrower);
        }

        // APPLY DAMAGE

        event.getAffectedEntities().forEach(entity -> {
            double intensity = event.getIntensity(entity);
            entity.damage(baseDamage * intensity, builder.build());
        });
    }

    @Override
    public boolean toBeRemoved() {
        try {
            return this.removable.toBeRemoved();
        } catch (Exception e) {
            return true;
        }
    }
}
