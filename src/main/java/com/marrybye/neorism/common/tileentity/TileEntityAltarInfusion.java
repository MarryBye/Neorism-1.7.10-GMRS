package com.marrybye.neorism.common.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.marrybye.neorism.common.blocks.BlockAltarPillar;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampireLeveling;
import com.marrybye.neorism.common.vampire.VampireLeveling.AltarInfusionRequirement;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

/**
 * Canonical TileEntity for the Altar of Infusion.
 * Ported 1:1 from TeamLapen's AltarInfusionBlockEntity.java.
 */
public class TileEntityAltarInfusion extends TileEntityNeorism implements IInventory {

    public static final int DURATION_TICK = 450;
    public static final int MAX_PILLARS = 9;
    public static final int RISING_TICKS = 60;
    public static final float MAX_SPHERE_RITUAL_HEIGHT = 2.25F;
    public static final double MAX_PLAYER_DISTANCE_SQ = 30.0D * 30.0D;
    public static final UUID ID_MOVEMENT_SLOWDOWN = UUID.fromString("e5730a44-a09b-4654-8c43-ff12f2048994");

    private final ItemStack[] items = new ItemStack[3];
    private final List<ChunkCoordinates> tips = new ArrayList<ChunkCoordinates>();

    private int runTime = 0;
    private int targetLevel = 0;
    private String activatingPlayerName = "";
    private UUID playerUUID = null;

    // Client-side animation variables
    public int animationTime = 0;
    public float rotation = 0.0F;
    public float prevRotation = 0.0F;
    public float targetRotation = 0.0F;
    public float verticalOffset = 0.05F;
    public int runningTicks = 0;
    public int stoppingTicks = 0;
    public float startHeight = 0.05F;

    public boolean isRunning() {
        return this.runTime > 0;
    }

    public int getRunTime() {
        return this.runTime;
    }

    public List<ChunkCoordinates> getTips() {
        return this.tips;
    }

    public EntityPlayer getPlayer() {
        if (worldObj == null) return null;
        if (activatingPlayerName != null && !activatingPlayerName.isEmpty()) {
            return worldObj.getPlayerEntityByName(activatingPlayerName);
        }
        return null;
    }

    public Phase getCurrentPhase() {
        if (this.runTime < 1) return Phase.NOT_RUNNING;
        if (this.runTime == 1) return Phase.CLEAN_UP;
        if (this.runTime > DURATION_TICK - 100) return Phase.PARTICLE_SPREAD;
        if (this.runTime >= DURATION_TICK - 200 && this.runTime < DURATION_TICK - 160) return Phase.BEAM_CONNECT;
        if (this.runTime <= DURATION_TICK - 200 && this.runTime > 50) return Phase.BEAM_PLAYER;
        if (this.runTime == 50) return Phase.LEVELUP;
        if (this.runTime < 50) return Phase.ENDING;
        return Phase.WAITING;
    }

    public Result tryActivate(EntityPlayer player) {
        if (isRunning()) return Result.STILL_RUNNING;

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null) return Result.LEVEL_WRONG;

        this.targetLevel = fph.getCurrentLevel() + 1;
        AltarInfusionRequirement req = VampireLeveling.getInfusionRequirement(this.targetLevel);
        if (req == null) return Result.LEVEL_WRONG;

        if (worldObj.isDaytime()) return Result.NIGHT_ONLY;
        if (!checkStructureLevel(req.getRequiredStructurePoints())) return Result.MISSING_PILLARS;
        if (!checkItemRequirements(this.targetLevel)) return Result.MISSING_ITEMS;

