package net.jandie1505.combattest.game.game.listeners;

import net.chaossquad.mclib.WorldUtils;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
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
        System.out.println("not cancelled");

        if (!(event.getEntity() instanceof Trident trident)) return;
        System.out.println("not trident");
        if (WorldUtils.getWeather(this.game.getWorld()) == WorldUtils.WeatherType.THUNDER) return;
        System.out.println("not thundering");

        if (!(trident.getShooter() instanceof Player player)) return;
        System.out.println("shooter is player");
        if (!this.game.isPlayerIngame(player)) return;
        System.out.println("shooter is ingame");

        ItemStack item = trident.getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        System.out.println("has item meta");

        if (!meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_TRIDENT_WEATHER_MANIPULATION, PersistentDataType.BOOLEAN, false)) return;
        System.out.println("trident has weather manipulation");

        int randomValue = new Random().nextInt(100);
        System.out.println(randomValue);
        if (randomValue >= 50) return;
        System.out.println("lets go");

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
