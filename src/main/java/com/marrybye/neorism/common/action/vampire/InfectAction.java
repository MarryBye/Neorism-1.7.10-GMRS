package com.marrybye.neorism.common.action.vampire;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * Vampire Infect ability.
 * Canonical port of TeamLapen's InfectAction:
 * Allows a vampire to directly infect a peaceful creature or villager with Sanguinare Vampiris without entering full feeding.
 */
public class InfectAction extends ActionBase {

    public InfectAction() {
        super("infect", 20, 0, false, 2);
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        EntityLivingBase target = getTarget(player);
        if (target == null) {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.infect.no_target"));
            }
            return false;
        }

        if (Helper.canReallySee(target, player, false)) {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.infect.sees_you"));
            }
            return false;
        }

        if (Helper.isVampire(target)) {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.infect.can_not_infect"));
            }
            return false;
        }

        if (!player.worldObj.isRemote) {
            if (target instanceof EntityPlayer) {
                EntityPlayer targetPlayer = (EntityPlayer) target;
                FactionPlayerHandler fph = FactionPlayerHandler.get(targetPlayer);
                if (fph != null && fph.canJoin(com.marrybye.neorism.common.core.ModFactions.VAMPIRE)) {
                    targetPlayer.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 24000, 0));
                    targetPlayer.addChatMessage(new ChatComponentTranslation("chat.neorism.contracted_sanguinare"));
                }
            } else {
                ExtendedCreature creature = ExtendedCreature.get(target);
                if (creature != null) {
                    creature.tryInfect(VampirePlayer.get(player));
                } else {
                    target.addPotionEffect(new PotionEffect(ModPotions.sanguinare.id, 24000, 0));
                }
            }
        }

        ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 1.0F, 1.0F);
        return true;
    }

    private EntityLivingBase getTarget(EntityPlayer player) {
        Vec3 src = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLookVec();
        double dist = 3.5D;
        Vec3 dst = src.addVector(look.xCoord * dist, look.yCoord * dist, look.zCoord * dist);

        MovingObjectPosition mop = player.worldObj.rayTraceBlocks(src, dst);
        if (mop != null) {
            dst = Vec3.createVectorHelper(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
        }

        java.util.List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(
            player, player.boundingBox.addCoord(look.xCoord * dist, look.yCoord * dist, look.zCoord * dist).expand(1.0D, 1.0D, 1.0D)
        );
        EntityLivingBase closest = null;
        double minDist = Double.MAX_VALUE;

        for (Object obj : list) {
            if (obj instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) obj;
                if (!entity.canBeCollidedWith()) continue;
                float border = entity.getCollisionBorderSize();
                net.minecraft.util.AxisAlignedBB aabb = entity.boundingBox.expand(border, border, border);
                MovingObjectPosition intercept = aabb.calculateIntercept(src, dst);
                if (intercept != null) {
                    double d = src.distanceTo(intercept.hitVec);
                    if (d < minDist) {
                        minDist = d;
                        closest = entity;
                    }
                }
            }
        }
        return closest;
    }
}
