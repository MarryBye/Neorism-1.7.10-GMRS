package com.marrybye.neorism.common.entity.hunter;

import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Faction Taskmaster NPC offering bounties and supplies in exchange for vampire spoils.
 */
public class EntityTaskmaster extends EntityHunterBase {

    public EntityTaskmaster(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.setCustomNameTag("Hunter Taskmaster");
        this.setAlwaysRenderNameTag(true);
    }

    @Override
    protected void setupAI() {
        this.tasks.addTask(1, new net.minecraft.entity.ai.EntityAISwimming(this));
        this.tasks.addTask(2, new net.minecraft.entity.ai.EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new net.minecraft.entity.ai.EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(8, new net.minecraft.entity.ai.EntityAIWander(this, 0.7D));
        this.tasks.addTask(9, new net.minecraft.entity.ai.EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new net.minecraft.entity.ai.EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, false));
    }

    @Override
    public net.minecraft.entity.IEntityLivingData onSpawnWithEgg(net.minecraft.entity.IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(4, new ItemStack(ModItems.hunterHat));
        this.equipmentDropChances[4] = 0.0F;
        return data;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.worldObj.isRemote) {
            return true;
        }

        if (tryCureSanguinare(player)) {
            return true;
        }

        if (Helper.isVampire(player)) {
            player.addChatMessage(com.marrybye.neorism.common.faction.FactionRestriction.getFactionRestrictionMessage(com.marrybye.neorism.common.core.ModFactions.HUNTER));
            return true;
        }

        // Bounty Turn-in: 4 Vampire Fangs -> 1 Hunter Intel & 2 Emeralds
        if (countItem(player, ModItems.vampireFang) >= 4) {
            consumeItem(player, ModItems.vampireFang, 4);
            player.inventory.addItemStackToInventory(new ItemStack(ModItems.hunterIntel, 1));
            player.inventory.addItemStackToInventory(new ItemStack(Items.emerald, 2));
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.taskmaster_bounty_reward"));
            this.worldObj.playSoundAtEntity(player, "random.orb", 1.0F, 1.0F);
            ModLogger.trade("Player %s exchanged 4 Vampire Fangs for Hunter Intel & 2 Emeralds with Taskmaster",
                player.getCommandSenderName());
            return true;
        } else {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.taskmaster_need_fangs"));
        }

        return super.interact(player);
    }

    private int countItem(EntityPlayer player, Item item) {
        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == item) {
                count += stack.stackSize;
            }
        }
        return count;
    }

    private void consumeItem(EntityPlayer player, Item item, int amount) {
        int left = amount;
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                if (stack.stackSize > left) {
                    stack.stackSize -= left;
                    return;
                } else {
                    left -= stack.stackSize;
                    player.inventory.mainInventory[i] = null;
                    if (left <= 0) return;
                }
            }
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
