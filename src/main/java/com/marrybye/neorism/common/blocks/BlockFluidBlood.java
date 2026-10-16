package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

/**
 * Liquid Blood block.
 * Heals and restores blood to vampires submerged in it; weakens mortals.
 */
public class BlockFluidBlood extends BlockFluidClassic {

    @SideOnly(Side.CLIENT)
    private IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    private IIcon flowIcon;

    public BlockFluidBlood(Fluid fluid) {
        super(fluid, Material.water);
        setBlockName("neorism.blood_fluid");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        super.onEntityCollidedWithBlock(world, x, y, z, entity);
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (Helper.isVampire(player)) {
                if (player.ticksExisted % 40 == 0) {
                    player.heal(1.0F);
                    VampirePlayer vp = VampirePlayer.get(player);
                    if (vp != null && vp.getBloodStats() != null && vp.getBloodStats().needsBlood()) {
                        vp.getBloodStats().addBlood(1, 0.5F);
                    }
                }
            } else {
                if (player.ticksExisted % 40 == 0) {
                    player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 1));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        stillIcon = register.registerIcon(Neorism.MODID + ":blood_still");
        flowIcon = register.registerIcon(Neorism.MODID + ":blood_flow");
        definedFluid.setIcons(stillIcon, flowIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 0 || side == 1 ? stillIcon : flowIcon;
    }
}
