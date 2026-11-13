package com.marrybye.neorism.common.blocks;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.items.ItemRitualKnife;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Velmorra Altar block used to sacrifice blood and ignite the Velmorra Gateway.
 * Requires highest Vampire Lord rank (Lord Level 5) and an Infused Ritual Knife.
 * Ported 1:1 from TeamLapen's VelmorraAltarBlock.java.
 */
public class BlockVelmorraAltar extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon iconNormal;
    @SideOnly(Side.CLIENT)
    private IIcon iconBloody;

    public BlockVelmorraAltar() {
        super(Material.rock);
        this.setHardness(4.0F);
        this.setResistance(25.0F);
        this.setStepSound(soundTypeStone);
        this.setBlockName("velmorra_altar");
        this.setCreativeTab(ModCreativeTabs.tabVampirism);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
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
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ItemRitualKnife) {
            if (!ItemRitualKnife.isCharged(held)) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.velmorra_altar.need_infused_knife"));
                return true;
            }

            // Blood sacrifice (7.5 damage = ~3.75 hearts)
            player.attackEntityFrom(DamageSource.generic, 7.5F);
            world.setBlockMetadataWithNotify(x, y, z, 1, 3);
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 1.0F, 0.8F);

            FactionPlayerHandler fph = FactionPlayerHandler.get(player);
            if (fph == null || !fph.isVampire() || fph.getLordLevel() < 5) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.velmorra_altar.weak"));
                for (int i = 0; i < 12; i++) {
                    world.spawnParticle("smoke", x + 0.5D, y + 0.8D, z + 0.5D, 0.0D, 0.1D, 0.0D);
                }
                return true;
            }

            // Supreme Lord activates the gateway!
            boolean activated = igniteNearestPortal(world, x, y, z);
            if (activated) {
                world.addWeatherEffect(new EntityLightningBolt(world, x + 0.5D, y + 1.0D, z + 0.5D));
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.velmorra_altar.activated"));
            } else {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.velmorra_altar.no_frame"));
            }
            return true;
        } else {
            player.addChatMessage(new ChatComponentTranslation("message.vampirism.velmorra_altar.hint"));
            return true;
        }
    }

    private boolean igniteNearestPortal(World world, int ax, int ay, int az) {
        // Search within 8 blocks for the portal frame
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                for (int dy = -2; dy <= 4; dy++) {
                    int bx = ax + dx;
                    int by = ay + dy;
                    int bz = az + dz;

                    // Check Z-aligned arch (dx width 3, dy height 4)
                    if (isPortalOpeningZ(world, bx, by, bz)) {
                        fillPortalZ(world, bx, by, bz);
                        return true;
                    }

                    // Check X-aligned arch (dz width 3, dy height 4)
                    if (isPortalOpeningX(world, bx, by, bz)) {
                        fillPortalX(world, bx, by, bz);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isFrameBlock(Block b) {
        return b == ModBlocks.darkStoneBricks ||
               b == ModBlocks.darkStoneTiles ||
               b == ModBlocks.darkStone ||
               b == Blocks.stonebrick ||
               b == Blocks.obsidian;
    }

    private boolean isPortalOpeningZ(World world, int x, int y, int z) {
        // 3 wide along X, 4 high
        for (int ox = 0; ox < 3; ox++) {
            for (int oy = 0; oy < 4; oy++) {
                if (!world.isAirBlock(x + ox, y + oy, z)) {
                    return false;
                }
            }
        }
        // Validate surrounding frame
        for (int ox = 0; ox < 3; ox++) {
            if (!isFrameBlock(world.getBlock(x + ox, y - 1, z))) return false;
            if (!isFrameBlock(world.getBlock(x + ox, y + 4, z))) return false;
        }
        for (int oy = 0; oy < 4; oy++) {
            if (!isFrameBlock(world.getBlock(x - 1, y + oy, z))) return false;
            if (!isFrameBlock(world.getBlock(x + 3, y + oy, z))) return false;
        }
        return true;
    }

    private void fillPortalZ(World world, int x, int y, int z) {
        for (int ox = 0; ox < 3; ox++) {
            for (int oy = 0; oy < 4; oy++) {
                world.setBlock(x + ox, y + oy, z, ModBlocks.velmorraPortal, 0, 3);
            }
        }
    }

    private boolean isPortalOpeningX(World world, int x, int y, int z) {
        // 3 wide along Z, 4 high
        for (int oz = 0; oz < 3; oz++) {
            for (int oy = 0; oy < 4; oy++) {
                if (!world.isAirBlock(x, y + oy, z + oz)) {
                    return false;
                }
            }
        }
        // Validate surrounding frame
        for (int oz = 0; oz < 3; oz++) {
            if (!isFrameBlock(world.getBlock(x, y - 1, z + oz))) return false;
            if (!isFrameBlock(world.getBlock(x, y + 4, z + oz))) return false;
        }
        for (int oy = 0; oy < 4; oy++) {
            if (!isFrameBlock(world.getBlock(x, y + oy, z - 1))) return false;
            if (!isFrameBlock(world.getBlock(x, y + oy, z + 3))) return false;
        }
        return true;
    }

    private void fillPortalX(World world, int x, int y, int z) {
        for (int oz = 0; oz < 3; oz++) {
            for (int oy = 0; oy < 4; oy++) {
                world.setBlock(x, y + oy, z + oz, ModBlocks.velmorraPortal, 0, 3);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (world.getBlockMetadata(x, y, z) == 1) {
            double px = x + 0.5D + (rand.nextDouble() - 0.5D) * 0.4D;
            double py = y + 0.8D;
            double pz = z + 0.5D + (rand.nextDouble() - 0.5D) * 0.4D;
            world.spawnParticle("reddust", px, py, pz, 0.4D, 0.0D, 0.0D);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconNormal = register.registerIcon(Neorism.MODID + ":altar_infusion");
        iconBloody = register.registerIcon(Neorism.MODID + ":velmorra_altar_bloody");
        this.blockIcon = iconNormal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return meta == 1 ? iconBloody : iconNormal;
    }
}
