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

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import com.marrybye.neorism.common.util.Helper;
import net.minecraft.potion.PotionEffect;

/**
 * Aristocratic Vampire clothing (crown, hat, robe legs, boots).
 * Canonical port of TeamLapen's VampireClothingItem.
 */
public class ItemVampireClothing extends ItemArmor implements IFactionRestriction {

    private final String pieceName;

    public ItemVampireClothing(ArmorMaterial material, int armorType, String pieceName) {
        super(material, 0, armorType);
        this.pieceName = pieceName;
        setUnlocalizedName("neorism.vampire_clothing_" + pieceName);
        setTextureName(Neorism.MODID + ":vampire_clothing_" + pieceName);
        setCreativeTab(ModCreativeTabs.tabVampire);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (slot == 2) {
            return "neorism:textures/entity/equipment/humanoid_leggings/vampire_clothing_legs.png";
        }
        return "neorism:textures/entity/equipment/humanoid/vampire_clothing_" + pieceName + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.model.ModelBiped getArmorModel(net.minecraft.entity.EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        return com.marrybye.neorism.client.model.ModelVampirismArmor.getVampireClothingModel(this.pieceName, entityLiving);
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
        if (!world.isRemote && (world.getTotalWorldTime() % 16 == 8)) {
            if (!Helper.isVampire(player)) {
                player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 20, 1));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tooltip.neorism.vampire_clothing.desc"));
        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.VAMPIRE;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return 0;
    }

    @Override
    public String getRequiredSkill(ItemStack stack) {
        return null;
    }

    @Override
    public String getCustomMessageKey(ItemStack stack) {
        return "message.vampirism.restriction.vampire_clothing";
    }
}
