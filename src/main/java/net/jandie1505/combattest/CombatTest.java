package net.jandie1505.combattest;

import net.jandie1505.combattest.commands.CombatTestCommand;
import net.jandie1505.combattest.config.ConfigManager;
import net.jandie1505.combattest.config.DefaultConfigValues;
import net.jandie1505.combattest.lobby.Lobby;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CombatTest extends JavaPlugin {
    private ConfigManager configManager;
    private ConfigManager mapConfig;
    private GamePart game;
    private List<UUID> bypassingPlayers;
    private String permissionPrefix;
    private boolean singleServer;
    private boolean autostartNewGame;
    private int timeStep;
    private int autostartNewGameTimer;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this, DefaultConfigValues.getGeneralConfig(), false, "config.json");
        this.mapConfig = new ConfigManager(this, DefaultConfigValues.getWorldConfig(), true, "maps.json");
        this.configManager.reloadConfig();
        this.mapConfig.reloadConfig();
        this.game = null;
        this.bypassingPlayers = Collections.synchronizedList(new ArrayList<>());
        this.permissionPrefix = this.configManager.getConfig().optString("permissionsPrefix", "combattest");
        this.singleServer = this.configManager.getConfig().optBoolean("singleServerMode", false);
        this.autostartNewGame = this.configManager.getConfig().optBoolean("autostartNewGame", false);
        this.timeStep = 0;
        this.autostartNewGameTimer = 30;

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

                } else {

                    for (Player player : List.copyOf(this.getServer().getOnlinePlayers())) {

                        if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
                            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                        }

                        if (this.autostartNewGame && this.singleServer) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§b--- Starting new game in " + this.autostartNewGameTimer + " seconds ---"));
                        }

                    }

                    if (this.autostartNewGame && this.timeStep == 1) {

                        if (this.autostartNewGameTimer <= 0) {
                            this.autostartNewGameTimer = 30;
                            this.startGame();
                        } else {
                            this.autostartNewGameTimer--;
                        }

                    }

                }

                if (this.timeStep != 0) {
                    this.timeStep = 0;
                } else {
                    this.timeStep = 1;
                }

            } catch (Exception e) {
                this.getLogger().warning("Exception in game: " + e + "\nMessage: " + e.getMessage() + "\nStacktrace: " + Arrays.toString(e.getStackTrace()) + "--- END ---");
                this.stopGame();
            }
        }, 0, 10);
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

    public ConfigManager getMapConfig() {
        return this.mapConfig;
    }

    public String getPermissionPrefix() {
        return permissionPrefix;
    }

    public boolean isSingleServer() {
        return this.singleServer;
    }

    public boolean isAutostartNewGame() {
        return this.autostartNewGame;
    }

    public void setAutostartNewGame(boolean autostartNewGame) {
        this.autostartNewGame = autostartNewGame;
    }

    public static int getWeather(World world) {

        if (world == null) {
            return -1;
        }

        if (world.hasStorm() && world.getWeatherDuration() > 0) {
            if (world.isThundering() && world.getThunderDuration() > 0) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }

    }

    public static void setClearWeather(World world) {

        if (world == null) {
            return;
        }

        world.setStorm(false);
        world.setThundering(false);
        world.setClearWeatherDuration(112801);
        world.setWeatherDuration(0);
        world.setThunderDuration(0);

    }

    public static void setRainingWeather(World world) {

        if (world == null) {
            return;
        }

        world.setStorm(true);
        world.setThundering(false);
        world.setClearWeatherDuration(0);
        world.setWeatherDuration(15376);
        world.setThunderDuration(15376);

    }

    public static void setThunderingWeather(World world) {

        if (world == null) {
            return;
        }

        world.setStorm(true);
        world.setThundering(true);
        world.setClearWeatherDuration(0);
        world.setWeatherDuration(13834);
        world.setThunderDuration(13834);

    }

}
