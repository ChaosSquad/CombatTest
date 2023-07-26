package net.jandie1505.combattest;

import de.myzelyam.api.vanish.VanishAPI;
import net.jandie1505.combattest.commands.CombatTestCommand;
import net.jandie1505.combattest.config.ConfigManager;
import net.jandie1505.combattest.config.DefaultConfigValues;
import net.jandie1505.combattest.game.Game;
import net.jandie1505.combattest.lobby.Lobby;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

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
    private List<World> managedWorlds;
    private boolean cloudSystemMode;
    private boolean svLoaded;

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
        this.managedWorlds = Collections.synchronizedList(new ArrayList<>());
        this.cloudSystemMode = this.singleServer && this.configManager.getConfig().optJSONObject("cloudSystemMode", new JSONObject()).optBoolean("enable", false);

        try {
            Class.forName("de.myzelyam.api.vanish.VanishAPI");
            this.svLoaded = true;
            this.getLogger().info("SuperVanish/PremiumVanish integration enabled (auto-bypass when vanished)");
        } catch (ClassNotFoundException ignored) {
            this.svLoaded = false;
        }

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

                    // Player management

                    for (Player player : List.copyOf(this.getServer().getOnlinePlayers())) {

                        if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
                            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                        }

                        if (this.autostartNewGame && this.singleServer) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§b--- Starting new game in " + this.autostartNewGameTimer + " seconds ---"));
                        }

                    }

                    // Autostart new game

                    if (this.autostartNewGame && this.timeStep == 1) {

                        if (this.autostartNewGameTimer <= 0) {
                            this.autostartNewGameTimer = 30;
                            this.startGame();
                        } else {
                            this.autostartNewGameTimer--;
                        }

                    }

                }

                // Manage worlds

                for (World world : List.copyOf(this.managedWorlds)) {

                    if (world == null || !this.getServer().getWorlds().contains(world) || this.getServer().getWorlds().get(0) == world) {
                        this.managedWorlds.remove(world);
                        continue;
                    }

                    if (!(this.game instanceof Lobby || this.game instanceof Game) || (this.game instanceof Game && ((Game) this.game).getWorld() != world)) {
                        this.unloadWorld(world);
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

        this.getLogger().info("CombatTest Plugin was successfully enabled");

        if (this.isCloudSystemMode()) {
            this.getLogger().info("Cloud System Mode enabled (autostart game + switch to ingame + shutdown on end)");
            this.startGame();
        }
    }

    @Override
    public void onDisable() {

        this.game = null;

        for (World world : List.copyOf(this.managedWorlds)) {
            this.unloadWorld(world);
        }

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

        if (this.getBypassingPlayers().contains(playerId)) {
            return true;
        }

        if (this.configManager.getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("supervanish-premiumvanish", false) && this.svLoaded) {

            Player player = this.getServer().getPlayer(playerId);

            if (player == null) {
                return false;
            }

            return VanishAPI.isInvisible(player);

        }

        return false;
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

    public World loadWorld(String name) {

        World world = this.getServer().getWorld(name);

        if (world != null) {
            this.managedWorlds.add(world);
            world.setAutoSave(false);
            this.getLogger().info("World [" + this.getServer().getWorlds().indexOf(world) + "] " + world.getUID() + " (" + world.getName() + ") is already loaded and was added to managed worlds");
            return world;
        }

        world = this.getServer().createWorld(new WorldCreator(name));

        if (world != null) {
            this.managedWorlds.add(world);
            world.setAutoSave(false);
            this.getLogger().info("Loaded world [" + this.getServer().getWorlds().indexOf(world) + "] " + world.getUID() + " (" + world.getName() + ")");
        } else {
            this.getLogger().warning("Error while loading world " + name);
        }

        return world;

    }

    public boolean unloadWorld(World world) {

        if (world == null || this.getServer().getWorlds().get(0) == world || !this.managedWorlds.contains(world) || !this.getServer().getWorlds().contains(world)) {
            return false;
        }

        UUID uid = world.getUID();
        int index = this.getServer().getWorlds().indexOf(world);
        String name = world.getName();

        for (Player player : world.getPlayers()) {
            player.teleport(new Location(this.getServer().getWorlds().get(0), 0, 0, 0));
        }

        boolean success = this.getServer().unloadWorld(world, false);

        if (success) {
            this.managedWorlds.remove(world);
            this.getLogger().info("Unloaded world [" + index + "] " + uid + " (" + name + ")");
        } else {
            this.getLogger().warning("Error white unloading world [" + index + "] " + uid + " (" + name + ")");
        }

        return success;

    }

    public List<World> getManagedWorlds() {
        return List.copyOf(this.managedWorlds);
    }

    public boolean isCloudSystemMode() {
        return this.singleServer && this.cloudSystemMode;
    }

    public void givePointsToPlayer(Player player, int amount, String message) {

        if (this.configManager.getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("playerpoints", false)) {

            try {
                Class.forName("org.black_ixx.playerpoints.PlayerPoints");
                Class.forName("org.black_ixx.playerpoints.PlayerPointsAPI");

                PlayerPointsAPI pointsAPI = PlayerPoints.getInstance().getAPI();

                if (amount <= 0) {
                    return;
                }

                if (amount > this.configManager.getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("maxRewardsAmount", 5000)) {
                    amount = this.configManager.getConfig().optJSONObject("playerPointsRewards", new JSONObject()).optInt("maxRewardsAmount", 5000);
                }

                pointsAPI.give(player.getUniqueId(), amount);

                if (message != null) {
                    player.sendMessage(message.replace("{points}", String.valueOf(amount)));
                }

            } catch (ClassNotFoundException e) {

            }

        }

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
