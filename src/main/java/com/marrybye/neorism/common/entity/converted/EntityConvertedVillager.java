package com.marrybye.neorism.common.entity.converted;

import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Converted vampiric villager that can be peacefully fed upon by vampire masters and offers dark trading.
 */
public class EntityConvertedVillager extends EntityVillager implements IVampireEntity {

    public EntityConvertedVillager(World world) {
        super(world);
    }

    @Override
    public IFaction getFaction() {
        return ModFactions.VAMPIRE;
    }

    @Override
    public EntityLivingBase asEntity() {
        return this;
    }

    @Override
    public boolean isGettingSundamage() {
        return false;
    }

    @Override
    public boolean isIgnoringSundamage() {
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.worldObj.isRemote) {
            return true;
        }

        if (Helper.isHunter(player)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.converted_villager_fear_hunter"));
            return true;
        }

        // If vampire sneaks and right-clicks: peaceful feeding
        if (player.isSneaking() && Helper.isVampire(player)) {
            ExtendedCreature ext = ExtendedCreature.get(this);
            if (ext != null && ext.canBeBitten(null)) {
                com.marrybye.neorism.common.vampire.VampirePlayer vp =
                    com.marrybye.neorism.common.vampire.VampirePlayer.get(player);
                if (vp != null && vp.wantsBlood()) {
                    int drained = ext.onBite(null);
                    if (drained > 0) {
                        vp.drinkBlood(drained, 1.0F);
                        ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 1.0F, 1.0F);
                        player.addChatMessage(new ChatComponentTranslation("chat.neorism.fed_on_converted_villager"));
                        ModLogger.combat("Vampire player %s fed peacefully on converted villager (+%d blood)",
                            player.getCommandSenderName(), drained);
                        return true;
                    }
                }
            }
        }

        return super.interact(player);
    }

    @Override
    public EntityVillager createChild(EntityAgeable ageable) {
        return new EntityConvertedVillager(this.worldObj);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        super.dropFewItems(recentlyHit, looting);
        if (this.rand.nextInt(2) == 0) {
            this.dropItem(ModItems.vampireFang, 1);
        }
    }
}
