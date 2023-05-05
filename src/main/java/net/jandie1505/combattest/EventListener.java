package net.jandie1505.combattest;

import net.jandie1505.combattest.game.ItemStorage;
import net.jandie1505.combattest.game.Game;
import net.jandie1505.combattest.game.Lobby;
import net.jandie1505.combattest.game.PlayerMenu;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class EventListener implements Listener {
    private final CombatTest plugin;

    public EventListener(CombatTest plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).isPlayerIngame(event.getPlayer()) && !this.plugin.isPlayerBypassing(event.getPlayer().getUniqueId())) {

            if (((Game) this.plugin.getGame()).isEnableBorder() && ((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId()).isAlive()) {

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

            } else if (!((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId()).isAlive()) {

                event.setCancelled(true);

            }

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

            ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId()).setAlive(false);
            event.getEntity().setGameMode(GameMode.SPECTATOR);
            event.getDrops().clear();

            if (!((Game) this.plugin.getGame()).getWorld().getGameRuleValue("doImmediateRespawn").equalsIgnoreCase("true")) {
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                    event.getEntity().spigot().respawn();
                }, 2);
            }

        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {
            event.setRespawnLocation(event.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (this.plugin.getGame() instanceof Game && event.getWhoClicked() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getClickedInventory() != null && event.getClickedInventory().getHolder() == event.getWhoClicked()) {

            if (event.getSlot() == 8) {
                event.setCancelled(true);
            }

            if (event.getHotbarButton() == 8) {
                event.setCancelled(true);
            }

            return;
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof PlayerMenu) {

            event.setCancelled(true);

            if (this.plugin.getGame() instanceof Game && event.getWhoClicked() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getCurrentItem() != null) {

                PlayerMenu menu = ((Game) this.plugin.getGame()).getPlayerMenu(event.getWhoClicked().getUniqueId());

                if (menu.getPage() == 0) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getMeleeButton())) {

                        menu.setPage(1);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    }

                } else if (menu.getPage() == 1) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else {

                        Integer itemId = ItemStorage.getMeleeReverse(event.getCurrentItem());

                        if (itemId != null) {

                            ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId()).setMeleeEquipment(itemId);
                            event.getWhoClicked().openInventory(menu.getInventory());

                        }

                    }

                }

            }

            return;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
                event.setCancelled(true);
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
