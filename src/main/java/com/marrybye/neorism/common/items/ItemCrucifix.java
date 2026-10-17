package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.api.entity.IVampireEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.api.items.IItemWithTier;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModDamageSource;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.faction.FactionRestriction;
import com.marrybye.neorism.common.faction.IFactionRestriction;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Holy crucifix item (Wooden, Iron, Golden).
 * Repels undead and vampire entities when channeled by mortal hunters.
 * Ported 1:1 from TeamLapen's CrucifixItem.java.
 */
public class ItemCrucifix extends ItemNeorism implements IItemWithTier, IFactionRestriction {

    public static final int TIERS = 3;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemCrucifix() {
        super("crucifix");
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public Tier getVampirismTier() {
        return Tier.NORMAL;
    }

    public Tier getVampirismTier(ItemStack stack) {
        int meta = stack != null ? stack.getItemDamage() : 0;
        switch (meta) {
            case 1: return Tier.ENHANCED;
            case 2: return Tier.ULTIMATE;
            default: return Tier.NORMAL;
        }
    }

    @Override
    public IFaction getRequiredFaction(ItemStack stack) {
        return ModFactions.HUNTER;
    }

    @Override
    public int getMinLevel(ItemStack stack) {
        return 1;
    }

    @Override
    public String getRequiredSkill(ItemStack stack) {
        Tier tier = getVampirismTier(stack);
        return tier == Tier.ULTIMATE ? "ultimate_crucifix" : "crucifix_wielder";
    }

    @Override
    public String getCustomMessageKey(ItemStack stack) {
        return "message.vampirism.restriction.holy";
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote) {
            Helper.handleHeldNonVampireItem(stack, entity, isSelected);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!FactionRestriction.canUse(player, stack, true)) {
            return stack;
        }
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        World world = player.worldObj;
        int tier = stack.getItemDamage();
        double radius = tier == 2 ? 10.0D : (tier == 1 ? 8.0D : 4.0D);

        // 1. If player is a vampire, holy power burns them!
        if (Helper.isVampire(player)) {
            if (count % 20 == 0 && !world.isRemote) {
                player.attackEntityFrom(ModDamageSource.causeIndirectHolyWaterDamage(player, player), 2.0F);
                ModSounds.playAtEntity(player, ModSounds.SIZZLE, 0.8F, 1.0F);
                ModLogger.combat("Crucifix burned unholy wielder %s", player.getCommandSenderName());
            }
            if (world.isRemote) {
                world.spawnParticle("smoke", player.posX, player.posY + 1.0D, player.posZ, 0.0D, 0.1D, 0.0D);
            }
            return;
        }

        // 2. Mortal channeling: Repel vampires and undead
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            player.posX - radius, player.posY - radius, player.posZ - radius,
            player.posX + radius, player.posY + radius, player.posZ + radius
        );
        List list = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

        for (Object obj : list) {
            if (obj instanceof EntityLivingBase && obj != player) {
                EntityLivingBase entity = (EntityLivingBase) obj;
                double dist = entity.getDistanceToEntity(player);
                if (dist <= radius) {
                    boolean isVampire = Helper.isVampire(entity) ||
                        (entity instanceof IVampireEntity) ||
                        entity.getClass().getName().toLowerCase().contains("vampire");

                    if (isVampire || entity.isEntityUndead()) {
                        // Push away from player
                        double dx = entity.posX - player.posX;
                        double dz = entity.posZ - player.posZ;
                        double d = Math.sqrt(dx * dx + dz * dz);
                        if (d > 0.01D) {
                            double strength = 0.15D * (1.0D - (dist / radius));
                            entity.motionX += (dx / d) * strength;
                            entity.motionZ += (dz / d) * strength;
                        }

                        // Apply slowness and suppress abilities
                        entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 30, 1));
                        if (isVampire) {
                            entity.addPotionEffect(new PotionEffect(ModPotions.garlic.id, 40, 0));
                        }

                        if (world.isRemote && world.rand.nextInt(3) == 0) {
                            world.spawnParticle("magicCrit", entity.posX, entity.posY + entity.height * 0.5D, entity.posZ,
                                (world.rand.nextDouble() - 0.5D) * 0.2D, 0.2D, (world.rand.nextDouble() - 0.5D) * 0.2D);
                        }
                    }
                }
            }
        }

        // Visual sparkles around the wielder
        if (world.isRemote && count % 5 == 0) {
            world.spawnParticle("enchantmenttable", player.posX, player.posY + 1.2D, player.posZ,
                (world.rand.nextDouble() - 0.5D) * 0.5D, 0.3D, (world.rand.nextDouble() - 0.5D) * 0.5D);
        }

        if (count % 20 == 0 && !world.isRemote) {
            ModSounds.playAtEntity(player, ModSounds.BEAM_ENTER, 0.4F, 1.4F);
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int tier = stack.getItemDamage();
        String specificKey = tier == 2 ? "item.neorism.crucifix_ultimate.name" :
            (tier == 1 ? "item.neorism.crucifix_enhanced.name" : "item.neorism.crucifix_normal.name");

        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[3];
        icons[0] = register.registerIcon(Neorism.MODID + ":crucifix_wooden");
        icons[1] = register.registerIcon(Neorism.MODID + ":crucifix_iron");
        icons[2] = register.registerIcon(Neorism.MODID + ":crucifix_gold");
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
        int tier = stack.getItemDamage();
        Tier vTier = getVampirismTier(stack);
        if (vTier != Tier.NORMAL) {
            EnumChatFormatting format = vTier == Tier.ENHANCED ? EnumChatFormatting.YELLOW : EnumChatFormatting.AQUA;
            tooltip.add(format + StatCollector.translateToLocal("tooltip.neorism.tier." + vTier.getName()));
        }

        double radius = tier == 2 ? 10.0D : (tier == 1 ? 8.0D : 4.0D);
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("tooltip.neorism.crucifix", radius));

        FactionRestriction.addTooltipIfExist(player, stack, tooltip);
    }
}
