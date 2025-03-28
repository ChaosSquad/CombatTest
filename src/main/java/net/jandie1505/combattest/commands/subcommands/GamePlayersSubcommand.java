package net.jandie1505.combattest.commands.subcommands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.combattest.GamePart;
import net.jandie1505.combattest.commands.subcommands.players.GamePlayersAddSubcommand;
import net.jandie1505.combattest.commands.subcommands.players.GamePlayersListSubcommand;
import net.jandie1505.combattest.commands.subcommands.players.GamePlayersRemoveSubcommand;
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
