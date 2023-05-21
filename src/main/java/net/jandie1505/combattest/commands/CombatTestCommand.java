package net.jandie1505.combattest.commands;

import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.GameStatus;
import net.jandie1505.combattest.game.*;
import net.jandie1505.combattest.lobby.Lobby;
import net.jandie1505.combattest.lobby.MapData;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CombatTestCommand implements CommandExecutor, TabCompleter {
    private final CombatTest plugin;

    public CombatTestCommand(CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (args.length < 1) {

            if (this.hasPermissionAdmin(sender)) {
                sender.sendMessage("§7Usage: /combattest stop/start/status/addplayer/removeplayer/getplayers/bypass/settime/getpoints/setpoints/getmelee/setmelee/getranged/setranged/getarmor/setarmor/getteam/setteam/isautostart/setautostart/getmaxtime/setmaxtime/reload/menu/points/leave/stats");
            } else {
                sender.sendMessage("§7Usage: /combattest menu/points/leave/stats");
            }

            return true;
        }

        switch (args[0]) {
            case "stop":
                this.stopSubcommand(sender);
                break;
            case "start":
                this.startSubcommand(sender);
                break;
            case "status":
                this.statusSubcommand(sender);
                break;
            case "addplayer":
                this.addPlayerSubcommand(sender, args);
                break;
            case "removeplayer":
                this.removePlayerSubcommand(sender, args);
                break;
            case "getplayers":
                this.getPlayersSubcommand(sender, args);
                break;
            case "bypass":
                this.bypassSubcommand(sender, args);
                break;
            case "settime":
                this.setTimeSubcommand(sender, args);
                break;
            case "getpoints":
                this.getScoreSubcommand(sender, args, 0);
                break;
            case "setpoints":
                this.setScoreSubcommand(sender, args, 0);
                break;
            case "getmelee":
                this.getScoreSubcommand(sender, args, 1);
                break;
            case "setmelee":
                this.setScoreSubcommand(sender, args, 1);
                break;
            case "getranged":
                this.getScoreSubcommand(sender, args, 2);
                break;
            case "setranged":
                this.setScoreSubcommand(sender, args, 2);
                break;
            case "getarmor":
                this.getScoreSubcommand(sender, args, 3);
                break;
            case "setarmor":
                this.setScoreSubcommand(sender, args, 3);
                break;
            case "getteam":
                this.getScoreSubcommand(sender, args, 4);
                break;
            case "setteam":
                this.setScoreSubcommand(sender, args, 4);
                break;
            case "isautostart":
                this.isAutostartSubcommand(sender);
                break;
            case "setautostart":
                this.setAutostartSubcommand(sender, args);
                break;
            case "givepoints":
                this.givePointsSubcommand(sender, args);
                break;
            case "reload":
                this.reloadSubcommand(sender);
                break;
            case "menu":
                this.menuSubcommand(sender);
                break;
            case "points":
                this.pointsSubcommand(sender);
                break;
            case "leave":
                this.leaveSubcommand(sender);
                break;
            case "stats":
                this.statsSubcommand(sender, args);
                break;
            case "disableweather":
                this.disableWeatherSubcommand(sender);
                break;
            case "pay":
                this.paySubcommand(sender, args);
                break;
            case "world":
            case "worlds":
                this.worldsSubcommand(sender, args);
                break;
            case "map":
            case "maps":
                this.mapsSubcommand(sender);
                break;
            case "forcemap":
                this.forcemapSubcommand(sender, args);
                break;
            default:
                sender.sendMessage("§cUnknown command");
                break;
        }

        return true;
    }

    private void stopSubcommand(CommandSender sender) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (this.plugin.stopGame()) {
            sender.sendMessage("§aGame successfully stopped");
        } else {
            sender.sendMessage("§cNo game running");
        }

    }

    public void startSubcommand(CommandSender sender) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo Permission");
            return;
        }

        if (this.plugin.getGame() == null) {

            if (this.plugin.startGame()) {
                sender.sendMessage("§aGame successfully started");
            } else {
                sender.sendMessage("§cCould not start game (unknown error)");
            }

        } else if (this.plugin.getGame() instanceof Lobby) {

            ((Lobby) this.plugin.getGame()).forcestart();
            sender.sendMessage("§aForcing game to start...");

        } else {
            sender.sendMessage("§cGame is already in ingame or endlobby status");
        }

    }

    public void statusSubcommand(CommandSender sender) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo Permission");
            return;
        }

        GamePart game = this.plugin.getGame();

        if (game == null) {
            sender.sendMessage("§7No game running");
        } else if (game instanceof Lobby) {
            sender.sendMessage("§7Game status: LOBBY");
        } else if (game instanceof Game) {
            sender.sendMessage("§7Game status: INGAME");
            sender.sendMessage("§7Time: " + ((Game) game).getTime());
        } else {
            sender.sendMessage("§cUnknown game status (stop with /combattest stop)");
        }

    }

    public void addPlayerSubcommand(CommandSender sender, String[] args) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo Permission");
            return;
        }

        if (this.plugin.getGame() == null) {
            sender.sendMessage("§cNo game running");
            return;
        }

        Player player = this.plugin.getPlayerFromString(args[1]);

        if (player == null) {
            sender.sendMessage("§cPlayer offline");
            return;
        }

        this.plugin.getGame().addPlayer(player);
        sender.sendMessage("§aPlayer successfully added");

    }

    public void removePlayerSubcommand(CommandSender sender, String[] args) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo Permission");
            return;
        }

        if (this.plugin.getGame() == null) {
            sender.sendMessage("§cNo game running");
            return;
        }

        Player player = this.plugin.getPlayerFromString(args[1]);

        if (player == null) {
            sender.sendMessage("§cPlayer offline (will be removed automatically)");
            return;
        }

        this.plugin.getGame().removePlayer(player.getUniqueId());
        sender.sendMessage("§aPlayer successfully removed");

    }

    public void getPlayersSubcommand(CommandSender sender, String[] args) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo Permission");
            return;
        }

        if (this.plugin.getGame() == null) {
            sender.sendMessage("§cNo game running");
            return;
        }

        List<UUID> playerList;

        if (args.length >= 2) {

            List<UUID> inputList = new ArrayList<>();

            for (int i = 1; i < args.length; i++) {

                Player player = this.plugin.getPlayerFromString(args[i]);

                if (player == null) {
                    continue;
                }

                inputList.add(player.getUniqueId());

            }

            playerList = this.plugin.getGame().getPlayers(inputList.toArray(new UUID[0]));
            sender.sendMessage("§aPart of player list:");

        } else {

            playerList = this.plugin.getGame().getPlayers();
            sender.sendMessage("§aPlayer list:");

        }

        for (UUID uuid : playerList) {

            Player player = this.plugin.getServer().getPlayer(uuid);

            if (player == null) {
                sender.sendMessage("§a" + uuid.toString() + " (OFFLINE, will be removed automatically);");
                continue;
            }

            sender.sendMessage("§a" + player.getName() + " (" + uuid.toString() + ");");

        }

    }

    public void bypassSubcommand(CommandSender sender, String[] args) {

        if (args.length == 1 && sender instanceof Player) {

            sender.sendMessage("§7Your bypass status: " + this.plugin.isPlayerBypassing(((Player) sender).getUniqueId()));

        } else {

            if (!this.hasPermissionAdmin(sender)) {
                sender.sendMessage("§cNo permission");
                return;
            }

            if (args.length == 2) {

                if (sender instanceof Player && (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true"))) {

                    this.plugin.addBypassingPlayer(((Player) sender).getUniqueId());
                    sender.sendMessage("§aBypassing mode enabled");

                } else if (sender instanceof Player && (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false"))) {

                    this.plugin.removeBypassingPlayer(((Player) sender).getUniqueId());
                    sender.sendMessage("§aBypassing mode disabled");

                } else {

                    UUID playerId;

                    try {
                        playerId = UUID.fromString(args[1]);
                    } catch (IllegalArgumentException e) {
                        OfflinePlayer player = this.plugin.getOfflinePlayerFromString(args[1]);

                        if (player == null) {
                            sender.sendMessage("§cUnknown player");
                            return;
                        }

                        playerId = player.getUniqueId();
                    }

                    sender.sendMessage("§7Bypassing status of the player: " + this.plugin.isPlayerBypassing(playerId));

                }

            } else if (args.length == 3) {

                UUID playerId;

                try {
                    playerId = UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    OfflinePlayer player = this.plugin.getOfflinePlayerFromString(args[1]);

                    if (player == null) {
                        sender.sendMessage("§cUnknown player");
                        return;
                    }

                    playerId = player.getUniqueId();
                }

                if (args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true")) {

                    this.plugin.addBypassingPlayer(playerId);
                    sender.sendMessage("§aBypass for player enabled");

                } else if (args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false")) {

                    this.plugin.removeBypassingPlayer(playerId);
                    sender.sendMessage("§aBypass for player disabled");

                } else {
                    sender.sendMessage("§cUsage: /combattest bypass <player> on/off");
                    return;
                }

            } else {
                sender.sendMessage("§cUnknown command usage");
            }

        }

    }

    public void getScoreSubcommand(CommandSender sender, String[] args, int score) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /combattest get<score> <player>");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        Player player = this.plugin.getPlayerFromString(args[1]);

        if (player == null) {
            sender.sendMessage("§cPlayer is offline");
            return;
        }

        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(player.getUniqueId());

        if (playerData == null) {
            sender.sendMessage("§cPlayer not ingame");
            return;
        }

        switch (score) {
            case 0:
                sender.sendMessage("§7Points: " + playerData.getPoints());
                break;
            case 1:
                sender.sendMessage("§7Melee score: " + playerData.getMeleeEquipment());
                break;
            case 2:
                sender.sendMessage("§7Ranged score: " + playerData.getRangedEquipment());
                break;
            case 3:
                sender.sendMessage("§7Armor score: " + playerData.getArmorEquipment());
                break;
            case 4:
                sender.sendMessage("§7Team score: " + playerData.getTeam());
                break;
            default:
                sender.sendMessage("§cUnknown error");
                break;
        }

    }

    public void setTimeSubcommand(CommandSender sender, String[] args) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /combattest settime <score>");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        try {
            ((Game) this.plugin.getGame()).setTime(Integer.parseInt(args[1]));
            sender.sendMessage("§aTime successfully changed");
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cPlease specify a valid int value");
        }

    }

    public void setScoreSubcommand(CommandSender sender, String[] args, int score) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length != 3) {
            sender.sendMessage("§cUsage: /combattest set<score> <player> <score>");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        Player player = this.plugin.getPlayerFromString(args[1]);

        if (player == null) {
            sender.sendMessage("§cPlayer is offline");
            return;
        }

        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(player.getUniqueId());

        if (playerData == null) {
            sender.sendMessage("§cPlayer not ingame");
            return;
        }

        int value;

        try {

            value = Integer.parseInt(args[2]);

        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cPlease specify a valid int value");
            return;
        }

        switch (score) {
            case 0:
                playerData.setPoints(value);
                sender.sendMessage("§aPoints set");
                break;
            case 1:
                playerData.setMeleeEquipment(value);
                sender.sendMessage("§aMelee score set");
                break;
            case 2:
                playerData.setRangedEquipment(value);
                sender.sendMessage("§aRanged score set");
                break;
            case 3:
                playerData.setArmorEquipment(value);
                sender.sendMessage("§aArmor score set");
                break;
            case 4:
                if (value >= 0) {
                    playerData.setTeam(value);
                    sender.sendMessage("§aTeam score set");
                } else {
                    sender.sendMessage("§cTeam values must be 0 for no team or a positive value for a team id");
                }
                break;
            default:
                sender.sendMessage("§cUnknown error");
                break;
        }

    }

    public void menuSubcommand(CommandSender sender) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by a player");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        PlayerMenu menu = ((Game) this.plugin.getGame()).getPlayerMenu(((Player) sender).getUniqueId());

        if (menu == null) {
            sender.sendMessage("§cYou are not ingame");
            return;
        }

        menu.setPage(0);
        ((Player) sender).openInventory(menu.getInventory());

    }

    public void pointsSubcommand(CommandSender sender) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by a player");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(((Player) sender).getUniqueId());

        if (playerData == null) {
            sender.sendMessage("§cYou are not ingame");
            return;
        }

        sender.sendMessage("§7Your points: " + playerData.getPoints());

    }

    public void leaveSubcommand(CommandSender sender) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by a player");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        if (!((Game) this.plugin.getGame()).getPlayerMap().containsKey(((Player) sender).getUniqueId())) {
            sender.sendMessage("§cYou are not ingame");
            return;
        }

        ((Game) this.plugin.getGame()).removePlayer(((Player) sender).getUniqueId());
        sender.sendMessage("§aYou successfully left the game");

    }

    public void statsSubcommand(CommandSender sender, String[] args) {

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        Player player;
        PlayerData playerData;

        if (args.length == 2) {

            player = this.plugin.getPlayerFromString(args[1]);

            if (player == null) {
                sender.sendMessage("§cPlayer is offline");
                return;
            }

            playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(player.getUniqueId());

        } else {

            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be executed by a player");
                return;
            }

            player = (Player) sender;
            playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(player.getUniqueId());

        }

        if (playerData == null) {
            sender.sendMessage("§cPlayer not ingame");
            return;
        }

        sender.sendMessage("§7§lStats of §a§l" + player.getName() + "§a§l:§r\n" +
                "§7Kills: §a" + playerData.getKills() + "\n" +
                "§7Deaths: §a" + playerData.getDeaths() + "\n" +
                "§7K/D: §a" + PlayerData.getKD(playerData.getKills(), playerData.getDeaths()));

        if (playerData.getTeam() > 0) {
            sender.sendMessage("§7&lStats of §a§lTeam " + playerData.getTeam() + "§a§l:§r\n" +
                    "§7Kills: §a" + ((Game) this.plugin.getGame()).getTeamKills(playerData.getTeam()) + "\n" +
                    "§7Deaths: §a" + ((Game) this.plugin.getGame()).getTeamDeaths(playerData.getTeam()) + "\n" +
                    "§7K/D: §a" + PlayerData.getKD(((Game) this.plugin.getGame()).getTeamKills(playerData.getTeam()), ((Game) this.plugin.getGame()).getTeamDeaths(playerData.getTeam())));
        }

    }

    public void isAutostartSubcommand(CommandSender sender) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        sender.sendMessage("§7Autostart: " + this.plugin.isAutostartNewGame());

    }

    public void setAutostartSubcommand(CommandSender sender, String[] args) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /combattest setmaxtime <time>");
            return;
        }

        this.plugin.setAutostartNewGame(Boolean.parseBoolean(args[1]));
        sender.sendMessage("§aAutostart of new games set to: " + this.plugin.isAutostartNewGame());

    }

    public void givePointsSubcommand(CommandSender sender, String[] args) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /combattest givepoints <amount>");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        try {

            for (UUID playerId : ((Game) this.plugin.getGame()).getPlayerMap().keySet()) {
                PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(playerId);

                playerData.setPoints(playerData.getPoints() + Integer.parseInt(args[1]));
            }

        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cPlease specify a valid int value");
        }

    }

    public void reloadSubcommand(CommandSender sender) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        this.plugin.getConfigManager().reloadConfig();
        this.plugin.getMapConfig().reloadConfig();
        sender.sendMessage("§aReloading config...");

    }

    public void disableWeatherSubcommand(CommandSender sender) {

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        PlayerData playerData = ((Game) this.plugin.getGame()).getPlayerMap().get(((Player) sender).getUniqueId());

        if (playerData == null) {
            sender.sendMessage("§cYou are not ingame");
            return;
        }

        playerData.setWeatherDisabled(!playerData.isWeatherDisabled());

        if (playerData.isWeatherDisabled()) {
            sender.sendMessage("§aRain and thunder disabled");
        } else {
            sender.sendMessage("§aUsing global weather");
        }

    }

    public void paySubcommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be executed by a player");
            return;
        }

        if (!(this.plugin.getGame() instanceof Game)) {
            sender.sendMessage("§cNo game running");
            return;
        }

        if (!((Game) this.plugin.getGame()).getPlayerMap().containsKey(((Player) sender).getUniqueId())) {
            sender.sendMessage("§cYou are not ingame");
            return;
        }

        if (args.length != 3) {
            sender.sendMessage("§cUsage: /combattest pay <player> <amount>");
            return;
        }

        PlayerData senderData = ((Game) this.plugin.getGame()).getPlayerMap().get(((Player) sender).getUniqueId());

        if (senderData == null) {
            sender.sendMessage("§cNot ingame");
            return;
        }

        Player receiver = this.plugin.getPlayerFromString(args[1]);

        if (receiver == null) {
            sender.sendMessage("§cReceiving player is not online");
            return;
        }

        PlayerData receiverData = ((Game) this.plugin.getGame()).getPlayerMap().get(receiver.getUniqueId());

        if (receiverData == null) {
            sender.sendMessage("§cReceiving player is not ingame");
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cPlease specify a valid int value");
            return;
        }

        if (senderData.getPoints() < amount) {
            sender.sendMessage("§cYou don't have enough points");
            return;
        }

        senderData.setPoints(senderData.getPoints() - amount);
        receiverData.setPoints(receiverData.getPoints() + amount);
        sender.sendMessage("§aSuccessfully sent " + amount + " points to " + receiver.getName());
        receiver.sendMessage("§bReceived " + amount + " points from " + sender.getName());

    }

    public void worldsSubcommand(CommandSender sender, String[] args) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /bedwars world list/load/unload");
            return;
        }

        switch (args[1]) {
            case "list": {

                String message = "§7Loaded worlds:\n";

                int i = 0;
                for (World world : List.copyOf(this.plugin.getServer().getWorlds())) {

                    message = message + "§7[" + i + "] " + world.getName() + " (" + world.getUID() + ");\n";
                    i++;

                }

                sender.sendMessage(message);

                return;
            }
            case "load": {

                if (args.length < 3) {
                    sender.sendMessage("§cYou need to specify a world name");
                    return;
                }

                if (this.plugin.getServer().getWorld(args[2]) != null) {
                    sender.sendMessage("§cWorld already loaded");
                    return;
                }

                sender.sendMessage("§eLoading/creating world...");
                this.plugin.getServer().createWorld(new WorldCreator(args[2]));
                sender.sendMessage("§aWorld successfully loaded/created");

                return;
            }
            case "unload": {

                if (args.length < 3) {
                    sender.sendMessage("§cYou need to specify a world name/uid/index");
                    return;
                }

                World world = null;

                try {
                    world = this.plugin.getServer().getWorld(UUID.fromString(args[2]));
                } catch (IllegalArgumentException e) {

                    try {
                        world = this.plugin.getServer().getWorlds().get(Integer.parseInt(args[2]));
                    } catch (IllegalArgumentException e2) {
                        world = this.plugin.getServer().getWorld(args[2]);
                    }

                }

                if (world == null) {
                    sender.sendMessage("§cWorld is not loaded");
                    return;
                }

                boolean save = false;

                if (args.length >= 4) {
                    save = Boolean.parseBoolean(args[3]);
                }

                this.plugin.getServer().unloadWorld(world, save);
                sender.sendMessage("§aUnloaded world (save=" + save + ")");

                return;
            }
            case "teleport": {

                if (args.length < 3) {
                    sender.sendMessage("§cYou need to specify a world name/uid/index");
                    return;
                }

                World world = null;

                try {
                    world = this.plugin.getServer().getWorld(UUID.fromString(args[2]));
                } catch (IllegalArgumentException e) {

                    try {
                        world = this.plugin.getServer().getWorlds().get(Integer.parseInt(args[2]));
                    } catch (IllegalArgumentException e2) {
                        world = this.plugin.getServer().getWorld(args[2]);
                    }

                }

                if (world == null) {
                    sender.sendMessage("§cWorld is not loaded");
                    return;
                }

                Location location = new Location(world, 0, 0, 0, 0, 0);

                if (args.length >= 4) {

                    Player player = this.plugin.getPlayerFromString(args[3]);

                    if (player == null) {
                        sender.sendMessage("§cPlayer not online");
                        return;
                    }

                    player.teleport(location);
                    sender.sendMessage("§aTeleporting " + player.getName() + " to " + world.getName());

                } else {

                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§cYou need to be a player to teleport yourself");
                        return;
                    }

                    ((Player) sender).teleport(location);
                    sender.sendMessage("§aTeleporting yourself to " + world.getName());

                }

                return;
            }
            default:
                sender.sendMessage("§cUnknown subcommand");
                return;
        }

    }

    public void mapsSubcommand(CommandSender sender) {

        if (!(this.plugin.getGame() instanceof Lobby)) {
            sender.sendMessage("§cNo lobby running");
            return;
        }

        MapData mapData = ((Lobby) this.plugin.getGame()).getSelectedMap();

        if (mapData == null) {
            sender.sendMessage("§7No map selected");
        } else {
            sender.sendMessage("§7Selected map: " + mapData.getName() + " (" + mapData.getWorld() + ")");
        }

        sender.sendMessage("§7Available Maps:");

        for (MapData map : List.copyOf(((Lobby) this.plugin.getGame()).getMaps())) {
            sender.sendMessage("§7" + map.getName() + " (" + map.getWorld() + ")");
        }

    }

    public void forcemapSubcommand(CommandSender sender, String[] args) {

        if (!this.hasPermissionAdmin(sender)) {
            sender.sendMessage("§cNo permission");
            return;
        }

        if (!(this.plugin.getGame() instanceof Lobby)) {
            sender.sendMessage("§cNo lobby running");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /combattest forcemap <mapName/w:worldName>");
            return;
        }

        MapData mapData = null;

        for (MapData map : List.copyOf(((Lobby) this.plugin.getGame()).getMaps())) {

            if (args[1].startsWith("w:")) {

                if (map.getWorld().equals(args[1].substring(2))) {
                    mapData = map;
                }

            } else {

                if (map.getName().equals(args[1])) {
                    mapData = map;
                }

            }

        }

        if (mapData == null) {
            sender.sendMessage("§cMap does not exist");
            return;
        }

        ((Lobby) this.plugin.getGame()).selectMap(mapData);
        sender.sendMessage("§aMap successfully selected");

    }

    public boolean hasPermissionAdmin(CommandSender sender) {
        return (sender instanceof ConsoleCommandSender) || (sender instanceof Player && sender.hasPermission(this.plugin.getPermissionPrefix() + "." + "admin"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        List<String> tabComplete = new ArrayList<>();

        if (args.length == 1) {

            if (hasPermissionAdmin(sender)) {

                tabComplete.add("stop");
                tabComplete.add("start");
                tabComplete.add("status");
                tabComplete.add("addplayer");
                tabComplete.add("removeplayer");
                tabComplete.add("getplayers");
                tabComplete.add("bypass");
                tabComplete.add("settime");
                tabComplete.add("getpoints");
                tabComplete.add("setpoints");
                tabComplete.add("getmelee");
                tabComplete.add("setmelee");
                tabComplete.add("getranged");
                tabComplete.add("setranged");
                tabComplete.add("getarmor");
                tabComplete.add("setarmor");
                tabComplete.add("getteam");
                tabComplete.add("setteam");
                tabComplete.add("isautostart");
                tabComplete.add("setautostart");
                tabComplete.add("givepoints");
                tabComplete.add("reload");
                tabComplete.add("world");
                tabComplete.add("forcemap");

            }

            tabComplete.add("menu");
            tabComplete.add("points");
            tabComplete.add("leave");
            tabComplete.add("stats");
            tabComplete.add("disableweather");
            tabComplete.add("pay");
            tabComplete.add("maps");

        }

        return List.copyOf(tabComplete);
    }
}
