package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.util.Helper;
import net.minecraft.potion.PotionEffect;

/**
 * Hunter Hat headwear with Broad (meta 0) and Tall (meta 1) variants.
 * Iconic vampire hunter brimmed hats.
 */
public class ItemHunterHat extends ItemArmor implements IFactionRestriction {

    @SideOnly(Side.CLIENT)
    private IIcon iconBroad;
    @SideOnly(Side.CLIENT)
    private IIcon iconTall;

    public ItemHunterHat(ArmorMaterial material) {
        super(material, 0, 0); // Helmet slot
        setHasSubtypes(true);
        setMaxDamage(220);
        setUnlocalizedName("neorism.hunter_hat");
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return stack.getItemDamage() == 1
            ? "neorism:textures/entity/equipment/humanoid/hunter_hat_tall.png"
            : "neorism:textures/entity/equipment/humanoid/hunter_hat_broad.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.model.ModelBiped getArmorModel(net.minecraft.entity.EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        int meta = itemStack != null ? itemStack.getItemDamage() : 0;
        return com.marrybye.neorism.client.model.ModelVampirismArmor.getHunterHatModel(meta, entityLiving);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return stack.getItemDamage() == 1 ? "item.neorism.hunter_hat_tall" : "item.neorism.hunter_hat_broad";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = getUnlocalizedName(stack) + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        return stack.getItemDamage() == 1 ? "Tall Hunter Hat" : "Hunter Hat";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        iconBroad = register.registerIcon(Neorism.MODID + ":hunter_hat_broad");
        iconTall = register.registerIcon(Neorism.MODID + ":hunter_hat_tall");
        itemIcon = iconBroad;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return meta == 1 ? iconTall : iconBroad;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public void onArmorTick(net.minecraft.world.World world, EntityPlayer player, ItemStack itemStack) {
        if (!world.isRemote && (world.getTotalWorldTime() % 16 == 8)) {
            if (!Helper.isHunter(player)) {
                player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 20, 1));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.hunter_hat.desc"));
        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.HUNTER;
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
        return "message.vampirism.restriction.hunter_clothing";
    }
}
