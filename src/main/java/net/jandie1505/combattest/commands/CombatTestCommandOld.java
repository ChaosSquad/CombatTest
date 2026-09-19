package net.jandie1505.combattest.commands;

import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.constants.Permissions;
import net.jandie1505.combattest.game.base.GamePart;
import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.data.PlayerData;
import net.jandie1505.combattest.game.lobby.Lobby;
import net.jandie1505.combattest.game.lobby.LobbyPlayerData;
import net.jandie1505.combattest.game.lobby.MapData;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Deprecated(forRemoval = true)
public class CombatTestCommandOld implements CommandExecutor, TabCompleter {
    private final CombatTest plugin;

    public CombatTestCommandOld(CombatTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (args.length < 1) {

            if (this.hasPermissionAdmin(sender)) {
                sender.sendMessage("§7Usage: /combattest stop/start/status/addplayer/removeplayer/getplayers/bypass/settime/getpoints/setpoints/getmelee/setmelee/getranged/setranged/getarmor/setarmor/getteam/setteam/isautostart/setautostart/getmaxtime/setmaxtime/reload/getvote/setvote/getlobbyteam/setlobbyteam/forcemap/getserverstatus/menu/points/leave/stats/votemap/maps");
            } else {
                sender.sendMessage("§7Usage: /combattest menu/points/leave/stats");
            }

            return true;
        }

        switch (args[0]) {
            case "map":
            case "maps":
                this.mapsSubcommand(sender);
                break;
            case "votemap":
                this.votemapCommand(sender, args);
                break;
            case "forcemap":
                this.forcemapSubcommand(sender, args);
                break;
            case "equipments":

            default:
                sender.sendMessage("§cUnknown command");
                break;
        }

        return true;
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

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /combattest forcemap <mapName/w:worldName>");
            return;
        }

        String mapName = args[1];

        for (int i = 2; i < args.length; i++) {

            mapName = mapName + " " + args[i];

        }

        MapData mapData = null;

        for (MapData map : List.copyOf(((Lobby) this.plugin.getGame()).getMaps())) {

            if (mapName.startsWith("w:")) {

                if (map.getWorld().equals(mapName.substring(2))) {
                    mapData = map;
                }

            } else {

                if (map.getName().equals(mapName)) {
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

    public void votemapCommand(CommandSender sender, String[] args) {

        if (!(this.plugin.getGame() instanceof Lobby)) {
            sender.sendMessage("§cNo lobby running");
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThe command needs to be executed by a player");
            return;
        }

        LobbyPlayerData playerData = ((Lobby) this.plugin.getGame()).getPlayerMap().get(((Player) sender).getUniqueId());

        if (playerData == null) {
            sender.sendMessage("§cYou are not in the lobby");
            return;
        }

        if (!((Lobby) this.plugin.getGame()).isMapVoting() || ((Lobby) this.plugin.getGame()).getSelectedMap() != null) {
            sender.sendMessage("§cMap voting is already over");
            return;
        }

        if (args.length < 2) {
            playerData.setVote(null);
            sender.sendMessage("§aYou successfully removed your vote");
            return;
        }

        String mapName = args[1];

        for (int i = 2; i < args.length; i++) {

            mapName = mapName + " " + args[i];

        }

        MapData mapData = null;

        for (MapData map : ((Lobby) this.plugin.getGame()).getMaps()) {

            if (map.getName().equals(mapName)) {
                mapData = map;
                break;
            }

        }

        if (mapData == null) {
            sender.sendMessage("§cMap does not exist");
            return;
        }

        playerData.setVote(mapData);
        sender.sendMessage("§aYou voted for " + mapData.getName());

    }

    public boolean hasPermissionAdmin(CommandSender sender) {
        return Permissions.hasPermission(sender, Permissions.ADMIN);
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
                tabComplete.add("isautostart");
                tabComplete.add("setautostart");
                tabComplete.add("givepoints");
                tabComplete.add("reload");
                tabComplete.add("forcemap");
                tabComplete.add("getvote");
                tabComplete.add("setvote");
                tabComplete.add("getlobbyteam");
                tabComplete.add("setlobbyteam");
                tabComplete.add("randomteams");
                tabComplete.add("getserverstatus");

            }

            tabComplete.add("menu");
            tabComplete.add("points");
            tabComplete.add("leave");
            tabComplete.add("stats");
            tabComplete.add("disableweather");
            tabComplete.add("pay");
            tabComplete.add("maps");
            tabComplete.add("votemap");

        } else if (args.length == 2) {

            switch (args[0]) {
                case "stats":
                case "pay":
                    tabComplete.addAll(this.getIngamePlayerNames());
                    break;
                case "votemap":
                    tabComplete.addAll(this.getMapStrings());
                    break;
                case "forcemap":
                    if (hasPermissionAdmin(sender)) {
                        tabComplete.addAll(this.getMapStrings());
                    }
                    break;
                case "addplayer":
                    if (this.plugin.getGame() instanceof Game) {
                        for (Player player : this.plugin.getServer().getOnlinePlayers()) {

                            if (!((Game) this.plugin.getGame()).getPlayerMap().containsKey(player.getUniqueId())) {
                                tabComplete.add(player.getName());
                            }

                        }
                    }
                    break;
                case "removeplayer":
                case "givepoints":
                    if (hasPermissionAdmin(sender)) {
                        tabComplete.addAll(this.getIngamePlayerNames());
                    }
                    break;
                case "setautostart":
                case "bypass":
                    tabComplete.add("true");
                    tabComplete.add("false");
                    break;
                default:
                    break;
            }

        }

        return List.copyOf(tabComplete);
    }

    private List<String> getIngamePlayerNames() {
        List<String> returnList = new ArrayList<>();

        if (this.plugin.getGame() instanceof Game) {
            for (UUID playerId : ((Game) this.plugin.getGame()).getPlayerMap().keySet()) {

                Player player = this.plugin.getServer().getPlayer(playerId);

                if (player != null) {
                    returnList.add(player.getName());
                }

            }
        }

        return List.copyOf(returnList);
    }

    private List<String> getMapStrings() {
        List<String> returnList = new ArrayList<>();

        if (this.plugin.getGame() instanceof Lobby) {

            for (MapData mapData : ((Lobby) this.plugin.getGame()).getMaps()) {
                returnList.add(mapData.getName());
            }

        }

        return List.copyOf(returnList);
    }
}
