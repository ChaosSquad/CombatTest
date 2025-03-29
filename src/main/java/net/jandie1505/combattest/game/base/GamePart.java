package net.jandie1505.combattest.game.base;

import net.chaossquad.mclib.command.DynamicSubcommandProvider;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.executable.CoreExecutable;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.game.base.commands.GamePlayersSubcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class GamePart extends CoreExecutable implements DynamicSubcommandProvider {
    @NotNull private final CombatTest plugin;
    @NotNull private final Map<String, SubcommandEntry> subcommands;

    public GamePart(@NotNull CombatTest plugin) {
        super(plugin.getListenerManager(), plugin.getLogger());
        this.plugin = plugin;
        this.subcommands = new HashMap<>();

        this.subcommands.put("players", SubcommandEntry.of(new GamePlayersSubcommand(this)));
    }

    public abstract boolean addPlayer(Player player);

    public abstract boolean removePlayer(UUID playerId);

    /**
     * Returns a set of all uuids of the ingame players.
     * @return uuids of registered players
     */
    public abstract Set<UUID> getRegisteredPlayers();

    @Deprecated(forRemoval = true)
    public final List<UUID> getPlayers() {
        return List.copyOf(this.getRegisteredPlayers());
    }

    @Deprecated(forRemoval = true)
    public final List<UUID> getPlayers(UUID[] playerIds) {
        List<UUID> returnList = new ArrayList<>();

        for (UUID playerId : playerIds) {

            if (this.getRegisteredPlayers().contains(playerId)) {
                returnList.add(playerId);
            }

        }

        return List.copyOf(returnList);
    }

    /**
     * Returns a set of all ingame players that are currently online.
     * @return online registered players
     */
    public final Set<Player> getOnlinePlayers() {
        return this.getRegisteredPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public final Map<String, SubcommandEntry> getDynamicSubcommands() {
        return this.subcommands;
    }

    public final @NotNull CombatTest getPlugin() {
        return this.plugin;
    }

}
