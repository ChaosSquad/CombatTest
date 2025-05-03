package net.jandie1505.combattest.game.game.scoreboard;

import net.jandie1505.combattest.game.game.Game;
import net.jandie1505.combattest.game.game.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameScoreboardManager {
    @NotNull private final Game game;
    @NotNull private final Component name;

    private final Map<UUID, Scoreboard> playerScoreboards;

    public GameScoreboardManager(
            @NotNull Game game,
            @NotNull Component name
    ) {
        this.game = game;
        this.name = name;
        this.playerScoreboards = Collections.synchronizedMap(new HashMap<>());
    }

    // HANDLE SCOREBOARDS

    /**
     * Removes all scoreboards from offline players.
     */
    public void cleanupPlayerScoreboards() {

        for (UUID uuid : Map.copyOf(this.playerScoreboards).keySet()) {
            Player player = this.game.getPlugin().getServer().getPlayer(uuid);

            if (player != null) {
                continue;
            }

            this.playerScoreboards.remove(uuid);

        }

    }

    /**
     * Creates a scoreboard for the specified player and sets it.
     * @param player Bukkit player
     */
    public void setupScoreboard(Player player) {

        Scoreboard scoreboard = this.playerScoreboards.get(player.getUniqueId());

        if (scoreboard != null && player.getScoreboard() != scoreboard) {
            player.setScoreboard(scoreboard);
        }

        if (scoreboard == null) {
            scoreboard = this.game.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
            this.playerScoreboards.put(player.getUniqueId(), scoreboard);
            player.setScoreboard(scoreboard);
        }

    }

    /**
     * Handles the scoreboard teams of a player scoreboard.
     * @param player bukkit player
     */
    public void handleScoreboardTeams(Player player) {

        // get scoreboard

        Scoreboard scoreboard = this.getPlayerScoreboard(player.getUniqueId());

        if (scoreboard == null) {
            return;
        }

        // Player data

        @Nullable final PlayerData playerData = this.game.getPlayerData(player);

        // Own team (current player when not in a team)

        Team ownTeam = scoreboard.getTeam("own");
        if (ownTeam == null) {
            ownTeam = scoreboard.registerNewTeam("own");
            ownTeam.displayName(Component.text("No team", NamedTextColor.GREEN));
            ownTeam.setAllowFriendlyFire(false);
            ownTeam.setCanSeeFriendlyInvisibles(true);
            ownTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM); // Only collide with other team
            ownTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS); // Only show name tags for the own team
            ownTeam.color(NamedTextColor.GREEN);
            ownTeam.prefix(Component.text("[No Team] ").color(NamedTextColor.GREEN));
        }

        if (playerData != null && playerData.getTeam() <= 0) {
            ownTeam.addEntry(player.getName());
        } else {
            ownTeam.removeEntry(player.getName());
        }

        // cleanup teams

        List<Integer> teams = this.game.getTeams();
        for (Team team : List.copyOf(scoreboard.getTeams())) {

            if (team.getName().startsWith("team.")) {

                int id;
                try {
                    id = Integer.parseInt(team.getName().substring(5));
                } catch (Exception e) {
                    team.unregister();
                    continue;
                }

                if (teams.contains(id)) continue;

                team.unregister();
                continue;
            } else if (team.getName().equals("noteam") || team.getName().equals("own")) {
                // can exist, do nothing
            } else {
                team.unregister();
            }

        }

        // handle teams

        teams = this.game.getTeams();
        for (int id : teams) {

            // create team if not exist and set options

            Team scoreboardTeam = scoreboard.getTeam("team." + id);

            if (scoreboardTeam == null) {

                scoreboardTeam = scoreboard.registerNewTeam(String.valueOf(id));
                scoreboardTeam.displayName(Component.text("Team " + id));
                scoreboardTeam.setAllowFriendlyFire(false);
                scoreboardTeam.setCanSeeFriendlyInvisibles(true);
                scoreboardTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM); // Only collide with other team
                scoreboardTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS); // Only show name tags for the own team

                if (playerData != null && playerData.getTeam() == id) {
                    scoreboardTeam.color(NamedTextColor.GREEN);
                    scoreboardTeam.prefix(Component.text("[Team " + id + "] ").color(NamedTextColor.GREEN));
                } else {
                    scoreboardTeam.color(NamedTextColor.RED);
                    scoreboardTeam.prefix(Component.text("[Team " + id + "] ").color(NamedTextColor.RED));
                }

            }

            // add players to team

            for (Player teamPlayer : this.game.getOnlinePlayers()) {
                PlayerData teamPlayerData = this.game.getPlayerData(teamPlayer);
                if (teamPlayerData == null) continue;
                if (teamPlayerData.getTeam() != id) continue; // Check if player is in the current team

                // do not add the player to any teams in the player's own scoreboard if the player is bypassing

                if (teamPlayer == player && this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) {
                    continue;
                }

                // do not add the player if the player is already added

                if (scoreboardTeam.getEntries().contains(teamPlayer.getName())) {
                    continue;
                }

                scoreboardTeam.addEntry(teamPlayer.getName());

            }

            // remove players from team

            for (String scoreboardEntry : List.copyOf(scoreboardTeam.getEntries())) {
                Player teamPlayer = this.game.getPlugin().getServer().getPlayer(scoreboardEntry);

                // remove player if player is not online

                if (teamPlayer == null) {
                    scoreboardTeam.removeEntry(scoreboardEntry);
                    continue;
                }

                // remove player in the player's own scoreboard if the player is bypassing

                if (teamPlayer == player && this.game.getPlugin().isPlayerBypassing(teamPlayer.getUniqueId())) {
                    scoreboardTeam.removeEntry(scoreboardEntry);
                    continue;
                }

                // remove player if player is not in team

                PlayerData teamPlayerData = this.game.getPlayerData(teamPlayer);

                if (teamPlayerData == null || teamPlayerData.getTeam() != id) {
                    scoreboardTeam.removeEntry(scoreboardEntry);
                    continue;
                }

            }

        }

        // No teams (other players which are not in a team)

        Team otherTeam = scoreboard.getTeam("other");
        if (otherTeam == null) {
            otherTeam = scoreboard.registerNewTeam("other");
            otherTeam.displayName(Component.text("No team", NamedTextColor.RED));
            otherTeam.setAllowFriendlyFire(false);
            otherTeam.setCanSeeFriendlyInvisibles(true);
            otherTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM); // Only collide with other team
            otherTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS); // Only show name tags for the own team
            otherTeam.color(NamedTextColor.RED);
            otherTeam.prefix(Component.text("[No Team] ").color(NamedTextColor.RED));
        }

        for (Player teamPlayer : this.game.getOnlinePlayers()) {
            if (teamPlayer.getUniqueId().equals(player.getUniqueId())) continue;

            PlayerData teamPlayerData = this.game.getPlayerData(teamPlayer);
            if (teamPlayerData == null) continue;
            if (teamPlayerData.getTeam() > 0) continue; // Check if player is in the current team

            // do not add the player to any teams in the player's own scoreboard if the player is bypassing

            if (this.game.getPlugin().isPlayerBypassing(player.getUniqueId())) {
                continue;
            }

            // do not add the player if the player is already added

            if (otherTeam.getEntries().contains(teamPlayer.getName())) {
                continue;
            }

            otherTeam.addEntry(teamPlayer.getName());
        }

        for (String scoreboardEntry : List.copyOf(otherTeam.getEntries())) {
            Player teamPlayer = this.game.getPlugin().getServer().getPlayer(scoreboardEntry);

            // remove player if player is not online

            if (teamPlayer == null) {
                otherTeam.removeEntry(scoreboardEntry);
                continue;
            }

            // remove player in the player's own scoreboard if the player is bypassing

            if (teamPlayer == player && this.game.getPlugin().isPlayerBypassing(teamPlayer.getUniqueId())) {
                otherTeam.removeEntry(scoreboardEntry);
                continue;
            }

            // remove player if player is not in team

            PlayerData teamPlayerData = this.game.getPlayerData(teamPlayer);

            if (teamPlayerData == null || teamPlayerData.getTeam() > 0) {
                otherTeam.removeEntry(scoreboardEntry);
                continue;
            }

        }

    }

    public void handleDisplaySlots(Player player, List<Component> sidebar) {

        // get scoreboard

        Scoreboard scoreboard = this.getPlayerScoreboard(player.getUniqueId());

        if (scoreboard == null) {
            return;
        }

        // copy sidebar

        sidebar = List.copyOf(sidebar);

        // get or create sidebar objective

        Objective objective = scoreboard.getObjective("sidebar");

        if (objective == null) {
            objective = scoreboard.registerNewObjective("sidebar", Criteria.DUMMY, this.name);
        }

        // handle sidebar

        for (int i = 0; i < 15; i++) {

            // set line

            Component line;

            if (i < sidebar.size()) {
                line = sidebar.get(i);
            } else {
                line = null;
            }

            // set line name

            String lineName = "§r";

            for (int i2 = 0; i2 < i; i2++) {
                lineName = lineName + "§r";
            }

            if (line == null) {

                if (scoreboard.getEntries().contains(lineName)) {
                    scoreboard.resetScores(lineName);
                }

                continue;
            }

            // get score

            Score score = objective.getScore(lineName);

            // remove if empty

            // set position

            score.setScore(15 - i);

            // get scoreboard team

            Team team = scoreboard.getTeam("sidebar." + i);

            if (team == null) {
                team = scoreboard.registerNewTeam("sidebar." + i);
            }

            // add score player to team

            team.addEntry(lineName);

            // set text

            team.suffix(line);

        }

        // set display slot

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    }

    // GETTER

    public @NotNull Game getGame() {
        return this.game;
    }

    /**
     * Get the scoreboard of a player
     * @param uuid player uuid
     * @return bukkit scoreboard
     */
    public Scoreboard getPlayerScoreboard(UUID uuid) {
        return this.playerScoreboards.get(uuid);
    }
}
