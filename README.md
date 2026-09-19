# ChaosSquad CombatTest

This plugin is a team deathmatch and/or FFA gamemode, originally created to test the combat system of the conquest mode from Castle Conquest 2 without revealing the project.
It then developed into its own gamemode.

## Requirements

- Minecraft 1.21.7+ (should also work with newer versions)
- Paper server (Spigot is no longer supported)
- Java 21 (this might change to Java 25 soon)
- Maps you want to play on

### Optional integrations

All of these are soft dependencies. If the plugin is missing, the corresponding feature is simply skipped.
Each one can additionally be switched off in the `integrations` config section.

| Plugin                      | Used for                                                                     |
|-----------------------------|------------------------------------------------------------------------------|
| CloudNet 4 (Bridge)         | Switching the service to ingame state, used together with cloud system mode. |
| Party and Friends           | Putting party members into the same team in the lobby.                       |
| SuperVanish / PremiumVanish | Vanished players automatically bypass the game and plugin restrictions.      |
| PlayerPoints                | Global point rewards for kills/assists/upgrades.                             |
| PlayerLevels                | Global XP rewards for kills/assists/upgrades.                                |

## Building

```sh
./gradlew clean build
```

The shaded plugin jar ends up in `build/libs/` (the `-original` jar is the unshaded one and is not usable).

## Setup

1. Have a Paper server.
2. Shut it down (if it is running).
3. Put the plugin jar into the `plugins` directory.
4. Start the server once so that `plugins/CombatTest/config.json` and `plugins/CombatTest/maps.json` get created.
5. Stop the server again.
6. Configure your maps in `maps.json` and edit `config.json` to your needs.
7. Start the server.
8. Start a game with `/combattest start` (or let autostart / cloud system mode do it).

### Always available

| Command                                                                    | Description                                                         | Who can run it                      |
|----------------------------------------------------------------------------|---------------------------------------------------------------------|-------------------------------------|
| `/combattest`                                                              | Show help page                                                      | Everyone (admin sees more)          |
| `/combattest start`                                                        | Start a new lobby                                                   | Admin                               |
| `/combattest stop`                                                         | Stop the running game part                                          | Admin                               |
| `/combattest status`                                                       | Show current game status                                            | Admin                               |
| `/combattest bypass [on/off/<player>]`                                     | Show/toggle bypass mode (bypassing players are ignored by the game) | Own status: everyone, others: Admin |
| `/combattest settings [autostart/single_server/cloud_system_mode] [value]` | Show settings, change autostart at runtime                          | Admin                               |
| `/combattest worlds <list/load/unload/teleport/clear>`                     | World management, independent from the rest of the plugin           | Admin                               |
| `/combattest players <add/remove/list>`                                    | Manage the players of the running game                              | Admin                               |

### Only while a lobby is running

| Command                                                | Description                 | Who can run it     |
|--------------------------------------------------------|-----------------------------|--------------------|
| `/combattest force-start` / `/start`                   | Force-start the game        | `combattest.start` |
| `/combattest value time [value]`                       | Get/set the lobby countdown | Admin              |
| `/combattest value map [<map>/null]`                   | Get/set the selected map    | Admin              |
| `/combattest players value <player> team [value]`      | Get/set a player's team     | Admin              |
| `/combattest players value <player> vote [<map>/null]` | Get/set a player's map vote | Admin              |
| `/combattest votemap <map>` / `/votemap <map>`         | Vote for a map              | Ingame             |

### Only while a game is running

| Command                                                               | Description                         | Who can run it |
|-----------------------------------------------------------------------|-------------------------------------|----------------|
| `/combattest value time [value]`                                      | Get/set the remaining game time     | Admin          |
| `/combattest players value <player> <team/alive/points/kills/deaths>` | Get/set player values               | Admin          |
| `/combattest players value <player> equipments`                       | Show/set the player's equipment ids | Admin          |
| `/combattest players value <player> equipmentcountdowns`              | Show the player's item countdowns   | Admin          |
| `/combattest menu` / `/menu`                                          | Open the player menu                | Ingame         |
| `/combattest pay <player> <amount>` / `/pay <player> <amount>`        | Pay ingame points to another player | Ingame         |

