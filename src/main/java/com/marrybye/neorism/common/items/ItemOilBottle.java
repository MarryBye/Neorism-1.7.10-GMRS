package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;

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
 */
public class ItemOilBottle extends Item {

    public static final int TYPES = 8;
    public static final String[] OIL_NAMES = {
        "empty", "vampire_killer", "garlic", "bleeding", "fire", "poison", "weakness", "slowness"
    };

    public static final int CHARGES_PER_BOTTLE = 32;

    @SideOnly(Side.CLIENT)
    private IIcon iconBottle;
    @SideOnly(Side.CLIENT)
    private IIcon iconStrong;

    public ItemOilBottle() {
        super();
        setMaxStackSize(16);
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.oil_bottle");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    public static String getOilType(int meta) {
        if (meta < 0 || meta >= OIL_NAMES.length) return "empty";
        return OIL_NAMES[meta];
    }

    public static int getMetaForOil(String oilType) {
        for (int i = 0; i < OIL_NAMES.length; i++) {
            if (OIL_NAMES[i].equalsIgnoreCase(oilType)) return i;
        }
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.neorism.oil." + getOilType(stack.getItemDamage());
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String key = "item.neorism.oil." + getOilType(stack.getItemDamage()) + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key);
        }
        return super.getItemStackDisplayName(stack);
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
        String descKey = "item.neorism.oil." + getOilType(meta) + ".desc";
        if (StatCollector.canTranslate(descKey)) {
            list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal(descKey));
        }
        if (meta > 0) {
            list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.neorism.oil.charges") + ": " + CHARGES_PER_BOTTLE);
            list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("tooltip.neorism.oil.apply_hint"));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.getItemDamage() == 0) return stack;

        // Try applying to weapon in hotbar slots
        for (int i = 0; i < 9; i++) {
            ItemStack slotStack = player.inventory.getStackInSlot(i);
            if (slotStack != null && isCoatableWeapon(slotStack)) {
                applyOilToWeapon(slotStack, getOilType(stack.getItemDamage()), CHARGES_PER_BOTTLE);
                world.playSoundAtEntity(player, "random.fizz", 0.7F, 1.2F);
                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(this, 1, 0))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(this, 1, 0), false);
                    }
                }
                return stack;
            }
        }
        return stack;
    }

    public static boolean isCoatableWeapon(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item instanceof net.minecraft.item.ItemSword ||
               item instanceof net.minecraft.item.ItemAxe ||
               item instanceof ItemStake ||
               item instanceof ItemCrossbow;
    }

    public static void applyOilToWeapon(ItemStack weapon, String oilType, int charges) {
        if (weapon == null) return;
        NBTTagCompound tag = weapon.hasTagCompound() ? weapon.getTagCompound() : new NBTTagCompound();
        NBTTagCompound oilTag = new NBTTagCompound();
        oilTag.setString("type", oilType);
        oilTag.setInteger("charges", charges);
        tag.setTag("NeorismOil", oilTag);
        weapon.setTagCompound(tag);
    }

    public static String getAppliedOilType(ItemStack weapon) {
        if (weapon != null && weapon.hasTagCompound() && weapon.getTagCompound().hasKey("NeorismOil")) {
            return weapon.getTagCompound().getCompoundTag("NeorismOil").getString("type");
        }
        return null;
    }

    public static int getAppliedOilCharges(ItemStack weapon) {
        if (weapon != null && weapon.hasTagCompound() && weapon.getTagCompound().hasKey("NeorismOil")) {
            return weapon.getTagCompound().getCompoundTag("NeorismOil").getInteger("charges");
        }
        return 0;
    }

    public static void consumeOilCharge(ItemStack weapon) {
        if (weapon == null || !weapon.hasTagCompound() || !weapon.getTagCompound().hasKey("NeorismOil")) return;
        NBTTagCompound oilTag = weapon.getTagCompound().getCompoundTag("NeorismOil");
        int charges = oilTag.getInteger("charges") - 1;
        if (charges <= 0) {
            weapon.getTagCompound().removeTag("NeorismOil");
        } else {
            oilTag.setInteger("charges", charges);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        iconBottle = register.registerIcon(Neorism.MODID + ":oil_bottle");
        iconStrong = register.registerIcon(Neorism.MODID + ":oil_bottle_strong");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return (meta >= 1 && meta <= 3) ? iconStrong : iconBottle;
    }
}
