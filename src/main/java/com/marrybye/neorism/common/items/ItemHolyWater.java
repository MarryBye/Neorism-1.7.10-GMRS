package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.items.IItemWithTier;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;

/**
 * Drinkable Holy Water bottle (Normal, Enhanced, Ultimate).
 * Canonical port of TeamLapen's HolyWaterBottleItem.
 */
public class ItemHolyWater extends ItemNeorism implements IItemWithTier, IFactionRestriction {

    public static final int TIERS = 3;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemHolyWater() {
        super("holy_water_bottle");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(16);
        setCreativeTab(ModCreativeTabs.tabHunter);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.getBlock(x, y, z) == com.marrybye.neorism.common.core.ModBlocks.cursedEarth) {
            if (!world.isRemote) {
                world.setBlock(x, y, z, net.minecraft.init.Blocks.dirt, 0, 3);
                int uses = stack.getItemDamage() == 2 ? 100 : (stack.getItemDamage() == 1 ? 50 : 25);
                if (!player.capabilities.isCreativeMode && world.rand.nextInt(uses) == 0) {
                    stack.stackSize--;
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.potion.smash", 0.8F, 1.0F);
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (FactionRestriction.canUse(player, stack, true)) {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            int tier = stack.getItemDamage();
            float dmg = tier == 2 ? 22.0F : (tier == 1 ? 14.0F : 8.0F);

            if (Helper.isVampire(player)) {
                // Vampire burns from holy water
                player.attackEntityFrom(ModDamageSource.causeIndirectHolyWaterDamage(player, player), dmg);
                player.addPotionEffect(new PotionEffect(Potion.confusion.id, 160, 0));
                player.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 1));
                ModSounds.playAtEntity(player, ModSounds.SIZZLE, 1.0F, 1.0F);
                ModLogger.combat("Vampire player %s drank holy water tier %d and suffered %.1f holy damage",
                    player.getCommandSenderName(), tier, dmg);
            } else {
                // Non-vampires: cure infection
                if (player.isPotionActive(ModPotions.sanguinare)) {
                    player.removePotionEffect(ModPotions.sanguinare.id);
                    ModSounds.playAtEntity(player, ModSounds.HEARTBEAT, 1.0F, 1.0F);
                    ModLogger.combat("Player %s was cured of Sanguinare Vampiris by drinking holy water",
                        player.getCommandSenderName());
                }
                // Gentle divine restoration
                player.heal(tier == 2 ? 6.0F : (tier == 1 ? 4.0F : 2.0F));
                player.addPotionEffect(new PotionEffect(Potion.regeneration.id, (tier + 1) * 60, 0));
            }

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
                if (stack.stackSize <= 0) {
                    return new ItemStack(Items.glass_bottle);
                }
                if (!player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle))) {
                    player.dropPlayerItemWithRandomChoice(new ItemStack(Items.glass_bottle), false);
                }
            }
        }
        return stack;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage();
        String specificKey = "item.neorism.holy_water_bottle." + tier + ".name";
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        String suffix = tier == 2 ? " (Ultimate)" : (tier == 1 ? " (Enhanced)" : "");
        return StatCollector.translateToLocal("item.neorism.holy_water_bottle.name").trim() + suffix;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[3];
        icons[0] = register.registerIcon(Neorism.MODID + ":holy_water_bottle_normal");
        icons[1] = register.registerIcon(Neorism.MODID + ":holy_water_bottle_enhanced");
        icons[2] = register.registerIcon(Neorism.MODID + ":holy_water_bottle_ultimate");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (damage < 0 || damage >= icons.length) damage = 0;
        return icons[damage];
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
        addTierInformation(stack, tooltip);
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
        return "message.vampirism.restriction.holy";
    }
}
