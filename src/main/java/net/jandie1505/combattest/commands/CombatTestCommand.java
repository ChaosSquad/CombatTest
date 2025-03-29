package net.jandie1505.combattest.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.combattest.CombatTest;
import net.jandie1505.combattest.commands.subcommands.*;
import net.jandie1505.combattest.game.base.GamePart;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CombatTestCommand extends SubcommandCommand {
    @NotNull private final CombatTest plugin;

    public CombatTestCommand(@NotNull CombatTest plugin) {
        super(plugin, (PermissionProvider) null, () -> {
            GamePart game = plugin.getGame();
            if (game == null) return Map.of();
            return game.getDynamicSubcommands();
        });
        this.plugin = plugin;

        this.addSubcommand("stop", SubcommandEntry.of(new CombatTestStopSubcommand(this.plugin)));
        this.addSubcommand("status", SubcommandEntry.of(new CombatTestStatusSubcommand(this.plugin)));
        this.addSubcommand("start", SubcommandEntry.of(new CombatTestStartSubcommand(this.plugin)));
        this.addSubcommand("bypass", SubcommandEntry.of(new CombatTestBypassSubcommand(this.plugin)));
        this.addSubcommand("settings", SubcommandEntry.of(new SettingsSubcommand(this.plugin)));
        this.addSubcommand("worlds", SubcommandEntry.of(new WorldsSubcommand()));
    }

}
