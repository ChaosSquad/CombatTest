package net.jandie1505.combattest.game;

import net.jandie1505.combattest.CombatTest;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Endlobby implements GamePart {
    private final CombatTest plugin;

    public Endlobby(CombatTest plugin) {
        this.plugin = plugin;
    }


    @Override
    public int tick() {
        return GameStatus.NEXT;
    }

    @Override
    public GamePart getNextStatus() {
        return null;
    }

    @Override
    public boolean addPlayer(Player player) {
        return false;
    }

    @Override
    public boolean removePlayer(UUID playerId) {
        return false;
    }

    @Override
    public List<UUID> getPlayers() {
        return null;
    }

    @Override
    public List<UUID> getPlayers(UUID[] playerIds) {
        return null;
    }
}
