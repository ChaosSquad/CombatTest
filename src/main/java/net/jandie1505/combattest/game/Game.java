package net.jandie1505.combattest.game;

import net.jandie1505.combattest.CombatTest;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class Game implements GamePart {
    private final CombatTest plugin;
    private boolean killswitch;
    private int time;
    World world;
    private Map<UUID, PlayerData> players;
    private List<Spawnpoint> spawnpoints;
    private boolean enableBorder;
    private int[] border;
    private Map<UUID, PlayerMenu> playerMenus;

    public Game(CombatTest plugin, int time, World world, List<UUID> players, List<Spawnpoint> spawnpoints, boolean enableBorder, int[] border) {
        this.plugin = plugin;
        this.killswitch = false;
        this.time = time;
        this.world = world;

        if (world == null) {
            this.killswitch = true;
        }

        this.players = Collections.synchronizedMap(new HashMap<>());

        for (UUID playerId : players) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                continue;
            }

            this.players.put(playerId, new PlayerData(playerId));
        }

        this.spawnpoints = Collections.synchronizedList(new ArrayList<>());
        this.spawnpoints.addAll(spawnpoints);

        this.enableBorder = enableBorder;
        this.border = border;
        if (enableBorder && border.length < 6) {
            this.killswitch = true;
        }

        this.playerMenus = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public int tick() {

        // KILL SWITCH

        if (this.killswitch) {
            return GameStatus.ABORT;
        }

        // TIME MANAGEMENT

        if (this.time >= 0) {
            this.time--;
        } else {
            return GameStatus.NEXT;
        }

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.removePlayer(playerId);
                break;
            }

            PlayerData playerData = this.players.get(playerId);

            if (!playerData.isAlive()) {

                if ((player.getGameMode() != GameMode.SPECTATOR) && !(this.plugin.isPlayerBypassing(playerId))) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                if (playerData.getRespawntimer() > 0) {
                    player.sendTitle("§cDEAD", "§7Respawn in " + playerData.getRespawntimer() + " seconds", 0, 20, 0);
                    player.sendMessage("§7You will respawn in " + playerData.getRespawntimer() + " seconds");
                    playerData.setRespawntimer(playerData.getRespawntimer() - 1);
                } else {
                    this.respawnPlayer(player);
                }

            } else {

                if ((player.getGameMode() != GameMode.ADVENTURE) && !(this.plugin.isPlayerBypassing(playerId))) {
                    player.setGameMode(GameMode.ADVENTURE);
                }

                if (playerData.getRespawntimer() < 5) {
                    playerData.setRespawntimer(5);
                }

            }

            if (!this.playerMenus.containsKey(playerId)) {

                this.playerMenus.put(playerId, new PlayerMenu(this, playerId));

            }

            if (!ItemStorage.getPlayerMenuButton().isSimilar(player.getInventory().getItem(8))) {

                player.getInventory().remove(ItemStorage.getPlayerMenuButton());
                player.getInventory().setItem(8, ItemStorage.getPlayerMenuButton());

            }

            int meleeScore = playerData.getMeleeEquipment();

            if (!((meleeScore == 0) || (meleeScore >= 100 && meleeScore <= 102) || (meleeScore >= 200 && meleeScore <= 202) || (meleeScore >= 1100 && meleeScore <= 1103) || (meleeScore >= 1200 && meleeScore <= 1203) || (meleeScore >= 1300 && meleeScore <= 1303) || (meleeScore >= 1400 && meleeScore <= 1403) || (meleeScore >= 1500 && meleeScore <= 1503) || (meleeScore >= 1600 && meleeScore <= 1603))) {
                playerData.setMeleeEquipment(0);
            }

        }

        // HANDLE MENUS

        for (UUID playerId : this.getPlayerMenus().keySet()) {

            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null || !this.players.containsKey(playerId)) {

                this.playerMenus.remove(playerId);

            }

        }

        // SINGLE SERVER MODE

        if (this.plugin.isSingleServer()) {

            for (Player player : List.copyOf(this.plugin.getServer().getOnlinePlayers())) {

                if (player == null) {
                    continue;
                }

                if (this.players.containsKey(player.getUniqueId())) {
                    continue;
                }

                if (player.getGameMode() != GameMode.SPECTATOR && !this.plugin.isPlayerBypassing(player.getUniqueId())) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

            }

        }

        return GameStatus.NORMAL;
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

        Spawnpoint spawnpoint = this.getRandomSpawnpoint(0);

        if (spawnpoint == null) {
            return false;
        }

        player.teleport(spawnpoint.buildLocation(world));
        this.getPlayerMap().get(player.getUniqueId()).setAlive(true);

        return true;
    }

    private Spawnpoint getRandomSpawnpoint(int teamMode) {
        int attempt = 0;

        while (attempt < 5) {

            try {

                Random random = new Random();
                Spawnpoint spawnpoint = this.spawnpoints.get(random.nextInt(this.spawnpoints.size()));

                if (attempt < 4) {
                    if (teamMode == spawnpoint.getTeamMode() || teamMode == -1) {
                        Collection<Entity> nearbyEntities = world.getNearbyEntities(spawnpoint.buildLocation(world), this.plugin.getConfigManager().getConfig().getInt("spawnpointBlockedRadius"), 2, this.plugin.getConfigManager().getConfig().getInt("spawnpointBlockedRadius"));

                        if (nearbyEntities.isEmpty()) {
                            return spawnpoint;
                        }
                    }
                } else {
                    return spawnpoint;
                }

            } catch (Exception ignored) {
                // ignored
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

    public int getTime() {
        return this.time;
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

    @Override
    public GamePart getNextStatus() {
        return new Endlobby(this.plugin);
    }
}
