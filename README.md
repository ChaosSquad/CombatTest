# ChaosSquad CombatTest
This plugin is for testing a combat system of a project without revealing the project :)

## Requirements
- 1.19
- Spigot or Paper Server (Bukkit not supported)
- You are willing to enter all the spawn points manually in the Config

## How to setup
1. Have a Spigot / Paper Server
2. Shut it down (if it is running)
3. Put the plugin jar into the plugins directory
4. Start the server
5. Get all coordinates for spawn points and world border and enter them in config
6. Change config values to what you want
7. Reload plugin config with `/combattest reload`
8. Start a game or do whatever you want

## Permission
For playing the game, no permission is required (except for joining when single server mode is off).  
The admin permission is `<permission prefix>.admin` (`combattest.admin` if it wasn't changed in config).

## Commands
Most of the commands are self-explanatory.
| Command | Description | Who can run it |
|--|--|--|
| /combattest | Show help page | Everyone (admin sees more) |
| /combattest stop | Stop running game | Admin |
| /combattest start (When no game running) | Start new game  | Admin |
| /combattest start (When lobby running) | Start lobby (force start) | Admin |
| /combattest status | See current game status + time if available | Admin |
| /combattest get<melee/ranged/armor/points/team/time> | Get specified value | Admin |
| /combattest set<melee/ranged/armor/points/team/time> | Set specified value | Admin |
| /combattest isautostart | See if autostart new game is enabled | Admin |
| /combattest | setautostart | Set autostart new game true/false | Admin |
| /combattest addplayer/removeplayer/getplayers | Manage players | Admin |
| /combattest reload | Reload config | Admin |
| /combattest menu | Open player menu | Ingame |
| /combattest points | See own points | Ingame |
| /combattest leave | Leave game | Ingame |
| /combattest stats | Show player stats | Ingame |

## Config
| Option | Description                                                                                                                                                                       |
|--|--|
| `permissionsPrefix` | Change permission prefix: `<PREFIX>.<PERMISSION>`                                                                                                                                 |
| `singleServerMode` | All players will be added to the game during lobby.<br/>All players will be set spectator when joining during ingame state.<br/>Enables scoreboard and tablist management<br/>... |
| `autostartNewGame` | Set if a new game should be started (after 30 seconds) if no game is running                                                                                                      |
| `world` | World of the game                                                                                                                                                                 |
| `spawnpoints` | Points where the players can respawn                                                                                                                                              |
| `border` | World border (ingame players cannot leave the border)                                                                                                                             |
| `time` | Duration of a game                                                                                                                                                                |
| `enforcepvp` | Players which don't fight for 60 seconds will get glowing                                                                                                                         |

## Equipment Scores
- Melee: 0; 100-102; 200-202; 300-302; 1100-1103; 1200-1203; 1300-1303; 1400-1403; 1500-1503; 1600-1603
- Ranged: Same as melee but for the maximum values one less (100-101; ...)
- Armor: 0; 100-102; 1100-1103; 1200-1203

## End
Hopefully that was all...
