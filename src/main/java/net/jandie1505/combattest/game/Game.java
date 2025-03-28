package net.jandie1505.combattest.game;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import net.chaossquad.mclib.WorldUtils;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.ItemStorage;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.endlobby.Endlobby;
import net.jandie1505.combattest.game.equipment.DefaultEquipment;
import net.jandie1505.combattest.game.equipment.EquipmentSystem;
import net.jandie1505.combattest.lobby.LobbyPlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Game extends GamePart {
    @NotNull private final CombatTest plugin;
    @NotNull private final World world;
    @NotNull private final Map<UUID, PlayerData> players;
    @NotNull private final EquipmentSystem equipmentSystem;
    private final List<Spawnpoint> spawnpoints;
    private int time;
    private boolean killswitch;
    private boolean enableBorder;
    private int[] border;
    @Deprecated private Map<UUID, PlayerMenu> playerMenus;
    private boolean enforcePvp;

    public Game(CombatTest plugin, int time, World world, Map<UUID, LobbyPlayerData> players, List<Spawnpoint> spawnpoints, boolean enableBorder, int[] border, boolean enforcePvp) {
        super(plugin);
        this.plugin = plugin;
        this.killswitch = false;
        this.time = time;
        this.world = world;
        this.equipmentSystem = new EquipmentSystem(this, () -> false);
        this.equipmentSystem.getEquipmentMap().putAll(DefaultEquipment.getEquipment());

        if (world == null) {
            this.killswitch = true;
        }

        this.players = Collections.synchronizedMap(new HashMap<>());

        for (UUID playerId : Map.copyOf(players).keySet()) {
            LobbyPlayerData lobbyPlayerData = players.get(playerId);
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                continue;
            }

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);

            PlayerData playerData = new PlayerData(playerId);

            if (lobbyPlayerData != null) {
                playerData.setTeam(lobbyPlayerData.getTeam());
            }

            this.players.put(playerId, playerData);

            player.getInventory().clear();
        }

        this.spawnpoints = Collections.synchronizedList(new ArrayList<>());
        this.spawnpoints.addAll(spawnpoints);

        this.enableBorder = enableBorder;
        this.border = border;
        if (enableBorder && border.length < 6) {
            this.killswitch = true;
        }

        this.enforcePvp = enforcePvp;

        this.playerMenus = Collections.synchronizedMap(new HashMap<>());

        if (world != null) {
            world.setTime(6000);
        }

        if (this.plugin.isCloudSystemMode()) {

            // Custom command

            String customCommand = this.plugin.getConfigManager().getConfig().optJSONObject("cloudSystemMode", new JSONObject()).optString("switchToIngameCommand", "");

            if (!customCommand.equalsIgnoreCase("")) {
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), customCommand);
            }

            // CloudNet ingame state

            if (this.plugin.getConfigManager().getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("cloudnet", false)) {

                try {

                    try {
                        Class.forName("eu.cloudnetservice.driver.inject.InjectionLayer");
                        Class.forName("eu.cloudnetservice.modules.bridge.BridgeServiceHelper");

                        BridgeServiceHelper bridgeServiceHelper = InjectionLayer.ext().instance(BridgeServiceHelper.class);

                        if (bridgeServiceHelper != null) {
                            bridgeServiceHelper.changeToIngame();
                            this.plugin.getLogger().info("Changed server to ingame state (CloudNet)");
                        }
                    } catch (ClassNotFoundException ignored) {
                        // ignored (cloudnet not installed)
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        this.getTaskScheduler().scheduleRepeatingTask(this::timeTask, 1, 20, "time");
        this.getTaskScheduler().scheduleRepeatingTask(this::tridentCleanupTask, 1, 20, "trident_cleanup");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerRespawnTask, 1, 20, "player_respawn");
        this.getTaskScheduler().scheduleRepeatingTask(this::offlineIngamePlayersTask, 1, 20, "offline_player");
        this.getTaskScheduler().scheduleRepeatingTask(this::task, 1, 10, "old_task");
        this.getTaskScheduler().scheduleRepeatingTask(this::weatherTask, 1, 20, "weather");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerScoreboardTask, 1, 20, "player_scoreboard");
        this.getTaskScheduler().scheduleRepeatingTask(this::playerMiscValuesTask, 1, 10, "player_misc_values");
        this.getTaskScheduler().scheduleRepeatingTask(this::notIngamePlayersTask, 1, 20, "not_ingame_players");
    }

    @Override
    public boolean shouldExecute() {
        return super.shouldExecute() && !this.killswitch;
    }

    // ----- TASKS -----

    /**
     * Manages the time and finishes the game when expired.
     */
    private void timeTask() {

        if (this.time >= 0) {
            this.time--;
        } else {
            this.finishGame();
            this.killswitch = true;
            return;
        }

    }

    /**
     * Cleans up trident entities in the world and trident item entities.
     */
    private void tridentCleanupTask() {

        for (Entity entity : world.getEntities()) {

            if (entity instanceof Trident trident) {

                // Remove when the trident is in the world for a specific time
                if (trident.getTicksLived() > 600) {
                    trident.remove();
                    continue;
                }

                // Remove trident when it has no data
                if (trident.getItemStack().getItemMeta() == null) {
                    trident.remove();
                    continue;
                }

                // Remove trident when it has no loyalty
                if (!trident.getItemStack().getItemMeta().hasEnchant(Enchantment.LOYALTY)) {
                    trident.remove();
                    continue;
                }

                // Remove trident when it has no shooter
                if (trident.getShooter() == null) {
                    trident.remove();
                    continue;
                }

                // Remove trident if its shooter is not a player
                if (!(trident.getShooter() instanceof Player shooter)) {
                    trident.remove();
                    continue;
                }

                // Sets a specific trident damage for specific equipments
                double rangedDamage = trident.getItemStack().getItemMeta().getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_TRIDENT_RANGED_DAMAGE, PersistentDataType.DOUBLE, trident.getDamage());
                if (rangedDamage != trident.getDamage()) {
                    trident.setDamage(rangedDamage);
                }

                // Removes the trident when the player is not ingame
                if (!this.players.containsKey(shooter.getUniqueId())) {
                    trident.remove();
                    continue;
                }

            } else if (entity instanceof Item itemEntity) {

                // Remove dropped tridents
                if (itemEntity.getItemStack().getType() == Material.TRIDENT) {
                    entity.remove();
                    continue;
                }

            }

        }

    }

    /**
     * Handles player respawns, gamemode and other basic values of players.
     */
    private void playerRespawnTask() {

        for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {
            PlayerData playerData = this.players.get(player.getUniqueId());
            if (playerData == null) continue;

            // Do actions for when the player is alive and when the player is not alive
            if (playerData.isAlive()) { // PLAYER IS ALIVE

                // Enforce adventure to adventure
                if ((player.getGameMode() != GameMode.ADVENTURE) && !(this.plugin.isPlayerBypassing(player.getUniqueId()))) {
                    player.setGameMode(GameMode.ADVENTURE);
                }

                // Reset respawn timer
                if (playerData.getRespawntimer() < 5) {
                    playerData.setRespawntimer(5);
                }

            } else { // PLAYER IS NOT ALIVE

                // Enforce spectator gamemode
                if (player.getGameMode() != GameMode.SPECTATOR && !this.plugin.isPlayerBypassing(player.getUniqueId())) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                if (playerData.getRespawntimer() > 0) { // Respawn timer counting down

                    // Sends the dead title
                    player.showTitle(Title.title(
                            Component.text("DEAD", NamedTextColor.RED),
                            Component.text("You will respawn in "),
                            Title.Times.times(Duration.ZERO, Duration.ofMillis(1250), Duration.ZERO)
                    ));

                    // Count down respawn timer
                    playerData.setRespawntimer(playerData.getRespawntimer() - 1);

                } else { // Respawn timer has expired
                    this.respawnPlayer(player);  // Respawn the player
                }

            }


        }

    }

    /**
     * Set player to not alive when player is offline.
     */
    private void offlineIngamePlayersTask() {

        for (Map.Entry<UUID, PlayerData> entry : this.players.entrySet()) {
            Player player = this.plugin.getServer().getPlayer(entry.getKey());
            if (player != null) return;
            entry.getValue().setAlive(false);
        }

    }

    /**
     * Changes the weather in specific intervals.
     */
    private void weatherTask() {

        if ((this.time % 100) == 0) {

            if (this.time > 0) {
                if (new Random().nextInt(2) == 1) {

                    switch (new Random().nextInt(6)) {
                        case 0:
                        case 1:
                        case 2:
                            WorldUtils.setWeather(this.world, WorldUtils.WeatherType.CLEAR);
                            break;
                        case 3:
                        case 4:
                            WorldUtils.setWeather(this.world, WorldUtils.WeatherType.RAIN);
                            break;
                        case 5:
                            WorldUtils.setWeather(this.world, WorldUtils.WeatherType.THUNDER);
                            break;
                        default:
                            break;
                    }

                }
            } else {
                WorldUtils.setWeather(this.world, WorldUtils.WeatherType.CLEAR);
            }

        }

    }

    /**
     * Manages player stuff that does not fit into the other categories.
     */
    private void playerMiscValuesTask() {

        for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {
            PlayerData playerData = this.players.get(player.getUniqueId());
            if (playerData == null) continue;

            // Saturation

            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
            }

            if (player.getSaturation() < 20) {
                player.setSaturation(20);
            }

            // Regeneration

            if (!player.hasPotionEffect(PotionEffectType.REGENERATION) && playerData.getRegenerationCooldown() >= 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999, 0, false, false));
            } else if (!player.hasPotionEffect(PotionEffectType.REGENERATION) && playerData.getRegenerationCooldown() < 10) {
                playerData.setRegenerationCooldown(playerData.getRegenerationCooldown() + 1);
            }

            // Player weather

            if (playerData.isWeatherDisabled() && player.getPlayerWeather() != WeatherType.CLEAR) {
                player.setPlayerWeather(WeatherType.CLEAR);
            } else if (!playerData.isWeatherDisabled() && ((WorldUtils.getWeather(this.world) != WorldUtils.WeatherType.CLEAR && player.getPlayerWeather() != WeatherType.DOWNFALL) || (WorldUtils.getWeather(this.world) == WorldUtils.WeatherType.CLEAR && player.getPlayerWeather() == WeatherType.DOWNFALL))){
                player.resetPlayerWeather();
            }

        }

    }

    /**
     * Handles the player scoreboards.
     */
    private void playerScoreboardTask() {
        if (!this.plugin.isSingleServer()) return;

        for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {
            PlayerData playerData = this.players.get(player.getUniqueId());
            if (playerData == null) continue;

            Scoreboard scoreboard = playerData.getScoreboard();

            // Reset all scores
            for (String playerString : scoreboard.getEntries()) {
                scoreboard.resetScores(playerString);
            }

            // Sidebar
            if (scoreboard.getObjective("sidebar") == null) {
                Objective sidebarObjective = scoreboard.registerNewObjective("sidebar", Criteria.DUMMY, "§6§lCOMBAT TEST");
                sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            Objective sidebarObjective = scoreboard.getObjective("sidebar");

            sidebarObjective.getScore("§§§§").setScore(12);
            sidebarObjective.getScore("Kills: §a" + playerData.getKills()).setScore(11);
            sidebarObjective.getScore("Deaths: §a" + playerData.getDeaths()).setScore(10);
            sidebarObjective.getScore("K/D: §a" + PlayerData.getKD(playerData.getKills(), playerData.getDeaths())).setScore(9);
            sidebarObjective.getScore("Points: §a" + playerData.getPoints()).setScore(8);

            if (playerData.getTeam() > 0) {

                sidebarObjective.getScore("§§§").setScore(7);
                sidebarObjective.getScore("Team: §a" + playerData.getTeam()).setScore(6);
                sidebarObjective.getScore("Team Kills: §a" + this.getTeamKills(playerData.getTeam())).setScore(5);
                sidebarObjective.getScore("Team Deaths: §a" + this.getTeamDeaths(playerData.getTeam())).setScore(4);
                sidebarObjective.getScore("Team K/D: §a" + PlayerData.getKD(this.getTeamKills(playerData.getTeam()), this.getTeamDeaths(playerData.getTeam()))).setScore(3);

            }

            sidebarObjective.getScore("§§").setScore(2);
            sidebarObjective.getScore("Time: §a" + this.time + "s").setScore(1);
            sidebarObjective.getScore("§").setScore(0);

            // Teams
            if (scoreboard.getTeam("spectator") == null) {
                scoreboard.registerNewTeam("spectator");
            }
            Team spectatorTeam = scoreboard.getTeam("spectator");

            if (scoreboard.getTeam("own") == null) {
                Team ownTeam = scoreboard.registerNewTeam("own");
                ownTeam.setAllowFriendlyFire(false);
                ownTeam.setColor(ChatColor.GREEN);
                ownTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            }
            Team ownTeam = scoreboard.getTeam("own");

            if (scoreboard.getTeam("enemy") == null) {
                Team enemyTeam = scoreboard.registerNewTeam("enemy");
                enemyTeam.setAllowFriendlyFire(true);
                enemyTeam.setColor(ChatColor.RED);
                enemyTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
            Team enemyTeam = scoreboard.getTeam("enemy");

            if (scoreboard.getObjective("tablist") == null) {
                Objective tablistObjective = scoreboard.registerNewObjective("tablist", Criteria.DUMMY, "");
                tablistObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            }
            Objective tablistObjective = scoreboard.getObjective("tablist");

            ownTeam.addEntry(player.getName());
            tablistObjective.getScore(player.getName()).setScore(playerData.getTeam());

            for (Player p : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {

                if (p == player) {
                    continue;
                }

                if (this.getPlayerMap().containsKey(p.getUniqueId())) {
                    PlayerData pdata = this.getPlayerMap().get(p.getUniqueId());

                    if (playerData.getTeam() > 0 && pdata.getTeam() == playerData.getTeam()) {
                        ownTeam.addEntry(p.getName());
                    } else {
                        enemyTeam.addEntry(p.getName());
                    }

                    tablistObjective.getScore(p.getName()).setScore(pdata.getTeam());

                } else {
                    spectatorTeam.addEntry(p.getName());
                }

            }

            // Set scoreboard
            if (player.getScoreboard() != scoreboard) {
                player.setScoreboard(scoreboard);
            }

        }

    }

    private void notIngamePlayersTask() {
        if (!this.plugin.isSingleServer()) return;

        for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {

            // Filter for non-ingame players only
            if (this.players.containsKey(player.getUniqueId())) {
                continue;
            }

            // Enforce spectator mode
            if (player.getGameMode() != GameMode.SPECTATOR && !this.plugin.isPlayerBypassing(player.getUniqueId())) {
                player.setGameMode(GameMode.SPECTATOR);
            }

            // Teleport to map world
            if (player.getLocation().getWorld() != this.world && !this.plugin.isPlayerBypassing(player.getUniqueId())) {
                player.teleport(new Location(this.world, 0, 0, 0));
            }

            // Reset the scoreboard to the main scoreboard
            if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }

        }

    }

    @Deprecated(forRemoval = true)
    public void task() {

        // KILL SWITCH

        if (this.killswitch) return;

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                break;
            }

            PlayerData playerData = this.players.get(playerId);

            // Player Menu Object

            if (!this.playerMenus.containsKey(playerId)) {

                this.playerMenus.put(playerId, new PlayerMenu(this, playerId));

            }

            // Player Menu Button

            if (!ItemStorage.getPlayerMenuButton().isSimilar(player.getInventory().getItem(8))) {

                player.getInventory().remove(ItemStorage.getPlayerMenuButton());
                player.getInventory().setItem(8, ItemStorage.getPlayerMenuButton());

            }

            // Remove empty bottles and buckets

            if (player.getInventory().contains(Material.GLASS_BOTTLE)) {
                player.getInventory().remove(Material.GLASS_BOTTLE);
            }

            if (player.getInventory().contains(Material.BUCKET)) {
                player.getInventory().remove(Material.BUCKET);
            }

            // Clear riptide trident after use

            /*
            if (!player.isRiptiding() && playerData.hasUsedTrident() && playerData.getRangedEquipment() >= 1500 && playerData.getRangedEquipment() <= 1599) {
                playerData.setHasUsedTrident(false);
                player.getInventory().remove(Material.TRIDENT);
                player.sendMessage("§bYour Trident no longer has enough energy to fly and needs to recharge first");
            }

             */

            // No PvP Timer

            if (this.enforcePvp) {

                /*
                TODO: Put this back into execute every second
                if (this.timeStep >= 1) {

                    if (playerData.getNoPvpTimer() >= 45) {
                        player.sendMessage("§bYour position was revealed because you were not in combat for too long");
                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0, true, true));
                        playerData.setNoPvpTimer(0);
                    } else {
                        playerData.setNoPvpTimer(playerData.getNoPvpTimer() + 1);
                    }

                }
                 */

                // see commit above
                if (playerData.getNoPvpTimer() >= 45) {
                    player.sendMessage("§bYour position was revealed because you were not in combat for too long");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0, true, true));
                    playerData.setNoPvpTimer(0);
                } else {
                    playerData.setNoPvpTimer(playerData.getNoPvpTimer() + 1);
                }

            }

            // Idle points

            /*
            TODO: Put this back into execute every second
            if (this.timeStep >= 1) {
                playerData.setPoints(playerData.getPoints() + 2);
            }
             */

            // See comment above
            playerData.setPoints(playerData.getPoints() + 2);

            // Actionbar

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a" + playerData.getKills() + " kills §8§l|§r§c " + playerData.getDeaths() + " deaths §8§l|§r§6 Points: " + playerData.getPoints() + " §8§l|§r§6 " + this.time + "s"));

        }

        // HANDLE MENUS

        for (UUID playerId : this.getPlayerMenus().keySet()) {

            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null || !this.players.containsKey(playerId)) {

                this.playerMenus.remove(playerId);

            }

        }

    }

    /**
     * Returns true if the player has a loyalty trident.
     * @param player player
     * @return true = has trident
     */
    private boolean hasTrident(@NotNull Player player) {

        boolean tridentContinue = false;
        for (Trident trident : this.world.getEntitiesByClass(Trident.class)) {

            // I don' know why this is here
            if (trident == null) {
                continue;
            }

            // Check for the player
            if (trident.getShooter() != player) {
                continue;
            }

            // Skip non-loyalty tridents
            if (trident.getLoyaltyLevel() <= 0) {
                continue;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean addPlayer(Player player) {
        if (player == null) {
            return false;
        }

        if (this.players.containsKey(player.getUniqueId())) {
            return false;
        }

        this.players.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        return true;
    }

    @Override
    public boolean removePlayer(UUID uuid) {
        return this.players.remove(uuid) != null;
    }

    @Override
    public List<UUID> getPlayers() {
        return List.copyOf(this.getPlayerMap().keySet());
    }

    @Override
    public List<UUID> getPlayers(UUID[] playerIds) {
        List<UUID> returnList = new ArrayList<>();

        for (UUID playerId : playerIds) {

            if (this.getPlayers().contains(playerId)) {
                returnList.add(playerId);
            }

        }

        return List.copyOf(returnList);
    }

    /**
     * Returns a set of all online ingame players.
     * @return ingame online players
     */
    public final Set<Player> getOnlinePlayers() {
        return this.players.keySet().stream().map(Bukkit::getPlayer).collect(Collectors.toSet());
    }

    /**
     * Returns the player data for the specified player UUID.<br/>
     * Returns null if the player is not ingame.
     * @param playerId player uuid
     * @return player data
     */
    public final @Nullable PlayerData getPlayerData(@NotNull UUID playerId) {
        return this.players.get(playerId);
    }

    /**
     * Returns
     * @param player
     * @return
     */
    public final @Nullable PlayerData getPlayerData(@Nullable OfflinePlayer player) {
        if (player == null) return null;
        return this.players.get(player.getUniqueId());
    }

    public Map<UUID, PlayerData> getPlayerMap() {
        return Map.copyOf(this.players);
    }

    public boolean isPlayerIngame(Player player) {
        if (player == null) {
            return false;
        }

        return this.getPlayerMap().containsKey(player.getUniqueId());
    }

    public boolean respawnPlayer(Player player) {
        if (player == null) {
            return false;
        }

        if (!this.getPlayerMap().containsKey(player.getUniqueId())) {
            return false;
        }

        PlayerData playerData = this.getPlayerMap().get(player.getUniqueId());

        Spawnpoint spawnpoint = this.getRandomSpawnpoint(playerData.getTeam());

        if (spawnpoint == null) {
            return false;
        }

        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(spawnpoint.buildLocation(world));
        this.getPlayerMap().get(player.getUniqueId()).setAlive(true);

        return true;
    }

    private Spawnpoint getRandomSpawnpoint(int teamMode) {
        int attempt = 0;

        while (attempt < 11) {

            try {

                Random random = new Random();
                Spawnpoint spawnpoint = this.spawnpoints.get(random.nextInt(this.spawnpoints.size()));

                if (attempt < 10) {
                    if (teamMode == spawnpoint.getTeamMode() || spawnpoint.getTeamMode() == -1) {
                        int radius = this.plugin.getConfigManager().getConfig().optInt("spawnpointBlockedRadius", 10);

                        Collection<Entity> nearbyEntities = world.getNearbyEntities(spawnpoint.buildLocation(world), radius, radius, radius);

                        if (nearbyEntities.isEmpty()) {
                            return spawnpoint;
                        }
                    }
                } else {
                    return spawnpoint;
                }

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

            attempt++;
        }

        return null;
    }

    public boolean isInBorders(Location location) {
        if (location.getWorld() != this.world) {
            return false;
        }

        return location.getBlockX() >= this.border[0] && location.getBlockY() >= this.border[1] && location.getBlockZ() >= this.border[2] && location.getBlockX() <= this.border[3] && location.getBlockY() <= this.border[4] && location.getBlockZ() <= this.border[5];
    }

    public final CombatTest getPlugin() {
        return this.plugin;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isEnableBorder() {
        return this.enableBorder;
    }

    public World getWorld() {
        return this.world;
    }

    private Map<UUID, PlayerMenu> getPlayerMenus() {
        return Map.copyOf(this.playerMenus);
    }

    public PlayerMenu getPlayerMenu(UUID playerId) {
        return this.getPlayerMenus().get(playerId);
    }

    public List<Integer> getTeams() {
        List<Integer> teamList = new ArrayList<>();

        for (UUID p : this.getPlayerMap().keySet()) {
            PlayerData playerData = this.getPlayerMap().get(p);

            if (playerData.getTeam() > 0 && !teamList.contains(playerData.getTeam())) {
                teamList.add(playerData.getTeam());
            }
        }

        return List.copyOf(teamList);
    }

    public List<UUID> getTeamMembers(int teamId) {
        List<UUID> teamMembers = new ArrayList<>();

        for (UUID p : this.getPlayerMap().keySet()) {

            if (this.getPlayerMap().get(p).getTeam() == teamId) {
                teamMembers.add(p);
            }

        }

        return List.copyOf(teamMembers);
    }

    public int getTeamKills(int teamId) {
        int teamKills = 0;

        for (UUID p : this.getPlayerMap().keySet()) {
            PlayerData playerData = this.getPlayerMap().get(p);
            if (playerData.getTeam() == teamId) {
                teamKills = teamKills + this.getPlayerMap().get(p).getKills();
            }
        }

        return teamKills;
    }

    public int getTeamDeaths(int teamId) {
        int teamDeaths = 0;

        for (UUID p : this.getPlayerMap().keySet()) {
            PlayerData playerData = this.getPlayerMap().get(p);
            if (playerData.getTeam() == teamId) {
                teamDeaths = teamDeaths + this.getPlayerMap().get(p).getDeaths();
            }
        }

        return teamDeaths;
    }

    public void finishGame() {
        this.plugin.unloadWorld(this.world);
        this.plugin.stopGame();
        this.plugin.startGame(new Endlobby(this.plugin, this.players));
    }

}
