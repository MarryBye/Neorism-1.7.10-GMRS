package com.marrybye.neorism.common.items;

import java.util.Random;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.entity.hunter.EntityAdvancedHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunter;
import com.marrybye.neorism.common.entity.hunter.EntityHunterTrainer;
import com.marrybye.neorism.common.entity.hunter.EntityTaskmaster;
import com.marrybye.neorism.common.entity.vampire.EntityAdvancedVampire;
import com.marrybye.neorism.common.entity.vampire.EntityVampire;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaron;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaroness;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

/**
 * Custom Faction Spawn Eggs that spawn a random entity of the faction:
 * - Vampire Spawn Egg: spawns random Vampire, Advanced Vampire, or Vampire Baron/Baroness
 * - Hunter Spawn Egg: spawns random Hunter, Advanced Hunter, Hunter Trainer, or Taskmaster
 */
public class ItemFactionSpawnEgg extends ItemNeorism {

    public enum Type {
        VAMPIRE("vampire_spawn_egg"),
        HUNTER("vampire_hunter_spawn_egg");

        public final String textureName;

        Type(String textureName) {
            this.textureName = textureName;
        }
    }

    private final Type eggType;
    private static final Random rand = new Random();

    public ItemFactionSpawnEgg(Type eggType) {
        super(eggType.textureName);
        this.eggType = eggType;
        setMaxStackSize(64);
        setCreativeTab(eggType == Type.VAMPIRE ? ModCreativeTabs.tabVampire : ModCreativeTabs.tabHunter);
    }

    public ItemFactionSpawnEgg(boolean isVampire) {
        this(isVampire ? Type.VAMPIRE : Type.HUNTER);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            Block block = world.getBlock(x, y, z);
            x += Facing.offsetsXForSide[side];
            y += Facing.offsetsYForSide[side];
            z += Facing.offsetsZForSide[side];
            double spawnY = 0.0D;

            if (side == 1 && block.getRenderType() == 11) {
                spawnY = 0.5D;
            }

            Entity entity = createRandomEntity(world, eggType);
            if (entity != null) {
                entity.setLocationAndAngles(x + 0.5D, y + spawnY, z + 0.5D, Math.max(0.0F, rand.nextFloat() * 360.0F), 0.0F);
                if (entity instanceof EntityLiving) {
                    ((EntityLiving) entity).onSpawnWithEgg(null);
                }
                world.spawnEntityInWorld(entity);
                world.playSoundAtEntity(entity, "random.pop", 0.5F, 1.0F);

                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }
            }
            return true;
        }
    }

    private static Entity createRandomEntity(World world, Type type) {
        if (type == Type.VAMPIRE) {
            int roll = rand.nextInt(100);
            if (roll < 60) {
                return new EntityVampire(world);
            } else if (roll < 85) {
                return new EntityAdvancedVampire(world);
            } else if (roll < 95) {
                return new EntityVampireBaron(world);
            } else {
                return new EntityVampireBaroness(world);
            }
        } else {
            int roll = rand.nextInt(100);
            if (roll < 55) {
                return new EntityHunter(world);
            } else if (roll < 80) {
                return new EntityAdvancedHunter(world);
            } else if (roll < 92) {
                return new EntityHunterTrainer(world);
            } else {
                return new EntityTaskmaster(world);
            }
        }
    }
}
