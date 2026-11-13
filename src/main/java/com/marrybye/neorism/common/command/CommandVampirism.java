package com.marrybye.neorism.common.command;

import java.util.Arrays;
import java.util.List;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.api.faction.IPlayableFaction;
import com.marrybye.neorism.common.entity.vampire.EntityDracula;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.items.ItemVampireSword;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.BloodStats;
import com.marrybye.neorism.common.vampire.VampirePlayer;
import com.marrybye.neorism.common.world.dimensions.velmorra.DraculaFightManager;
import com.marrybye.neorism.common.world.dimensions.velmorra.TeleporterVelmorra;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Canonical /vampirism (aliases /v, /neorism) command.
 * Consolidates BloodBarCommand, AppearanceCommand (Eye, Fang, GlowingEye),
 * VampireSwordCommand, HealCommand, VelmorraCommand, plus level/lord shortcuts.
 */
public class CommandVampirism extends CommandBase {

    @Override
    public String getCommandName() {
        return "vampirism";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("v", "neorism");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "command.neorism.vampirism.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String sub = args[0].toLowerCase();
        if ("level".equals(sub)) {
            handleLevel(sender, args);
        } else if ("lord".equals(sub)) {
            handleLord(sender, args);
        } else if ("bloodbar".equals(sub)) {
            handleBloodBar(sender, args);
        } else if ("appearance".equals(sub)) {
            handleAppearance(sender, args);
        } else if ("sword".equals(sub)) {
            handleSword(sender, args);
        } else if ("heal".equals(sub)) {
            handleHeal(sender, args);
        } else if ("velmorra".equals(sub)) {
            handleVelmorra(sender, args);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private void handleLevel(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        IPlayableFaction faction = CommandFactions.parseFaction(args[1]);
        if (faction == null) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        int level = faction.isNeutral() ? 0 : 1;
        EntityPlayerMP target;
        if (args.length >= 3) {
            if (CommandFactions.isInteger(args[2])) {
                level = parseInt(sender, args[2]);
                target = CommandFactions.resolveTargetPlayer(sender, args, 3);
            } else {
                target = getPlayer(sender, args[2]);
            }
        } else {
            target = getCommandSenderAsPlayer(sender);
        }
        CommandFactions.applyFactionLevel(sender, this, target, faction, level);
    }

    private void handleLord(ICommandSender sender, String[] args) {
        processLordArgs(sender, this, args, 1);
    }

    /**
     * Shared lord argument parser for both `/vampirism lord [faction] [level] [player]`
     * and `/lord [faction] [level] [player]`.
     */
    public static void processLordArgs(ICommandSender sender, CommandBase cmd, String[] args, int startIdx) {
        IPlayableFaction faction = null;
        int lordLevel = 5;
        EntityPlayerMP target = null;

        if (args.length <= startIdx) {
            target = getCommandSenderAsPlayer(sender);
            FactionPlayerHandler fph = FactionPlayerHandler.get(target);
            faction = fph != null ? fph.getCurrentFaction() : null;
        } else {
            int idx = startIdx;
            IPlayableFaction parsed = CommandFactions.parseFaction(args[idx]);
            if (parsed != null) {
                faction = parsed;
                idx++;
            }
            if (idx < args.length && CommandFactions.isInteger(args[idx])) {
                lordLevel = parseInt(sender, args[idx]);
                idx++;
            }
            target = CommandFactions.resolveTargetPlayer(sender, args, idx);
            if (faction == null) {
                FactionPlayerHandler fph = FactionPlayerHandler.get(target);
                faction = fph != null ? fph.getCurrentFaction() : null;
            }
        }

        if (faction == null || faction.isNeutral() || faction.getMaxLordLevel() <= 0) {
            throw new CommandException("command.neorism.lord.no_faction",
                target != null ? target.getCommandSenderName() : sender.getCommandSenderName());
        }
        CommandFactions.applyLordLevel(sender, cmd, target, faction, lordLevel, null);
    }

    private void handleBloodBar(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        String mode = args[1].toLowerCase();
        EntityPlayerMP target;
        int amount;

        if ("fill".equals(mode)) {
            target = CommandFactions.resolveTargetPlayer(sender, args, 2);
            amount = Integer.MAX_VALUE;
        } else if ("empty".equals(mode)) {
            target = CommandFactions.resolveTargetPlayer(sender, args, 2);
            amount = 0;
        } else if ("set".equals(mode)) {
            if (args.length < 3) {
                throw new WrongUsageException(getCommandUsage(sender));
            }
            amount = parseInt(sender, args[2]);
            target = CommandFactions.resolveTargetPlayer(sender, args, 3);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (!Helper.isVampire(target)) {
            throw new CommandException("command.neorism.bloodbar.not_vampire", target.getCommandSenderName());
        }
        VampirePlayer vp = VampirePlayer.get(target);
        if (vp == null) {
            throw new CommandException("command.vampirism.failed_to_execute");
        }
        BloodStats stats = vp.getBloodStats();
        int clamped = Math.max(0, Math.min(amount, stats.getMaxBlood()));
        stats.setBloodLevel(clamped);
        vp.syncBlood();

        ModLogger.leveling("Command /vampirism bloodBar %s executed by %s for %s (blood=%d/%d)",
            mode, sender.getCommandSenderName(), target.getCommandSenderName(),
            stats.getBloodLevel(), stats.getMaxBlood());
        func_152373_a(sender, this, "command.neorism.bloodbar.success",
            target.func_145748_c_(), Integer.valueOf(stats.getBloodLevel()), Integer.valueOf(stats.getMaxBlood()));
    }

    private void handleAppearance(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        String feature = args[1].toLowerCase();
        EntityPlayerMP target = CommandFactions.resolveTargetPlayer(sender, args, 3);
        FactionPlayerHandler fph = FactionPlayerHandler.get(target);
        if (fph == null) {
            throw new CommandException("command.vampirism.failed_to_execute");
        }

        if ("eye".equals(feature)) {
            int raw = parseInt(sender, args[2]);
            int clamped = Math.max(0, Math.min(18, raw));
            fph.setEyeType(clamped);
            fph.sync();
            ModLogger.debug("Command /vampirism appearance eye %d executed by %s for %s",
                clamped, sender.getCommandSenderName(), target.getCommandSenderName());
            func_152373_a(sender, this, "command.vampirism.base.eye.success", Integer.valueOf(clamped));
        } else if ("fang".equals(feature)) {
            int raw = parseInt(sender, args[2]);
            int clamped = Math.max(0, Math.min(6, raw));
            fph.setFangType(clamped);
            fph.sync();
            ModLogger.debug("Command /vampirism appearance fang %d executed by %s for %s",
                clamped, sender.getCommandSenderName(), target.getCommandSenderName());
            func_152373_a(sender, this, "command.vampirism.base.fang.success", Integer.valueOf(clamped));
        } else if ("glowingeye".equals(feature)) {
            boolean on = parseBoolean(sender, args[2]);
            fph.setGlowingEyes(on);
            fph.sync();
            ModLogger.debug("Command /vampirism appearance glowingEye %b executed by %s for %s",
                on, sender.getCommandSenderName(), target.getCommandSenderName());
            func_152373_a(sender, this,
                on ? "command.vampirism.base.glowing_eyes.enabled" : "command.vampirism.base.glowing_eyes.disabled");
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private void handleSword(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        String mode = args[1].toLowerCase();
        if (!"charge".equals(mode) && !"trained".equals(mode)) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        float raw = (float) parseDouble(sender, args[2]);
        float clamped = Math.max(0.0F, Math.min(1.0F, raw));
        EntityPlayerMP target = CommandFactions.resolveTargetPlayer(sender, args, 3);
        ItemStack held = target.getHeldItem();

        if (held == null || !(held.getItem() instanceof ItemVampireSword)) {
            String key = "charge".equals(mode)
                ? "command.vampirism.test.swordcharged.nosword"
                : "command.vampirism.test.swordtrained.nosword";
            throw new CommandException(key);
        }

        ItemVampireSword sword = (ItemVampireSword) held.getItem();
        if ("charge".equals(mode)) {
            sword.setChargePercentage(held, clamped);
            target.inventory.markDirty();
            target.inventoryContainer.detectAndSendChanges();
            ModLogger.debug("Command /vampirism sword charge %.2f executed by %s for %s",
                clamped, sender.getCommandSenderName(), target.getCommandSenderName());
            func_152373_a(sender, this, "command.neorism.sword.charge.success",
                Integer.valueOf(Math.round(clamped * 100.0F)));
        } else {
            sword.setTrained(held, target, clamped);
            target.inventory.markDirty();
            target.inventoryContainer.detectAndSendChanges();
            ModLogger.debug("Command /vampirism sword trained %.2f executed by %s for %s",
                clamped, sender.getCommandSenderName(), target.getCommandSenderName());
            func_152373_a(sender, this, "command.neorism.sword.trained.success",
                Integer.valueOf(Math.round(clamped * 100.0F)));
        }
    }

    private void handleHeal(ICommandSender sender, String[] args) {
        EntityPlayerMP target = CommandFactions.resolveTargetPlayer(sender, args, 1);
        target.setHealth(target.getMaxHealth());
        target.getFoodStats().addStats(20, 20.0F);
        target.extinguish();

        if (Helper.isVampire(target)) {
            VampirePlayer vp = VampirePlayer.get(target);
            if (vp != null) {
                vp.getBloodStats().setBloodLevel(vp.getBloodStats().getMaxBlood());
                vp.syncBlood();
            }
        }

        ModLogger.debug("Command /vampirism heal executed by %s for %s",
            sender.getCommandSenderName(), target.getCommandSenderName());
        func_152373_a(sender, this, "command.neorism.heal.success", target.func_145748_c_());
    }

    private void handleVelmorra(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }
        String sub = args[1].toLowerCase();
        if ("tp".equals(sub)) {
            EntityPlayerMP target = CommandFactions.resolveTargetPlayer(sender, args, 2);
            MinecraftServer server = MinecraftServer.getServer();
            WorldServer velmorra = server != null ? server.worldServerForDimension(Config.dimensionVelmorraId) : null;
            if (velmorra == null) {
                throw new CommandException("command.vampirism.failed_to_execute");
            }

            if (target.dimension != Config.dimensionVelmorraId) {
                target.getEntityData().setInteger("VelmorraReturnX", (int) Math.floor(target.posX));
                target.getEntityData().setInteger("VelmorraReturnY", (int) Math.floor(target.posY));
                target.getEntityData().setInteger("VelmorraReturnZ", (int) Math.floor(target.posZ));
                server.getConfigurationManager().transferPlayerToDimension(
                    target, Config.dimensionVelmorraId, new TeleporterVelmorra(velmorra, 0.5D, 65.0D, -34.0D)
                );
            } else {
                target.playerNetServerHandler.setPlayerLocation(0.5D, 65.0D, -34.0D, target.rotationYaw, 0.0F);
            }

            ModLogger.debug("Command /vampirism velmorra tp executed by %s for %s (dim=%d)",
                sender.getCommandSenderName(), target.getCommandSenderName(), Config.dimensionVelmorraId);
            func_152373_a(sender, this, "command.neorism.velmorra.tp.success", target.func_145748_c_());
            return;
        }

        if ("fight".equals(sub)) {
            if (args.length < 3) {
                throw new WrongUsageException(getCommandUsage(sender));
            }
            World world = sender.getEntityWorld();
            if (world == null || world.provider.dimensionId != Config.dimensionVelmorraId) {
                throw new CommandException("command.neorism.velmorra.not_in_dim");
            }

            String fightOp = args[2].toLowerCase();
            if ("start".equals(fightOp)) {
                DraculaFightManager mgr = DraculaFightManager.get(world);
                if (mgr != null && mgr.debugStartFight()) {
                    ModLogger.debug("Command /vampirism velmorra fight start executed by %s", sender.getCommandSenderName());
                    func_152373_a(sender, this, "command.neorism.velmorra.fight.started");
                } else {
                    throw new CommandException("command.neorism.velmorra.fight.start_failed");
                }
            } else if ("phase".equals(fightOp)) {
                if (args.length < 4) {
                    throw new WrongUsageException(getCommandUsage(sender));
                }
                int phase = Math.max(1, Math.min(3, parseInt(sender, args[3])));
                EntityDracula dracula = findActiveDracula(world);
                if (dracula == null) {
                    throw new CommandException("command.neorism.velmorra.fight.no_dracula");
                }
                dracula.debugSetPhase(phase);
                ModLogger.debug("Command /vampirism velmorra fight phase %d executed by %s",
                    phase, sender.getCommandSenderName());
                func_152373_a(sender, this, "command.neorism.velmorra.fight.phase", Integer.valueOf(phase));
            } else {
                throw new WrongUsageException(getCommandUsage(sender));
            }
            return;
        }

        throw new WrongUsageException(getCommandUsage(sender));
    }

    @SuppressWarnings("unchecked")
    private EntityDracula findActiveDracula(World world) {
        AxisAlignedBB arenaBB = AxisAlignedBB.getBoundingBox(-64.0D, 0.0D, -64.0D, 64.0D, 256.0D, 64.0D);
        List<EntityDracula> list = world.getEntitiesWithinAABB(EntityDracula.class, arenaBB);
        for (EntityDracula d : list) {
            if (d != null && d.isEntityAlive()) {
                return d;
            }
        }
        return null;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "level", "lord", "bloodBar", "appearance", "sword", "heal", "velmorra");
        }
        String sub = args[0].toLowerCase();
        if ("level".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, CommandFactions.FACTION_IDS);
            }
            if (args.length == 3) {
                return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
            }
            if (args.length == 4 && CommandFactions.isInteger(args[2])) {
                return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
            }
        } else if ("lord".equals(sub)) {
            return getLordTabCompletions(args, 1);
        } else if ("bloodbar".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "fill", "empty", "set");
            }
            if (args.length == 3 && !"set".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
            }
            if (args.length == 4 && "set".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
            }
        } else if ("appearance".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "eye", "fang", "glowingEye");
            }
            if (args.length == 3 && "glowingeye".equalsIgnoreCase(args[1])) {
                return getListOfStringsMatchingLastWord(args, "true", "false");
            }
            if (args.length == 4) {
                return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
            }
        } else if ("sword".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "charge", "trained");
            }
            if (args.length == 4) {
                return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
            }
        } else if ("heal".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
            }
        } else if ("velmorra".equals(sub)) {
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, "tp", "fight");
            }
            if (args.length == 3) {
                if ("tp".equalsIgnoreCase(args[1])) {
                    return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
                }
                if ("fight".equalsIgnoreCase(args[1])) {
                    return getListOfStringsMatchingLastWord(args, "start", "phase");
                }
            }
            if (args.length == 4 && "fight".equalsIgnoreCase(args[1]) && "phase".equalsIgnoreCase(args[2])) {
                return getListOfStringsMatchingLastWord(args, "1", "2", "3");
            }
        }
        return null;
    }

    public static List getLordTabCompletions(String[] args, int startIdx) {
        int rel = args.length - startIdx;
        if (rel == 1) {
            java.util.List<String> options = new java.util.ArrayList<String>();
            java.util.Collections.addAll(options, CommandFactions.NON_NEUTRAL_FACTION_IDS);
            java.util.Collections.addAll(options, CommandFactions.getOnlinePlayerNames());
            return getListOfStringsFromIterableMatchingLastWord(args, options);
        }
        if (rel == 2 && (CommandFactions.parseFaction(args[startIdx]) != null || CommandFactions.isInteger(args[startIdx]))) {
            return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
        }
        if (rel == 3 && CommandFactions.parseFaction(args[startIdx]) != null && CommandFactions.isInteger(args[startIdx + 1])) {
            return getListOfStringsMatchingLastWord(args, CommandFactions.getOnlinePlayerNames());
        }
        return null;
    }

    public static int getLordUsernameIndex(String[] args, int startIdx) {
        int idx = startIdx;
        if (idx < args.length && CommandFactions.parseFaction(args[idx]) != null) {
            idx++;
        }
        if (idx < args.length && CommandFactions.isInteger(args[idx])) {
            idx++;
        }
        return idx;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if (args.length < 1) return false;
        String sub = args[0].toLowerCase();
        if ("heal".equals(sub)) return index == 1;
        if ("level".equals(sub)) return (args.length > 2 && !CommandFactions.isInteger(args[2])) ? index == 2 : index == 3;
        if ("lord".equals(sub)) return index == getLordUsernameIndex(args, 1);
        if ("bloodbar".equals(sub)) return args.length >= 2 && ("set".equalsIgnoreCase(args[1]) ? index == 3 : index == 2);
        if ("appearance".equals(sub)) return index == 3;
        if ("sword".equals(sub)) return index == 3;
        if ("velmorra".equals(sub) && args.length >= 2 && "tp".equalsIgnoreCase(args[1])) return index == 2;
        return false;
    }
}
