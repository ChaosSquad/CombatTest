package net.jandie1505.combattest;

import net.chaossquad.mclib.dynamicevents.EventListenerManager;
import net.chaossquad.mclib.executable.CoreExecutable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class GamePart extends CoreExecutable {
    private final CombatTest plugin;

    public GamePart(@NotNull CombatTest plugin) {
        super(plugin.getListenerManager(), plugin.getLogger());
        this.plugin = plugin;
    }

    public abstract boolean addPlayer(Player player);
    public abstract boolean removePlayer(UUID playerId);
    public abstract List<UUID> getPlayers();
    public abstract List<UUID> getPlayers(UUID[] playerIds);

}
