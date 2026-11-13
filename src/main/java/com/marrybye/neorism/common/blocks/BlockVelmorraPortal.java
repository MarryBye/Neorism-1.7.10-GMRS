package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.common.world.dimensions.velmorra.TeleporterVelmorra;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Gothic Gateway to Velmorra, Dracula's realm.
 * Emits ominous purple/red particles and transitions players across dimensions.
 */
public class BlockVelmorraPortal extends BlockBreakable {

    public BlockVelmorraPortal() {
        super("neorism:velmorra_portal", Material.portal, false);
        setTickRandomly(true);
        setHardness(-1.0F);
        setStepSound(soundTypeGlass);
        setLightLevel(0.75F);
        setBlockName("velmorra_portal");
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        this.blockIcon = net.minecraft.init.Blocks.portal.getBlockTextureFromSide(0);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && entity instanceof EntityPlayerMP && entity.ridingEntity == null && entity.riddenByEntity == null) {
            EntityPlayerMP player = (EntityPlayerMP) entity;

            if (player.timeUntilPortal > 0) {
                player.timeUntilPortal = 100;
                return;
            }

            player.timeUntilPortal = 100;
            MinecraftServer server = MinecraftServer.getServer();
            if (server == null) return;

            int currentDim = player.dimension;
            int targetDim = (currentDim == Config.dimensionVelmorraId) ? 0 : Config.dimensionVelmorraId;

            WorldServer targetWorld = server.worldServerForDimension(targetDim);
            if (targetWorld == null) return;

            if (targetDim == Config.dimensionVelmorraId) {
                // Save entry portal coordinates in Overworld so the player returns to the exact location
                player.getEntityData().setInteger("VelmorraReturnX", x);
                player.getEntityData().setInteger("VelmorraReturnY", y);
                player.getEntityData().setInteger("VelmorraReturnZ", z);

                server.getConfigurationManager().transferPlayerToDimension(player, targetDim,
                    new TeleporterVelmorra(targetWorld, 0.5D, 65.0D, -26.0D));
                targetWorld.playSoundAtEntity(player, "portal.travel", 0.7F, 1.0F);
            } else {
                // Return to Overworld: retrieve saved portal coordinates or fall back to bed/spawn
                double tx, ty, tz;
                if (player.getEntityData().hasKey("VelmorraReturnX")) {
                    int rx = player.getEntityData().getInteger("VelmorraReturnX");
                    int ry = player.getEntityData().getInteger("VelmorraReturnY");
                    int rz = player.getEntityData().getInteger("VelmorraReturnZ");
                    tx = rx + 0.5D;
                    ty = ry;
                    tz = rz + (targetWorld.isAirBlock(rx, ry, rz + 2) ? 1.5D : -1.5D);
                } else {
                    net.minecraft.util.ChunkCoordinates bed = player.getBedLocation(0);
                    net.minecraft.util.ChunkCoordinates coords = bed != null ? net.minecraft.entity.player.EntityPlayer.verifyRespawnCoordinates(targetWorld, bed, player.isSpawnForced(0)) : null;
                    if (coords != null) {
                        tx = coords.posX + 0.5D;
                        ty = coords.posY + 0.1D;
                        tz = coords.posZ + 0.5D;
                    } else {
                        int sx = targetWorld.getSpawnPoint().posX;
                        int sy = targetWorld.getTopSolidOrLiquidBlock(sx, targetWorld.getSpawnPoint().posZ);
                        int sz = targetWorld.getSpawnPoint().posZ;
                        tx = sx + 0.5D;
                        ty = sy + 1.0D;
                        tz = sz + 0.5D;
                    }
                }
                server.getConfigurationManager().transferPlayerToDimension(player, targetDim,
                    new TeleporterVelmorra(targetWorld, tx, ty, tz));
                targetWorld.playSoundAtEntity(player, "portal.travel", 0.7F, 1.0F);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(100) == 0) {
            world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
        }
        for (int i = 0; i < 4; ++i) {
            double px = x + rand.nextFloat();
            double py = y + rand.nextFloat();
            double pz = z + rand.nextFloat();
            double vx = (rand.nextFloat() - 0.5D) * 0.5D;
            double vy = (rand.nextFloat() - 0.5D) * 0.5D;
            double vz = (rand.nextFloat() - 0.5D) * 0.5D;
            world.spawnParticle("portal", px, py, pz, vx, vy, vz);
            world.spawnParticle("reddust", px, py, pz, 0.4D, 0.0D, 0.0D);
        }
    }
}
