package com.marrybye.neorism.common.items;

import java.util.List;

import com.google.common.collect.Multimap;
import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.util.Helper;

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
import net.minecraft.world.World;

/**
 * Hunter Coat armor set (head, chest, legs, feet) with 3 tiers (Normal, Enhanced, Ultimate).
 * Grants movement speed attribute bonuses and protection against vampire attacks.
 */
public class ItemHunterArmor extends ItemArmor {

    public static final int TIERS = 3;
    private static final String[] SLOTS = {"head", "chest", "legs", "feet"};
    private static final String[] SUFFIXES = {"normal", "enhanced", "ultimate"};

    private final String partName;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHunterArmor(ArmorMaterial material, int armorType, String partName) {
        super(material, 0, armorType);
        this.partName = partName;
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.hunter_coat_" + partName);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        int baseDurability = 200 + tier * 250;
        return baseDurability;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        String suffix = tier == 0 ? "" : "_" + SUFFIXES[tier];
        if (slot == 2) {
            return "neorism:textures/entity/equipment/humanoid_leggings/hunter_coat" + suffix + ".png";
        }
        return "neorism:textures/entity/equipment/humanoid/hunter_coat" + suffix + ".png";
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (!world.isRemote && Helper.isVampire(player)) {
            if (world.getTotalWorldTime() % 40 == 0) {
                player.attackEntityFrom(com.marrybye.neorism.common.core.ModDamageSource.causeIndirectHolyWaterDamage(player, player), 1.0F);
                player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.potion.Potion.weakness.id, 60, 0));
                com.marrybye.neorism.common.core.ModSounds.playAtEntity(player, com.marrybye.neorism.common.core.ModSounds.SIZZLE, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap multimap = super.getAttributeModifiers(stack);
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        double speedBonus = (tier + 1) * 0.03D; // +3% / +6% / +9% speed per piece
        multimap.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(),
            new AttributeModifier(field_111210_e, "Hunter Armor Speed", speedBonus, 1));
        return multimap;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        String specificKey = "item.neorism.hunter_coat_" + partName + "_" + SUFFIXES[tier] + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String numberedKey = "item.neorism.hunter_coat_" + partName + "." + tier + ".name";
        if (StatCollector.canTranslate(numberedKey)) {
            return StatCollector.translateToLocal(numberedKey).trim();
        }
        String baseKey = "item.neorism.hunter_coat_" + partName + ".name";
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
            icons[i] = register.registerIcon(Neorism.MODID + ":hunter_coat_" + partName + "_" + SUFFIXES[i]);
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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int tier = Math.max(0, Math.min(stack.getItemDamage(), TIERS - 1));
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.hunter_armor.desc"));
        tooltip.add(EnumChatFormatting.BLUE + "+" + ((tier + 1) * 3) + "% " +
            StatCollector.translateToLocal("tooltip.neorism.movement_speed"));
        tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("tooltip.neorism.faction_restriction.hunter"));
    }
}