## Permissions

For playing the game, no permission is required (except for joining when single server mode is off).

| Permission                   | Description                                      | Default |
|------------------------------|--------------------------------------------------|---------|
| `combattest.admin`           | Admin permission, implies every other permission | op      |
| `combattest.start`           | Allows force-starting a lobby with `/start`      | op      |
| `combattest.chat_formatting` | Allows using formatting codes in the ingame chat | op      |
| `combattest.*`               | Wildcard permission                              | op      |

Note: unlike in older versions, the permission prefix from the config is **not** used for these
permissions anymore — they are hardcoded to `combattest.*`.

## Configuration

The plugin uses two JSON files in `plugins/CombatTest/`.

### `config.json`

Unknown keys are kept, missing keys fall back to the defaults. Setting `"recreateConfig": true` makes the
plugin overwrite the file with the defaults on the next start.

| Option                                   | Description                                                                                                                                           |
|------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `singleServerMode`                       | All players are added to the game during lobby, players joining ingame become spectators, enables scoreboard and tablist management                   |
| `autostartNewGame`                       | Start a new lobby 30 seconds after no game is running                                                                                                 |
| `lobby.spawnpoint`                       | Lobby spawn (`x`, `y`, `z`, `yaw`, `pitch`) in the server's main world                                                                                |
| `lobby.border`                           | Lobby border (`enable`, `x1`, `y1`, `z1`, `x2`, `y2`, `z2`)                                                                                           |
| `lobby.time`                             | Lobby countdown in seconds (default 90)                                                                                                               |
| `lobby.requiredPlayers`                  | Currently unused — the required player count is hardcoded to 2                                                                                        |
| `lobby.mapVoting`                        | Count the map votes when the map is auto-selected. The voting GUI is always available, no matter what this is set to.                                 |
| `lobby.teamSelection`                    | Currently unused (this is a bug). The team selection GUI is always available.                                                                         |
| `cloudSystemMode.enable`                 | Cloud system mode: autostart a lobby on server start, switch to ingame, shut the server down when the game ends (requires `singleServerMode`)         |
| `cloudSystemMode.switchToIngameCommand`  | Console command executed when the game starts (leave empty to disable)                                                                                |
| `integrations.cloudnet`                  | Enable the CloudNet bridge integration                                                                                                                |
| `integrations.partyandfriends`           | Enable the Party and Friends integration                                                                                                              |
| `integrations.supervanish-premiumvanish` | Vanished players bypass the game                                                                                                                      |
| `integrations.playerpoints`              | Enable the PlayerPoints reward integration                                                                                                            |
| `integrations.playerlevels`              | Enable the PlayerLevels XP reward integration (read by the code, but **not** written into the default config)                                         |
| `playerPointsRewards.playerKill`         | PlayerPoints given for a kill                                                                                                                         |
| `playerPointsRewards.indirectPlayerKill` | PlayerPoints given for an assist                                                                                                                      |
| `playerPointsRewards.upgradePurchased`   | PlayerPoints given for buying a shop item                                                                                                             |
| `playerPointsRewards.maxRewardsAmount`   | Maximum PlayerPoints payout per player and round                                                                                                      |
| `playerLevelsRewards.*`                  | Same as above, but for PlayerLevels XP                                                                                                                |
| `spawnpointBlockedRadius`                | Radius that has to be free of entities for a spawn point to be used (read from `config.json`, although the default value is written into `maps.json`) |

### `maps.json`

Configures the maps.
Every top-level key is a world name, and the world is loaded on demand when the map is selected.

```json
{
  "myworld": {
    "name": "My Map",
    "spawnpoints": [
      { "x": 10, "y": 64, "z": 10, "yaw": 0, "pitch": 0, "team": -1 }
    ],
    "border": { "enable": true, "x1": -100, "y1": 0, "z1": -100, "x2": 100, "y2": 255, "z2": 100 },
    "spawnpointBlockedRadius": 10,
    "time": 900,
    "enforcepvp": false
  }
}
```

