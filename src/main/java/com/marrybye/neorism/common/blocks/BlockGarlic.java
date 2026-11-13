package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Garlic Crop Block (8 growth stages using 4 stage textures, drops and plants ModItems.garlic).
 * Canonical port of TeamLapen's GarlicBlock.
 */
public class BlockGarlic extends BlockCrops {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockGarlic() {
        super();
        setBlockName("neorism.garlic");
    }

    @Override
    protected Item func_149866_i() {
        return ModItems.garlic;
    }

    @Override
    protected Item func_149865_P() {
        return ModItems.garlic;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = Math.max(0, Math.min(7, world.getBlockMetadata(x, y, z)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (2.0F + meta) / 16.0F, 1.0F);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && world.getBlockMetadata(x, y, z) > 5 && entity instanceof EntityLivingBase) {
            if (Helper.isVampire(entity) || entity instanceof IVampireEntity) {
                EntityLivingBase living = (EntityLivingBase) entity;
                if (living.ticksExisted % 20 == 0) {
                    living.attackEntityFrom(ModDamageSource.GARLIC, 1.0F);
                    living.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 80, 0));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (icons == null) return null;
        if (meta < 2) return icons[0];
        if (meta < 4) return icons[1];
        if (meta < 7) return icons[2];
        return icons[3];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons = new IIcon[4];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = register.registerIcon(Neorism.MODID + ":garlic_stage" + i);
        }
    }
}
