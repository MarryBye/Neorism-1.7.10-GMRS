package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModSounds;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * Heart Seeker holy sword with 3 tiers (Normal, Enhanced, Ultimate).
 * Consecrated blade that deals severe bonus holy damage to vampires and unholy monsters.
 */
public class ItemHeartSeeker extends ItemSword {

    public static final int TIERS = 3;
    private static final float[] BONUS_DAMAGE = {4.0F, 6.5F, 9.5F};
    private static final int[] MAX_DURABILITY = {450, 950, 2000};

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHeartSeeker() {
        super(ToolMaterial.EMERALD);
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.heart_seeker");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        return MAX_DURABILITY[tier];
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));

        boolean isUnholy = Helper.isVampire(target) ||
            target.isEntityUndead() ||
            (target instanceof IVampireEntity) ||
            target.getClass().getName().toLowerCase().contains("vampire");

        if (isUnholy) {
            float bonus = BONUS_DAMAGE[tier];
            if (!target.worldObj.isRemote) {
                target.attackEntityFrom(ModDamageSource.HOLY_WATER, bonus);
                target.worldObj.spawnParticle("crit", target.posX, target.posY + target.height * 0.5D, target.posZ, 0, 0.2D, 0);
                ModSounds.playAtEntity(target, ModSounds.BEAM_ENTER, 0.8F, 1.4F);
                ModLogger.combat("Heart Seeker (Tier %d) smote unholy entity %s for +%.1f holy damage",
                    tier + 1, target.getCommandSenderName(), bonus);
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
        String specificKey = "item.neorism.heart_seeker_" + suffix + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TIERS];
        icons[0] = register.registerIcon(Neorism.MODID + ":heart_seeker_normal");
        icons[1] = register.registerIcon(Neorism.MODID + ":heart_seeker_enhanced");
        icons[2] = register.registerIcon(Neorism.MODID + ":heart_seeker_ultimate");
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
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.heart_seeker.desc"));
        tooltip.add(EnumChatFormatting.YELLOW + "+" + BONUS_DAMAGE[tier] + " " +
            StatCollector.translateToLocal("tooltip.neorism.unholy_bonus_damage"));
    }
}
