package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.tileentity.TileEntityTent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Hunter Tent Block.
 * Canonical port of TeamLapen's TentBlock.
 * Functions as a 2x2 multiblock shelter for hunters and spawns camp sentries.
 */
public class BlockTent extends BlockContainer {

    // Directions: 0 = SOUTH, 1 = WEST, 2 = NORTH, 3 = EAST
    // Offsets from pos 0 (Right-Front) for pos 1 (Left-Front), pos 2 (Left-Back), pos 3 (Right-Back): {dx, dz}
    private static final int[][][] OFFSETS_FROM_ZERO = new int[][][] {
        // SOUTH (0):
        { {-1, 0}, {-1, -1}, {0, -1} },
        // WEST (1):
        { {0, -1}, {1, -1}, {1, 0} },
        // NORTH (2):
        { {1, 0}, {1, 1}, {0, 1} },
        // EAST (3):
        { {0, 1}, {-1, 1}, {-1, 0} }
    };

    @SideOnly(Side.CLIENT)
    private IIcon iconBack;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;

    private boolean isDestroyingMultiblock = false;

    public BlockTent() {
        super(Material.cloth);
        setBlockName("neorism.tent");
        setHardness(1.0F);
        setResistance(2.0F);
        setStepSound(soundTypeCloth);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return ((meta >> 2) & 3) == 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ((meta >> 2) & 3) == 0 ? new TileEntityTent() : null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int dir = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int[][] offs = OFFSETS_FROM_ZERO[dir];
        int x1 = x + offs[0][0], z1 = z + offs[0][1];
        int x2 = x + offs[1][0], z2 = z + offs[1][1];
        int x3 = x + offs[2][0], z3 = z + offs[2][1];

        if (canReplace(world, x1, y, z1) && canReplace(world, x2, y, z2) && canReplace(world, x3, y, z3)) {
            world.setBlockMetadataWithNotify(x, y, z, (0 << 2) | dir, 3);
            world.setBlock(x1, y, z1, this, (1 << 2) | dir, 3);
            world.setBlock(x2, y, z2, this, (2 << 2) | dir, 3);
            world.setBlock(x3, y, z3, this, (3 << 2) | dir, 3);
        } else {
            world.setBlockToAir(x, y, z);
            if (placer instanceof EntityPlayer && !((EntityPlayer) placer).capabilities.isCreativeMode) {
                dropBlockAsItem(world, x, y, z, new ItemStack(this));
            }
        }
    }

    public static boolean canPlaceTent(World world, int x, int y, int z, int dir) {
        int[][] offs = OFFSETS_FROM_ZERO[dir];
        return canReplaceAt(world, x, y, z) &&
               canReplaceAt(world, x + offs[0][0], y, z + offs[0][1]) &&
               canReplaceAt(world, x + offs[1][0], y, z + offs[1][1]) &&
               canReplaceAt(world, x + offs[2][0], y, z + offs[2][1]);
    }

    public static void placeTent(World world, int x, int y, int z, int dir) {
        int[][] offs = OFFSETS_FROM_ZERO[dir];
        world.setBlock(x, y, z, com.marrybye.neorism.common.core.ModBlocks.tent, (0 << 2) | dir, 3);
        world.setBlock(x + offs[0][0], y, z + offs[0][1], com.marrybye.neorism.common.core.ModBlocks.tent, (1 << 2) | dir, 3);
        world.setBlock(x + offs[1][0], y, z + offs[1][1], com.marrybye.neorism.common.core.ModBlocks.tent, (2 << 2) | dir, 3);
        world.setBlock(x + offs[2][0], y, z + offs[2][1], com.marrybye.neorism.common.core.ModBlocks.tent, (3 << 2) | dir, 3);
    }

    private static boolean canReplaceAt(World world, int x, int y, int z) {
        return world.isAirBlock(x, y, z) || world.getBlock(x, y, z).isReplaceable(world, x, y, z);
    }

    private boolean canReplace(World world, int x, int y, int z) {
        return canReplaceAt(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!isDestroyingMultiblock) {
            isDestroyingMultiblock = true;
            try {
                int pos = (meta >> 2) & 3;
                int dir = meta & 3;
                int x0 = (pos == 0) ? x : x - OFFSETS_FROM_ZERO[dir][pos - 1][0];
                int z0 = (pos == 0) ? z : z - OFFSETS_FROM_ZERO[dir][pos - 1][1];

                int[][] allPos = new int[][] {
                    { x0, z0 },
                    { x0 + OFFSETS_FROM_ZERO[dir][0][0], z0 + OFFSETS_FROM_ZERO[dir][0][1] },
                    { x0 + OFFSETS_FROM_ZERO[dir][1][0], z0 + OFFSETS_FROM_ZERO[dir][1][1] },
                    { x0 + OFFSETS_FROM_ZERO[dir][2][0], z0 + OFFSETS_FROM_ZERO[dir][2][1] }
                };

                for (int[] p : allPos) {
                    if (p[0] == x && p[1] == z) continue;
                    if (world.getBlock(p[0], y, p[1]) == this) {
                        world.setBlockToAir(p[0], y, p[1]);
                    }
                }
            } finally {
                isDestroyingMultiblock = false;
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return ((meta >> 2) & 3) == 0 ? 1 : 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        int meta = world.getBlockMetadata(x, y, z);
        int pos = (meta >> 2) & 3;
        int dir = meta & 3;

        // Delegate to position 0 if activated on another part
        if (pos != 0) {
            int x0 = x - OFFSETS_FROM_ZERO[dir][pos - 1][0];
            int z0 = z - OFFSETS_FROM_ZERO[dir][pos - 1][1];
            if (world.getBlock(x0, y, z0) == this) {
                return onBlockActivated(world, x0, y, z0, player, side, hitX, hitY, hitZ);
            }
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
        blockIcon = register.registerIcon(Neorism.MODID + ":item_tent");
        iconBack = register.registerIcon(Neorism.MODID + ":tent_back");
        iconSide = register.registerIcon(Neorism.MODID + ":wool_colored_white");
        iconTop = register.registerIcon(Neorism.MODID + ":wool_colored_gray");

        // Register textures used in JSON models
        register.registerIcon(Neorism.MODID + ":wool_colored_silver");
        register.registerIcon(Neorism.MODID + ":tent/floor_bl");
        register.registerIcon(Neorism.MODID + ":tent/floor_br");
        register.registerIcon(Neorism.MODID + ":tent/floor_tl");
        register.registerIcon(Neorism.MODID + ":tent/floor_tr");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }
}
