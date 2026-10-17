package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockModDoor extends BlockDoor {

    @SideOnly(Side.CLIENT)
    private IIcon[] iconsUpper;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconsLower;

    private final String texturePrefix;
    private Item doorItem;

    public BlockModDoor(String name, String texturePrefix) {
        super(Material.wood);
        this.texturePrefix = texturePrefix;
        setBlockName("neorism." + name);
        setHardness(3.0F);
        setStepSound(soundTypeWood);
        disableStats();
    }

    public void setDoorItem(Item item) {
        this.doorItem = item;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        this.iconsUpper = new IIcon[2];
        this.iconsLower = new IIcon[2];
        this.iconsUpper[0] = register.registerIcon(Neorism.MODID + ":" + texturePrefix + "_top");
        this.iconsLower[0] = register.registerIcon(Neorism.MODID + ":" + texturePrefix + "_bottom");
        this.iconsUpper[1] = new IconFlipped(this.iconsUpper[0], true, false);
        this.iconsLower[1] = new IconFlipped(this.iconsLower[0], true, false);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return this.iconsLower[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (side != 0 && side != 1) {
            int fullMeta = this.func_150012_g(world, x, y, z);
            int dir = fullMeta & 3;
            boolean isOpen = (fullMeta & 4) != 0;
            boolean isUpper = (fullMeta & 8) != 0;
            boolean isHingeLeft = (fullMeta & 16) != 0;

            if (isOpen) {
                if (dir == 0 && side == 2) isHingeLeft = !isHingeLeft;
                else if (dir == 1 && side == 5) isHingeLeft = !isHingeLeft;
                else if (dir == 2 && side == 3) isHingeLeft = !isHingeLeft;
                else if (dir == 3 && side == 4) isHingeLeft = !isHingeLeft;
            } else {
                if (dir == 0 && side == 5) isHingeLeft = !isHingeLeft;
                else if (dir == 1 && side == 3) isHingeLeft = !isHingeLeft;
                else if (dir == 2 && side == 4) isHingeLeft = !isHingeLeft;
                else if (dir == 3 && side == 2) isHingeLeft = !isHingeLeft;
            }

            return isUpper ? this.iconsUpper[isHingeLeft ? 1 : 0] : this.iconsLower[isHingeLeft ? 1 : 0];
        }
        return this.iconsLower[0];
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return (meta & 8) != 0 ? null : (doorItem != null ? doorItem : Item.getItemFromBlock(this));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(World world, int x, int y, int z) {
        return doorItem != null ? doorItem : Item.getItemFromBlock(this);
    }
}
