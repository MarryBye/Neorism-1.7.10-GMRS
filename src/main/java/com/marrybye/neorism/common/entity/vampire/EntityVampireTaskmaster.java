package com.marrybye.neorism.common.entity.vampire;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Vampire Taskmaster NPC offering Lord Rank advancement, bounties, and minion gear.
 * Ported 1:1 from TeamLapen's VampireTaskMasterEntity.java.
 */
public class EntityVampireTaskmaster extends EntityVampireBase {

    public EntityVampireTaskmaster(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.setCustomNameTag("Vampire Taskmaster");
        this.setAlwaysRenderNameTag(true);
    }

    @Override
    protected void setupAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWander(this, 0.7D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new com.marrybye.neorism.common.entity.ai.EntityAIHurtByTargetFaction(this, ModFactions.VAMPIRE));
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.worldObj.isRemote) {
            return true;
        }

        if (Helper.isHunter(player)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.vampire_taskmaster_hunter"));
            return true;
        }

        if (!Helper.isVampire(player)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.vampire_taskmaster_mortal"));
            return true;
        }

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        int playerLevel = Helper.getLevel(player);
        if (playerLevel < 14) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.vampire_taskmaster_need_lvl14"));
            return true;
        }

        int lordLvl = fph != null ? fph.getLordLevel() : 0;

        switch (lordLvl) {
            case 0:
                if (hasPureBloodTier5(player, 5) && countItem(player, Items.gold_ingot) >= 32) {
                    consumePureBloodTier5(player, 5);
                    consumeItem(player, Items.gold_ingot, 32);
                    fph.setLordLevel(1);
                    player.inventory.addItemStackToInventory(new ItemStack(ModItems.vampireMinionBinding, 1));
                    advanceLordRank(player, 1);
                } else {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.lord_req_1"));
                }
                break;

            case 1:
                if (hasPureBloodTier5(player, 5) && countItem(player, Items.gold_ingot) >= 48) {
                    consumePureBloodTier5(player, 5);
                    consumeItem(player, Items.gold_ingot, 48);
                    fph.setLordLevel(2);
                    player.inventory.addItemStackToInventory(new ItemStack(ModItems.vampireMinionUpgradeSimple, 1));
                    advanceLordRank(player, 2);
                } else {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.lord_req_2"));
                }
                break;

            case 2:
                if (hasPureBloodTier5(player, 5) && countItem(player, Items.gold_ingot) >= 48) {
                    consumePureBloodTier5(player, 5);
                    consumeItem(player, Items.gold_ingot, 48);
                    fph.setLordLevel(3);
                    player.inventory.addItemStackToInventory(new ItemStack(ModItems.vampireMinionUpgradeEnhanced, 1));
                    advanceLordRank(player, 3);
                } else {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.lord_req_3"));
                }
                break;

            case 3:
                if (hasPureBloodTier5(player, 10) && countItem(player, Items.gold_ingot) >= 64) {
                    consumePureBloodTier5(player, 10);
                    consumeItem(player, Items.gold_ingot, 64);
                    fph.setLordLevel(4);
                    advanceLordRank(player, 4);
                } else {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.lord_req_4"));
                }
                break;

            case 4:
                if (hasPureBloodTier5(player, 10) && countItem(player, Item.getItemFromBlock(Blocks.gold_block)) >= 16) {
                    consumePureBloodTier5(player, 10);
                    consumeItem(player, Item.getItemFromBlock(Blocks.gold_block), 16);
                    fph.setLordLevel(5);
                    player.inventory.addItemStackToInventory(new ItemStack(ModItems.vampireMinionUpgradeSpecial, 1));
                    advanceLordRank(player, 5);
                } else {
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.lord_req_5"));
                }
                break;

            default:
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.lord_max_rank"));
                break;
        }

        return true;
    }

    private void advanceLordRank(EntityPlayer player, int rank) {
        this.worldObj.playSoundAtEntity(player, "random.levelup", 1.0F, 1.0F);
        player.addChatMessage(new ChatComponentTranslation("chat.neorism.lord_advancement", rank));
        ModLogger.leveling("Player %s promoted to Vampire Lord Rank %d by Vampire Taskmaster",
            player.getCommandSenderName(), rank);
    }

    private boolean hasPureBloodTier5(EntityPlayer player, int amount) {
        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == ModItems.pureBlood && stack.getItemDamage() == 4) {
                count += stack.stackSize;
            }
        }
        return count >= amount;
    }

    private void consumePureBloodTier5(EntityPlayer player, int amount) {
        int left = amount;
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == ModItems.pureBlood && stack.getItemDamage() == 4) {
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
    public boolean canDespawn() {
        return false;
    }
}
