package com.marrybye.neorism.common.entity.hunter;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Friendly NPC Hunter Trainer located in camps and villages.
 * Inspects player progression and promotes hunters through levels 1 to 14.
 */
public class EntityHunterTrainer extends EntityHunterBase {

    public EntityHunterTrainer(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.setCustomNameTag("Hunter Trainer");
        this.setAlwaysRenderNameTag(true);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.maxHealth).setBaseValue(300.0D);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.attackDamage).setBaseValue(19.0D);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed).setBaseValue(0.17D);
    }

    @Override
    public net.minecraft.entity.IEntityLivingData onSpawnWithEgg(net.minecraft.entity.IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setCurrentItemOrArmor(4, new ItemStack(ModItems.hunterHat));
        this.setCurrentItemOrArmor(0, new ItemStack(ModItems.hunterAxe, 1, 0));
        this.equipmentDropChances[0] = 0.0F;
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

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null) return false;

        if (Helper.isVampire(player)) {
            player.addChatMessage(com.marrybye.neorism.common.faction.FactionRestriction.getFactionRestrictionMessage(ModFactions.HUNTER));
            return true;
        }

        if (fph.isNeutral()) {
            // Check for initial hunter induction: 1 Stake + 1 Garlic
            if (player.inventory.hasItem(ModItems.stake) && player.inventory.hasItem(ModItems.garlic)) {
                consumeItem(player, ModItems.stake, 1, 0);
                consumeItem(player, ModItems.garlic, 1, 0);
                fph.setFaction(ModFactions.HUNTER, 1);
                this.worldObj.playSoundAtEntity(player, "random.levelup", 1.0F, 1.0F);
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.hunter_joined"));
                ModLogger.leveling("Player %s inducted into Hunter Faction (Level 1) by Hunter Trainer",
                    player.getCommandSenderName());
            } else {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.trainer_initiation_req"));
            }
            return true;
        }

        if (fph.isHunter()) {
            int curLevel = fph.getCurrentLevel();
            if (curLevel >= com.marrybye.neorism.common.hunter.HunterLeveling.MAX_LEVEL) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.trainer_max_level"));
                return true;
            }

            int nextLevel = curLevel + 1;
            if (com.marrybye.neorism.common.hunter.HunterLeveling.canLevelUp(player, nextLevel)) {
                if (com.marrybye.neorism.common.hunter.HunterLeveling.tryLevelUp(player)) {
                    this.worldObj.playSoundAtEntity(player, "random.levelup", 1.0F, 1.0F);
                }
            } else {
                if (nextLevel <= 4) {
                    com.marrybye.neorism.common.hunter.HunterLeveling.BasicTrainingRequirement req =
                        com.marrybye.neorism.common.hunter.HunterLeveling.getBasicRequirement(nextLevel);
                    int fangs = req != null ? req.fangsRequired : 0;
                    player.addChatMessage(new ChatComponentTranslation("chat.neorism.trainer_req_info", nextLevel, fangs));
                } else {
                    com.marrybye.neorism.common.hunter.HunterLeveling.AdvancedTrainingRequirement req =
                        com.marrybye.neorism.common.hunter.HunterLeveling.getAdvancedRequirement(nextLevel);
                    if (req != null) {
                        player.addChatMessage(new ChatComponentTranslation("chat.neorism.trainer_intel_req",
                            nextLevel, req.intelTier + 1, req.ironRequired, req.goldRequired));
                    }
                }
            }
            return true;
        }

        return super.interact(player);
    }

    private int countItem(EntityPlayer player, Item item, int meta) {
        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == item && (meta < 0 || stack.getItemDamage() == meta)) {
                count += stack.stackSize;
            }
        }
        return count;
    }

    private void consumeItem(EntityPlayer player, Item item, int amount, int meta) {
        int left = amount;
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item && (meta < 0 || stack.getItemDamage() == meta)) {
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
