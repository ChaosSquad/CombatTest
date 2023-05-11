package net.jandie1505.combattest.game;

import net.jandie1505.combattest.CombatTest;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.*;

public class Game implements GamePart {
    private final CombatTest plugin;
    private boolean killswitch;
    private int timeStep;
    private int time;
    World world;
    private Map<UUID, PlayerData> players;
    private List<Spawnpoint> spawnpoints;
    private boolean enableBorder;
    private int[] border;
    private Map<UUID, PlayerMenu> playerMenus;
    private boolean enforcePvp;

    public Game(CombatTest plugin, int time, World world, List<UUID> players, List<Spawnpoint> spawnpoints, boolean enableBorder, int[] border, boolean enforcePvp) {
        this.plugin = plugin;
        this.killswitch = false;
        this.timeStep = 0;
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
    }

    @Override
    public int tick() {

        // KILL SWITCH

        if (this.killswitch) {
            return GameStatus.ABORT;
        }

        // TIME MANAGEMENT

        if (this.timeStep >= 1) {

            if (this.time >= 0) {
                this.time--;
            } else {
                return GameStatus.NEXT;
            }

            this.timeStep = 0;

        } else {
            this.timeStep++;
        }

        // TRIDENT ENTITIES

        List<Player> tridentList = new ArrayList<>();
        for (Entity entity : world.getEntities()) {

            if (entity instanceof Trident) {
                Trident trident = (Trident) entity;

                if (trident.getTicksLived() > 600) {
                    trident.remove();
                    continue;
                }

                if (trident.getItem().getItemMeta() == null) {
                    trident.remove();
                    continue;
                }

                if (!trident.getItem().getItemMeta().hasEnchant(Enchantment.LOYALTY)) {
                    trident.remove();
                    continue;
                }

                if (trident.getShooter() == null) {
                    trident.remove();
                    continue;
                }

                if (!(trident.getShooter() instanceof Player)) {
                    trident.remove();
                    continue;
                }

                if (this.players.containsKey(((Player) trident.getShooter()).getUniqueId())) {
                    tridentList.add((Player) trident.getShooter());
                } else {
                    trident.remove();
                    continue;
                }
            } else if (entity instanceof Item) {

                if (((Item) entity).getItemStack().getType() == Material.TRIDENT) {
                    entity.remove();
                    continue;
                }

            }

        }

        // PLAYER MANAGEMENT

        for (UUID playerId : this.getPlayerMap().keySet()) {
            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null) {
                this.removePlayer(playerId);
                break;
            }

            PlayerData playerData = this.players.get(playerId);

            // Respawn

            if (!playerData.isAlive()) {

                if ((player.getGameMode() != GameMode.SPECTATOR) && !(this.plugin.isPlayerBypassing(playerId))) {
                    player.setGameMode(GameMode.SPECTATOR);
                }

                if (this.timeStep >= 1) {
                    if (playerData.getRespawntimer() > 0) {
                        player.sendTitle("§cDEAD", "§7Respawn in " + playerData.getRespawntimer() + " seconds", 0, 20, 0);
                        player.sendMessage("§7You will respawn in " + playerData.getRespawntimer() + " seconds");
                        playerData.setRespawntimer(playerData.getRespawntimer() - 1);
                    } else {
                        this.respawnPlayer(player);
                    }
                }

            } else {

                if ((player.getGameMode() != GameMode.ADVENTURE) && !(this.plugin.isPlayerBypassing(playerId))) {
                    player.setGameMode(GameMode.ADVENTURE);
                }

                if (playerData.getRespawntimer() < 5) {
                    playerData.setRespawntimer(5);
                }

            }

            // Player Menu Object

            if (!this.playerMenus.containsKey(playerId)) {

                this.playerMenus.put(playerId, new PlayerMenu(this, playerId));

            }

            // Player Menu Button

            if (!ItemStorage.getPlayerMenuButton().isSimilar(player.getInventory().getItem(8))) {

                player.getInventory().remove(ItemStorage.getPlayerMenuButton());
                player.getInventory().setItem(8, ItemStorage.getPlayerMenuButton());

            }

            // Check scores

            if (!((playerData.getMeleeEquipment() == 0) || (playerData.getMeleeEquipment() >= 100 && playerData.getMeleeEquipment() <= 102) || (playerData.getMeleeEquipment() >= 200 && playerData.getMeleeEquipment() <= 202) || (playerData.getMeleeEquipment() >= 300 && playerData.getMeleeEquipment() <= 302) || (playerData.getMeleeEquipment() >= 1100 && playerData.getMeleeEquipment() <= 1103) || (playerData.getMeleeEquipment() >= 1200 && playerData.getMeleeEquipment() <= 1203) || (playerData.getMeleeEquipment() >= 1300 && playerData.getMeleeEquipment() <= 1303) || (playerData.getMeleeEquipment() >= 1400 && playerData.getMeleeEquipment() <= 1403) || (playerData.getMeleeEquipment() >= 1500 && playerData.getMeleeEquipment() <= 1503) || (playerData.getMeleeEquipment() >= 1600 && playerData.getMeleeEquipment() <= 1603))) {
                playerData.setMeleeEquipment(0);
            }

            if (!((playerData.getRangedEquipment() == 0) || (playerData.getRangedEquipment() >= 100 && playerData.getRangedEquipment() <= 101) || (playerData.getRangedEquipment() >= 200 && playerData.getRangedEquipment() <= 201) || (playerData.getRangedEquipment() >= 300 && playerData.getRangedEquipment() <= 301) || (playerData.getRangedEquipment() >= 1100 && playerData.getRangedEquipment() <= 1102) || (playerData.getRangedEquipment() >= 1200 && playerData.getRangedEquipment() <= 1202) || (playerData.getRangedEquipment() >= 1300 && playerData.getRangedEquipment() <= 1302) || (playerData.getRangedEquipment() >= 1400 && playerData.getRangedEquipment() <= 1402) || (playerData.getRangedEquipment() >= 1500 && playerData.getRangedEquipment() <= 1502) || (playerData.getRangedEquipment() >= 1600 && playerData.getRangedEquipment() <= 1602))) {
                playerData.setRangedEquipment(0);
            }

            if (!((playerData.getArmorEquipment() == 0) || (playerData.getArmorEquipment() >= 100 && playerData.getArmorEquipment() <= 102) || (playerData.getArmorEquipment() >= 1100 && playerData.getArmorEquipment() <= 1103) || (playerData.getArmorEquipment() >= 1200 && playerData.getArmorEquipment() <= 1203))) {
                playerData.setArmorEquipment(0);
            }

            // Player inventory handling (give and remove equipment)

            boolean meleeItemMissing = true;
            boolean rangedItemMissing = true;
            boolean armorItemMissing = true;

            for (ItemStack item : Arrays.copyOf(player.getInventory().getContents(), player.getInventory().getContents().length)) {

                if (item != null) {

                    // Melee item
                    Integer meleeId = ItemStorage.getMeleeReverse(item);
                    if (meleeId != null) {
                        if (meleeId == playerData.getMeleeEquipment()) {
                            meleeItemMissing = false;
                        } else {
                            player.getInventory().remove(item);
                        }
                        continue;
                    }

                    // Ranged item
                    Integer rangedId = ItemStorage.getRangedReverse(item);
                    if (rangedId != null) {
                        if (rangedId == playerData.getRangedEquipment()) {
                            rangedItemMissing = false;
                        } else {
                            player.getInventory().remove(item);
                        }
                        continue;
                    }

                    // remove firework rocket
                    if (!(playerData.getRangedEquipment() == 1301 || playerData.getRangedEquipment() == 1302) && ItemStorage.getIdPrefix(item).equals(ItemStorage.EQUIPMENT_RANGED) && ItemStorage.getId(item) == 9000) {
                        player.getInventory().remove(item);
                        player.getInventory().setItem(40, new ItemStack(Material.AIR));
                        continue;
                    }

                    // Armor item
                    Integer armorId = ItemStorage.getArmorReverse(item);
                    if (armorId != null) {
                        if (armorId == playerData.getArmorEquipment()) {
                            armorItemMissing = false;
                        } else {
                            player.getInventory().remove(item);
                            player.getInventory().setItem(39, new ItemStack(Material.AIR));
                            player.getInventory().setItem(38, new ItemStack(Material.AIR));
                            player.getInventory().setItem(37, new ItemStack(Material.AIR));
                            player.getInventory().setItem(36, new ItemStack(Material.AIR));
                        }
                        continue;
                    }

                }

            }

            // Give melee equipment

            ItemStack meleeItem = ItemStorage.getMelee(playerData.getMeleeEquipment());
            if (meleeItem != null && meleeItemMissing && !(player.getItemOnCursor() != null && ItemStorage.getIdPrefix(player.getItemOnCursor()).equals(ItemStorage.EQUIPMENT_MELEE) && ItemStorage.getId(player.getItemOnCursor()) == playerData.getMeleeEquipment())) {

                if ((playerData.getMeleeEquipment() >= 300 && playerData.getMeleeEquipment() <= 399) || (playerData.getMeleeEquipment() >= 1500 && playerData.getMeleeEquipment() <= 1699)) {

                    playerData.setPotionTimer(playerData.getPotionTimer() + 0.5);

                    switch (playerData.getMeleeEquipment()) {
                        case 300:
                        case 1600:
                            if (playerData.getPotionTimer() >= 6) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            }
                            break;
                        case 301:
                        case 1601:
                            if (playerData.getPotionTimer() >= 5) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            }
                            break;
                        case 302:
                        case 1602:
                            if (playerData.getPotionTimer() >= 4) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            }
                            break;
                        case 1500:
                        case 1603:
                            if (playerData.getPotionTimer() >= 3) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            }
                            break;
                        case 1501:
                            if (playerData.getPotionTimer() >= 2) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            }
                            break;
                        case 1502:
                            if (playerData.getPotionTimer() >= 1) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            }
                            break;
                        case 1503:
                            if (playerData.getPotionTimer() >= 0.5) {
                                player.getInventory().addItem(meleeItem);
                                playerData.setPotionTimer(0);
                            }
                            break;
                        default:
                            playerData.setPotionTimer(0);
                            break;
                    }

                } else {
                    player.getInventory().addItem(meleeItem);
                }

            }

            // Give ranged equipment

            ItemStack rangedItem = ItemStorage.getRanged(playerData.getRangedEquipment());
            if (rangedItem != null && rangedItemMissing && !(player.getItemOnCursor() != null && ItemStorage.getIdPrefix(player.getItemOnCursor()).equals(ItemStorage.EQUIPMENT_RANGED) && ItemStorage.getId(player.getItemOnCursor()) == playerData.getRangedEquipment())) {

                if ((playerData.getRangedEquipment() >= 300 && playerData.getRangedEquipment() <= 399) || (playerData.getRangedEquipment() >= 1500 && playerData.getRangedEquipment() <= 1699)) {
                    if (!tridentList.contains(player)) {
                        player.getInventory().addItem(rangedItem);
                    }
                } else {
                    player.getInventory().addItem(rangedItem);
                }

            }

            if ((playerData.getRangedEquipment() == 1301 || playerData.getRangedEquipment() == 1302) && (player.getInventory().getItem(40) == null || !player.getInventory().getItem(40).isSimilar(ItemStorage.getRocketLauncherAmmo()))) {
                player.getInventory().setItem(40, ItemStorage.getRocketLauncherAmmo());
            }

            // Give armor equipment

            ItemStack armorItem = ItemStorage.getArmor(playerData.getArmorEquipment());
            if (armorItem != null && armorItemMissing) {
                player.getInventory().setItem(39, armorItem);
            }

            if (player.getInventory().getItem(38) == null || !(ItemStorage.getIdPrefix(player.getInventory().getItem(38)).equals(ItemStorage.EQUIPMENT_ARMOR) && ItemStorage.getId(player.getInventory().getItem(38)) == 10)) {
                player.getInventory().setItem(38, ItemStorage.getArmorChestplate());
            }

            if (player.getInventory().getItem(37) == null || !(ItemStorage.getIdPrefix(player.getInventory().getItem(37)).equals(ItemStorage.EQUIPMENT_ARMOR) && ItemStorage.getId(player.getInventory().getItem(37)) == 10)) {
                player.getInventory().setItem(37, ItemStorage.getArmorLeggings());
            }

            if (player.getInventory().getItem(36) == null || !(ItemStorage.getIdPrefix(player.getInventory().getItem(36)).equals(ItemStorage.EQUIPMENT_ARMOR) && ItemStorage.getId(player.getInventory().getItem(36)) == 10)) {
                player.getInventory().setItem(36, ItemStorage.getArmorBoots());
            }

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

            // No PvP Timer

            if (this.enforcePvp) {

                if (this.timeStep >= 1) {

                    if (playerData.getNoPvpTimer() >= 45) {
                        player.sendMessage("§bYour position was revealed because you were not in combat for too long");
                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0, true, true));
                        playerData.setNoPvpTimer(0);
                    } else {
                        playerData.setNoPvpTimer(playerData.getNoPvpTimer() + 1);
                    }

                }

            }

            // Idle points

            if (this.timeStep >= 1) {
                playerData.setPoints(playerData.getPoints() + 2);
            }

            // Actionbar

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a" + playerData.getKills() + " kills §8§l|§r§c " + playerData.getDeaths() + " deaths §8§l|§r§6 Points: " + playerData.getPoints() + " §8§l|§r§6 " + this.time + "s"));

            // Scoreboard

            if (this.plugin.isSingleServer()) {

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

                sidebarObjective.getScore("§§§§").setScore(11);
                sidebarObjective.getScore("Kills: §a" + playerData.getKills()).setScore(10);
                sidebarObjective.getScore("Deaths: §a" + playerData.getDeaths()).setScore(9);
                sidebarObjective.getScore("K/D: §a" + PlayerData.getKD(playerData.getKills(), playerData.getDeaths())).setScore(8);
                sidebarObjective.getScore("Points: §a" + playerData.getPoints()).setScore(7);

                if (playerData.getTeam() > 0) {

                    sidebarObjective.getScore("§§§").setScore(6);
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

        // HANDLE MENUS

        for (UUID playerId : this.getPlayerMenus().keySet()) {

            Player player = this.plugin.getServer().getPlayer(playerId);

            if (player == null || !this.players.containsKey(playerId)) {

                this.playerMenus.remove(playerId);

            }

        }

        // WEATHER

        if ((this.time % 100) == 0) {

            if (time > 0) {
                Random random = new Random();

                if (random.nextInt(2) == 1) {

                    switch (random.nextInt(6)) {
                        case 0:
                        case 1:
                        case 2:
                            CombatTest.setClearWeather(this.world);
                            break;
                        case 3:
                        case 4:
                            CombatTest.setRainingWeather(this.world);
                            break;
                        case 5:
                            CombatTest.setThunderingWeather(this.world);
                            break;
                        default:
                            break;
                    }

                }
            } else {
                CombatTest.setClearWeather(this.world);
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

                if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
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

        PlayerData playerData = this.getPlayerMap().get(player.getUniqueId());

        Spawnpoint spawnpoint = this.getRandomSpawnpoint(playerData.getTeam());

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

    @Override
    public GamePart getNextStatus() {
        return new Endlobby(this.plugin, this.players);
    }
}
