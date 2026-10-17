package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * Aristocratic Vampire clothing (crown, hat, robe legs, boots).
 * Gives noble vampire stature, resistance, and blood capacity enhancement.
 */
public class ItemVampireClothing extends ItemArmor {

    private final String pieceName;

    public ItemVampireClothing(ArmorMaterial material, int armorType, String pieceName) {
        super(material, 0, armorType);
        this.pieceName = pieceName;
        setUnlocalizedName("neorism.vampire_clothing_" + pieceName);
        setTextureName(Neorism.MODID + ":vampire_clothing_" + pieceName);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (slot == 2) {
            return "neorism:textures/entity/equipment/humanoid_leggings/vampire_clothing_legs.png";
        }
        return "neorism:textures/entity/equipment/humanoid/vampire_clothing_" + pieceName + ".png";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = "item.neorism.vampire_clothing_" + pieceName + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void onArmorTick(net.minecraft.world.World world, EntityPlayer player, ItemStack itemStack) {
        if (!world.isRemote && com.marrybye.neorism.common.util.Helper.isHunter(player)) {
            if (world.getTotalWorldTime() % 40 == 0) {
                player.attackEntityFrom(net.minecraft.util.DamageSource.magic, 1.0F);
                player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.potion.Potion.confusion.id, 60, 0));
                com.marrybye.neorism.common.core.ModSounds.playAtEntity(player, com.marrybye.neorism.common.core.ModSounds.SIZZLE, 0.5F, 1.0F);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tooltip.neorism.vampire_clothing.desc"));
        tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tooltip.neorism.faction_restriction.vampire"));
    }
}
