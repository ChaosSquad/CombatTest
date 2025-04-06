package net.jandie1505.combattest.game.lobby;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.spigot.api.party.PartyManager;
import de.simonsator.partyandfriends.spigot.api.party.PlayerParty;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.executable.ManagedListener;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.constants.NamespacedKeys;
import net.jandie1505.combattest.game.base.GamePart;
import net.jandie1505.combattest.ItemStorage;
import net.jandie1505.combattest.game.base.commands.GamePlayersSubcommand;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.Spawnpoint;
import net.jandie1505.combattest.game.lobby.commands.CombatTestLobbyStartSubcommand;
import net.jandie1505.combattest.game.lobby.commands.LobbyPlayersValueSubcommand;
import net.jandie1505.combattest.game.lobby.commands.LobbyValueSubcommand;
import net.jandie1505.combattest.game.lobby.commands.LobbyVoteCommand;
import net.jandie1505.combattest.game.lobby.constants.LobbyItems;
import net.jandie1505.combattest.game.lobby.gui.LobbyVoteGUI;
import net.jandie1505.combattest.game.lobby.gui.LobbyTeamSelectionGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Lobby extends GamePart implements ManagedListener {
    private final CombatTest plugin;
    private final List<MapData> maps;
    private final Map<UUID, LobbyPlayerData> players;
    @NotNull private final LobbyVoteGUI voteMenu;
    @NotNull private final LobbyTeamSelectionGUI teamSelectionGUI;
    private final boolean lobbyBorderEnabled;
    private final int[] lobbyBorder;
    private final Location lobbySpawn;
    private boolean killswitch;
    private int time;
    private boolean forcestart;
    private MapData selectedMap;
    private World world;
    private boolean mapVoting;
    private boolean teamSelection;

    public Lobby(CombatTest plugin) {
        super(plugin);
        this.plugin = plugin;
        this.time = this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optInt("time", 90);
        this.players = Collections.synchronizedMap(new HashMap<>());
        this.voteMenu = new LobbyVoteGUI(this);
        this.teamSelectionGUI = new LobbyTeamSelectionGUI(this);
        this.killswitch = false;
        this.forcestart = false;
        this.maps = new ArrayList<>();
        this.selectedMap = null;
        this.world = this.plugin.getServer().getWorlds().getFirst();
        this.getDynamicSubcommands().put("force-start", SubcommandEntry.of(new CombatTestLobbyStartSubcommand(this)));
        this.getDynamicSubcommands().put("value", SubcommandEntry.of(new LobbyValueSubcommand(this)));
        ((GamePlayersSubcommand) this.getDynamicSubcommands().get("players").executor()).addSubcommand("value", SubcommandEntry.of(new LobbyPlayersValueSubcommand(this)));
        this.getDynamicSubcommands().put("votemap", SubcommandEntry.of(new LobbyVoteCommand(this.getPlugin())));
        this.lobbyBorderEnabled = this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optBoolean("enable", false);
        this.lobbyBorder = new int[]{
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x1", -10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y1", -10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z1", -10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("x2", 10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("y2", 10),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("border", new JSONObject()).optInt("z2", 10)
        };
        this.lobbySpawn = new Location(
                this.world,
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("x", 0),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("y", 0),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optInt("z", 0),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optFloat("yaw", 0.0F),
                this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optJSONObject("spawnpoint", new JSONObject()).optFloat("pitch", 0.0F)
        );
        this.mapVoting = this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optBoolean("mapVoting", false);
        this.teamSelection = this.plugin.getConfigManager().getConfig().optJSONObject("lobby", new JSONObject()).optBoolean("teamSelection", false);

        for (String world : List.copyOf(this.plugin.getMapConfig().getConfig().keySet())) {
            try {
                JSONObject mapConfig = this.plugin.getMapConfig().getConfig().getJSONObject(world);

                List<Spawnpoint> spawnpoints = new ArrayList<>();

                for (Object object : mapConfig.getJSONArray("spawnpoints")) {

                    if (!(object instanceof JSONObject)) {
                        continue;
                    }

                    try {
                        JSONObject spawnpoint = (JSONObject) object;
                        spawnpoints.add(new Spawnpoint(spawnpoint.getInt("x"), spawnpoint.getInt("y"), spawnpoint.getInt("z"), spawnpoint.getInt("yaw"), spawnpoint.getInt("pitch"), spawnpoint.getInt("team")));
                    } catch (JSONException e) {
                        this.plugin.getLogger().warning("Error while loading a spawnpoint in map config " + world + ". Please check your configuration.");
                    }
                }

                this.maps.add(new MapData(
                        world,
                        mapConfig.getString("name"),
                        spawnpoints,
                        mapConfig.getJSONObject("border").getBoolean("enable"),
                        new int[]{mapConfig.getJSONObject("border").getInt("x1"),mapConfig.getJSONObject("border").getInt("y1"),mapConfig.getJSONObject("border").getInt("z1"),mapConfig.getJSONObject("border").getInt("x2"),mapConfig.getJSONObject("border").getInt("y2"),mapConfig.getJSONObject("border").getInt("z2")},
                        mapConfig.getInt("spawnpointBlockedRadius"),
                        mapConfig.getInt("time"),
                        mapConfig.getBoolean("enforcepvp")
                ));
            } catch (JSONException e) {
                this.plugin.getLogger().warning("Error while loading map config " + world + ". Please check your configuration.");
            }
        }

        this.registerListener(this);
        this.getTaskScheduler().runTaskLater(this.plugin.getListenerManager()::manageListeners, 1);

        this.getTaskScheduler().scheduleRepeatingTask(this::timeTask, 1, 20, "time");
        this.getTaskScheduler().scheduleRepeatingTask(this::autoSelectMapTask, 1, 20, "auto_select_map");
        this.getTaskScheduler().scheduleRepeatingTask(this::task, 1, 20); // TODO: Split into multiple tasks
    }

    @Override
    public boolean shouldExecute() {
        return super.shouldExecute() && !this.killswitch;
    }

    /**
     * Counts down start timer and starts map.
     */
    private void timeTask() {

        if (time > 0) {

            if (players.size() >= 2) {
                time--;
            } else if(time < 60) {
                time++;
            }
        } else {
            this.start();
        }

    }

    /**
     * Automatically selects a map when the time is low.
     */
    private void autoSelectMapTask() {

        if (this.selectedMap == null && this.time <= 10) {
            this.autoSelectMap();
            this.displayMap();
        }

    }

    public void task() {

        // KILLSWITCH

        if (killswitch) return;



        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            // Cleanup

            if (player == null) {
                this.players.remove(playerId);
                continue;
            }

            // Get Player Data

            LobbyPlayerData playerData = this.players.get(playerId);

            // Force adventure mode

            if ((player.getGameMode() != GameMode.ADVENTURE) && !this.plugin.isPlayerBypassing(playerId)) {
                player.setGameMode(GameMode.ADVENTURE);
            }

            // Health

            if (player.getHealth() < 20) {
                player.setHealth(20);
            }

            // Saturation

            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(20);
            }

            // Actionbar

            if (this.players.size() >= 2) {

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aStarting in " + this.time + "s §8§l|§r§a Players: " + this.players.size() + " / 2"));

            } else {

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cNot enough players (" + this.players.size() + " / 2)"));

            }

            // Messages

            if ((this.time <= 5 || (this.time % 10 == 0)) && players.size() >= 2) {
                player.sendMessage("§7The game starts in " + this.time + " seconds");
            }

            // Single Server stuff

            if (this.plugin.isSingleServer()) {

                // Lobby location

                if (!this.plugin.isPlayerBypassing(playerId) && this.lobbyBorderEnabled) {

                    Location location = player.getLocation();

                    if (!(location.getBlockX() >= this.lobbyBorder[0] && location.getBlockY() >= this.lobbyBorder[1] && location.getBlockZ() >= this.lobbyBorder[2] && location.getBlockX() <= this.lobbyBorder[3] && location.getBlockY() <= this.lobbyBorder[4] && location.getBlockZ() <= this.lobbyBorder[5])) {
                        player.teleport(this.lobbySpawn);
                    }

                }

                // Scoreboard

                String mapName = "---";

                if (this.selectedMap != null) {
                    mapName = this.selectedMap.getName();
                }

                Scoreboard scoreboard = this.plugin.getServer().getScoreboardManager().getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective("lobby", Criteria.DUMMY, "");

                objective.setDisplayName("§6§lCOMBAT TEST");

                objective.getScore("§§§§").setScore(7);

                if (this.players.size() >= 2) {

                    objective.getScore("§bStarting in " + this.time).setScore(6);
                    objective.getScore("§§§").setScore(5);
                    objective.getScore("§7Players: §a" + this.players.size() + " / 2").setScore(4);

                } else {

                    objective.getScore("§cNot enough players").setScore(6);
                    objective.getScore("§§§").setScore(5);
                    objective.getScore("§7Players: §c" + this.players.size() + " / 2").setScore(4);

                }

                objective.getScore("§§").setScore(3);
                objective.getScore("§7Map: §a" + mapName).setScore(2);

                objective.getScore("§").setScore(0);

                if (playerData.getTeam() > 0) {
                    objective.getScore("§7Team: §a" + playerData.getTeam()).setScore(1);
                } else {
                    objective.getScore("§7Team: §a" + "---").setScore(1);
                }

                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                player.setScoreboard(scoreboard);
            }

        }

        // SINGLE SERVER MODE

        if (this.plugin.isSingleServer()) {

            for (Player player : this.plugin.getServer().getOnlinePlayers()) {

                if (!this.players.containsKey(player.getUniqueId()) && !(this.plugin.isPlayerBypassing(player.getUniqueId()))) {
                    this.addPlayer(player);
                }

            }

        }

        // FORCE START

        if (this.forcestart) {
            boolean success = this.start();
            if (!success) this.killswitch = true;
        }
    }

    @Override
    public boolean addPlayer(Player player) {
        if (player == null) {
            return false;
        }

        if (this.players.containsKey(player.getUniqueId())) {
            return false;
        }

        // Add player
        this.players.put(player.getUniqueId(), new LobbyPlayerData());

        // Inventory
        player.getInventory().clear();
        player.getInventory().setItem(2, LobbyItems.mapVotingMenuItem());
        player.getInventory().setItem(6, LobbyItems.teamSelectionMenuItem(player));

        return true;
    }

    @Override
    public boolean removePlayer(UUID playerId) {
        return this.players.remove(playerId) != null;
    }

    public Map<UUID, LobbyPlayerData> getPlayerMap() {
        return Map.copyOf(this.players);
    }

    @Override
    public Set<UUID> getRegisteredPlayers() {
        return Set.copyOf(this.getPlayerMap().keySet());
    }

    private List<MapData> getHighestVotedMaps() {

        // Get map votes

        Map<MapData, Integer> mapVotes = new HashMap<>();

        for (UUID playerId : this.getPlayerMap().keySet()) {
            LobbyPlayerData playerData = this.players.get(playerId);

            if (playerData.getVote() == null) {
                continue;
            }

            if (mapVotes.containsKey(playerData.getVote())) {
                mapVotes.put(playerData.getVote(), mapVotes.get(playerData.getVote()) + 1);
            } else {
                mapVotes.put(playerData.getVote(), 1);
            }

        }

        // Get list of maps with the highest vote count

        List<MapData> highestVotedMaps = new ArrayList<>();
        int maxVotes = Integer.MIN_VALUE;

        for (Map.Entry<MapData, Integer> entry : mapVotes.entrySet()) {
            int votes = entry.getValue();
            if (votes > maxVotes) {
                maxVotes = votes;
                highestVotedMaps.clear();
                highestVotedMaps.add(entry.getKey());
            } else if (votes == maxVotes) {
                highestVotedMaps.add(entry.getKey());
            }
        }

        return highestVotedMaps;

    }

    private void autoSelectMap() {

        MapData selectedMap = null;

        if (this.mapVoting) {

            List<MapData> highestVotedMaps = this.getHighestVotedMaps();

            if (!highestVotedMaps.isEmpty()) {

                selectedMap = highestVotedMaps.get(new Random().nextInt(highestVotedMaps.size()));

            }

        }

        if (selectedMap == null) {

            if (!this.maps.isEmpty()) {

                selectedMap = this.maps.get(new Random().nextInt(this.maps.size()));

            }

        }

        this.selectedMap = selectedMap;

    }

    private void displayMap() {

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                continue;
            }

            if (this.selectedMap == null) {
                return;
            }

            player.sendMessage("§bThe map has been set to " + this.selectedMap.getName());

        }

    }

    public void randomTeams() {

        // do random teams

        for (UUID playerId : this.getPlayerMap().keySet()) {

            LobbyPlayerData playerData = this.players.get(playerId);

            if (playerData == null || playerData.getTeam() == 1 || playerData.getTeam() == 2) {
                continue;
            }

            if (this.getTeamMembers(1).size() == this.getTeamMembers(2).size()) {

                playerData.setTeam(new Random().nextInt(3));

            } else if (this.getTeamMembers(1).size() < this.getTeamMembers(2).size()) {

                playerData.setTeam(1);

            } else if (this.getTeamMembers(1).size() > this.getTeamMembers(2).size()) {

                playerData.setTeam(2);

            }

        }

        // prevent jandie1505 and TheRainbowFox of being in the same team

        for (int teamId : this.getTeams()) {

            UUID jandie1505 = UUID.fromString("55eda3b4-f5bb-4155-8b9b-a16f09c57aa3");
            UUID theRainbowFox = UUID.fromString("9be4b569-58f3-4580-bb81-2242f3f5ddcf");

            List<UUID> teamMembers = this.getTeamMembers(teamId);

            if (teamMembers.contains(jandie1505) && teamMembers.contains(theRainbowFox)) {
                LobbyPlayerData playerData = this.getPlayerMap().get(theRainbowFox);

                if (playerData == null) {
                    continue;
                }

                for (int replacementTeamId : this.getTeams()) {
                    List<UUID> replacementTeamMembers = this.getTeamMembers(replacementTeamId);

                    if (replacementTeamMembers.contains(jandie1505)) {
                        continue;
                    }

                    playerData.setTeam(replacementTeamId);
                    break;

                }

            }

        }

    }

    private void setPartyTeams() {

        if (this.plugin.getConfigManager().getConfig().optJSONObject("integrations", new JSONObject()).optBoolean("partyandfriends", false)) {

            try {
                Class.forName("de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager");
                Class.forName("de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayer");
                Class.forName("de.simonsator.partyandfriends.spigot.api.party.PartyManager");
                Class.forName("de.simonsator.partyandfriends.spigot.api.party.PlayerParty");

                List<PlayerParty> parties = new ArrayList<>();

                for (UUID playerId : this.getPlayers()) {
                    Player player = this.plugin.getServer().getPlayer(playerId);

                    if (player == null) {
                        continue;
                    }

                    PAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(playerId);

                    if (pafPlayer == null) {
                        continue;
                    }

                    PlayerParty playerParty = PartyManager.getInstance().getParty(pafPlayer);

                    if (playerParty == null) {
                        continue;
                    }

                    if (!parties.contains(playerParty)) {
                        parties.add(playerParty);
                    }

                }

                for (PlayerParty party : List.copyOf(parties)) {

                    int teamId = -1;

                    for (int i = 1; i <= 100; i++) {

                        if (!this.getTeamMembers(teamId).isEmpty()) {
                            continue;
                        }

                        teamId = i;

                        break;
                    }

                    if (teamId <= 0) {
                        continue;
                    }

                    for (PAFPlayer pafPlayer : List.copyOf(party.getAllPlayers())) {
                        Player player = this.plugin.getServer().getPlayer(pafPlayer.getUniqueId());
                        LobbyPlayerData playerData = this.players.get(pafPlayer.getUniqueId());

                        if (player == null || playerData == null) {
                            continue;
                        }

                        if (this.plugin.isPlayerBypassing(pafPlayer.getUniqueId())) {
                            continue;
                        }

                        playerData.setTeam(teamId);
                        player.sendMessage("§bYou have been placed on team " + teamId + " with your party, regardless of your team selection");

                    }

                }

            } catch (ClassNotFoundException e) {
                // ignored
            }

        }

    }


    private boolean start() {

        if (this.selectedMap == null) {
            this.autoSelectMap();
            this.displayMap();
        }

        if (selectedMap == null) {
            this.plugin.getLogger().warning("Game stopped because no world was selected");
            return false;
        }

        World world = this.plugin.loadWorld(selectedMap.getWorld());

        if (world == null || !this.plugin.getServer().getWorlds().contains(world)) {
            this.plugin.getLogger().warning("Game stopped because world does not exist");
            return false;
        }

        if (world == this.plugin.getServer().getWorlds().get(0)) {
            this.plugin.getLogger().warning("Game stopped because selected world is default world on server");
            return false;
        }

        world.setAutoSave(false);

        this.setPartyTeams();

        this.plugin.stopGame();
        return this.plugin.startGame(new Game(
                this.plugin,
                selectedMap.getTime(),
                world,
                players,
                selectedMap.getSpawnpoints(),
                selectedMap.isEnableBorder(),
                selectedMap.getBorder(),
                selectedMap.isEnforcePvp()
        ));
    }

    public void forcestart() {
        this.forcestart = true;
    }

    // ----- EVENTS -----

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getHolder() != event.getWhoClicked()) return;
        if (this.getPlugin().isPlayerBypassing(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getHolder() != event.getWhoClicked()) return;
        if (this.getPlugin().isPlayerBypassing(player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (this.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        // Cancel all interactions except for bypassing players
        if (!this.getPlugin().isPlayerBypassing(event.getPlayer())) {
            event.setCancelled(true);
        }

        // Only right-clicks from now on
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_VOTING_MENU, PersistentDataType.BOOLEAN, false)) {
            event.setCancelled(true); // Cancel this interaction again in case a bypassing player wants to use the item
            event.getPlayer().openInventory(this.getVoteMenu().getInventory(event.getPlayer()));
            event.getPlayer().playSound(event.getPlayer().getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        } else if (meta.getPersistentDataContainer().getOrDefault(NamespacedKeys.ITEM_TEAM_SELECTION_MENU, PersistentDataType.BOOLEAN, false)) {
            event.setCancelled(true);
            event.getPlayer().openInventory(this.getTeamSelectionGUI().getInventory(event.getPlayer()));
            event.getPlayer().playSound(event.getPlayer().getLocation().clone(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        }

    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (this.getPlugin().isPlayerBypassing(event.getPlayer())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        // Allow bypassing players to take damage
        if (event.getEntity() instanceof Player player && this.getPlugin().isPlayerBypassing(player)) {
            return;
        }

        // Allow bypassing players to deal damage
        if (event instanceof EntityDamageByEntityEvent byEntityEvent &&
                byEntityEvent.getDamager() instanceof Player damager &&
                this.getPlugin().isPlayerBypassing(damager.getUniqueId())
        ) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("has joined", NamedTextColor.GRAY))
        );

        event.getPlayer().teleport(this.getLobbySpawn().clone());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(Component.empty()
                .append(event.getPlayer().displayName())
                .appendSpace()
                .append(Component.text("has left", NamedTextColor.GRAY))
        );
    }

    // ----- OTHER -----

    public MapData getSelectedMap() {
        return this.selectedMap;
    }

    public void selectMap(MapData mapData) {
        this.selectedMap = mapData;
        this.displayMap();
    }

    public List<MapData> getMaps() {
        return List.copyOf(this.maps);
    }

    public @NotNull LobbyVoteGUI getVoteMenu() {
        return this.voteMenu;
    }

    public @NotNull LobbyTeamSelectionGUI getTeamSelectionGUI() {
        return this.teamSelectionGUI;
    }

    public boolean isMapVoting() {
        return this.mapVoting;
    }

    public void setMapVoting(boolean mapVoting) {
        this.mapVoting = mapVoting;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isTeamSelection() {
        return this.teamSelection;
    }

    public void setTeamSelection(boolean teamSelection) {
        this.teamSelection = teamSelection;
    }

    public Location getLobbySpawn() {
        return this.lobbySpawn.clone();
    }

    public List<Integer> getTeams() {
        List<Integer> teamList = new ArrayList<>();

        for (UUID p : this.getPlayerMap().keySet()) {
            LobbyPlayerData playerData = this.getPlayerMap().get(p);

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

    @Override
    public boolean toBeRemoved() {
        return false;
    }
}
