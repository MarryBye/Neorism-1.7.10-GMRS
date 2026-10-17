package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.tileentity.TileEntityTent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Hunter Tent Block.
 * Canonical port of TeamLapen's TentBlock.
 * Functions as a shelter for hunters and spawns camp sentries.
 */
public class BlockTent extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon iconBack;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;

    public BlockTent() {
        super(Material.cloth);
        setBlockName("neorism.tent");
        setHardness(1.0F);
        setResistance(2.0F);
        setStepSound(soundTypeCloth);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTent();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null || fph.getCurrentFaction() != ModFactions.HUNTER) {
            player.addChatMessage(new ChatComponentTranslation("message.vampirism.tent.cant_use"));
            return true;
        }

        // Hunter sleep logic
        if (world.isDaytime() && !world.isRaining()) {
            player.addChatMessage(new ChatComponentTranslation("message.vampirism.tent.rested"));
            player.heal(4.0F);
        } else {
            EntityPlayer.EnumStatus status = player.sleepInBedAt(x, y, z);
            if (status != EntityPlayer.EnumStatus.OK) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.tent.occupied"));
            }
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconBack = register.registerIcon(Neorism.MODID + ":tent_back");
        iconSide = register.registerIcon(Neorism.MODID + ":wool_colored_white");
        iconTop = register.registerIcon(Neorism.MODID + ":wool_colored_gray");
        blockIcon = iconSide;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) return iconTop;
        if (side == 2 || side == 3) return iconBack;
        return iconSide;
    }
}
