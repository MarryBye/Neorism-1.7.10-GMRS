package com.marrybye.neorism.common.blocks;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.tileentity.TileEntityGarlicDiffuser;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.world.garlic.EnumStrength;
import com.marrybye.neorism.common.world.garlic.GarlicHandler;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Garlic Diffuser block (Normal, Long, Strong tiers).
 * Canonical port of TeamLapen's GarlicDiffuserBlock.
 */
public class BlockGarlicDiffuser extends BlockModeled implements ITileEntityProvider {

    private final EnumStrength strength;
    private final int chunkRadius;

    public BlockGarlicDiffuser() {
        this("garlic_diffuser_normal", EnumStrength.WEAK, Config.garlicDiffuserNormalDist);
    }

    public BlockGarlicDiffuser(String name, EnumStrength strength, int chunkRadius) {
        super(name, "garlic_diffuser", Material.iron);
        this.strength = strength;
        this.chunkRadius = chunkRadius;
        setHardness(3.5F);
        setStepSound(soundTypeMetal);
        setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 0.75F, 0.875F);
    }

    public EnumStrength getStrength() {
        return strength;
    }

    public int getChunkRadius() {
        return chunkRadius;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityGarlicDiffuser(this.strength, this.chunkRadius);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        if (!Helper.isHunter(player)) {
            player.addChatMessage(com.marrybye.neorism.common.faction.FactionRestriction.getFactionRestrictionMessage(ModFactions.HUNTER));
            return true;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityGarlicDiffuser) {
            TileEntityGarlicDiffuser diffuser = (TileEntityGarlicDiffuser) te;
            ItemStack held = player.getHeldItem();
            if (held != null && held.getItem() == ModItems.purifiedGarlic) {
                diffuser.addFuel(TileEntityGarlicDiffuser.PURIFIED_GARLIC_BURN_TICKS);
                if (!player.capabilities.isCreativeMode) {
                    held.stackSize--;
                }
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.garlic_diffuser.fueled", diffuser.getRemainingSeconds()));
                return true;
            }
            if (diffuser.isActive()) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.garlic_diffuser.status_active",
                    diffuser.getStrength().getName(), 1 + 2 * diffuser.getChunkRadius(), diffuser.getRemainingSeconds()));
            } else if (diffuser.getLitTime() > 0) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.garlic_diffuser.status_booting",
                    diffuser.getBootSecondsRemaining()));
            } else {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.garlic_diffuser.status_empty"));
            }
        }
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && entity instanceof EntityPlayer && Helper.isVampire(entity)) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityGarlicDiffuser) {
                ((TileEntityGarlicDiffuser) te).onTouched((EntityPlayer) entity);
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            GarlicHandler.instance().removeGarlicBlock(world, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
