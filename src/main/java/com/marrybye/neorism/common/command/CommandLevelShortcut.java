package com.marrybye.neorism.common.command;

import java.util.List;

import com.marrybye.neorism.api.faction.IPlayableFaction;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Canonical /level <vampire|hunter|neutral> [level] [player] dev/admin shortcut.
 * Ports TeamLapen FactionAPI FactionCommand.registerDev("/level").
 * Defaults to the faction's max normal level (14 for vampire/hunter, 0 for neutral) when [level] is omitted.
 */
public class CommandLevelShortcut extends CommandBase {

    @Override
    public String getCommandName() {
        return "level";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "command.neorism.level.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        IPlayableFaction faction = CommandFactions.parseFaction(args[0]);
        if (faction == null) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        int level = faction.getMaxLevel(); // 14 for vampire/hunter, 0 for neutral
        EntityPlayerMP target;
        if (args.length >= 2) {
            if (CommandFactions.isInteger(args[1])) {
                level = parseInt(sender, args[1]);
                target = CommandFactions.resolveTargetPlayer(sender, args, 2);
            } else {
                target = getPlayer(sender, args[1]);
            }
        } else {
            target = getCommandSenderAsPlayer(sender);
        }

        CommandFactions.applyFactionLevel(sender, this, target, faction, level);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, CommandFactions.FACTION_IDS);
        }
        if (args.length == 2 || args.length == 3) {
            return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1 || index == 2;
    }
}
