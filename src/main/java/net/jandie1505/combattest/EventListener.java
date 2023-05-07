package net.jandie1505.combattest;

import net.jandie1505.combattest.game.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

            // Block player menu slot
            if (event.getSlot() == 8) {
                event.setCancelled(true);

                if ((event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_ONE) && event.getCurrentItem() != null && ItemStorage.getIdPrefix(event.getCurrentItem()).equals(ItemStorage.HOTBAR_ITEM) && ItemStorage.getId(event.getCurrentItem()) == 0) {

                    PlayerMenu menu = ((Game) this.plugin.getGame()).getPlayerMenu(event.getWhoClicked().getUniqueId());

                    menu.setPage(0);
                    event.getWhoClicked().openInventory(menu.getInventory());

                    if (event.getWhoClicked().getItemOnCursor() != null && ItemStorage.getIdPrefix(event.getWhoClicked().getItemOnCursor()).equals(ItemStorage.HOTBAR_ITEM) && ItemStorage.getId(event.getWhoClicked().getItemOnCursor()) == 0) {

                        event.getWhoClicked().getInventory().setItem(40, new ItemStack(Material.AIR));

                    }

                }

                return;
            }

            // Block player menu slot for number keys
            if (event.getHotbarButton() == 8) {
                event.setCancelled(true);
                return;
            }

            // Block offhand when player has rocket launcher crossbow
            if (event.getSlot() == 40) {
                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                if (playerData.getRangedEquipment() == 1301 || playerData.getRangedEquipment() == 1302) {
                    event.setCancelled(true);
                }

                return;
            }

            return;
        }

        // Player Menu
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof PlayerMenu) {

            event.setCancelled(true);

            if (this.plugin.getGame() instanceof Game && event.getWhoClicked() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getWhoClicked().getUniqueId()) && event.getCurrentItem() != null) {

                PlayerMenu menu = ((Game) this.plugin.getGame()).getPlayerMenu(event.getWhoClicked().getUniqueId());

                if (menu.getPage() == 0) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getMeleeButton())) {

                        menu.setPage(1);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else if (event.getCurrentItem().isSimilar(ItemStorage.getRangedButton())) {

                        menu.setPage(2);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    }

                } else if (menu.getPage() == 1) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else {

                        Integer itemId = ItemStorage.getMeleeReverse(event.getCurrentItem());
                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (itemId != null && itemId > playerData.getMeleeEquipment()) {

                            if (playerData.getPoints() >= ItemStorage.getMeleePrice(itemId)) {

                                playerData.setMeleeEquipment(itemId);
                                playerData.setPoints(playerData.getPoints() - ItemStorage.getMeleePrice(itemId));
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§aItem successfully upgraded");

                            } else {

                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§cYou don't have enough points to upgrade (Price: " + ItemStorage.getMeleePrice(itemId) + ")");

                            }

                        } else {

                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cYou cannot upgrade to an item which is the same or below the item you already have");

                        }

                    }

                } else if (menu.getPage() == 2) {

                    if (event.getCurrentItem().isSimilar(ItemStorage.getBackButton())) {

                        menu.setPage(0);
                        event.getWhoClicked().openInventory(menu.getInventory());

                    } else {

                        Integer itemId = ItemStorage.getRangedReverse(event.getCurrentItem());
                        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getWhoClicked().getUniqueId());

                        if (itemId != null && itemId > playerData.getRangedEquipment()) {

                            if (playerData.getPoints() >= ItemStorage.getRangedPrice(itemId)) {

                                playerData.setRangedEquipment(itemId);
                                playerData.setPoints(playerData.getPoints() - ItemStorage.getRangedPrice(itemId));
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§aItem successfully upgraded");

                            } else {

                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§cYou don't have enough points to upgrade (Price: " + ItemStorage.getRangedPrice(itemId) + ")");

                            }

                        } else {

                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§cYou cannot upgrade to an item which is the same or below the item you already have");

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

            if (ItemStorage.getMeleeReverse(event.getItemDrop().getItemStack()) != null) {
                event.setCancelled(true);
            }

            if (ItemStorage.getRangedReverse(event.getItemDrop().getItemStack()) != null) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (this.plugin.getGame() instanceof  Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            if (event.getItem() != null && ItemStorage.getIdPrefix(event.getItem()).equals(ItemStorage.HOTBAR_ITEM) && ItemStorage.getId(event.getItem()) == 0) {

                event.setCancelled(true);

                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                    PlayerMenu menu = ((Game) this.plugin.getGame()).getPlayerMenu(event.getPlayer().getUniqueId());

                    menu.setPage(0);
                    event.getPlayer().openInventory(menu.getInventory());

                }

                return;
            }

        }

    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {

            if (event.getPlayer().getInventory().getHeldItemSlot() == 8) {
                event.setCancelled(true);
                return;
            }

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getPlayer().getUniqueId());

            if (playerData.getRangedEquipment() == 1301 || playerData.getRangedEquipment() == 1302) {
                event.setCancelled(true);
                return;
            }

        }
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player && this.plugin.getGame() instanceof Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (this.plugin.getGame() instanceof Game) {

            if (event.getEntity() instanceof Player && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

                ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId()).setRegenerationCooldown(0);
                ((Player) event.getEntity()).removePotionEffect(PotionEffectType.REGENERATION);

            }

        }
    }

    /*
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && this.plugin.getGame() instanceof  Game && ((Game) this.plugin.getGame()).getPlayerMap().containsKey(event.getEntity().getUniqueId())) {

            PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(event.getEntity().getUniqueId());

            if (event.getBow() != null && playerData.getRangedEquipment() == 1602 && event.getBow().isSimilar(ItemStorage.getRanged(1602))) {

                Random random = new Random();

                if (random.nextInt(10) == 9) {

                    if (((Game) this.plugin.getGame()).getWorld().getWeatherDuration() >= 0) {
                        event.getEntity().sendMessage("§bThe weather has been changed through your weather manipulation ability");
                    }

                    ((Game) this.plugin.getGame()).getWorld().setThundering(true);


                }

            }

        }
    }
     */

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
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
