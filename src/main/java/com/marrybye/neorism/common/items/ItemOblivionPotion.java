package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.skills.SkillHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Oblivion Potion. Resets all allocated skill points on consumption.
 * Ported 1:1 from TeamLapen's OblivionPotionItem.java.
 */
public class ItemOblivionPotion extends ItemNeorism {

    public ItemOblivionPotion() {
        super("oblivion_potion");
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabVampirism);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            SkillHandler sh = SkillHandler.get(player);
            if (sh != null) {
                sh.resetSkills();
                world.playSoundAtEntity(player, "random.levelup", 1.0F, 0.8F);
                player.addChatMessage(new ChatComponentTranslation("chat.neorism.skills_reset"));
            }
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        String key = "tooltip.factionapi.oblivion_potion.desc";
        if (StatCollector.canTranslate(key)) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(key));
        } else {
            list.add(EnumChatFormatting.GRAY + "Resets all assigned faction skill points when consumed.");
        }
    }
}
