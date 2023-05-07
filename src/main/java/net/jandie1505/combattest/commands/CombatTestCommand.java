package net.jandie1505.combattest.commands;

import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.*;
import org.bukkit.OfflinePlayer;
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
                sender.sendMessage("§7Usage: /combattest stop/start/status/addplayer/removeplayer/getplayers/bypass/settime/getpoints/setpoints/getmelee/setmelee/getranged/setranged/getarmor/setarmor/menu/points/leave");
            } else {
                sender.sendMessage("§7Usage: /combattest menu/points/leave");
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
            case "menu":
                this.menuSubcommand(sender);
                break;
            case "points":
                this.pointsSubcommand(sender);
                break;
            case "leave":
                this.leaveSubcommand(sender);
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

        ((Game) this.plugin.getGame()).getPlayerMap().remove(((Player) sender).getUniqueId());
        sender.sendMessage("§aYou successfully left the game");

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

            }

            tabComplete.add("menu");
            tabComplete.add("points");
            tabComplete.add("leave");

        }

        return List.copyOf(tabComplete);
    }
}
