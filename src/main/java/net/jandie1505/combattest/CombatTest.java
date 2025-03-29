package net.jandie1505.combattest;

import de.myzelyam.api.vanish.VanishAPI;
import net.chaossquad.mclib.dynamicevents.EventListenerManager;
import net.jandie1505.combattest.commands.CombatTestCommand;
import net.jandie1505.combattest.commands.CombatTestCommandOld;
import net.jandie1505.combattest.config.ConfigManager;
import net.jandie1505.combattest.config.DefaultConfigValues;
import net.jandie1505.combattest.game.base.GamePart;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.commands.GamePayCommand;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;

public class CombatTest extends JavaPlugin {
    private ConfigManager configManager;
    private ConfigManager mapConfig;
    private EventListenerManager listenerManager;
    private GamePart game;
    private List<UUID> bypassingPlayers;
    private String permissionPrefix;
    private boolean singleServer;
    private boolean autostartNewGame;
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

        this.listenerManager = new EventListenerManager(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                CombatTest.this.listenerManager.manageListeners();
            }
        }.runTaskTimer(CombatTest.this, 0L, 10*20L);

        this.game = null;
        this.bypassingPlayers = Collections.synchronizedList(new ArrayList<>());
        this.permissionPrefix = this.configManager.getConfig().optString("permissionsPrefix", "combattest");
        this.singleServer = this.configManager.getConfig().optBoolean("singleServerMode", false);
        this.autostartNewGame = this.configManager.getConfig().optBoolean("autostartNewGame", false);
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

        CombatTestCommand command = new CombatTestCommand(this);
        this.getCommand("combattest").setExecutor(command);
        this.getCommand("combattest").setTabCompleter(command);

        PluginCommand payCommand = this.getCommand("pay");
        if (payCommand != null) {
            GamePayCommand cmd = new GamePayCommand(this);
            payCommand.setExecutor(cmd);
            payCommand.setTabCompleter(cmd);
        }

        this.getCommand("combattest-old").setExecutor(new CombatTestCommandOld(this));
        this.getCommand("combattest-old").setTabCompleter(new CombatTestCommandOld(this));

        Listener listener = new EventListener(this);
        this.listenerManager.addExceptedListener(listener);
        this.getServer().getPluginManager().registerEvents(listener, this);

        /*
        Game tick task
         */
        new BukkitRunnable() {
            @Override
            public void run() {

                if (CombatTest.this.game == null) return;

                try {

                    boolean success = CombatTest.this.game.tick();
                    if (!success) {
                        CombatTest.this.stopGame();
                        CombatTest.this.getLogger().log(Level.WARNING, "Stopped game because of game stop request of the game instancec.");
                    }

                } catch (Exception e) {
                    CombatTest.this.getLogger().log(Level.SEVERE, "Exception in game. Stopping game.", e);
                    CombatTest.this.stopGame();
                }

            }
        }.runTaskTimer(CombatTest.this, 0L, 1L);

        /*
        Autostart new game task
         */
        new BukkitRunnable() {
            @Override
            public void run() {

                if (CombatTest.this.game != null) return;

                // Player management

                for (Player player : List.copyOf(CombatTest.this.getServer().getOnlinePlayers())) {

                    if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
                        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    }

                    if (CombatTest.this.autostartNewGame && CombatTest.this.singleServer) {
                        player.sendActionBar(Component.text("--- Starting new game in " + CombatTest.this.autostartNewGameTimer + " seconds ---", NamedTextColor.AQUA));
                    }

                }

                // Autostart new game

                if (CombatTest.this.autostartNewGame) {

                    if (CombatTest.this.autostartNewGameTimer <= 0) {
                        CombatTest.this.autostartNewGameTimer = 30;
                        CombatTest.this.startGame();
                    } else {
                        CombatTest.this.autostartNewGameTimer--;
                    }

                }

            }
        }.runTaskTimer(CombatTest.this, 0L, 20L);

        /*
        Manage worlds task
         */
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {

                for (World world : List.copyOf(this.managedWorlds)) {

                    if (world == null || !this.getServer().getWorlds().contains(world) || this.getServer().getWorlds().get(0) == world) {
                        this.managedWorlds.remove(world);
                        continue;
                    }

                    if (!(this.game instanceof Lobby || this.game instanceof Game) || (this.game instanceof Game g && (g).getWorld() != world)) {
                        this.unloadWorld(world);
                    }

                }

            } catch (Exception e) {
                this.getLogger().warning("Exception in game: " + e + "\nMessage: " + e.getMessage() + "\nStacktrace: " + Arrays.toString(e.getStackTrace()) + "--- END ---");
                this.stopGame();
            }
        }, 0, 20L);

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

    public boolean startGame(@NotNull GamePart game) {
        if (this.game == null) {
            this.game = game;
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

    public List<UUID> getLocalBypassingPlayers() {
        return List.copyOf(this.bypassingPlayers);
    }

    public boolean isPlayerBypassing(UUID playerId) {

        if (this.getLocalBypassingPlayers().contains(playerId)) {
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

    public boolean isPlayerBypassing(@Nullable OfflinePlayer player) {
        if (player == null) return false;
        return this.isPlayerBypassing(player.getUniqueId());
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public ConfigManager getMapConfig() {
        return this.mapConfig;
    }

    public EventListenerManager getListenerManager() {
        return this.listenerManager;
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

}
