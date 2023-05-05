package net.jandie1505.combattest;

import net.jandie1505.combattest.commands.CombatTestCommand;
import net.jandie1505.combattest.config.ConfigManager;
import net.jandie1505.combattest.config.DefaultConfigValues;
import net.jandie1505.combattest.game.GamePart;
import net.jandie1505.combattest.game.GameStatus;
import net.jandie1505.combattest.game.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CombatTest extends JavaPlugin {
    private ConfigManager configManager;
    private GamePart game;
    private List<UUID> bypassingPlayers;
    private String permissionPrefix;
    private boolean singleServer;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this, DefaultConfigValues.getGeneralConfig(), false, "config.json");
        this.configManager.reloadConfig();
        this.game = null;
        this.bypassingPlayers = Collections.synchronizedList(new ArrayList<>());
        this.permissionPrefix = this.configManager.getConfig().optString("permissionsPrefix", "combattest");
        this.singleServer = this.configManager.getConfig().optBoolean("singleServerMode", false);

        this.getCommand("combattest").setExecutor(new CombatTestCommand(this));
        this.getCommand("combattest").setTabCompleter(new CombatTestCommand(this));

        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {

                if (this.game != null) {

                    int status = this.game.tick();

                    if (status == GameStatus.ABORT) {
                        this.stopGame();
                    } else if (status == GameStatus.NEXT) {
                        this.game = this.game.getNextStatus();
                    }

                }

            } catch (Exception e) {
                this.getLogger().warning("Exception in game: " + e + "\nMessage: " + e.getMessage() + "\nStacktrace: " + Arrays.toString(e.getStackTrace()) + "--- END ---");
                this.stopGame();
            }
        }, 0, 20);
    }

    public boolean startGame() {
        if (game == null) {
            this.game = new Lobby(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean stopGame() {
        boolean returnValue = this.game != null;

        this.game = null;

        return returnValue;
    }

    public GamePart getGame() {
        return this.game;
    }

    public Player getPlayerFromString(String playerString) {
        try {
            UUID uuid = UUID.fromString(playerString);
            return this.getServer().getPlayer(uuid);
        } catch (IllegalArgumentException e) {
            return this.getServer().getPlayer(playerString);
        }
    }

    public OfflinePlayer getOfflinePlayerFromString(String playerString) {
        try {
            UUID uuid = UUID.fromString(playerString);
            return this.getServer().getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            return this.getServer().getOfflinePlayer(playerString);
        }
    }

    public void addBypassingPlayer(UUID playerId) {
        this.bypassingPlayers.add(playerId);
    }

    public void removeBypassingPlayer(UUID playerId) {
        this.bypassingPlayers.remove(playerId);
    }

    public List<UUID> getBypassingPlayers() {
        return List.copyOf(this.bypassingPlayers);
    }

    public boolean isPlayerBypassing(UUID playerId) {
        return this.getBypassingPlayers().contains(playerId);
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public String getPermissionPrefix() {
        return permissionPrefix;
    }

    public boolean isSingleServer() {
        return this.singleServer;
    }

    public static ItemStack buildInventoryButton(Material material, String name, List<String> lore, int id) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

        if (name != null) {
            meta.setDisplayName(name);
        }

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(String.valueOf(id));

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }
}
