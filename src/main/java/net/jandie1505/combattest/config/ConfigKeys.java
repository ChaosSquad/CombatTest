package net.jandie1505.combattest.config;

import org.jetbrains.annotations.NotNull;

public interface ConfigKeys {
    @NotNull String SINGLE_SERVER_MODE = "single_server_mode";
    @NotNull String AUTOSTART_NEW_GAME = "autostart_new_game";

    @NotNull String LOBBY_SPAWNPOINT = "lobby.spawnpoint";
    @NotNull String LOBBY_SPAWNPOINT_X = LOBBY_SPAWNPOINT + ".x";
    @NotNull String LOBBY_SPAWNPOINT_Y = LOBBY_SPAWNPOINT + ".y";
    @NotNull String LOBBY_SPAWNPOINT_Z = LOBBY_SPAWNPOINT + ".z";
    @NotNull String LOBBY_SPAWNPOINT_YAW = LOBBY_SPAWNPOINT + ".yaw";
    @NotNull String LOBBY_SPAWNPOINT_PITCH = LOBBY_SPAWNPOINT + ".pitch";

    @NotNull String LOBBY_BORDER = "lobby.border";
    @NotNull String LOBBY_BORDER_ENABLE = LOBBY_BORDER + ".enable";
    @NotNull String LOBBY_BORDER_X1 = LOBBY_BORDER + ".x1";
    @NotNull String LOBBY_BORDER_Y1 = LOBBY_BORDER + ".y1";
    @NotNull String LOBBY_BORDER_Z1 = LOBBY_BORDER + ".z1";
    @NotNull String LOBBY_BORDER_X2 = LOBBY_BORDER + ".x2";
    @NotNull String LOBBY_BORDER_Y2 = LOBBY_BORDER + ".y2";
    @NotNull String LOBBY_BORDER_Z2 = LOBBY_BORDER + ".z2";

    @NotNull String LOBBY_TIME = "lobby.time";
    @NotNull String LOBBY_REQUIRED_PLAYERS = "lobby.required_players";
    @NotNull String LOBBY_MAP_VOTING = "lobby.map_voting";
    @NotNull String LOBBY_TEAM_SELECTION = "lobby.team_selection";

    @NotNull String CLOUD_SYSTEM_MODE_ENABLE = "cloud_system_mode.enable";
    @NotNull String CLOUD_SYSTEM_MODE_SWITCH_TO_INGAME_COMMAND = "cloud_system_mode.switch_to_ingame_command";

    @NotNull String INTEGRATIONS_CLOUDNET = "integrations.cloudnet";
    @NotNull String INTEGRATIONS_PARTY_AND_FRIENDS = "integrations.party_and_friends";
    @NotNull String INTEGRATIONS_SUPERVANISH_PREMIUMVANISH = "integrations.sv_pv";

    @NotNull String INTEGRATIONS_PLAYERPOINTS = "integrations.playerpoints";
    @NotNull String INTEGRATIONS_PLAYERPOINTS_ENABLE = INTEGRATIONS_PLAYERPOINTS + ".enable";
    @NotNull String INTEGRATIONS_PLAYERPOINTS_KILL = INTEGRATIONS_PLAYERPOINTS + ".player_kill";
    @NotNull String INTEGRATIONS_PLAYERPOINTS_INDIRECT_KILL = INTEGRATIONS_PLAYERPOINTS + ".indirect_player_kill";
    @NotNull String INTEGRATIONS_PLAYERPOINTS_UPGRADE_PURCHASED = INTEGRATIONS_PLAYERPOINTS + ".upgrade_purchased";
    @NotNull String INTEGRATIONS_PLAYERPOINTS_MAX_REWARDS_AMOUNT = INTEGRATIONS_PLAYERPOINTS + ".max_rewards_amount";

    @NotNull String INTEGRATIONS_PLAYERLEVELS = "integrations.playerlevels";
    @NotNull String INTEGRATIONS_PLAYERLEVELS_ENABLE = INTEGRATIONS_PLAYERLEVELS + ".enable";
    @NotNull String INTEGRATIONS_PLAYERLEVELS_KILL = INTEGRATIONS_PLAYERLEVELS + ".player_kill";
    @NotNull String INTEGRATIONS_PLAYERLEVELS_INDIRECT_KILL = INTEGRATIONS_PLAYERLEVELS + ".indirect_player_kill";
    @NotNull String INTEGRATIONS_PLAYERLEVELS_UPGRADE_PURCHASED = INTEGRATIONS_PLAYERLEVELS + ".upgrade_purchased";
    @NotNull String INTEGRATIONS_PLAYERLEVELS_MAX_REWARDS_AMOUNT = INTEGRATIONS_PLAYERLEVELS + ".max_rewards_amount";

    @NotNull String GAME_SPAWNPOINT_BLOCKED_RADIUS = "game.spawnpoint_blocked_radius";
}
