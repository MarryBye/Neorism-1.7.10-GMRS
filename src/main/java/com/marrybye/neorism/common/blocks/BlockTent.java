package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.tileentity.TileEntityTent;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Hunter Tent Block.
 * Canonical port of TeamLapen's TentBlock & TentMainBlock.
 * Functions as a 2x2 multiblock shelter for hunters and spawns camp sentries.
 *
 * Arrangement (looking from entrance towards back in placement direction):
 * 2 3
 * 1 0
 */
public class BlockTent extends BlockContainer {

    // Directions: 0 = SOUTH, 1 = WEST, 2 = NORTH, 3 = EAST
    // Offsets from pos 0 (Front-Right) for pos 1 (Front-Left), pos 2 (Back-Left), pos 3 (Back-Right): {dx, dz}
    private static final int[][][] OFFSETS_FROM_ZERO = new int[][][] {
        // SOUTH (0):
        { {1, 0}, {1, 1}, {0, 1} },
        // WEST (1):
        { {0, 1}, {-1, 1}, {-1, 0} },
        // NORTH (2):
        { {-1, 0}, {-1, -1}, {0, -1} },
        // EAST (3):
        { {0, -1}, {1, -1}, {1, 0} }
    };

    // Canonical sleep position offsets from TentBlock.offsets: [position][facing] -> {dx, dz}
    private static final double[][][] SLEEP_OFFSETS = new double[][][] {
        // position 0: SOUTH (0), WEST (1), NORTH (2), EAST (3)
        { {0.0D, -0.6D}, {1.6D, 0.0D}, {1.0D, 1.6D}, {-0.6D, 1.0D} },
        // position 1: SOUTH (0), WEST (1), NORTH (2), EAST (3)
        { {0.0D, 1.6D}, {-0.6D, 0.0D}, {1.0D, -0.6D}, {1.6D, 1.0D} },
        // position 2: SOUTH (0), WEST (1), NORTH (2), EAST (3)
        { {0.0D, 0.6D}, {0.4D, 0.0D}, {1.0D, 0.4D}, {0.6D, 1.0D} },
        // position 3: SOUTH (0), WEST (1), NORTH (2), EAST (3)
        { {0.0D, 0.4D}, {0.6D, 0.0D}, {1.0D, 0.6D}, {0.4D, 1.0D} }
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
        setHardness(0.6F);
        setResistance(1.0F);
        setStepSound(soundTypeCloth);
        setCreativeTab((CreativeTabs) null);
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
        int dir = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int[][] offs = OFFSETS_FROM_ZERO[dir];
        int x1 = x + offs[0][0], z1 = z + offs[0][1];
        int x2 = x + offs[1][0], z2 = z + offs[1][1];
        int x3 = x + offs[2][0], z3 = z + offs[2][1];

        if (canReplaceAt(world, x1, y, z1) && canReplaceAt(world, x2, y, z2) && canReplaceAt(world, x3, y, z3)) {
            int oppDir = (dir + 2) & 3;
            world.setBlockMetadataWithNotify(x, y, z, (0 << 2) | oppDir, 3);
            world.setBlock(x1, y, z1, this, (1 << 2) | dir, 3);
            world.setBlock(x2, y, z2, this, (2 << 2) | dir, 3);
            world.setBlock(x3, y, z3, this, (3 << 2) | oppDir, 3);
        } else {
            world.setBlockToAir(x, y, z);
            if (placer instanceof EntityPlayer && !((EntityPlayer) placer).capabilities.isCreativeMode) {
                dropBlockAsItem(world, x, y, z, new ItemStack(ModItems.itemTent));
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
        int oppDir = (dir + 2) & 3;
        world.setBlock(x, y, z, ModBlocks.tent, (0 << 2) | oppDir, 3);
        world.setBlock(x + offs[0][0], y, z + offs[0][1], ModBlocks.tent, (1 << 2) | dir, 3);
        world.setBlock(x + offs[1][0], y, z + offs[1][1], ModBlocks.tent, (2 << 2) | dir, 3);
        world.setBlock(x + offs[2][0], y, z + offs[2][1], ModBlocks.tent, (3 << 2) | oppDir, 3);
    }

    private static boolean canReplaceAt(World world, int x, int y, int z) {
        return world.isAirBlock(x, y, z) || world.getBlock(x, y, z).isReplaceable(world, x, y, z);
    }

    private static int getPlacementDir(int pos, int facing) {
        return (pos == 0 || pos == 3) ? ((facing + 2) & 3) : facing;
    }

    private static int[] getMainBlockCoords(int x, int z, int meta) {
        int pos = (meta >> 2) & 3;
        int facing = meta & 3;
        if (pos == 0) {
            return new int[] { x, z };
        }
        int tentDir = getPlacementDir(pos, facing);
        return new int[] {
            x - OFFSETS_FROM_ZERO[tentDir][pos - 1][0],
            z - OFFSETS_FROM_ZERO[tentDir][pos - 1][1]
        };
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        if (!world.isRemote && player != null && !player.capabilities.isCreativeMode) {
            int[] mainCoords = getMainBlockCoords(x, z, meta);
            int x0 = mainCoords[0];
            int z0 = mainCoords[1];

            dropBlockAsItem(world, x0, y, z0, new ItemStack(ModItems.itemTent, 1));

            TileEntity te = world.getTileEntity(x0, y, z0);
            if (te instanceof TileEntityTent && ((TileEntityTent) te).isSpawner()) {
                ItemStack bonus;
                switch (world.rand.nextInt(4)) {
                    case 0:
                        bonus = new ItemStack(Blocks.planks, 1);
                        break;
                    case 1:
                        bonus = new ItemStack(Items.stick, 1);
                        break;
                    default:
                        bonus = new ItemStack(Blocks.wool, 1);
                        break;
                }
                dropBlockAsItem(world, x0, y, z0, bonus);
            }
            ModLogger.debug("Hunter tent harvested by %s at [%d, %d, %d] (main=[%d, %d, %d])",
                player.getCommandSenderName(), x, y, z, x0, y, z0);
        }
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!isDestroyingMultiblock) {
            isDestroyingMultiblock = true;
            try {
                int pos = (meta >> 2) & 3;
                int facing = meta & 3;
                int tentDir = getPlacementDir(pos, facing);
                int[] mainCoords = getMainBlockCoords(x, z, meta);
                int x0 = mainCoords[0];
                int z0 = mainCoords[1];

                int[][] allPos = new int[][] {
                    { x0, z0 },
                    { x0 + OFFSETS_FROM_ZERO[tentDir][0][0], z0 + OFFSETS_FROM_ZERO[tentDir][0][1] },
                    { x0 + OFFSETS_FROM_ZERO[tentDir][1][0], z0 + OFFSETS_FROM_ZERO[tentDir][1][1] },
                    { x0 + OFFSETS_FROM_ZERO[tentDir][2][0], z0 + OFFSETS_FROM_ZERO[tentDir][2][1] }
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
        return 0;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(ModItems.itemTent);
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
        super.onFallenUpon(world, x, y, z, entity, fallDistance * 0.7F);
    }

    @Override
    public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player) {
        return true;
    }

    @Override
    public int getBedDirection(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int thisPos = (meta >> 2) & 3;
        int thisFacing = meta & 3;
        if (thisPos != 0) {
            int[] mainCoords = getMainBlockCoords(x, z, meta);
            if (world.getBlock(mainCoords[0], y, mainCoords[1]) == this) {
                meta = world.getBlockMetadata(mainCoords[0], y, mainCoords[1]);
                thisPos = (meta >> 2) & 3;
                thisFacing = meta & 3;
            }
        }
        return getPlacementDir(thisPos, thisFacing);
    }

    public static void setTentSleepPosition(EntityPlayer player, int x, int y, int z, int position, int facing) {
        double[] offset = SLEEP_OFFSETS[position & 3][facing & 3];
        player.setPosition(x + offset[0], y + 0.0625D, z + offset[1]);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null || fph.getCurrentFaction() != ModFactions.HUNTER || fph.getCurrentLevel() == 0) {
            player.addChatComponentMessage(new ChatComponentTranslation("message.vampirism.tent.cant_use"));
            return true;
        }

        int meta = world.getBlockMetadata(x, y, z);
        int targetX = x;
        int targetZ = z;
        int targetMeta = meta;
        int thisPos = (meta >> 2) & 3;
        if (thisPos != 0) {
            int[] mainCoords = getMainBlockCoords(x, z, meta);
            if (world.getBlock(mainCoords[0], y, mainCoords[1]) == this) {
                targetX = mainCoords[0];
                targetZ = mainCoords[1];
                targetMeta = world.getBlockMetadata(targetX, y, targetZ);
            }
        }

        if (!world.provider.canRespawnHere() || world.getBiomeGenForCoords(targetX, targetZ) == BiomeGenBase.hell) {
            world.setBlockToAir(targetX, y, targetZ);
            world.newExplosion(null, targetX + 0.5D, y + 0.5D, targetZ + 0.5D, 5.0F, true, true);
            return true;
        }

        for (Object obj : world.playerEntities) {
            EntityPlayer otherPlayer = (EntityPlayer) obj;
            if (otherPlayer.isPlayerSleeping() && otherPlayer.playerLocation != null &&
                otherPlayer.playerLocation.posX == targetX &&
                otherPlayer.playerLocation.posY == y &&
                otherPlayer.playerLocation.posZ == targetZ) {
                player.addChatComponentMessage(new ChatComponentTranslation("message.vampirism.tent.occupied"));
                return true;
            }
        }

        EntityPlayer.EnumStatus status = player.sleepInBedAt(targetX, y, targetZ);
        if (status == EntityPlayer.EnumStatus.OK) {
            int finalPos = (targetMeta >> 2) & 3;
            int finalFacing = targetMeta & 3;
            setTentSleepPosition(player, targetX, y, targetZ, finalPos, finalFacing);
            ModLogger.debug("Hunter %s sleeping in tent at [%d, %d, %d] (pos=%d, facing=%d)",
                player.getCommandSenderName(), targetX, y, targetZ, finalPos, finalFacing);
        } else if (status == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
            player.addChatComponentMessage(new ChatComponentTranslation("message.vampirism.tent.night_only"));
        } else if (status == EntityPlayer.EnumStatus.TOO_FAR_AWAY) {
            player.addChatComponentMessage(new ChatComponentTranslation("message.vampirism.tent.too_far_away"));
        } else if (status == EntityPlayer.EnumStatus.NOT_SAFE) {
            player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
        } else {
            player.addChatComponentMessage(new ChatComponentTranslation("message.vampirism.tent.obstructed"));
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
