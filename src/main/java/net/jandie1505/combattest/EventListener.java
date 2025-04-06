package net.jandie1505.combattest;

import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.combattest.game.endlobby.Endlobby;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.PlayerData;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.jandie1505.combattest.game.lobby.LobbyMenu;
import net.jandie1505.combattest.game.lobby.LobbyPlayerData;
import net.jandie1505.combattest.game.lobby.MapData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Deprecated(forRemoval = true)
public class EventListener implements Listener {
    private final CombatTest plugin;

    public EventListener(CombatTest plugin) {
        this.plugin = plugin;
    }

    /*
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

            PlayerData victimData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId());

            ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId()).setAlive(false);
            event.getEntity().setGameMode(GameMode.SPECTATOR);
            event.getDrops().clear();

            if (!((Game) this.plugin.getGame()).getWorld().getGameRuleValue("doImmediateRespawn").equalsIgnoreCase("true")) {
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                    event.getEntity().spigot().respawn();
                }, 2);
            }

            if ((victimData.getMeleeEquipment() % 100) != 0) {
                victimData.setMeleeEquipment(victimData.getMeleeEquipment() - 1);
            }

            if ((victimData.getRangedEquipment() % 100) != 0) {
                victimData.setRangedEquipment(victimData.getRangedEquipment() - 1);
            }

            if ((victimData.getArmorEquipment() % 100) != 0) {
                victimData.setArmorEquipment(victimData.getArmorEquipment() - 1);
            }

            victimData.setDeaths(victimData.getDeaths() + 1);

            if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {

                Player damager;

                if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager().getUniqueId())) {
                    damager = (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
                } else if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Projectile && ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager()).getShooter() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(((Player) ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager()).getShooter()).getUniqueId())) {
                    damager = (Player) ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager()).getShooter();
                } else {
                    damager = null;
                }

                if (damager != null && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(damager.getUniqueId())) {

                    PlayerData damagerData = ((Game) this.plugin.getGame()).getPlayerMap().get(damager.getUniqueId());
                    damagerData.setPoints(damagerData.getPoints() + 2000);
                    damagerData.setRewardPoints(damagerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("playerKill", 0));
                    damager.sendMessage("§bPlayer Kill: + 2000 Points");

                    double comparedEquipmentLevels = PlayerData.compareEquipmentLevels(damagerData, victimData);

                    System.out.println(comparedEquipmentLevels);

                    if (comparedEquipmentLevels < 0) {
                        int receivedPoints = ((int) (1000.0 * comparedEquipmentLevels * (-1.0)));
                        damagerData.setPoints(damagerData.getPoints() + receivedPoints);
                        damager.sendMessage("§bPlayer Kill (Low Equipment Bonus): + " + receivedPoints);
                    } else if (comparedEquipmentLevels > 0) {
                        int receivedPoints = ((int) (500.0 * comparedEquipmentLevels));
                        victimData.setPoints(victimData.getPoints() + receivedPoints);
                        event.getEntity().sendMessage("§bYou were killed by a player with significantly higher equipment: + " + receivedPoints + " Points");
                    }

                    damagerData.setKills(damagerData.getKills() + 1);

                    if (damagerData.getTeam() > 0) {

                        for (UUID playerId : ((Game) this.plugin.getGame()).getTeamMembers(damagerData.getTeam())) {
                            if (!damager.getUniqueId().equals(playerId) && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(playerId)) {
                                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(playerId);
                                playerData.setPoints((playerData.getPoints() + 500));
                            }
                        }

                    }

                }

            } else if (event.getEntity().getKiller() != null) {

                Player damager = event.getEntity().getKiller();

                if (damager != null && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(damager.getUniqueId())) {

                    PlayerData damagerData = ((Game) this.plugin.getGame()).getPlayerMap().get(damager.getUniqueId());

                    damagerData.setPoints(damagerData.getPoints() + 1000);
                    damagerData.setRewardPoints(damagerData.getRewardPoints() + this.plugin.getConfigManager().getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("indirectPlayerKill", 0));
                    damager.sendMessage("§bIndirect Player Kill: + 1000 Points");

                    double comparedEquipmentLevels = PlayerData.compareEquipmentLevels(damagerData, victimData);

                    System.out.println(comparedEquipmentLevels);

                    if (comparedEquipmentLevels < 0) {
                        int receivedPoints = ((int) (500.0 * comparedEquipmentLevels * (-1.0)));
                        damagerData.setPoints(damagerData.getPoints() + receivedPoints);
                        damager.sendMessage("§bIndirect Player Kill (Low Equipment Bonus): + " + receivedPoints);
                    } else if (comparedEquipmentLevels > 0) {
                        int receivedPoints = ((int) (250.0 * comparedEquipmentLevels));
                        victimData.setPoints(victimData.getPoints() + receivedPoints);
                        event.getEntity().sendMessage("§bYou were killed by a player indirectly with significantly higher equipment: + " + receivedPoints + " Points");
                    }

                    damagerData.setKills(damagerData.getKills() + 1);

                    if (damagerData.getTeam() > 0) {

                        for (UUID playerId : ((Game) this.plugin.getGame()).getTeamMembers(damagerData.getTeam())) {
                            if (!damager.getUniqueId().equals(playerId) && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(playerId)) {
                                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(playerId);
                                playerData.setPoints((playerData.getPoints() + 250));
                            }
                        }

                    }

                }

            }

        }
    }

     */
}
