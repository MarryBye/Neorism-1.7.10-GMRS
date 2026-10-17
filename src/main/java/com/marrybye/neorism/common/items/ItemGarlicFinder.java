package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Garlic Finder item helping detect garlic sources.
 * Ported 1:1 from TeamLapen's garlic_finder item.
 */
public class ItemGarlicFinder extends ItemNeorism {

    public ItemGarlicFinder() {
        super("garlic_finder");
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabVampirism);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            int px = (int) player.posX;
            int py = (int) player.posY;
            int pz = (int) player.posZ;
            int radius = 16;
            int found = 0;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -4; dy <= 4; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (world.getBlock(px + dx, py + dy, pz + dz) == com.marrybye.neorism.common.core.ModBlocks.garlicDiffuser) {
                            found++;
                        }
                    }
                }
            }

            if (found > 0) {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.garlic_detected", found));
                world.playSoundAtEntity(player, "random.orb", 0.5F, 1.2F);
            } else {
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.no_garlic_detected"));
            }
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        String key = "tooltip.vampirism.garlic_finder";
        if (StatCollector.canTranslate(key)) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(key));
        }
    }
}
