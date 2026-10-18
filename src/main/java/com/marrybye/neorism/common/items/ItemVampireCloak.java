package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import com.marrybye.neorism.common.util.Helper;
import net.minecraft.potion.PotionEffect;

/**
 * Vampire Cloak wearable armor in 16 dye colors (metadata 0 to 15).
 * Distinctive vampire mantle providing nocturnal camouflage and noble prestige.
 */
public class ItemVampireCloak extends ItemArmor implements IFactionRestriction {

    public static final String[] COLORS = {
        "black", "red", "green", "brown", "blue", "purple", "cyan", "light_gray",
        "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"
    };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemVampireCloak(ArmorMaterial material) {
        super(material, 0, 1); // Chestplate slot
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.vampire_cloak");
        setCreativeTab(ModCreativeTabs.tabVampire);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 350;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        int meta = Math.max(0, Math.min(stack.getItemDamage(), COLORS.length - 1));
        return "neorism:textures/entity/equipment/humanoid/cloak/" + COLORS[meta] + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.model.ModelBiped getArmorModel(net.minecraft.entity.EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        return com.marrybye.neorism.client.model.ModelVampirismArmor.getVampireCloakModel(entityLiving);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack stack) {
        return this.getUnlocalizedName(stack);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = Math.max(0, Math.min(stack.getItemDamage(), COLORS.length - 1));
        return "item.neorism.vampire_cloak_" + COLORS[meta];
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int meta = Math.max(0, Math.min(stack.getItemDamage(), COLORS.length - 1));
        String key = "item.neorism.vampire_cloak_" + COLORS[meta] + ".name";
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key).trim();
        }
        String fallbackKey = "item.vampirism.vampire_cloak_" + COLORS[meta] + ".name";
        if (StatCollector.canTranslate(fallbackKey)) {
            return StatCollector.translateToLocal(fallbackKey).trim();
        }
        String baseKey = "item.neorism.vampire_cloak.name";
        if (StatCollector.canTranslate(baseKey)) {
            return StatCollector.translateToLocal(baseKey).trim() + " (" + COLORS[meta] + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[COLORS.length];
        for (int i = 0; i < COLORS.length; i++) {
            icons[i] = register.registerIcon(Neorism.MODID + ":vampire_cloak_" + COLORS[i]);
        }
        itemIcon = icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        int idx = Math.max(0, Math.min(meta, COLORS.length - 1));
        return icons != null ? icons[idx] : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < COLORS.length; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public void onArmorTick(net.minecraft.world.World world, EntityPlayer player, ItemStack itemStack) {
        if (!world.isRemote && (world.getTotalWorldTime() % 16 == 8)) {
            if (!Helper.isVampire(player)) {
                player.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 20, 1));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("tooltip.neorism.vampire_cloak.desc"));
        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.VAMPIRE;
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
        return "message.vampirism.restriction.vampire_clothing";
    }
}
