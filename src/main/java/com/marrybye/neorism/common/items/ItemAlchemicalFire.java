package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Alchemical Fire item placer and crafting component.
 * Ported 1:1 from TeamLapen's AlchemicalFireItem.java.
 */
public class ItemAlchemicalFire extends ItemNeorism {

    @SideOnly(Side.CLIENT)
    private IIcon layer0;
    @SideOnly(Side.CLIENT)
    private IIcon layer1;

    public ItemAlchemicalFire() {
        super("alchemical_fire");
        setMaxStackSize(64);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        layer0 = register.registerIcon(Neorism.MODID + ":alchemical_fire_layer0");
        layer1 = register.registerIcon(Neorism.MODID + ":alchemical_fire_layer1");
        itemIcon = layer0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 1 ? layer1 : layer0;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        x += Facing.offsetsXForSide[side];
        y += Facing.offsetsYForSide[side];
        z += Facing.offsetsZForSide[side];

        if (world.isAirBlock(x, y, z)) {
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            world.setBlock(x, y, z, Blocks.fire);

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        String desc1 = "tooltip.vampirism.alchemical_fire.desc1";
        String desc2 = "tooltip.vampirism.alchemical_fire.desc2";
        if (StatCollector.canTranslate(desc1)) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(desc1));
        }
        if (StatCollector.canTranslate(desc2)) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(desc2));
        }
    }
}
