package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

/**
 * Pitchfork weapon used by angry villagers and militia against vampires.
 * Deals bonus knockback repelling vampires.
 */
public class ItemPitchfork extends ItemSword {

    public ItemPitchfork() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("neorism.pitchfork");
        setTextureName(Neorism.MODID + ":pitchfork");
        setMaxDamage(250);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean isVampire = Helper.isVampire(target) ||
            (target instanceof IVampireEntity) ||
            target.getClass().getName().toLowerCase().contains("vampire");

        if (isVampire) {
            // Apply bonus knockback
            double dx = -MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180.0F);
            double dz = MathHelper.cos(attacker.rotationYaw * (float) Math.PI / 180.0F);
            target.addVelocity(dx * 0.8D, 0.25D, dz * 0.8D);
            ModSounds.playAtEntity(target, ModSounds.STAKE, 0.8F, 1.3F);
        }

        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = this.getUnlocalizedName(stack) + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.pitchfork.desc"));
        tooltip.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("tooltip.neorism.pitchfork.knockback"));
    }
}
