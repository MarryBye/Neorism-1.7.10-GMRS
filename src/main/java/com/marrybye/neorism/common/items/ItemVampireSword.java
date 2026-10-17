package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.items.IBloodChargeable;
import com.marrybye.neorism.api.items.IItemWithTier;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import com.marrybye.neorism.common.skills.SkillHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Canonical base class for Vampire Swords (Heart Striker, Heart Seeker).
 * Ported 1:1 from TeamLapen's VampireSwordItem.java.
 */
public abstract class ItemVampireSword extends ItemSword implements IItemWithTier, IBloodChargeable, IFactionRestriction {

    public static final int TIERS = 3;
    protected static final int[] MAX_DURABILITY = {500, 1750, 2500};

    public ItemVampireSword(ToolMaterial material) {
        super(material);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int tierIdx = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        return MAX_DURABILITY[tierIdx];
    }

    @Override
    public Tier getVampirismTier() {
        return Tier.NORMAL;
    }

    public Tier getVampirismTier(ItemStack stack) {
        int meta = stack != null ? stack.getItemDamage() : 0;
        switch (meta) {
            case 1: return Tier.ENHANCED;
            case 2: return Tier.ULTIMATE;
            default: return Tier.NORMAL;
        }
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.VAMPIRE;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        Tier tier = getVampirismTier(stack);
        switch (tier) {
            case ULTIMATE: return 14;
            case ENHANCED: return 9;
            default: return 4;
        }
    }

    @Override
    public String getRequiredSkill(ItemStack stack) {
        return null;
    }

    @Override
    public String getCustomMessageKey(ItemStack stack) {
        return null;
    }

    @Override
    public boolean canBeCharged(ItemStack stack) {
        return getChargePercentage(stack) < 1.0F;
    }

    @Override
    public int charge(ItemStack stack, int amount) {
        float factor = getChargingFactor(stack);
        float charge = getChargePercentage(stack);
        float actual = Math.min(factor * amount, 1.0F - charge);
        setChargePercentage(stack, charge + actual);
        return (int) (actual / factor);
    }

    protected float getChargingFactor(ItemStack stack) {
        Tier tier = getVampirismTier(stack);
        return 0.005F * 2.0F / (tier.ordinal() + 2.0F);
    }

    protected float getChargeUsage(ItemStack stack) {
        Tier tier = getVampirismTier(stack);
        return 0.015F * (tier.ordinal() + 2.0F) / 2.0F;
    }

    public float getTrained(ItemStack stack, EntityLivingBase player) {
        if (stack == null || !stack.hasTagCompound()) return 0.0F;
        NBTTagCompound tag = stack.getTagCompound();
        if (player instanceof EntityPlayer) {
            String key = "trained_" + player.getUniqueID().toString();
            if (tag.hasKey(key)) return tag.getFloat(key);
        }
        return tag.getFloat("trained");
    }

    public void setTrained(ItemStack stack, EntityLivingBase player, float value) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        float clamped = Math.max(0.0F, Math.min(1.0F, value));
        NBTTagCompound tag = stack.getTagCompound();
        tag.setFloat("trained", clamped);
        if (player instanceof EntityPlayer) {
            tag.setFloat("trained_" + player.getUniqueID().toString(), clamped);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            if (!FactionRestriction.canUse(player, stack, true)) {
                return false;
            }

            if (Helper.isVampire(player)) {
                // Vampire Finisher calculation
                if (!Helper.isVampire(target) && target.getMaxHealth() > 0) {
                    SkillHandler sh = SkillHandler.get(player);
                    boolean hasFinisher = sh != null && sh.isSkillUnlocked("sword_finisher");
                    if (hasFinisher && target.getHealth() <= target.getMaxHealth() * 0.35F) {
                        if (!target.worldObj.isRemote) {
                            target.attackEntityFrom(DamageSource.causePlayerDamage(player), 10000.0F);
                            target.worldObj.playAuxSFX(2002, (int) target.posX, (int) target.posY + 1, (int) target.posZ, 0x880000);
                            ModSounds.playAtEntity(target, ModSounds.BEAM_ENTER, 1.0F, 1.5F);
                            ModLogger.combat("VAMPIRE SWORD FINISHER by %s on %s", player.getCommandSenderName(), target.getCommandSenderName());
                        }
                    }
                }

                // Update sword training on kill
                if (target.getHealth() <= 0.0F) {
                    float trained = getTrained(stack, player);
                    float newTrained = (1.0F - trained) / 15.0F;
                    setTrained(stack, player, trained + newTrained);
                }

                // Consume blood charge
                float charged = getChargePercentage(stack);
                charged -= getChargeUsage(stack);
                setChargePercentage(stack, Math.max(0.0F, charged));
            }
        }

        stack.damageItem(1, attacker);
        return true;
    }

    public boolean isFullyCharged(ItemStack stack) {
        return getChargePercentage(stack) >= 1.0F;
    }

    public void tryName(ItemStack stack, EntityPlayer player) {
        // Reserved for sword naming GUI screen when implemented
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking() && canBeCharged(stack) && Helper.isVampire(player)) {
            SkillHandler sh = SkillHandler.get(player);
            if (sh != null && sh.isSkillUnlocked("blood_charge")) {
                VampirePlayer vp = VampirePlayer.get(player);
                if (vp != null && vp.getBloodStats() != null && vp.getBloodStats().getBloodLevel() >= 2) {
                    if (!world.isRemote) {
                        vp.getBloodStats().removeBlood(2, false);
                        charge(stack, 200);
                        world.playSoundAtEntity(player, "game.potion.smash", 0.5F, 1.5F);
                        ModLogger.combat("Vampire %s manually charged sword with 2 blood units", player.getCommandSenderName());
                    }
                    return stack;
                }
            }
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        Tier tier = getVampirismTier(stack);
        if (tier != Tier.NORMAL) {
            EnumChatFormatting format = tier == Tier.ENHANCED ? EnumChatFormatting.YELLOW : EnumChatFormatting.AQUA;
            tooltip.add(format + StatCollector.translateToLocal("tooltip.neorism.tier." + tier.getName()));
        }

        float charged = getChargePercentage(stack);
        float trained = getTrained(stack, player);
        tooltip.add(EnumChatFormatting.DARK_AQUA + StatCollector.translateToLocal("tooltip.neorism.sword_charged") + ": " + (int) Math.ceil(charged * 100.0F) + "%");
        tooltip.add(EnumChatFormatting.DARK_AQUA + StatCollector.translateToLocal("tooltip.neorism.sword_trained") + ": " + (int) Math.ceil(trained * 100.0F) + "%");

        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }
}
