package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.IFactionRestriction;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Alchemical Oil Bottles crafted in the cauldron and applied to hunter weapons, armor, or tools.
 * Ported from TeamLapen's ModOils.java and OilBottleItem.java.
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

    public static final int[] OIL_COLORS = {
        0x7E6D27, // plant
        0xB1096B, // vampire_blood
        0xFF1785, // sovereign_blood
        0x4E9331, // poison
        0x484D48, // weakness
        0x5A6C81, // slowness
        0xF82423, // healing
        0xE49A3A, // fire_resistance
        0x7CAFC6, // swiftness
        0xCD5CAB, // regeneration
        0x1F1FA1, // night_vision
        0x932423, // strength
        0x22FF4C, // jump
        0x2E5299, // water_breathing
        0x7F8392, // invisibility
        0xF6D7B7, // slow_falling
        0x339900, // luck
        0x430A09, // harm
        0xEB5E28, // smelt
        0x0B4D42, // teleport
        0x888800, // evasion
        0xFFFFFF, // garlic
        0xFF2211, // spitfire
        0x7A0073, // vampire_killer
        0xAA0000  // bleeding
    };

    public static int getOilColor(int meta) {
        if (meta >= 0 && meta < OIL_COLORS.length) {
            return OIL_COLORS[meta];
        }
        return 0xFFFFFF;
    }

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
        if (oilType == null) return -1;
        for (int i = 0; i < OIL_NAMES.length; i++) {
            if (OIL_NAMES[i].equalsIgnoreCase(oilType)) return i;
        }
        return -1;
    }

    /**
     * Oils that can be applied to melee weapons (canonical EffectWeaponOils available in 1.7.10
     * plus Neorism combat oils: garlic, spitfire, vampire_killer, bleeding).
     */
    public static boolean isWeaponOil(int meta) {
        return (meta >= 3 && meta <= 14) || meta == 17 || (meta >= 21 && meta <= 24);
    }

    /**
     * Smelting oil (meta 18) applies to pickaxes.
     */
    public static boolean isToolOil(int meta) {
        return meta == 18;
    }

    /**
     * Evasion oil (meta 20) applies to armor.
     */
    public static boolean isArmorOil(int meta) {
        return meta == 20;
    }

    public static boolean isApplicableOil(int meta) {
        return isWeaponOil(meta) || isToolOil(meta) || isArmorOil(meta);
    }

    /**
     * Canonical applicability check matching TeamLapen's ApplicableOilRecipe:
     * rejects crafting-only/quarrel-only oils (plant, vampire_blood, sovereign_blood, teleport, etc.)
     * and forbids non-hunter (e.g. vampire-restricted) weapons and armor.
     */
    public static boolean canApplyOil(int oilMeta, ItemStack target) {
        if (target == null || target.getItem() == null || !isApplicableOil(oilMeta)) {
            return false;
        }
        Item item = target.getItem();
        if (item instanceof IFactionRestriction) {
            IFaction req = ((IFactionRestriction) item).getRequiredFaction(target);
            if (req != null && req != ModFactions.HUNTER) {
                return false;
            }
        }
        if (isWeaponOil(oilMeta)) {
            return (item instanceof ItemSword || item instanceof ItemHunterAxe) && !(item instanceof ItemVampireSword);
        }
        if (isToolOil(oilMeta)) {
            return item instanceof ItemPickaxe || item.getToolClasses(target).contains("pickaxe");
        }
        if (isArmorOil(oilMeta)) {
            return item instanceof ItemArmor;
        }
        return false;
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

    public static void applyOil(ItemStack target, int oilMeta) {
        if (target == null) return;
        if (!target.hasTagCompound()) {
            target.setTagCompound(new NBTTagCompound());
        }
        target.getTagCompound().setString("appliedOil", getOilType(oilMeta));
        target.getTagCompound().setInteger("oilCharges", CHARGES_PER_BOTTLE);
    }

    public static void consumeOilCharge(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("oilCharges")) {
            int charges = stack.getTagCompound().getInteger("oilCharges") - 1;
            if (charges <= 0) {
                stack.getTagCompound().removeTag("appliedOil");
                stack.getTagCompound().removeTag("oilCharges");
                if (stack.getTagCompound().hasNoTags()) {
                    stack.setTagCompound(null);
                }
            } else {
                stack.getTagCompound().setInteger("oilCharges", charges);
            }
        }
    }

    public static String getLocalizedOilName(String type) {
        if (type == null || type.isEmpty()) return "";
        String[] keys = {
            "oil.neorism." + type,
            "oil.vampirism." + type,
            "item.neorism.oil." + type + ".name"
        };
        for (String key : keys) {
            if (StatCollector.canTranslate(key)) {
                return StatCollector.translateToLocal(key).trim();
            }
        }
        return "Oil of " + type;
    }

    public static String getLocalizedOilDesc(String type) {
        if (type == null || type.isEmpty()) return null;
        String[] keys = {
            "oil.neorism." + type + ".desc",
            "oil.vampirism." + type + ".desc",
            "item.neorism.oil." + type + ".desc"
        };
        for (String key : keys) {
            if (StatCollector.canTranslate(key)) {
                return StatCollector.translateToLocal(key).trim();
            }
        }
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.neorism.oil." + getOilType(stack.getItemDamage());
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return getLocalizedOilName(getOilType(stack.getItemDamage()));
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
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 1 ? iconOverlay : iconBottle;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 1) {
            return getOilColor(stack.getItemDamage());
        }
        return 0xFFFFFF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < TYPES; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int meta = stack.getItemDamage();
        String type = getOilType(meta);
        String desc = getLocalizedOilDesc(type);
        if (desc != null && !desc.isEmpty()) {
            list.add(EnumChatFormatting.GRAY + desc);
        }
        if (isApplicableOil(meta)) {
            list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.neorism.oil.charges") + ": " + CHARGES_PER_BOTTLE);
            list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("tooltip.neorism.oil.apply_hint"));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int oilMeta = stack.getItemDamage();
        if (!isApplicableOil(oilMeta)) {
            return stack;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack slot = player.inventory.getStackInSlot(i);
            if (slot != null && slot != stack && canApplyOil(oilMeta, slot)) {
                if (!world.isRemote) {
                    applyOil(slot, oilMeta);
                    world.playSoundAtEntity(player, "random.fizz", 0.5F, 1.5F);
                    if (!player.capabilities.isCreativeMode) {
                        stack.stackSize--;
                    }
                }
                return stack;
            }
        }
        return stack;
    }
}
