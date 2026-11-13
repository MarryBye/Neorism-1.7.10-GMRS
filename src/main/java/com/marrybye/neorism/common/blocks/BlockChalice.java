package com.marrybye.neorism.common.blocks;

import java.util.List;
import java.util.Random;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.vampire.EntityDracula;
import com.marrybye.neorism.common.items.ItemBloodBottle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Ritual Chalice for summoning Dracula in Velmorra Castle.
 * Supports empty (meta 0) and blood-filled (meta 1) states.
 * Ported 1:1 from TeamLapen's ChaliceBlock.java.
 */
public class BlockChalice extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon iconEmpty;
    @SideOnly(Side.CLIENT)
    private IIcon iconFilled;

    public BlockChalice() {
        super(Material.iron);
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setStepSound(soundTypeMetal);
        this.setBlockName("chalice");
        this.setCreativeTab(ModCreativeTabs.tabVampire);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.75F, 0.75F);
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
        return 0; // Always drops empty chalice
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        int meta = world.getBlockMetadata(x, y, z);
        ItemStack held = player.getHeldItem();

        if (meta == 0) {
            if (held != null && isBloodOffering(held)) {
                if (!world.isRemote) {
                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                    }

                    world.setBlockMetadataWithNotify(x, y, z, 1, 3);
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 1.0F, 0.8F);

                    checkAndAwakenDracula(world, x, y, z, player);
                }
                return true;
            } else {
                if (!world.isRemote) {
                    player.addChatMessage(new ChatComponentTranslation("message.vampirism.chalice_empty"));
                }
                return true;
            }
        } else {
            if (!world.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.vampirism.chalice_filled"));
            }
            return true;
        }
    }

    private boolean isBloodOffering(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        if (item == ModItems.vampireBloodBottle || item == ModItems.pureBlood || item == ModItems.sovereignBlood) {
            return true;
        }
        if (item instanceof ItemBloodBottle) {
            return ItemBloodBottle.getBlood(stack) >= ItemBloodBottle.MAX_BLOOD;
        }
        return false;
    }

    private void checkAndAwakenDracula(World world, int x, int y, int z, EntityPlayer player) {
        if (world.provider.dimensionId == Config.dimensionVelmorraId) {
            com.marrybye.neorism.common.world.dimensions.velmorra.DraculaFightManager mgr =
                com.marrybye.neorism.common.world.dimensions.velmorra.DraculaFightManager.get(world);
            if (mgr != null) {
                mgr.onChaliceFilled(x, y, z);
                return;
            }
        }

        // Fallback for non-Velmorra testing
        int filledCount = 0;
        int r = 24;
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    int bx = x + dx;
                    int by = y + dy;
                    int bz = z + dz;
                    if (world.getBlock(bx, by, bz) == this && world.getBlockMetadata(bx, by, bz) == 1) {
                        filledCount++;
                    }
                }
            }
        }

        if (filledCount >= 4) {
            // Check if Dracula is already alive nearby
            AxisAlignedBB arenaAABB = AxisAlignedBB.getBoundingBox(x - 32, y - 10, z - 32, x + 32, y + 20, z + 32);
            List<?> draculas = world.getEntitiesWithinAABB(EntityDracula.class, arenaAABB);
            if (draculas.isEmpty()) {
                // Awaken Dracula with theatrical lightning and sound!
                world.addWeatherEffect(new EntityLightningBolt(world, x + 0.5D, y + 2.0D, z + 0.5D));
                EntityDracula dracula = new EntityDracula(world);
                dracula.setLocationAndAngles(x + 0.5D, y + 1.0D, z + 0.5D, 0.0F, 0.0F);
                dracula.onSpawnWithEgg(null);
                world.spawnEntityInWorld(dracula);

                world.playSoundEffect(x + 0.5D, y + 1.0D, z + 0.5D, ModSounds.VAMPIRE_SCREAM, 2.0F, 0.6F);

                List<?> players = world.getEntitiesWithinAABB(EntityPlayer.class, arenaAABB);
                for (Object pObj : players) {
                    if (pObj instanceof EntityPlayer) {
                        ((EntityPlayer) pObj).addChatMessage(new ChatComponentTranslation("message.vampirism.dracula_awakened"));
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (world.getBlockMetadata(x, y, z) == 1) {
            // Crimson sacrificial particles
            double px = x + 0.5D + (rand.nextDouble() - 0.5D) * 0.3D;
            double py = y + 0.7D;
            double pz = z + 0.5D + (rand.nextDouble() - 0.5D) * 0.3D;
            world.spawnParticle("reddust", px, py, pz, 0.5D, 0.0D, 0.0D);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconEmpty = register.registerIcon(Neorism.MODID + ":chalice");
        iconFilled = register.registerIcon(Neorism.MODID + ":chalice");
        this.blockIcon = iconEmpty;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return meta == 1 ? iconFilled : iconEmpty;
    }
}