| Option                    | Description                                                                                                                                                                                                           |
|---------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `name`                    | Display name in the map voting GUI                                                                                                                                                                                    |
| `spawnpoints`             | Respawn points. `team` is the team the spawn point belongs to, `-1` means "any team". All six keys (`x`, `y`, `z`, `yaw`, `pitch`, `team`) are required, a spawn point missing one of them is skipped with a warning. |
| `border`                  | World border, ingame players cannot leave it                                                                                                                                                                          |
| `spawnpointBlockedRadius` | See above                                                                                                                                                                                                             |
| `time`                    | Duration of a round in seconds                                                                                                                                                                                        |
| `enforcepvp`              | Players who don't fight for 60 seconds start glowing                                                                                                                                                                  |

⚠️ The generated example map in `maps.json` uses `direction` instead of `yaw` and has no `team` key, so it
is skipped on load. Write your spawn points with the keys listed above.

## Teams

Teams are completely player-driven, there is no fixed team setup and no automatic team assignment.

- Every player starts in team `0`, which means *no team*. A player who never joins a team stays in team `0`.
- Teams are created and joined in the lobby through the team selection GUI.
- Party and Friends parties are the only exception: when the game starts, all members of a party that are in
  the lobby are moved into the lowest currently empty team, **overriding** the team they picked themselves
  (requires the `partyandfriends` integration).
- Admins can assign teams manually with `/combattest players value <player> team <team>`, both in the lobby
  and ingame.
- `/combattest randomteams` distributes all teamless players over the teams `1` and `2`. This is the only
  way to get random teams and has to be triggered manually while the lobby is running.

This means the gamemode is team deathmatch and FFA at the same time, depending on what the players do:

- Players of the same team (team id > 0) cannot damage each other, and they see each other's name tags and
  a team prefix on the scoreboard.
- Players without a team (team id = 0) can damage and can be damaged by other players, so everybody who does not join a team plays FFA.
- Spawn points can be restricted to a team id (see `maps.json`), `team: -1` means the spawn point can be used by everyone (leave this on `-1` if you don't know how this behaves ingame).

There is no win condition based on teams.
The endlobby only shows rankings (most kills, most deaths, best K/D) for players and, if any teams exist, for teams.

## Equipment

It's mostly the same equipment system as the one from CastleConquest 2.

Internally, equipment is identified by a numeric id. The IDs follow the following convention:

- `1xxx`: melee
- `2xxx`: ranged
- `3xxx`: armor

`1000`, `2000` and `3000` are the defaults every player starts with. Each equipment has an equipment level
(0–7) which is used for balancing (low equipment bonus, downgrade on death), an upgrade price and a list of
upgrades it can be upgraded to.

### Melee (`1xxx`)

documentation: todo...

### Ranged (`2xxx`)

documentation: todo...

### Armor (`3xxx`)

documentation: todo...

Only the helmet actually carries the armor attributes and the shield goes into the off hand; the chestplate,
leggings and boots are purely decorative.

### Shop

Besides the upgrade tree there is an item shop (arrows, medikits, golden apples, a few joke items) which is also paid for with ingame points.

## Combat mechanics

- Points are the ingame currency. A kill pays 2000 points, an assist up to 2000 points depending on the
  damage dealt, teammates of the killer get 100 points. Points can be transferred with `/pay` (the bug with the negative values has been fixed, so you don't need to try it ;) ...).
- Low equipment bonus: killing someone with better equipment than your own pays extra, and dying to
  someone with significantly better equipment pays a consolation bonus.
- Equipment downgrade: dying repeatedly against better equipped players lowers the downgrade score, which
  can downgrade equipment again.
- Adaptive armor has a damage shield that recharges over time and can be refilled instantly by eating a
  golden apple (+8) or an enchanted golden apple (full).
- Shields (part of the armor equipment) can be reloaded by pressing the off-hand swap key (F).
- Riptide tridents have a cooldown, riptide only works in rain/water. Players can disable their personal
  weather, which also disables riptide for them.
- Killers get a short regeneration effect.
- Hunger is disabled (food level and saturation are kept full), but the natural regeneration rate is slowed
  down to one heart per 5 seconds.
- Kills, deaths, assists and the combat tracker are shown on the scoreboard and in the action bar.

## Notes

The plugin is still a testing plugin. A lot of balancing values (kill payouts, upgrade prices, armor values)
are hardcoded constants and not configurable yet.
