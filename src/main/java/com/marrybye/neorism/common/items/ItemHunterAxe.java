package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.items.IItemWithTier;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * Canonical Hunter Axe weapon with 3 tiers (Normal, Enhanced, Ultimate).
 * Heavy cleaving weapon dealing extra damage against vampires and harvesting vampire souls.
 * Ported 1:1 from TeamLapen's HunterAxeItem.java.
 */
public class ItemHunterAxe extends ItemSword implements IItemWithTier, IFactionRestriction {

    public static final int TIERS = 3;
    private static final int[] MAX_DURABILITY = {250, 1561, 2031};
    private static final float[] VAMPIRE_MULT = {1.2F, 1.3F, 1.4F};

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHunterAxe() {
        super(ToolMaterial.IRON);
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.hunter_axe");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        return MAX_DURABILITY[tier];
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
        return ModFactions.HUNTER;
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
        return "message.vampirism.restriction.heavy";
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            if (!FactionRestriction.canUse(player, stack, true)) {
                return false;
            }
        }

        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));

        boolean isVampire = Helper.isVampire(target) ||
            (target instanceof IVampireEntity) ||
            target.getClass().getName().toLowerCase().contains("vampire");

        if (isVampire && !target.worldObj.isRemote) {
            float mult = VAMPIRE_MULT[tier];
            float extraDamage = 6.0F * (mult - 1.0F);
            target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(attacker, attacker), extraDamage);
            ModSounds.playAtEntity(target, ModSounds.STAKE, 1.0F, 0.8F);
            ModLogger.combat("Hunter Axe (Tier %d) dealt %.1f extra damage to vampire %s (mult: %.1fx)",
                tier + 1, extraDamage, target.getCommandSenderName(), mult);

            // Drop vampire soul on kill
            if (target.getHealth() <= 0.0F && ModItems.soulOrbVampire != null) {
                target.entityDropItem(new ItemStack(ModItems.soulOrbVampire, 1), 0.5F);
            }
        }

        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage();
        String suffix = tier == 1 ? "enhanced" : (tier == 2 ? "ultimate" : "normal");
        String specificKey = "item.neorism.hunter_axe_" + suffix + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String numberedKey = "item.neorism.hunter_axe." + tier + ".name";
        if (StatCollector.canTranslate(numberedKey)) {
            return StatCollector.translateToLocal(numberedKey).trim();
        }
        String baseKey = "item.neorism.hunter_axe.name";
        if (StatCollector.canTranslate(baseKey)) {
            return StatCollector.translateToLocal(baseKey).trim() + " (" + (tier + 1) + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TIERS];
        icons[0] = register.registerIcon(Neorism.MODID + ":hunter_axe_normal");
        icons[1] = register.registerIcon(Neorism.MODID + ":hunter_axe_enhanced");
        icons[2] = register.registerIcon(Neorism.MODID + ":hunter_axe_ultimate");
        itemIcon = icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        int idx = Math.max(0, Math.min(meta, TIERS - 1));
        return icons != null ? icons[idx] : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < TIERS; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        Tier vTier = getVampirismTier(stack);
        if (vTier != Tier.NORMAL) {
            EnumChatFormatting format = vTier == Tier.ENHANCED ? EnumChatFormatting.YELLOW : EnumChatFormatting.AQUA;
            tooltip.add(format + StatCollector.translateToLocal("tooltip.neorism.tier." + vTier.getName()));
        }

        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.hunter_axe.desc"));
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("tooltip.vampirism.more_damage_against",
            Math.round((VAMPIRE_MULT[tier] - 1.0F) * 100), StatCollector.translateToLocal("text.neorism.vampires")));

        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }
}
