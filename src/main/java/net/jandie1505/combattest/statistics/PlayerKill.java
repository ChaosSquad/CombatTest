package net.jandie1505.combattest.statistics;

import java.util.UUID;

public class PlayerKill {
    private final UUID killedPlayer;
    private final UUID killedBy;
    private final int killedPlayersEquipment;
    private final int killerEquipment;

    public PlayerKill(UUID killedPlayer, UUID killedBy, int killedPlayersEquipment, int killerEquipment) {
        this.killedPlayer = killedPlayer;
        this.killedBy = killedBy;
        this.killedPlayersEquipment = killedPlayersEquipment;
        this.killerEquipment = killerEquipment;
    }

    public UUID getKilledPlayer() {
        return killedPlayer;
    }

    public UUID getKilledBy() {
        return killedBy;
    }

    public int getKilledPlayersEquipment() {
        return killedPlayersEquipment;
    }

    public int getKillerEquipment() {
        return killerEquipment;
    }

}
