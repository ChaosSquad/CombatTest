package net.jandie1505.combattest;

import net.jandie1505.combattest.game.Game;
import net.jandie1505.combattest.game.Lobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventListener implements Listener {
    private final CombatTest plugin;

    public EventListener(CombatTest plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).isEnableBorder() && ((Game) this.plugin.getGame()).isPlayerIngame(event.getPlayer()) && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {

            if (!((Game) this.plugin.getGame()).isInBorders(event.getTo())) {

                if (((Game) this.plugin.getGame()).isInBorders(event.getFrom())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cYou cannot leave the game area");
                } else {
                    event.setCancelled(true);
                    ((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId()).setAlive(false);
                    event.getPlayer().sendMessage("§cYou have been killed for leaving the game area");
                }

            }

        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (this.plugin.getGame() instanceof Lobby) {

            if (event.getEntity() instanceof Player) {

                if (((Lobby) this.plugin.getGame()).getPlayers().contains(event.getEntity().getUniqueId())) {
                    event.setCancelled(true);
                } else if (event.getDamager() instanceof Player && ((Lobby) this.plugin.getGame()).getPlayers().contains(event.getDamager().getUniqueId())) {
                    event.setCancelled(true);
                }

            }

        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

    }
}
