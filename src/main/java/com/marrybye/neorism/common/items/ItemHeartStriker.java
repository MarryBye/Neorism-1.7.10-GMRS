package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.VampirePlayer;

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
 * Heart Striker corrupted vampire sword with 3 tiers (Normal, Enhanced, Ultimate).
 * Dark vampiric blade siphoning vital energy and restoring health/blood to the wielder.
 */
public class ItemHeartStriker extends ItemSword {

    public static final int TIERS = 3;
    private static final float[] LIFESTEAL_RATIO = {0.15F, 0.25F, 0.35F};
    private static final int[] MAX_DURABILITY = {450, 950, 2000};

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHeartStriker() {
        super(ToolMaterial.EMERALD);
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.heart_striker");
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

        if (!target.worldObj.isRemote && attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            if (!Helper.isVampire(player)) {
                player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.potion.Potion.wither.id, 60, 0));
                ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 0.5F, 0.8F);
                ModLogger.combat("Heart Striker withered non-vampire wielder %s", player.getCommandSenderName());
                stack.damageItem(1, attacker);
                return true;
            }

            float heal = 6.0F * LIFESTEAL_RATIO[tier];

            if (player.shouldHeal()) {
                player.heal(heal);
            }

            if (Helper.isVampire(player)) {
                VampirePlayer vp = VampirePlayer.get(player);
                if (vp != null && vp.getBloodStats() != null && vp.getBloodStats().needsBlood()) {
                    vp.getBloodStats().addBlood(1, 0.5F);
                }
            }

            ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 0.8F, 1.2F);
            ModLogger.combat("Heart Striker (Tier %d) siphoned %.1f HP for %s",
                tier + 1, heal, player.getCommandSenderName());
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
        String specificKey = "item.neorism.heart_striker_" + suffix + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String numberedKey = "item.neorism.heart_striker." + tier + ".name";
        if (StatCollector.canTranslate(numberedKey)) {
            return StatCollector.translateToLocal(numberedKey).trim();
        }
        String baseKey = "item.neorism.heart_striker.name";
        if (StatCollector.canTranslate(baseKey)) {
            return StatCollector.translateToLocal(baseKey).trim() + " (" + (tier + 1) + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TIERS];
        icons[0] = register.registerIcon(Neorism.MODID + ":heart_striker_normal");
        icons[1] = register.registerIcon(Neorism.MODID + ":heart_striker_enhanced");
        icons[2] = register.registerIcon(Neorism.MODID + ":heart_striker_ultimate");
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
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.heart_striker.desc"));
        tooltip.add(EnumChatFormatting.DARK_RED + "+" + (int) (LIFESTEAL_RATIO[tier] * 100) + "% " +
            StatCollector.translateToLocal("tooltip.neorism.lifesteal"));
    }
}
