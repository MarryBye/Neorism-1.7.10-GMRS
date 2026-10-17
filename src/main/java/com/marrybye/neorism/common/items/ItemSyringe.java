package com.marrybye.neorism.common.items;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.ExtendedCreature;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.BloodStats;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Syringes for blood extraction and vampire nourishment.
 * Ported directly from TeamLapen's SyringeItem.java and SerumInjectionItem.java.
 */
public class ItemSyringe extends ItemNeorism {

    public enum SyringeType {
        EMPTY,
        BLOOD,
        SERUM
    }

    public static final class Type {
        public static final SyringeType EMPTY = SyringeType.EMPTY;
        public static final SyringeType BLOOD = SyringeType.BLOOD;
        public static final SyringeType SERUM = SyringeType.SERUM;
    }

    private final SyringeType syringeType;

    public ItemSyringe(String name, SyringeType type) {
        super(name);
        this.syringeType = type;
        setMaxStackSize(type == SyringeType.SERUM ? 4 : 16);
        setCreativeTab(ModCreativeTabs.tabVampirism);
    }

    public SyringeType getSyringeType() {
        return syringeType;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        if (syringeType == SyringeType.BLOOD || syringeType == SyringeType.SERUM) {
            return EnumAction.drink;
        }
        return EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (syringeType == SyringeType.EMPTY && target != null) {
            ExtendedCreature ec = ExtendedCreature.get(target);
            if (ec != null && ec.canBeBitten(null)) {
                if (!player.worldObj.isRemote) {
                    int drained = ec.drainBlood(10, 0.5F);
                    if (drained > 0) {
                        ModSounds.playAtEntity(player, ModSounds.VAMPIRE_BITE, 1.0F, 1.0F);
                        ItemStack bloodSyringe = new ItemStack(ModItems.syringeBlood);
                        if (!player.capabilities.isCreativeMode) {
                            stack.stackSize--;
                            if (stack.stackSize <= 0) {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, bloodSyringe);
                            } else if (!player.inventory.addItemStackToInventory(bloodSyringe)) {
                                player.dropPlayerItemWithRandomChoice(bloodSyringe, false);
                            }
                        } else {
                            if (!player.inventory.addItemStackToInventory(bloodSyringe)) {
                                player.dropPlayerItemWithRandomChoice(bloodSyringe, false);
                            }
                        }
                        target.attackEntityFrom(net.minecraft.util.DamageSource.generic, 1.0F);
                        ModLogger.combat("Player %s extracted blood from %s using empty syringe",
                            player.getCommandSenderName(), target.getCommandSenderName());
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (syringeType == SyringeType.BLOOD || syringeType == SyringeType.SERUM) {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (syringeType == SyringeType.BLOOD) {
                if (Helper.isVampire(player)) {
                    VampirePlayer vp = VampirePlayer.get(player);
                    if (vp != null) {
                        BloodStats bs = vp.getBloodStats();
                        bs.addBlood(4);
                        world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
                    }
                } else {
                    player.addPotionEffect(new PotionEffect(Potion.hunger.id, 200, 1));
                    player.addPotionEffect(new PotionEffect(Potion.confusion.id, 100, 0));
                }
            } else if (syringeType == SyringeType.SERUM) {
                player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 1));
                player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 400, 0));
            }

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
                ItemStack empty = new ItemStack(ModItems.syringeEmpty);
                if (stack.stackSize <= 0) {
                    return empty;
                }
                if (!player.inventory.addItemStackToInventory(empty)) {
                    player.dropPlayerItemWithRandomChoice(empty, false);
                }
            }
        }
        return stack;
    }
}
