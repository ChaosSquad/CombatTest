package net.jandie1505.combattest.game.base.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.combattest.game.base.GamePart;
import net.jandie1505.combattest.game.base.commands.players.GamePlayersAddSubcommand;
import net.jandie1505.combattest.game.base.commands.players.GamePlayersListSubcommand;
import net.jandie1505.combattest.game.base.commands.players.GamePlayersRemoveSubcommand;
import org.jetbrains.annotations.NotNull;

public class GamePlayersSubcommand extends SubcommandCommand {
    @NotNull private final GamePart game;

    public GamePlayersSubcommand(@NotNull GamePart game) {
        super(game.getPlugin());
        this.game = game;

        this.addSubcommand("add", SubcommandEntry.of(new GamePlayersAddSubcommand(this.game)));
        this.addSubcommand("remove", SubcommandEntry.of(new GamePlayersRemoveSubcommand(this.game)));
        this.addSubcommand("list", SubcommandEntry.of(new GamePlayersListSubcommand(this.game)));
    }

}
