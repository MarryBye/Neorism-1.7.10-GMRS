package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Garlic Diffuser Core item (Normal, Long, Strong).
 * Emits a localized garlic aura around the carrier weakening approaching vampires.
 */
public class ItemGarlicDiffuserCore extends ItemNeorism {

    public static final int TIERS = 3;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemGarlicDiffuserCore() {
        super("garlic_diffuser_core");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(16);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity holder, int slot, boolean isSelected) {
        if (!world.isRemote && holder.ticksExisted % 20 == 0 && holder instanceof EntityPlayer) {
            double r = stack.getItemDamage() == 1 ? 5.0D : 3.5D;
            int amp = stack.getItemDamage() == 2 ? 1 : 0;

            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                holder.posX - r, holder.posY - r, holder.posZ - r,
                holder.posX + r, holder.posY + r, holder.posZ + r
            );
            List list = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

            for (Object obj : list) {
                if (obj instanceof EntityLivingBase) {
                    EntityLivingBase living = (EntityLivingBase) obj;
                    boolean isVampire = Helper.isVampire(living) ||
                        (living instanceof IVampireEntity) ||
                        living.getClass().getName().toLowerCase().contains("vampire");

                    if (isVampire) {
                        living.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 60, amp));
                    }
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage();
        String specificKey = tier == 2 ? "tile.neorism.garlic_diffuser_core_strong.name" :
            (tier == 1 ? "tile.neorism.garlic_diffuser_core_long.name" : "tile.neorism.garlic_diffuser_core.name");

        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[3];
        icons[0] = register.registerIcon(Neorism.MODID + ":garlic_diffuser_core");
        icons[1] = register.registerIcon(Neorism.MODID + ":garlic_diffuser_core_long");
        icons[2] = register.registerIcon(Neorism.MODID + ":garlic_diffuser_core_strong");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (damage < 0 || damage >= icons.length) damage = 0;
        return icons[damage];
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
        int tier = stack.getItemDamage();
        if (tier == 2) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("tooltip.neorism.garlic_diffuser_strong", 3, 3));
        } else if (tier == 1) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("tooltip.neorism.garlic_diffuser_long", 5, 5));
        } else {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.garlic_diffuser_core"));
        }
    }
}
