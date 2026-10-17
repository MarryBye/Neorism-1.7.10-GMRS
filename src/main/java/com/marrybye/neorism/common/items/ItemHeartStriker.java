package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionRestriction;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

/**
 * Canonical Heart Striker corrupted vampire sword with 3 tiers (Normal, Enhanced, Ultimate).
 * Heavy vampiric blade dealing massive damage and siphoning life/blood when charged.
 * Ported 1:1 from TeamLapen's HeartStrikerItem.java.
 */
public class ItemHeartStriker extends ItemVampireSword {

    private static final float[] LIFESTEAL_RATIO = {0.15F, 0.25F, 0.35F};

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHeartStriker() {
        super(ToolMaterial.EMERALD);
        setUnlocalizedName("neorism.heart_striker");
        setCreativeTab(ModCreativeTabs.tabVampire);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!super.hitEntity(stack, target, attacker)) {
            return false;
        }

        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));

        if (!target.worldObj.isRemote && attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            if (Helper.isVampire(player) && getChargePercentage(stack) > 0.0F) {
                float heal = 6.0F * LIFESTEAL_RATIO[tier];

                if (player.shouldHeal()) {
                    player.heal(heal);
                }

                VampirePlayer vp = VampirePlayer.get(player);
                if (vp != null && vp.getBloodStats() != null && vp.getBloodStats().needsBlood()) {
                    vp.getBloodStats().addBlood(1, 0.5F);
                }

                ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 0.8F, 1.2F);
                ModLogger.combat("Heart Striker (Tier %d) siphoned %.1f HP for %s",
                    tier + 1, heal, player.getCommandSenderName());
            }
        }

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
        super.addInformation(stack, player, tooltip, advanced);
    }
}
