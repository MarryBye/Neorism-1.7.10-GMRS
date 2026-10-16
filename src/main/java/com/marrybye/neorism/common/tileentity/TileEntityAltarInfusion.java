package com.marrybye.neorism.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;

/**
 * TileEntity for the Altar of Infusion.
 * Central node of the complex multiblock infusion ritual for vampire levels 5 through 14.
 */
public class TileEntityAltarInfusion extends TileEntityNeorism {

    public static final int RITUAL_DURATION = 100; // 5 seconds (100 ticks)

    private int state = 0; // 0 = IDLE, 1 = RUNNING
    private int ritualTicks = 0;
    private String activatingPlayerName = "";
    private int targetLevel = 0;

    private final List<TileEntityBloodPedestal> activePedestals = new ArrayList<TileEntityBloodPedestal>();

    public boolean isRunning() {
        return state == 1;
    }

    public int getRitualTicks() {
        return ritualTicks;
    }

    public boolean startRitual(EntityPlayer player) {
        if (worldObj.isRemote || state != 0 || player == null) return false;

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null || !Helper.isVampire(player)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.not_a_vampire"));
            return false;
        }

        int curLevel = fph.getCurrentLevel();
        if (curLevel < 4) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_infusion_too_low"));
            return false;
        }
        if (curLevel >= 14) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.vampire_max_level"));
            return false;
        }

        int next = curLevel + 1;
        int points = calculateStructurePoints();
        int reqPoints = getRequiredStructurePoints(next);

        if (points < reqPoints) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_infusion_need_points",
                reqPoints, points));
            return false;
        }

        List<TileEntityBloodPedestal> pedestals = findPedestals();
        if (!validateAndReserveCatalysts(next, pedestals)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_infusion_missing_items", next));
            return false;
        }

        // Start ritual
        this.state = 1;
        this.ritualTicks = 0;
        this.activatingPlayerName = player.getCommandSenderName();
        this.targetLevel = next;
        this.activePedestals.clear();
        this.activePedestals.addAll(pedestals);

        ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 1.0F, 0.8F);
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.altar_infusion_started", next));
        ModLogger.rituals("Infusion ritual started at [%d,%d,%d] for player %s (target lvl: %d, structure points: %d)",
            xCoord, yCoord, zCoord, activatingPlayerName, next, points);

        markForUpdate();
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (state == 1) {
            ritualTicks++;

            if (worldObj.isRemote) {
                // Client-side visual beams & particles
                for (TileEntityBloodPedestal ped : activePedestals) {
                    if (ped != null) {
                        double px = ped.xCoord + 0.5D;
                        double py = ped.yCoord + 0.8D;
                        double pz = ped.zCoord + 0.5D;
                        double dx = (xCoord + 0.5D) - px;
                        double dy = (yCoord + 1.0D) - py;
                        double dz = (zCoord + 0.5D) - pz;
                        worldObj.spawnParticle("reddust", px + dx * 0.5D, py + dy * 0.5D, pz + dz * 0.5D, 0, 0, 0);
                    }
                }
            } else {
                if (ritualTicks % 20 == 0) {
                    worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D,
                        "mob.wither.idle", 0.7F, 1.2F);
                }

                if (ritualTicks >= RITUAL_DURATION) {
                    completeRitual();
                }
            }
        }
    }

    private void completeRitual() {
        state = 0;
        ritualTicks = 0;

        EntityPlayer player = worldObj.getPlayerEntityByName(activatingPlayerName);
        if (player != null && Helper.isVampire(player)) {
            // Consume catalysts
            consumeCatalysts(targetLevel, activePedestals);

            FactionPlayerHandler fph = FactionPlayerHandler.get(player);
            if (fph != null) {
                fph.setFaction(ModFactions.VAMPIRE, targetLevel);
            }

            ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 1.2F, 1.2F);
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.vampire_leveled_up", targetLevel));
            ModLogger.rituals("Infusion ritual SUCCESS for player %s -> Level %d", activatingPlayerName, targetLevel);

            // Cosmetic lightning
            EntityLightningBolt bolt = new EntityLightningBolt(worldObj, xCoord + 0.5D, yCoord + 1.0D, zCoord + 0.5D);
            worldObj.spawnEntityInWorld(bolt);
        }

        activePedestals.clear();
        markForUpdate();
    }

    public int calculateStructurePoints() {
        int points = 0;
        int r = 3;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    int x = xCoord + dx;
                    int y = yCoord + dy;
                    int z = zCoord + dz;
                    Block b = worldObj.getBlock(x, y, z);
                    if (b == ModBlocks.altarTip) {
                        int meta = worldObj.getBlockMetadata(x, y, z);
                        points += (meta + 1); // 1, 2, 3 points per tip tier
                    }
                }
            }
        }
        return points;
    }

    private int getRequiredStructurePoints(int level) {
        if (level <= 6) return 4;
        if (level <= 8) return 8;
        if (level <= 10) return 12;
        return 16;
    }

    private List<TileEntityBloodPedestal> findPedestals() {
        List<TileEntityBloodPedestal> list = new ArrayList<TileEntityBloodPedestal>();
        int r = 4;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    net.minecraft.tileentity.TileEntity te = worldObj.getTileEntity(xCoord + dx, yCoord + dy, zCoord + dz);
                    if (te instanceof TileEntityBloodPedestal) {
                        list.add((TileEntityBloodPedestal) te);
                    }
                }
            }
        }
        return list;
    }

    private boolean validateAndReserveCatalysts(int level, List<TileEntityBloodPedestal> pedestals) {
        boolean hasPureBlood = false;
        boolean hasSecondary = false;

        int reqPureBloodTier = Math.min(4, Math.max(0, (level - 5) / 2)); // 0 to 4 (Tiers 1 to 5)
        Item secondaryItem = level >= 9 ? ModItems.sovereignBlood : (level >= 7 ? ModItems.vampireBook : ModItems.humanHeart);

        for (TileEntityBloodPedestal ped : pedestals) {
            ItemStack item = ped.getStoredItem();
            if (item != null) {
                if (item.getItem() == ModItems.pureBlood && item.getItemDamage() >= reqPureBloodTier) {
                    hasPureBlood = true;
                } else if (item.getItem() == secondaryItem) {
                    hasSecondary = true;
                }
            }
        }

        return hasPureBlood && hasSecondary;
    }

    private void consumeCatalysts(int level, List<TileEntityBloodPedestal> pedestals) {
        int reqPureBloodTier = Math.min(4, Math.max(0, (level - 5) / 2));
        Item secondaryItem = level >= 9 ? ModItems.sovereignBlood : (level >= 7 ? ModItems.vampireBook : ModItems.humanHeart);

        boolean consumedBlood = false;
        boolean consumedSecondary = false;

        for (TileEntityBloodPedestal ped : pedestals) {
            ItemStack item = ped.getStoredItem();
            if (item != null) {
                if (!consumedBlood && item.getItem() == ModItems.pureBlood && item.getItemDamage() >= reqPureBloodTier) {
                    ped.takeStoredItem();
                    consumedBlood = true;
                } else if (!consumedSecondary && item.getItem() == secondaryItem) {
                    ped.takeStoredItem();
                    consumedSecondary = true;
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("state", state);
        tag.setInteger("ritualTicks", ritualTicks);
        tag.setString("activatingPlayer", activatingPlayerName);
        tag.setInteger("targetLevel", targetLevel);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.state = tag.getInteger("state");
        this.ritualTicks = tag.getInteger("ritualTicks");
        this.activatingPlayerName = tag.getString("activatingPlayer");
        this.targetLevel = tag.getInteger("targetLevel");
    }
}