        return Result.SUCCESS;
    }

    public boolean checkStructureLevel(int required) {
        if (this.worldObj == null) return false;

        List<ValuedTip> valuedTips = new ArrayList<ValuedTip>();
        List<ChunkCoordinates> foundTips = findTips();

        for (ChunkCoordinates tip : foundTips) {
            int height = 0;
            double totalValue = 0.0D;

            while (height < 3) {
                int checkY = tip.posY - (height + 1);
                Block blockBelow = this.worldObj.getBlock(tip.posX, checkY, tip.posZ);
                if (blockBelow != ModBlocks.altarPillar) break;

                int meta = this.worldObj.getBlockMetadata(tip.posX, checkY, tip.posZ);
                totalValue += BlockAltarPillar.getPillarValue(meta);
                height++;
            }

            int val = (int) (10 * totalValue);
            valuedTips.add(new ValuedTip(tip, val));
        }

        Collections.sort(valuedTips, new Comparator<ValuedTip>() {
            @Override
            public int compare(ValuedTip o1, ValuedTip o2) {
                return Integer.compare(o2.value, o1.value);
            }
        });

        int sum = 0;
        this.tips.clear();
        int count = Math.min(MAX_PILLARS, valuedTips.size());
        for (int i = 0; i < count; i++) {
            ValuedTip vt = valuedTips.get(i);
            sum += vt.value;
            this.tips.add(vt.pos);
        }

        return sum >= required * 10;
    }

    private List<ChunkCoordinates> findTips() {
        List<ChunkCoordinates> result = new ArrayList<ChunkCoordinates>();
        if (this.worldObj == null) return result;

        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = 1; dy <= 3; dy++) {
                for (int dz = -5; dz <= 5; dz++) {
                    int x = xCoord + dx;
                    int y = yCoord + dy;
                    int z = zCoord + dz;
                    if (this.worldObj.getBlock(x, y, z) == ModBlocks.altarTip) {
                        result.add(new ChunkCoordinates(x, y, z));
                    }
                }
            }
        }

        return result;
    }

    public boolean checkItemRequirements(int targetLvl) {
        AltarInfusionRequirement req = VampireLeveling.getInfusionRequirement(targetLvl);
        if (req == null) return false;

        // Slot 0: Pure Blood
        if (req.getPureBloodQuantity() > 0) {
            ItemStack blood = items[0];
            if (blood == null || blood.getItem() != ModItems.pureBlood ||
                blood.getItemDamage() < req.getPureBloodLevel() ||
                blood.stackSize < req.getPureBloodQuantity()) {
                return false;
            }
        }

        // Slot 1: Human Hearts
        if (req.getHumanHeartQuantity() > 0) {
            ItemStack hearts = items[1];
            if (hearts == null || hearts.getItem() != ModItems.humanHeart ||
                hearts.stackSize < req.getHumanHeartQuantity()) {
                return false;
            }
        }

        // Slot 2: Vampire Books
        if (req.getVampireBookQuantity() > 0) {
            ItemStack books = items[2];
            if (books == null || books.getItem() != ModItems.vampireBook ||
                books.stackSize < req.getVampireBookQuantity()) {
                return false;
            }
        }

        return true;
    }

    private void consumeItems(int targetLvl) {
        AltarInfusionRequirement req = VampireLeveling.getInfusionRequirement(targetLvl);
        if (req == null) return;

        if (req.getPureBloodQuantity() > 0 && items[0] != null) {
            items[0].stackSize -= req.getPureBloodQuantity();
            if (items[0].stackSize <= 0) items[0] = null;
        }

        if (req.getHumanHeartQuantity() > 0 && items[1] != null) {
            items[1].stackSize -= req.getHumanHeartQuantity();
            if (items[1].stackSize <= 0) items[1] = null;
        }

        if (req.getVampireBookQuantity() > 0 && items[2] != null) {
            items[2].stackSize -= req.getVampireBookQuantity();
            if (items[2].stackSize <= 0) items[2] = null;
        }

        markDirty();
    }

    public void startRitual(EntityPlayer player) {
        if (worldObj == null || worldObj.isRemote || isRunning() || player == null) return;

        this.runTime = DURATION_TICK;
        this.activatingPlayerName = player.getCommandSenderName();
        this.playerUUID = player.getUniqueID();
        this.targetLevel = FactionPlayerHandler.get(player).getCurrentLevel() + 1;

        // Apply resistance and 100% movement slowdown
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, DURATION_TICK, 4, false));

        IAttributeInstance speedAttr = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (speedAttr != null && speedAttr.getModifier(ID_MOVEMENT_SLOWDOWN) == null) {
            speedAttr.applyModifier(new AttributeModifier(ID_MOVEMENT_SLOWDOWN, "Altar Infusion Slowdown", -1.0D, 2));
        }

        ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 1.0F, 0.8F);
        ModLogger.rituals("Infusion ritual started at [%d,%d,%d] for %s (target lvl: %d)",
            xCoord, yCoord, zCoord, activatingPlayerName, targetLevel);

        markForUpdate();
        markDirty();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (isRunning()) {
            if (!worldObj.isRemote) {
                EntityPlayer player = getPlayer();
                if (player == null || player.isDead ||
                    player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) > MAX_PLAYER_DISTANCE_SQ) {
                    endRitual();
                    return;
                }
            }

            this.runTime--;
            tickRitual();

            if (!isRunning()) {
                markForUpdate();
                markDirty();
            }
        }

        if (worldObj.isRemote) {
            tickSphereAnimation();
        }
    }

    private void tickRitual() {
        Phase phase = getCurrentPhase();

        if (worldObj.isRemote) {
            handleClientEffects(phase);
        } else {
            if (phase == Phase.LEVELUP) {
                handleLevelUp();
            } else if (phase == Phase.CLEAN_UP) {
                endRitual();
            }
        }
    }

    private void handleClientEffects(Phase phase) {
        if (phase == Phase.PARTICLE_SPREAD && this.runTime % 15 == 0) {
            for (ChunkCoordinates tip : this.tips) {
                double tx = tip.posX + 0.5D;
                double ty = tip.posY + 0.5D;
                double tz = tip.posZ + 0.5D;
                double dx = (xCoord + 0.5D) - tx;
                double dy = (yCoord + 1.0D) - ty;
                double dz = (zCoord + 0.5D) - tz;
                for (int i = 0; i < 4; i++) {
                    worldObj.spawnParticle("reddust", tx + dx * 0.25D * i, ty + dy * 0.25D * i, tz + dz * 0.25D * i, 0, 0, 0);
                }
            }
        }

        if (this.runTime == DURATION_TICK - 200) {
            EntityPlayer player = getPlayer();
            if (player != null) {
                worldObj.playSound(player.posX, player.posY, player.posZ, "neorism:beam_enter", 1.0F, 1.0F, false);
            }
        }
    }

    private void handleLevelUp() {
        if (worldObj == null || worldObj.isRemote) return;

        EntityPlayer player = getPlayer();
        if (player == null) return;

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null || fph.getCurrentLevel() != this.targetLevel - 1) return;

        if (!checkItemRequirements(this.targetLevel)) {
            endRitual();
            return;
        }

        consumeItems(this.targetLevel);
        fph.setFaction(ModFactions.VAMPIRE, this.targetLevel);

        VampirePlayer vp = VampirePlayer.get(player);
        if (vp != null) {
            vp.drinkBlood(Integer.MAX_VALUE, 1.0F);
        }

        ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 1.2F, 1.2F);
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.vampire_leveled_up", this.targetLevel));
        ModLogger.rituals("Infusion ritual SUCCESS for %s -> Level %d", activatingPlayerName, targetLevel);

        // Post ritual buffs: Saturation, Regeneration, Strength
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 400, 2));
        player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 400, 2));
        player.addPotionEffect(new PotionEffect(23, 400, 2)); // 23 = Saturation

        // Cosmetic lightning bolt
        EntityLightningBolt bolt = new EntityLightningBolt(worldObj, xCoord + 0.5D, yCoord + 1.0D, zCoord + 0.5D);
        worldObj.spawnEntityInWorld(bolt);

        markForUpdate();
        markDirty();
    }

    private void endRitual() {
        EntityPlayer target = getPlayer();
        if (target != null) {
            IAttributeInstance speedAttr = target.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            if (speedAttr != null && speedAttr.getModifier(ID_MOVEMENT_SLOWDOWN) != null) {
                speedAttr.removeModifier(speedAttr.getModifier(ID_MOVEMENT_SLOWDOWN));
            }
        }

        this.activatingPlayerName = "";
        this.playerUUID = null;
        this.tips.clear();
        this.runTime = 0;

        markForUpdate();
        markDirty();
    }

    public void tickSphereAnimation() {
        this.prevRotation = this.rotation;

        boolean running = isRunning();
        float spinSpeed = (running || this.stoppingTicks < RISING_TICKS) ? 0.4F : 0.025F;
        this.targetRotation += spinSpeed;

        while (this.rotation >= (float) Math.PI) {
            this.rotation -= (float) (Math.PI * 2);
        }
        while (this.rotation < (float) -Math.PI) {
            this.rotation += (float) (Math.PI * 2);
        }
        while (this.targetRotation >= (float) Math.PI) {
            this.targetRotation -= (float) (Math.PI * 2);
        }
        while (this.targetRotation < (float) -Math.PI) {
            this.targetRotation += (float) (Math.PI * 2);
        }

        float rotDiff = this.targetRotation - this.rotation;
        while (rotDiff >= (float) Math.PI) {
            rotDiff -= (float) (Math.PI * 2);
        }
        while (rotDiff < (float) -Math.PI) {
            rotDiff += (float) (Math.PI * 2);
        }
        this.rotation += rotDiff * 0.4F;

        this.animationTime++;
        float bobbingHeight = (float) (Math.sin(this.animationTime * 0.1F) * 0.05F + 0.05F);

        if (running) {
            this.stoppingTicks = 0;
            if (this.runningTicks == 0) {
                this.startHeight = bobbingHeight;
            }
            if (this.runningTicks < RISING_TICKS) {
                this.runningTicks++;
                float progress = (float) this.runningTicks / RISING_TICKS;
                this.verticalOffset = this.startHeight + (MAX_SPHERE_RITUAL_HEIGHT - this.startHeight) * progress;
            } else {
                this.verticalOffset = MAX_SPHERE_RITUAL_HEIGHT;
            }
        } else {
            this.runningTicks = 0;
            if (this.stoppingTicks == 0) {
                this.startHeight = this.verticalOffset;
            }
            if (this.stoppingTicks < RISING_TICKS) {
                this.stoppingTicks++;
                float progress = (float) this.stoppingTicks / RISING_TICKS;
                this.verticalOffset = this.startHeight + (bobbingHeight - this.startHeight) * progress;
            } else {
                this.verticalOffset = bobbingHeight;
            }
        }
    }

    // --- IInventory Implementation ---

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= 0 && slot < items.length) {
            return items[slot];
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (slot >= 0 && slot < items.length && items[slot] != null) {
            if (items[slot].stackSize <= count) {
                ItemStack stack = items[slot];
                items[slot] = null;
                markDirty();
                return stack;
            } else {
                ItemStack stack = items[slot].splitStack(count);
                if (items[slot].stackSize == 0) items[slot] = null;
                markDirty();
                return stack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot >= 0 && slot < items.length && items[slot] != null) {
            ItemStack stack = items[slot];
            items[slot] = null;
            markDirty();
            return stack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot >= 0 && slot < items.length) {
            items[slot] = stack;
            markDirty();
        }
    }

    @Override
    public String getInventoryName() {
        return "container.neorism.altar_infusion";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj != null && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
               player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        if (slot == 0) return stack.getItem() == ModItems.pureBlood;
        if (slot == 1) return stack.getItem() == ModItems.humanHeart;
        if (slot == 2) return stack.getItem() == ModItems.vampireBook;
        return false;
    }

    // --- NBT Serialization ---

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("runTime", this.runTime);
        tag.setInteger("targetLevel", this.targetLevel);
        if (activatingPlayerName != null) {
            tag.setString("activatingPlayerName", activatingPlayerName);
        }
        if (playerUUID != null) {
            tag.setString("playerUUID", playerUUID.toString());
        }

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                items[i].writeToNBT(itemTag);
                itemList.appendTag(itemTag);
            }
        }
        tag.setTag("Items", itemList);

        NBTTagList tipList = new NBTTagList();
        for (ChunkCoordinates tip : this.tips) {
            NBTTagCompound tipTag = new NBTTagCompound();
            tipTag.setInteger("X", tip.posX);
            tipTag.setInteger("Y", tip.posY);
            tipTag.setInteger("Z", tip.posZ);
            tipList.appendTag(tipTag);
        }
        tag.setTag("Tips", tipList);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.runTime = tag.getInteger("runTime");
        this.targetLevel = tag.getInteger("targetLevel");
        this.activatingPlayerName = tag.getString("activatingPlayerName");
        if (tag.hasKey("playerUUID")) {
            try {
                this.playerUUID = UUID.fromString(tag.getString("playerUUID"));
            } catch (Exception ignored) {}
        }

        NBTTagList itemList = tag.getTagList("Items", 10);
        for (int i = 0; i < items.length; i++) items[i] = null;
        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < items.length) {
                items[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }

        NBTTagList tipList = tag.getTagList("Tips", 10);
        this.tips.clear();
        for (int i = 0; i < tipList.tagCount(); i++) {
            NBTTagCompound tipTag = tipList.getCompoundTagAt(i);
            this.tips.add(new ChunkCoordinates(tipTag.getInteger("X"), tipTag.getInteger("Y"), tipTag.getInteger("Z")));
        }
    }

    public enum Phase {
        NOT_RUNNING, PARTICLE_SPREAD, BEAM_CONNECT, BEAM_PLAYER, WAITING, LEVELUP, ENDING, CLEAN_UP
    }

    public enum Result {
        SUCCESS("success"),
        STILL_RUNNING("still_running"),
        LEVEL_WRONG("level_wrong"),
        NIGHT_ONLY("night_only"),
        MISSING_PILLARS("missing_pillars"),
        MISSING_ITEMS("missing_items");

        public final String name;

        Result(String name) {
            this.name = name;
        }

        public IChatComponent getMessage() {
            return new ChatComponentTranslation("message.neorism.altar_infusion.ritual." + name);
        }
    }

    private static class ValuedTip {
        final ChunkCoordinates pos;
        final int value;

        ValuedTip(ChunkCoordinates pos, int value) {
            this.pos = pos;
            this.value = value;
        }
    }
}
