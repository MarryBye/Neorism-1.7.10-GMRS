package com.marrybye.neorism.common.items;

import java.util.List;

import com.google.common.collect.Multimap;
import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.util.Helper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.items.IItemWithTier;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Armor of Swiftness set (head, chest, legs, feet) with 3 tiers (Normal, Enhanced, Ultimate).
 * Canonical port of TeamLapen's ArmorOfSwiftnessItem.
 */
public class ItemSwiftnessArmor extends ItemArmor implements IItemWithTier, IFactionRestriction {

    public static final int TIERS = 3;
    private static final String[] SUFFIXES = {"normal", "enhanced", "ultimate"};

    private final String partName;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemSwiftnessArmor(ArmorMaterial material, int armorType, String partName) {
        super(material, 0, armorType);
        this.partName = partName;
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.armor_of_swiftness_" + partName);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        return 250 + tier * 300;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        String suffix = tier == 0 ? "" : "_" + SUFFIXES[tier];
        if (slot == 2) {
            return "neorism:textures/entity/equipment/humanoid_leggings/armor_of_swiftness" + suffix + ".png";
        }
        return "neorism:textures/entity/equipment/humanoid/armor_of_swiftness" + suffix + ".png";
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap multimap = super.getAttributeModifiers(stack);
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        // Canonical speed bonuses: Normal 0.035, Enhanced 0.075, Ultimate 0.10
        double speedBonus = tier == 2 ? 0.10D : (tier == 1 ? 0.075D : 0.035D);
        multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(),
            new AttributeModifier(field_111210_e, "Swiftness Armor Speed", speedBonus, 1));
        return multimap;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        String specificKey = "item.neorism.armor_of_swiftness_" + partName + "_" + SUFFIXES[tier] + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String numberedKey = "item.neorism.armor_of_swiftness_" + partName + "." + tier + ".name";
        if (StatCollector.canTranslate(numberedKey)) {
            return StatCollector.translateToLocal(numberedKey).trim();
        }
        String baseKey = "item.neorism.armor_of_swiftness_" + partName + ".name";
        if (StatCollector.canTranslate(baseKey)) {
            return StatCollector.translateToLocal(baseKey).trim() + " (" + (tier + 1) + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TIERS];
        for (int i = 0; i < TIERS; i++) {
            icons[i] = register.registerIcon(Neorism.MODID + ":armor_of_swiftness_" + partName + "_" + SUFFIXES[i]);
        }
        itemIcon = icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        int idx = Math.max(0, Math.min(meta, TIERS - 1));
        return icons != null ? icons[idx] : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < TIERS; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (world.isRemote) return;
        long time = world.getTotalWorldTime();

        // Toxicant penalty for non-hunters every 16 ticks
        if (time % 16 == 8) {
            if (!Helper.isHunter(player)) {
                player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 20, 1));
            }
        }

        // Full set bonus checked on chestplate every 45 ticks (matches reference tick % 45 == 3)
        if (this.armorType == 1 && time % 45 == 3) {
            boolean fullSet = true;
            int minTierOrdinal = 2; // ULTIMATE

            for (int i = 0; i < 4; i++) {
                ItemStack armorPiece = player.getCurrentArmor(i);
                if (armorPiece != null && armorPiece.getItem() instanceof ItemSwiftnessArmor) {
                    minTierOrdinal = Math.min(minTierOrdinal, armorPiece.getItemDamage());
                } else {
                    fullSet = false;
                    break;
                }
            }

            if (fullSet) {
                // Tier 2 (Ultimate): Jump Boost II (amplifier 1)
                // Tier 1 (Enhanced): Jump Boost I (amplifier 0)
                // Tier 0 (Normal): No jump boost (-1)
                if (minTierOrdinal == 2) {
                    player.addPotionEffect(new PotionEffect(Potion.jump.id, 50, 1, false));
                } else if (minTierOrdinal == 1) {
                    player.addPotionEffect(new PotionEffect(Potion.jump.id, 50, 0, false));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        addTierInformation(stack, tooltip);
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.armor_swiftness.desc"));
        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }

    @Override
    public Tier getVampirismTier() {
        return Tier.NORMAL;
    }

    @Override
    public Tier getVampirismTier(ItemStack stack) {
        if (stack == null) return Tier.NORMAL;
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        return Tier.values()[tier];
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.HUNTER;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return 0;
    }

    @Override
    public String getRequiredSkill(ItemStack stack) {
        return null;
    }

    @Override
    public String getCustomMessageKey(ItemStack stack) {
        return "message.vampirism.restriction.hunter_clothing";
    }
}
