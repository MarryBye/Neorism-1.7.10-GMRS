package com.marrybye.neorism.common.blocks;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.entity.EntitySit;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Grand Vampire Lord Throne.
 * Allows vampire lords and players to sit majestically upon their seat of power.
 * Ported 1:1 from TeamLapen's ThroneBlock.
 */
public class BlockThrone extends Block {

    public BlockThrone() {
        super(Material.wood);
        this.setHardness(2.5F);
        this.setResistance(10.0F);
        this.setStepSound(soundTypeWood);
        this.setBlockName("throne");
        this.setCreativeTab(ModCreativeTabs.tabVampirism);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.25F, 0.9375F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        // 0: South, 1: West, 2: North, 3: East
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        // Check if throne already occupied
        AxisAlignedBB checkAABB = AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
        List<?> sittingList = world.getEntitiesWithinAABB(EntitySit.class, checkAABB);
        if (!sittingList.isEmpty()) {
            return true;
        }

        int meta = world.getBlockMetadata(x, y, z);
        float sitYaw = 0.0F;
        switch (meta) {
            case 0: sitYaw = 0.0F; break;   // South
            case 1: sitYaw = 90.0F; break;  // West
            case 2: sitYaw = 180.0F; break; // North
            case 3: sitYaw = 270.0F; break; // East
        }

        EntitySit sit = new EntitySit(world, x + 0.5D, y + 0.4D, z + 0.5D, sitYaw);
        world.spawnEntityInWorld(sit);
        player.mountEntity(sit);

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(Neorism.MODID + ":throne");
    }
}
