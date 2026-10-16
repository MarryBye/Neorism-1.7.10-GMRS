package com.marrybye.neorism.client.render;

import java.util.List;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import com.gtnewhorizon.gtnhlib.client.model.WorldContext;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;
import com.marrybye.neorism.common.blocks.BlockNeorism;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Robust particle helper for 1.7.10 blocks and GTNHLib modeled blocks.
 * Bypasses missing particles caused by GTNHLib's air-block query bug on block destruction.
 */
@SideOnly(Side.CLIENT)
public class BlockParticleHelper {

    public static boolean isValidIcon(IIcon icon) {
        if (icon == null) return false;
        String name = icon.getIconName();
        if (name == null || name.isEmpty()) return false;
        return !name.equals("missingno") && !name.contains("missing");
    }

    public static IIcon getParticleIcon(Block block, int meta) {
        // 1. Try GTNHLib BakedModel directly by block instance and meta
        try {
            BlockState state = BlockPropertyRegistry.getBlockState(block, meta);
            if (state != null) {
                BakedModel model = ModelRegistry.getBakedModel(state);
                if (model != null) {
                    IIcon p = model.getParticle(null);
                    if (isValidIcon(p)) {
                        return p;
                    }

                    // Fallback to model quads if model.getParticle returned missingno
                    WorldContext ctx = new WorldContext();
                    ctx.blockState = state;
                    for (ModelQuadFacing facing : ModelQuadFacing.VALUES) {
                        ctx.quadFacing = facing;
                        try {
                            List<ModelQuadView> quads = model.getQuads(ctx);
                            if (quads != null) {
                                for (ModelQuadView quad : quads) {
                                    if (quad != null) {
                                        Object sprite = quad.celeritas$getSprite();
                                        if (sprite instanceof IIcon && isValidIcon((IIcon) sprite)) {
                                            return (IIcon) sprite;
                                        }
                                    }
                                }
                            }
                        } catch (Throwable ignored) {}
                    }
                }
            }
        } catch (Throwable ignored) {}

        // 2. Try block's own registered icon if it is a BlockNeorism
        if (block instanceof BlockNeorism) {
            IIcon fallback = ((BlockNeorism) block).getFallbackParticleIcon();
            if (isValidIcon(fallback)) {
                return fallback;
            }
        }

        // 3. Try standard block icon
        try {
            IIcon icon = block.getIcon(0, meta);
            if (isValidIcon(icon)) {
                return icon;
            }
        } catch (Throwable ignored) {}

        // 4. Safe material fallback so purple/black particles never appear
        Material mat = block.getMaterial();
        if (mat == Material.wood) {
            return Blocks.planks.getIcon(0, 0);
        } else if (mat == Material.ground || mat == Material.grass) {
            return Blocks.dirt.getIcon(0, 0);
        } else if (mat == Material.iron) {
            return Blocks.iron_block.getIcon(0, 0);
        } else if (mat == Material.glass) {
            return Blocks.glass.getIcon(0, 0);
        } else if (mat == Material.sand) {
            return Blocks.sand.getIcon(0, 0);
        } else if (mat == Material.cloth) {
            return Blocks.wool.getIcon(0, 0);
        }
        return Blocks.stone.getIcon(0, 0);
    }

    public static boolean spawnDestroyEffects(Block block, World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        IIcon icon = getParticleIcon(block, meta);
        byte b0 = 4;
        for (int i = 0; i < b0; ++i) {
            for (int j = 0; j < b0; ++j) {
                for (int k = 0; k < b0; ++k) {
                    double px = (double) x + ((double) i + 0.5D) / (double) b0;
                    double py = (double) y + ((double) j + 0.5D) / (double) b0;
                    double pz = (double) z + ((double) k + 0.5D) / (double) b0;
                    EntityDiggingFX fx = (EntityDiggingFX) new EntityDiggingFX(
                        world, px, py, pz,
                        px - (double) x - 0.5D, py - (double) y - 0.5D, pz - (double) z - 0.5D,
                        block, meta
                    ).applyColourMultiplier(x, y, z);
                    if (icon != null) {
                        fx.setParticleIcon(icon);
                    }
                    effectRenderer.addEffect(fx);
                }
            }
        }
        return true;
    }

    public static boolean spawnHitEffects(Block block, World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        int side = target.sideHit;
        int meta = world.getBlockMetadata(x, y, z);
        IIcon icon = getParticleIcon(block, meta);

        float f = 0.1F;
        double px = (double) x + world.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinX();
        double py = (double) y + world.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinY();
        double pz = (double) z + world.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinZ();

        if (side == 0) py = (double) y + block.getBlockBoundsMinY() - (double) f;
        if (side == 1) py = (double) y + block.getBlockBoundsMaxY() + (double) f;
        if (side == 2) pz = (double) z + block.getBlockBoundsMinZ() - (double) f;
        if (side == 3) pz = (double) z + block.getBlockBoundsMaxZ() + (double) f;
        if (side == 4) px = (double) x + block.getBlockBoundsMinX() - (double) f;
        if (side == 5) px = (double) x + block.getBlockBoundsMaxX() + (double) f;

        EntityDiggingFX fx = (EntityDiggingFX) new EntityDiggingFX(
            world, px, py, pz, 0.0D, 0.0D, 0.0D, block, meta, side
        ).applyColourMultiplier(x, y, z);
        fx.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
        if (icon != null) {
            fx.setParticleIcon(icon);
        }
        effectRenderer.addEffect(fx);
        return true;
    }
}
