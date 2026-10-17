package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Custom wood boats (Dark Spruce & Cursed Spruce, standard and chest variants).
 */
public class ItemModBoat extends ItemBoat {

    public enum BoatType {
        DARK_SPRUCE("dark_spruce_boat"),
        CURSED_SPRUCE("cursed_spruce_boat"),
        DARK_SPRUCE_CHEST("dark_spruce_chest_boat"),
        CURSED_SPRUCE_CHEST("cursed_spruce_chest_boat");

        public final String name;

        BoatType(String name) {
            this.name = name;
        }
    }

    private final BoatType boatType;

    public ItemModBoat(BoatType type) {
        super();
        this.boatType = type;
        setMaxStackSize(1);
        setUnlocalizedName("neorism." + type.name);
        setTextureName(Neorism.MODID + ":" + type.name);
        setCreativeTab(ModCreativeTabs.tabVampirism);
    }

    public ItemModBoat(String name, boolean cursed, boolean chest) {
        this(cursed ? (chest ? BoatType.CURSED_SPRUCE_CHEST : BoatType.CURSED_SPRUCE) : (chest ? BoatType.DARK_SPRUCE_CHEST : BoatType.DARK_SPRUCE));
    }

    public BoatType getBoatType() {
        return boatType;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        float f = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double posX = player.prevPosX + (player.posX - player.prevPosX) * f;
        double posY = player.prevPosY + (player.posY - player.prevPosY) * f + 1.62D - player.yOffset;
        double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3 vec3 = Vec3.createVectorHelper(posX, posY, posZ);
        float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cosPitch = -MathHelper.cos(-pitch * 0.017453292F);
        float sinPitch = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = sinYaw * cosPitch;
        float f7 = cosYaw * cosPitch;
        double distance = 5.0D;
        Vec3 vec31 = vec3.addVector(f6 * distance, sinPitch * distance, f7 * distance);
        MovingObjectPosition mop = world.rayTraceBlocks(vec3, vec31, true);

        if (mop == null) {
            return stack;
        }

        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;

            if (!world.isRemote) {
                EntityBoat boat = new EntityBoat(world, x + 0.5D, y + 1.0D, z + 0.5D);
                boat.rotationYaw = ((MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) - 1) * 90;
                world.spawnEntityInWorld(boat);
            }

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }
}
