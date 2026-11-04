package com.marrybye.neorism.common.action.vampire;

import java.util.UUID;

import com.marrybye.neorism.common.action.ActionBase;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.action.ActionRegistry;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.events.PlayerCapabilityEventHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.SundamageRegistry;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;

/**
 * Bat Form transformation ability granting agile flight.
 * Canonical port of TeamLapen's BatVampireAction:
 * Applies -90% health reduction, -100% armor, canonical bat flight speed,
 * and restricts flight in sunlight, water, garlic, or when holding umbrellas.
 */
public class BatFormAction extends ActionBase {

    public static final UUID BAT_HEALTH_MOD_UUID = UUID.fromString("6a35d980-9ec8-49f9-b883-2f08a54101e9");

    public BatFormAction() {
        super("bat", 60, -1, true, 3);
    }

    @Override
    public boolean canUse(EntityPlayer player) {
        if (!super.canUse(player)) return false;

        if (SundamageRegistry.instance().isGettingSundamage(player)) {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.bat.in_sun"));
            }
            return false;
        }
        if (SundamageRegistry.instance().isHoldingUmbrella(player)) {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.bat.has_umbrella"));
            }
            return false;
        }
        if (player.isPotionActive(ModPotions.garlic)) {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.bat.effected_by_garlic"));
            }
            return false;
        }
        if (player.isInWater()) {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.bat.in_water"));
            }
            return false;
        }
        if (player.ridingEntity != null) {
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.action.bat.in_vehicle"));
            }
            return false;
        }

        ActionHandler ah = ActionHandler.get(player);
        if (ah != null && ah.isActive(ActionRegistry.getAction("vampire_rage"))) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onActivated(EntityPlayer player) {
        player.capabilities.allowFlying = true;
        player.capabilities.isFlying = true;
        Helper.setFlySpeed(player, 0.05F);
        player.sendPlayerAbilities();

        // -90% health modifier in bat form
        IAttributeInstance healthAttr = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (healthAttr != null) {
            AttributeModifier mod = healthAttr.getModifier(BAT_HEALTH_MOD_UUID);
            if (mod != null) healthAttr.removeModifier(mod);
            healthAttr.applyModifier(new AttributeModifier(BAT_HEALTH_MOD_UUID, "Bat Health Reduction", -0.90D, 2));
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }

        PlayerCapabilityEventHandler.applyBatDimensions(player);
        ModSounds.playAtEntity(player, ModSounds.BAT_SWARM, 0.8F, 1.2F);
        return true;
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
        PlayerCapabilityEventHandler.applyBatDimensions(player);

        if (!player.capabilities.allowFlying) {
            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
        }
        if (!player.onGround && !player.capabilities.isFlying) {
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        }

        // Drop out if conditions violated
        if (player.isInWater() || SundamageRegistry.instance().isGettingSundamage(player) ||
            SundamageRegistry.instance().isHoldingUmbrella(player) || player.isPotionActive(ModPotions.garlic) ||
            player.ridingEntity != null) {
            ActionHandler ah = ActionHandler.get(player);
            if (ah != null) ah.deactivateAction(this);
            return;
        }

        // Blood upkeep: 1 blood every 60 ticks
        if (!player.worldObj.isRemote && player.ticksExisted % 60 == 0) {
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp != null) {
                if (vp.getBloodStats().getBloodLevel() <= 1) {
                    ActionHandler ah = ActionHandler.get(player);
                    if (ah != null) ah.deactivateAction(this);
                    return;
                }
                vp.getBloodStats().consumeBlood(1);
            }
        }
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
        }
        Helper.setFlySpeed(player, 0.05F);
        player.sendPlayerAbilities();

        // Remove health modifier
        IAttributeInstance healthAttr = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (healthAttr != null) {
            AttributeModifier mod = healthAttr.getModifier(BAT_HEALTH_MOD_UUID);
            if (mod != null) healthAttr.removeModifier(mod);
        }

        Helper.setEntitySize(player, 0.6F, 1.8F);
        player.height = 1.8F;
        player.yOffset = 1.62F;
        player.eyeHeight = player.getDefaultEyeHeight();
        if (player.boundingBox != null) {
            float f = 0.6F / 2.0F;
            player.boundingBox.setBounds(
                player.posX - (double) f, player.boundingBox.minY, player.posZ - (double) f,
                player.posX + (double) f, player.boundingBox.minY + 1.8D, player.posZ + (double) f
            );
        }

        // Safe landing resistance if reverting mid-air
        if (!player.onGround) {
            player.addPotionEffect(new PotionEffect(Potion.resistance.id, 60, 4, false));
        }

        ModSounds.playAtEntity(player, ModSounds.BAT_SWARM, 0.8F, 0.9F);
    }
}
