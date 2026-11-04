package com.marrybye.neorism.common.action.lord;

import java.util.List;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;

/**
 * Lord Speed Action. Canonical port of Vampirism SpeedLordAction:
 * Grants beneficial movement speed aura to nearby allied faction members and minions.
 */
public class SpeedLordAction extends ActionBase {

    public SpeedLordAction() {
        super("lord_speed", 2400, 600, false, 0);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        int lordLevel = fph != null ? fph.getLordLevel() : 0;
        int amp = Math.max(0, lordLevel - 1);
        int duration = 600;

        double r = 10.0D;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            player.posX - r, player.posY - r, player.posZ - r,
            player.posX + r, player.posY + r, player.posZ + r
        );
        List list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        IFaction playerFaction = Helper.getFaction(player);
        int applied = 0;

        for (Object obj : list) {
            if (obj instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) obj;
                if (living instanceof EntityPlayer) {
                    EntityPlayer targetPlayer = (EntityPlayer) living;
                    if (targetPlayer != player) {
                        FactionPlayerHandler targetFph = FactionPlayerHandler.get(targetPlayer);
                        if (targetFph != null && targetFph.getLordLevel() >= lordLevel) {
                            continue; // Higher/equal lords don't get overwritten
                        }
                    }
                }
                if (Helper.getFaction(living).equals(playerFaction)) {
                    if (ModPotions.lordSpeed != null) {
                        living.addPotionEffect(new PotionEffect(ModPotions.lordSpeed.id, duration, amp));
                        applied++;
                    }
                }
            }
        }

        if (applied > 0) {
            ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 0.8F, 1.2F);
            return true;
        } else {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.lord_range.no_target"));
            }
            return false;
        }
    }

    @Override
    public boolean isUnlocked(EntityPlayer player) {
        return Helper.getLordLevel(player) > 0;
    }
}
