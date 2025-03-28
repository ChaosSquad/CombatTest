package net.jandie1505.combattest;

import net.chaossquad.mclib.command.DynamicSubcommandProvider;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.executable.CoreExecutable;
import net.jandie1505.combattest.commands.subcommands.GamePlayersSubcommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public abstract List<UUID> getPlayers();
    public abstract List<UUID> getPlayers(UUID[] playerIds);

    @Override
    public final Map<String, SubcommandEntry> getDynamicSubcommands() {
        return this.subcommands;
    }

    public final @NotNull CombatTest getPlugin() {
        return this.plugin;
    }

}
