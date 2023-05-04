package net.jandie1505.combattest.commands;

import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.Game;
import net.jandie1505.combattest.game.GamePart;
import net.jandie1505.combattest.game.Lobby;
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
            sender.sendMessage("§7Usage: /combattest stop/start/status/addplayer/removeplayer/getplayers/bypass");
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

    public boolean hasPermissionAdmin(CommandSender sender) {
        return (sender instanceof ConsoleCommandSender) || (sender instanceof Player && sender.hasPermission(this.plugin.getPermissionPrefix() + "." + "admin"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        return List.of();
    }
}
