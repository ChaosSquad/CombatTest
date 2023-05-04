package net.jandie1505.combattest.game;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface GamePart {
    int tick();
    GamePart getNextStatus();
    boolean addPlayer(Player player);
    boolean removePlayer(UUID playerId);
    List<UUID> getPlayers();
    List<UUID> getPlayers(UUID[] playerIds);
}
