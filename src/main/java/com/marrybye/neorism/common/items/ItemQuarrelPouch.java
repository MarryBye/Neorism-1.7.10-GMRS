package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Quarrel Pouch that holds quarrel ammunition for crossbows.
 * Ported 1:1 from TeamLapen's QuarrelPouchItem.java.
 */
public class ItemQuarrelPouch extends ItemNeorism {

    public static final int MAX_QUARRELS = 128;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemQuarrelPouch() {
        super("quarrel_pouch");
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    public static int getAmmoCount(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("ammoCount")) {
            return stack.getTagCompound().getInteger("ammoCount");
        }
        return 0;
    }

    public static void setAmmoCount(ItemStack stack, int count) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("ammoCount", Math.max(0, Math.min(count, MAX_QUARRELS)));
    }

    public static int[] getQuarrelCounts(ItemStack stack) {
        int[] counts = new int[7];
        counts[0] = getAmmoCount(stack);
        return counts;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[4];
        for (int i = 0; i < 4; i++) {
            icons[i] = register.registerIcon(Neorism.MODID + ":quarrel_pouch_" + i);
        }
        itemIcon = icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        int count = getAmmoCount(stack);
        if (count == 0) return icons[0];
        if (count < MAX_QUARRELS / 3) return icons[1];
        if (count < (MAX_QUARRELS * 2) / 3) return icons[2];
        return icons[3];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return getIconIndex(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            int current = getAmmoCount(stack);
            if (player.isSneaking()) {
                // Dump all ammo from pouch to player inventory
                if (current > 0) {
                    int toGive = current;
                    while (toGive > 0) {
                        int amount = Math.min(toGive, 64);
                        ItemStack quarrels = new ItemStack(ModItems.quarrel, amount, 0);
                        if (!player.inventory.addItemStackToInventory(quarrels)) {
                            player.dropPlayerItemWithRandomChoice(quarrels, false);
                        }
                        toGive -= amount;
                    }
                    setAmmoCount(stack, 0);
                    world.playSoundAtEntity(player, "random.pop", 0.5F, 1.2F);
                }
            } else {
                // Collect quarrels from inventory into pouch
                int space = MAX_QUARRELS - current;
                if (space > 0) {
                    int collected = 0;
                    for (int i = 0; i < player.inventory.mainInventory.length && space > 0; i++) {
                        ItemStack item = player.inventory.mainInventory[i];
                        if (item != null && item.getItem() == ModItems.quarrel) {
                            int take = Math.min(item.stackSize, space);
                            item.stackSize -= take;
                            if (item.stackSize <= 0) {
                                player.inventory.setInventorySlotContents(i, null);
                            }
                            space -= take;
                            collected += take;
                        }
                    }
                    if (collected > 0) {
                        setAmmoCount(stack, current + collected);
                        world.playSoundAtEntity(player, "random.pop", 0.5F, 1.0F);
                    }
                }
            }
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int count = getAmmoCount(stack);
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.quarrel_pouch.ammo") + ": " + EnumChatFormatting.YELLOW + count + "/" + MAX_QUARRELS);
        list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("tooltip.neorism.quarrel_pouch.hint"));
    }
}
