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
    public boolean interact(EntityPlayer player) {
        if (this.worldObj.isRemote) {
            return true;
        }

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null) return false;

        if (Helper.isVampire(player)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.trainer_refuse_vampire"));
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
            if (curLevel >= 14) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.trainer_max_level"));
                return true;
            }

            int nextLevel = curLevel + 1;
            int reqFangs = Math.min(16, nextLevel * 2);
            int reqPureBloodTier = nextLevel >= 5 ? Math.min(4, nextLevel - 5) : -1;

            boolean hasFangs = countItem(player, ModItems.vampireFang, 0) >= reqFangs;
            boolean hasBlood = reqPureBloodTier < 0 || countItem(player, ModItems.pureBlood, reqPureBloodTier) >= 1;

            if (hasFangs && hasBlood) {
                consumeItem(player, ModItems.vampireFang, reqFangs, 0);
                if (reqPureBloodTier >= 0) {
                    consumeItem(player, ModItems.pureBlood, 1, reqPureBloodTier);
                }

                fph.setFaction(ModFactions.HUNTER, nextLevel);
                this.worldObj.playSoundAtEntity(player, "random.levelup", 1.0F, 1.0F);
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.hunter_level_up", nextLevel));
                ModLogger.leveling("Hunter %s leveled up to %d via Hunter Trainer", player.getCommandSenderName(), nextLevel);
            } else {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.trainer_req_info", nextLevel, reqFangs));
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
