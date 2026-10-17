package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Alchemical Weapon Oil Bottles crafted in the cauldron and applied to hunter and vampire blades.
 * Ported 1:1 from TeamLapen's ModOils.java and OilBottleItem.java.
 */
public class ItemOilBottle extends Item {

    public static final String[] OIL_NAMES = {
        "plant", "vampire_blood", "sovereign_blood", "poison", "weakness", "slowness",
        "healing", "fire_resistance", "swiftness", "regeneration", "night_vision", "strength",
        "jump", "water_breathing", "invisibility", "slow_falling", "luck", "harm",
        "smelt", "teleport", "evasion", "garlic", "spitfire", "vampire_killer", "bleeding"
    };

    public static final int TYPES = OIL_NAMES.length;
    public static final int CHARGES_PER_BOTTLE = 32;

    @SideOnly(Side.CLIENT)
    private IIcon iconBottle;
    @SideOnly(Side.CLIENT)
    private IIcon iconOverlay;

    public ItemOilBottle() {
        super();
        setMaxStackSize(16);
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.oil_bottle");
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    public static String getOilType(int meta) {
        if (meta < 0 || meta >= OIL_NAMES.length) return OIL_NAMES[0];
        return OIL_NAMES[meta];
    }

    public static int getMetaForOil(String oilType) {
        for (int i = 0; i < OIL_NAMES.length; i++) {
            if (OIL_NAMES[i].equalsIgnoreCase(oilType)) return i;
        }
        return 0;
    }

    public static String getAppliedOilType(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("appliedOil")) {
            return stack.getTagCompound().getString("appliedOil");
        }
        return null;
    }

    public static int getAppliedOilCharges(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("oilCharges")) {
            return stack.getTagCompound().getInteger("oilCharges");
        }
        return 0;
    }

    public static void consumeOilCharge(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("oilCharges")) {
            int charges = stack.getTagCompound().getInteger("oilCharges") - 1;
            if (charges <= 0) {
                stack.getTagCompound().removeTag("appliedOil");
                stack.getTagCompound().removeTag("oilCharges");
            } else {
                stack.getTagCompound().setInteger("oilCharges", charges);
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.neorism.oil." + getOilType(stack.getItemDamage());
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String type = getOilType(stack.getItemDamage());
        String key = "oil.neorism." + type;
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key);
        }
        String vampKey = "oil.vampirism." + type;
        if (StatCollector.canTranslate(vampKey)) {
            return StatCollector.translateToLocal(vampKey);
        }
        return "Oil of " + type;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        iconBottle = register.registerIcon(Neorism.MODID + ":oil_bottle");
        iconOverlay = register.registerIcon(Neorism.MODID + ":oil_bottle_overlay");
        itemIcon = iconBottle;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < TYPES; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int meta = stack.getItemDamage();
        String descKey = "oil.neorism." + getOilType(meta) + ".desc";
        if (StatCollector.canTranslate(descKey)) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(descKey));
        }
        list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.neorism.oil.charges") + ": " + CHARGES_PER_BOTTLE);
        list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("tooltip.neorism.oil.apply_hint"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Try applying to weapon in hotbar slots
        for (int i = 0; i < 9; i++) {
            ItemStack slot = player.inventory.getStackInSlot(i);
            if (slot != null && slot != stack) {
                if (slot.getItem() instanceof ItemVampireSword || slot.getItem() instanceof ItemHunterAxe) {
                    if (!slot.hasTagCompound()) slot.setTagCompound(new NBTTagCompound());
                    slot.getTagCompound().setString("appliedOil", getOilType(stack.getItemDamage()));
                    slot.getTagCompound().setInteger("oilCharges", CHARGES_PER_BOTTLE);
                    world.playSoundAtEntity(player, "random.fizz", 0.5F, 1.5F);
                    if (!player.capabilities.isCreativeMode) {
                        stack.stackSize--;
                    }
                    return stack;
                }
            }
        }
        return stack;
    }
}
