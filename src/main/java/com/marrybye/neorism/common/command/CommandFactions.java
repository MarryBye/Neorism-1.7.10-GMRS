package com.marrybye.neorism.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.marrybye.neorism.api.faction.IPlayableFaction;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.skills.SkillHandler;
import com.marrybye.neorism.common.skills.SkillRegistry;
import com.marrybye.neorism.common.tileentity.TileEntityTotem;
import com.marrybye.neorism.common.util.ModLogger;

import de.teamlapen.faction.api.factions.IPlayableFaction.TitleGender;
import de.teamlapen.faction.api.factions.skills.ISkill;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;

/**
 * Canonical /factions (alias /f) command.
 * Ports TeamLapen FactionAPI FactionCommand, SkillCommand, ResetActionsCommand, GenderCommand,
 * VillageCommand, and Vampirism DraculaCommand/MarshallCommand to Forge 1.7.10.
 */
public class CommandFactions extends CommandBase {

    public static final String[] FACTION_IDS = { "vampire", "hunter", "neutral" };
    public static final String[] NON_NEUTRAL_FACTION_IDS = { "vampire", "hunter" };

    @Override
    public String getCommandName() {
        return "factions";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("f");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "command.neorism.factions.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String sub = args[0].toLowerCase();
        if ("faction".equals(sub)) {
            handleFaction(sender, args);
        } else if ("skills".equals(sub)) {
            handleSkills(sender, args);
        } else if ("actions".equals(sub)) {
            handleActions(sender, args);
        } else if ("gender".equals(sub)) {
            handleGender(sender, args);
        } else if ("village".equals(sub)) {
            handleVillage(sender, args);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private void handleFaction(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        String action = args[1].toLowerCase();
        if ("get".equals(action)) {
            EntityPlayerMP target = resolveTargetPlayer(sender, args, 2);
            FactionPlayerHandler handler = FactionPlayerHandler.get(target);
            if (handler == null) {
                throw new CommandException("command.vampirism.failed_to_execute");
            }
            ModLogger.leveling("Command /factions faction get executed by %s for %s (faction=%s, level=%d, lord=%d)",
                sender.getCommandSenderName(), target.getCommandSenderName(),
                handler.getFaction().getId(), handler.getCurrentLevel(), handler.getLordLevel());
            func_152373_a(sender, this, "command.factionapi.base.get_level.successful",
                target.func_145748_c_(),
                new ChatComponentTranslation(handler.getFaction().getUnlocalizedName()),
                Integer.valueOf(handler.getCurrentLevel()),
                Integer.valueOf(handler.getLordLevel()));
            return;
        }

        if (!"set".equals(action) || args.length < 3) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String factionArg = args[2].toLowerCase();
        if ("dracula".equals(factionArg)) {
            EntityPlayerMP target = resolveTargetPlayer(sender, args, 3);
            applyLordLevel(sender, this, target, ModFactions.VAMPIRE, ModFactions.VAMPIRE.getMaxLordLevel(), "command.vampirism.base.dracula.success");
            return;
        }
        if ("marshall".equals(factionArg)) {
            EntityPlayerMP target = resolveTargetPlayer(sender, args, 3);
            applyLordLevel(sender, this, target, ModFactions.HUNTER, ModFactions.HUNTER.getMaxLordLevel(), "command.vampirism.base.marshall.success");
            return;
        }

        IPlayableFaction faction = parseFaction(args[2]);
        if (faction == null) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (args.length == 3) {
            EntityPlayerMP target = getCommandSenderAsPlayer(sender);
            applyFactionLevel(sender, this, target, faction, faction.isNeutral() ? 0 : 1);
            return;
        }

        String mode = args[3].toLowerCase();
        if ("dracula".equals(mode)) {
            EntityPlayerMP target = resolveTargetPlayer(sender, args, 4);
            if (!ModFactions.VAMPIRE.equals(faction)) {
                throw new CommandException("command.factionapi.base.lord.failed", target.getCommandSenderName());
            }
            applyLordLevel(sender, this, target, ModFactions.VAMPIRE, ModFactions.VAMPIRE.getMaxLordLevel(), "command.vampirism.base.dracula.success");
        } else if ("marshall".equals(mode)) {
            EntityPlayerMP target = resolveTargetPlayer(sender, args, 4);
            if (!ModFactions.HUNTER.equals(faction)) {
                throw new CommandException("command.factionapi.base.lord.failed", target.getCommandSenderName());
            }
            applyLordLevel(sender, this, target, ModFactions.HUNTER, ModFactions.HUNTER.getMaxLordLevel(), "command.vampirism.base.marshall.success");
        } else if ("level".equals(mode)) {
            if (args.length == 4) {
                EntityPlayerMP target = getCommandSenderAsPlayer(sender);
                applyFactionLevel(sender, this, target, faction, faction.isNeutral() ? 0 : 1);
            } else if (isInteger(args[4])) {
                int num = parseInt(sender, args[4]);
                EntityPlayerMP target = resolveTargetPlayer(sender, args, 5);
                int level = faction.isNeutral() ? 0 : Math.max(1, num);
                applyFactionLevel(sender, this, target, faction, level);
            } else {
                EntityPlayerMP target = getPlayer(sender, args[4]);
                applyFactionLevel(sender, this, target, faction, faction.isNeutral() ? 0 : 1);
            }
        } else if ("lord".equals(mode)) {
            if (args.length == 4) {
                EntityPlayerMP target = getCommandSenderAsPlayer(sender);
                applyLordLevel(sender, this, target, faction, 1, null);
            } else if (isInteger(args[4])) {
                int num = parseInt(sender, args[4]);
                EntityPlayerMP target = resolveTargetPlayer(sender, args, 5);
                applyLordLevel(sender, this, target, faction, num, null);
            } else {
                EntityPlayerMP target = getPlayer(sender, args[4]);
                applyLordLevel(sender, this, target, faction, 1, null);
            }
        } else if (isInteger(args[3])) {
            int num = parseInt(sender, args[3]);
            EntityPlayerMP target = resolveTargetPlayer(sender, args, 4);
            int level = faction.isNeutral() ? 0 : Math.max(1, num);
            applyFactionLevel(sender, this, target, faction, level);
        } else {
            EntityPlayerMP target = getPlayer(sender, args[3]);
            applyFactionLevel(sender, this, target, faction, faction.isNeutral() ? 0 : 1);
        }
    }

    private void handleSkills(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String op = args[1].toLowerCase();
        if (!"enable".equals(op) && !"disable".equals(op) && !"toggle".equals(op)) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String skillArg = args[2];
        boolean force = false;
        EntityPlayerMP target = null;

        for (int i = 3; i < args.length; i++) {
            if ("force".equalsIgnoreCase(args[i])) {
                force = true;
            } else if (target == null) {
                target = getPlayer(sender, args[i]);
            }
        }
        if (target == null) {
            target = getCommandSenderAsPlayer(sender);
        }

        FactionPlayerHandler fph = FactionPlayerHandler.get(target);
        SkillHandler sh = SkillHandler.get(target);
        if (fph == null || fph.isNeutral() || sh == null) {
            throw new CommandException("command.factionapi.test.skill.noinfaction");
        }

        if ("all".equalsIgnoreCase(skillArg)) {
            if ("disable".equals(op)) {
                sh.resetSkills();
                ModLogger.leveling("Command /factions skills disable all executed by %s for %s",
                    sender.getCommandSenderName(), target.getCommandSenderName());
                func_152373_a(sender, this, "command.factionapi.test.skill.all_locked", target.func_145748_c_());
            } else if ("enable".equals(op)) {
                sh.forceUnlockAllSkills(SkillRegistry.getSkillsForFaction(fph.getCurrentFaction()));
                ModLogger.leveling("Command /factions skills enable all executed by %s for %s (faction=%s)",
                    sender.getCommandSenderName(), target.getCommandSenderName(), fph.getCurrentFaction().getId());
                func_152373_a(sender, this, "command.factionapi.test.skill.all_unlocked", target.func_145748_c_());
            } else {
                throw new WrongUsageException(getCommandUsage(sender));
            }
            return;
        }

        ISkill skill = SkillRegistry.getSkill(skillArg);
        if (skill == null) {
            throw new CommandException("command.factionapi.test.skill.notfound", skillArg);
        }
        if (skill.getFaction() != null && !skill.getFaction().equals(fph.getCurrentFaction())) {
            throw new CommandException("command.factionapi.test.skill.otherfaction", skillArg);
        }

        if ("disable".equals(op)) {
            if (sh.forgetSkill(skillArg)) {
                ModLogger.leveling("Command /factions skills disable %s executed by %s for %s",
                    skillArg, sender.getCommandSenderName(), target.getCommandSenderName());
                func_152373_a(sender, this, "command.factionapi.test.skill.disabled", skillArg);
            } else {
                throw new CommandException("command.factionapi.test.skill.not_enabled", skillArg);
            }
        } else if ("toggle".equals(op)) {
            if (sh.isSkillUnlocked(skillArg)) {
                if (sh.forgetSkill(skillArg)) {
                    ModLogger.leveling("Command /factions skills toggle (off) %s executed by %s for %s",
                        skillArg, sender.getCommandSenderName(), target.getCommandSenderName());
                    func_152373_a(sender, this, "command.factionapi.test.skill.disabled", skillArg);
                } else {
                    throw new CommandException("command.factionapi.test.skill.not_enabled", skillArg);
                }
            } else {
                enableSingleSkill(sender, target, sh, skill, force);
            }
        } else {
            enableSingleSkill(sender, target, sh, skill, force);
        }
    }

    private void enableSingleSkill(ICommandSender sender, EntityPlayerMP target, SkillHandler sh, ISkill skill, boolean force) {
        String skillId = skill.getSkillId();
        if (sh.isSkillUnlocked(skillId)) {
            func_152373_a(sender, this, "command.factionapi.test.skill.alreadyenabled", skillId);
            return;
        }
        if (force) {
            if (sh.forceUnlockSkill(skillId)) {
                ModLogger.leveling("Command /factions skills enable %s (force=true) executed by %s for %s",
                    skillId, sender.getCommandSenderName(), target.getCommandSenderName());
                func_152373_a(sender, this, "command.factionapi.test.skill.enabled", skillId);
            } else {
                throw new CommandException("command.vampirism.failed_to_execute");
            }
            return;
        }

        if (sh.unlockSkill(skillId)) {
            ModLogger.leveling("Command /factions skills enable %s (force=false) executed by %s for %s",
                skillId, sender.getCommandSenderName(), target.getCommandSenderName());
            func_152373_a(sender, this, "command.factionapi.test.skill.enabled", skillId);
        } else if (sh.getSkillPoints() < skill.getSkillPointCost()) {
            throw new CommandException("command.factionapi.test.skill.nopoints", skillId);
        } else {
            throw new CommandException("command.factionapi.test.skill.parents.disabled", skillId);
        }
    }

    private void handleActions(ICommandSender sender, String[] args) {
        if (args.length < 2 || !"reset".equalsIgnoreCase(args[1])) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        EntityPlayerMP target = resolveTargetPlayer(sender, args, 2);
        ActionHandler ah = ActionHandler.get(target);
        if (ah != null) {
            ah.resetTimers();
        }
        ModLogger.leveling("Command /factions actions reset executed by %s for %s",
            sender.getCommandSenderName(), target.getCommandSenderName());
        func_152373_a(sender, this, "command.factionapi.test.resetactions", target.func_145748_c_());
    }

    private void handleGender(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        String gStr = args[1].toLowerCase();
        TitleGender gender;
        if ("male".equals(gStr)) {
            gender = TitleGender.MALE;
        } else if ("female".equals(gStr)) {
            gender = TitleGender.FEMALE;
        } else if ("unknown".equals(gStr)) {
            gender = TitleGender.UNKNOWN;
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        EntityPlayerMP target = resolveTargetPlayer(sender, args, 2);
        FactionPlayerHandler fph = FactionPlayerHandler.get(target);
        if (fph == null) {
            throw new CommandException("command.vampirism.failed_to_execute");
        }
        fph.setTitleGender(gender);
        ModLogger.debug("Command /factions gender %s executed by %s for %s",
            gender.getName(), sender.getCommandSenderName(), target.getCommandSenderName());
        func_152373_a(sender, this, "command.factionapi.base.gender.success", gender.getName());
    }

    private void handleVillage(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        ChunkCoordinates pos = sender.getPlayerCoordinates();
        TileEntityTotem totem = TileEntityTotem.getTotemNear(
            sender.getEntityWorld(), pos.posX + 0.5D, pos.posY + 0.5D, pos.posZ + 0.5D, 64.0D
        );
        if (totem == null) {
            throw new CommandException("command.factionapi.test.village.no_village");
        }

        String op = args[1].toLowerCase();
        if ("abort".equals(op)) {
            totem.breakCapture();
            ModLogger.debug("Command /factions village abort executed by %s at totem [%d, %d, %d]",
                sender.getCommandSenderName(), totem.xCoord, totem.yCoord, totem.zCoord);
            func_152373_a(sender, this, "command.factionapi.test.village.abort");
        } else if ("capture".equals(op)) {
            if (args.length < 3) {
                throw new WrongUsageException(getCommandUsage(sender));
            }
            IPlayableFaction faction = parseFaction(args[2]);
            if (faction == null) {
                throw new WrongUsageException(getCommandUsage(sender));
            }
            int fId = TileEntityTotem.getIdByFaction(faction);
            totem.forceFaction(fId);
            ModLogger.debug("Command /factions village capture %s executed by %s at totem [%d, %d, %d]",
                faction.getId(), sender.getCommandSenderName(), totem.xCoord, totem.yCoord, totem.zCoord);
            func_152373_a(sender, this, "command.factionapi.test.village.success",
                new ChatComponentTranslation(faction.getUnlocalizedName()));
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "faction", "skills", "actions", "gender", "village");
        }
        String sub = args[0].toLowerCase();
        if ("faction".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "set", "get");
            }
            if ("get".equalsIgnoreCase(args[1]) && args.length == 3) {
                return getListOfStringsMatchingLastWord(args, getOnlinePlayerNames());
            }
            if ("set".equalsIgnoreCase(args[1])) {
                if (args.length == 3) {
                    return getListOfStringsMatchingLastWord(args, "vampire", "hunter", "neutral", "dracula", "marshall");
                }
                if (args.length == 4) {
                    if ("dracula".equalsIgnoreCase(args[2]) || "marshall".equalsIgnoreCase(args[2])) {
                        return getListOfStringsMatchingLastWord(args, getOnlinePlayerNames());
                    }
                    List<String> options = new ArrayList<String>();
                    options.add("level");
                    options.add("lord");
                    options.add("dracula");
                    options.add("marshall");
                    Collections.addAll(options, getOnlinePlayerNames());
                    return getListOfStringsFromIterableMatchingLastWord(args, options);
                }
                if (args.length == 5) {
                    if ("level".equalsIgnoreCase(args[3]) || "lord".equalsIgnoreCase(args[3])
                        || "dracula".equalsIgnoreCase(args[3]) || "marshall".equalsIgnoreCase(args[3])
                        || isInteger(args[3])) {
                        return getListOfStringsMatchingLastWord(args, getOnlinePlayerNames());
                    }
                    return null;
                }
                if (args.length == 6
                    && ("level".equalsIgnoreCase(args[3]) || "lord".equalsIgnoreCase(args[3]))
                    && isInteger(args[4])) {
                    return getListOfStringsMatchingLastWord(args, getOnlinePlayerNames());
                }
            }
        } else if ("skills".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "enable", "disable", "toggle");
            }
            if (args.length == 3) {
                List<String> options = new ArrayList<String>();
                if (!"toggle".equalsIgnoreCase(args[1])) {
                    options.add("all");
                }
                for (ISkill s : SkillRegistry.getAllSkills()) {
                    options.add(s.getSkillId());
                }
                return getListOfStringsFromIterableMatchingLastWord(args, options);
            }
            if (args.length == 4) {
                List<String> options = new ArrayList<String>();
                Collections.addAll(options, getOnlinePlayerNames());
                if (!"disable".equalsIgnoreCase(args[1])) {
                    options.add("force");
                }
                return getListOfStringsFromIterableMatchingLastWord(args, options);
            }
            if (args.length == 5) {
                if ("force".equalsIgnoreCase(args[3])) {
                    return getListOfStringsMatchingLastWord(args, getOnlinePlayerNames());
                }
                if (!"disable".equalsIgnoreCase(args[1])) {
                    return getListOfStringsMatchingLastWord(args, "force");
                }
            }
        } else if ("actions".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "reset");
            }
            if (args.length == 3) {
                return getListOfStringsMatchingLastWord(args, getOnlinePlayerNames());
            }
        } else if ("gender".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "male", "female", "unknown");
            }
            if (args.length == 3) {
                return getListOfStringsMatchingLastWord(args, getOnlinePlayerNames());
            }
        } else if ("village".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "capture", "abort");
            }
            if (args.length == 3 && "capture".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, FACTION_IDS);
            }
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if (args.length < 2) return false;
        String sub = args[0].toLowerCase();
        if ("faction".equals(sub) && "get".equalsIgnoreCase(args[1])) return index == 2;
        if ("faction".equals(sub) && "set".equalsIgnoreCase(args[1])) {
            if (args.length >= 3 && ("dracula".equalsIgnoreCase(args[2]) || "marshall".equalsIgnoreCase(args[2]))) {
                return index == 3;
            }
            if (args.length >= 4) {
                String mode = args[3].toLowerCase();
                if ("dracula".equals(mode) || "marshall".equals(mode) || isInteger(args[3])) {
                    return index == 4;
                }
                if ("level".equals(mode) || "lord".equals(mode)) {
                    return (args.length >= 5 && isInteger(args[4])) ? index == 5 : index == 4;
                }
                return index == 3;
            }
            return false;
        }
        if ("skills".equals(sub)) {
            return (args.length > 3 && "force".equalsIgnoreCase(args[3])) ? index == 4 : index == 3;
        }
        if ("actions".equals(sub)) return index == 2;
        if ("gender".equals(sub)) return index == 2;
        return false;
    }

    // --- Shared Static Helpers for Command Package ---

    public static IPlayableFaction parseFaction(String input) {
        if (input == null) return null;
        String key = input.trim().toLowerCase();
        int colon = key.indexOf(':');
        if (colon >= 0) {
            String ns = key.substring(0, colon);
            if (!"vampirism".equals(ns) && !"neorism".equals(ns) && !"factionapi".equals(ns)) {
                return null;
            }
            key = key.substring(colon + 1);
        }
        if ("vampire".equals(key)) return ModFactions.VAMPIRE;
        if ("hunter".equals(key)) return ModFactions.HUNTER;
        if ("neutral".equals(key) || "none".equals(key)) return ModFactions.NEUTRAL;
        return null;
    }

    public static void applyFactionLevel(ICommandSender sender, CommandBase cmd, EntityPlayerMP target,
                                         IPlayableFaction faction, int requestedLevel) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(target);
        if (handler == null) {
            throw new CommandException("command.vampirism.failed_to_execute");
        }
        IPlayableFaction oldFaction = handler.getCurrentFaction();
        int finalLevel = faction.isNeutral()
            ? 0
            : Math.max(0, Math.min(requestedLevel, faction.getMaxLevel()));

        handler.setFaction(faction, finalLevel);
        if ((!faction.equals(oldFaction) || finalLevel < faction.getMaxLevel()) && handler.getLordLevel() > 0) {
            handler.setLordLevel(0);
        }

        ModLogger.leveling("Command /%s executed by %s: set %s to faction=%s level=%d",
            cmd.getCommandName(), sender.getCommandSenderName(),
            target.getCommandSenderName(), faction.getId(), finalLevel);

        func_152373_a(sender, cmd, "command.factionapi.base.level.successful",
            target.func_145748_c_(),
            new ChatComponentTranslation(faction.getUnlocalizedName()),
            Integer.valueOf(finalLevel));
    }

    public static void applyLordLevel(ICommandSender sender, CommandBase cmd, EntityPlayerMP target,
                                      IPlayableFaction faction, int requestedLordLevel, String customSuccessKey) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(target);
        if (handler == null || faction == null || faction.isNeutral() || faction.getMaxLordLevel() <= 0) {
            throw new CommandException("command.factionapi.base.lord.failed", target.getCommandSenderName());
        }
        int maxNormal = faction.getMaxLevel();
        int finalLord = Math.max(0, Math.min(requestedLordLevel, faction.getMaxLordLevel()));

        handler.setFaction(faction, maxNormal);
        handler.setLordLevel(finalLord);

        ModLogger.leveling("Command /%s executed by %s: set %s to faction=%s level=%d lordLevel=%d",
            cmd.getCommandName(), sender.getCommandSenderName(),
            target.getCommandSenderName(), faction.getId(), maxNormal, finalLord);

        if (customSuccessKey != null) {
            func_152373_a(sender, cmd, customSuccessKey, target.func_145748_c_());
        } else {
            func_152373_a(sender, cmd, "command.factionapi.base.lord.successful",
                target.func_145748_c_(),
                new ChatComponentTranslation(faction.getUnlocalizedName()),
                Integer.valueOf(finalLord));
        }
    }

    public static EntityPlayerMP resolveTargetPlayer(ICommandSender sender, String[] args, int index) {
        if (args.length > index) {
            return getPlayer(sender, args[index]);
        }
        return getCommandSenderAsPlayer(sender);
    }

    public static boolean isInteger(String s) {
        if (s == null || s.isEmpty()) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String[] getOnlinePlayerNames() {
        MinecraftServer server = MinecraftServer.getServer();
        return server != null ? server.getAllUsernames() : new String[0];
    }
}
