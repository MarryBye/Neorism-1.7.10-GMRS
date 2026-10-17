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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Pure Blood item in 5 tiers (metadata 0 to 4).
 * Drops from high-tier vampires, vampire barons, and blood infusions.
 * Drinkable by vampires for substantial blood restoration and saturation.
 */
public class ItemPureBlood extends ItemNeorism {

    public static final int TIERS = 5;
    private static final int[] BLOOD_VALUES = {4, 8, 12, 16, 20};
    private static final float[] SATURATION_VALUES = {1.0F, 1.5F, 2.0F, 2.5F, 3.0F};

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemPureBlood() {
        super("pure_blood");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(16);
        setCreativeTab(ModCreativeTabs.tabVampire);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            int tier = Math.min(stack.getItemDamage(), TIERS - 1);
            int bloodAmount = BLOOD_VALUES[tier];
            float sat = SATURATION_VALUES[tier];

            if (Helper.isVampire(player)) {
                VampirePlayer vp = VampirePlayer.get(player);
                if (vp != null && vp.getBloodStats() != null) {
                    vp.getBloodStats().addBlood(bloodAmount, sat);
                    ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 1.0F, 1.0F);
                    ModLogger.combat("Vampire %s drank Pure Blood Tier %d (+%d blood)",
                        player.getCommandSenderName(), tier + 1, bloodAmount);
                }
            } else {
                // Mortals suffer poison and confusion from consuming concentrated unholy blood
                player.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 1));
                player.addPotionEffect(new PotionEffect(Potion.confusion.id, 160, 0));
                ModSounds.playAtEntity(player, ModSounds.VAMPIRE_SCREAM, 0.8F, 1.2F);
            }

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage() + 1;
        String specificKey = "item.neorism.pure_blood." + stack.getItemDamage() + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        return StatCollector.translateToLocal("item.neorism.pure_blood.name") + " (" +
            StatCollector.translateToLocal("text.neorism.tier") + " " + tier + ")";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TIERS];
        for (int i = 0; i < TIERS; i++) {
            icons[i] = register.registerIcon(Neorism.MODID + ":pure_blood_" + i);
        }
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
        int tier = stack.getItemDamage() + 1;
        tooltip.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocalFormatted("tooltip.neorism.pure_blood.tier", tier));
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.pure_blood.desc"));
    }
}
