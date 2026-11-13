package com.marrybye.neorism.common.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

/**
 * Canonical /lord [vampire|hunter] [level] [player] dev/admin shortcut.
 * Ports TeamLapen FactionAPI FactionCommand.registerDev("/lord").
 * Sets faction to max normal level (14) and lord level (1..5), defaulting to current faction and lord level 5.
 */
public class CommandLordShortcut extends CommandBase {

    @Override
    public String getCommandName() {
        return "lord";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "command.neorism.lord.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        CommandVampirism.processLordArgs(sender, this, args, 0);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return CommandVampirism.getLordTabCompletions(args, 0);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == CommandVampirism.getLordUsernameIndex(args, 0);
    }
}
