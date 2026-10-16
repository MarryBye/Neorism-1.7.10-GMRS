package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.core.ModSounds;
import com.marrybye.neorism.common.entity.projectile.EntityQuarrel;
import com.marrybye.neorism.common.skills.SkillHandler;
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
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Hunter Crossbow with 4 variants (Basic, Enhanced, Double, Tech).
 * Powerful ranged weapon utilizing Quarrels with high velocity and flat trajectory.
 */
public class ItemCrossbow extends ItemBow {

    public static final int TYPES = 4;
    private static final int[] DURABILITY = {384, 650, 500, 1000};
    private static final int[] DRAW_TICKS = {24, 16, 28, 12};
    private static final float[] VELOCITY = {2.8F, 3.5F, 2.8F, 3.2F};

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemCrossbow() {
        super();
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("neorism.crossbow");
        setCreativeTab(ModCreativeTabs.tabMain);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int meta = Math.max(0, Math.min(stack.getItemDamage(), TYPES - 1));
        return DURABILITY[meta];
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    public static int getClipAmmo(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("clipAmmo")) {
            return stack.getTagCompound().getInteger("clipAmmo");
        }
        return 0;
    }

    public static void setClipAmmo(ItemStack stack, int ammo, boolean heavy) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) stack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        stack.getTagCompound().setInteger("clipAmmo", Math.max(0, ammo));
        stack.getTagCompound().setBoolean("clipHeavy", heavy);
    }

    public static boolean isClipHeavy(ItemStack stack) {
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().getBoolean("clipHeavy");
    }

    private boolean reloadClipFromInventory(ItemStack crossbow, EntityPlayer player) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemQuarrelClip) {
                int ammo = ItemQuarrelClip.getAmmoCount(stack);
                if (ammo > 0) {
                    boolean heavy = ItemQuarrelClip.isHeavy(stack);
                    setClipAmmo(crossbow, ammo, heavy);
                    stack.stackSize--;
                    if (stack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(i, null);
                    }
                    player.worldObj.playSoundAtEntity(player, "tile.piston.in", 0.8F, 1.3F);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int meta = Math.max(0, Math.min(stack.getItemDamage(), TYPES - 1));

        // Tech Crossbow: Semi-automatic firing from loaded clip!
        if (meta == 3) {
            int clipAmmo = getClipAmmo(stack);
            if (clipAmmo > 0) {
                boolean isHeavy = isClipHeavy(stack);
                float vel = VELOCITY[meta];

                if (!world.isRemote) {
                    EntityQuarrel bolt = new EntityQuarrel(world, player, vel, isHeavy);
                    world.spawnEntityInWorld(bolt);
                    ModLogger.combat("Player %s semi-auto fired Tech Crossbow (remaining clip: %d, heavy: %b)",
                        player.getCommandSenderName(), clipAmmo - 1, isHeavy);
                }

                world.playSoundAtEntity(player, "random.bow", 0.9F, 1.4F / (itemRand.nextFloat() * 0.2F + 0.9F));
                player.swingItem();

                if (!player.capabilities.isCreativeMode) {
                    setClipAmmo(stack, clipAmmo - 1, isHeavy);
                    stack.damageItem(1, player);
                }

                // If clip emptied, try reloading next clip from inventory
                if (clipAmmo - 1 <= 0 && !player.capabilities.isCreativeMode) {
                    reloadClipFromInventory(stack, player);
                }
                return stack;
            } else {
                // If clip is empty, try loading clip from inventory
                if (reloadClipFromInventory(stack, player)) {
                    world.playSoundAtEntity(player, "tile.piston.in", 0.8F, 1.3F);
                    return stack;
                }
            }
        }

        // Standard draw for other crossbows or Tech without clip
        if (player.capabilities.isCreativeMode || findQuarrel(player) != null) {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
        int useDuration = getMaxItemUseDuration(stack) - timeLeft;
        int meta = Math.max(0, Math.min(stack.getItemDamage(), TYPES - 1));
        int requiredDraw = DRAW_TICKS[meta];

        // Hunter skill: Crossbow Technique decreases draw time by 25%
        SkillHandler sh = SkillHandler.get(player);
        if (sh != null && sh.isSkillUnlocked("crossbow_technique")) {
            requiredDraw = Math.max(8, (int) (requiredDraw * 0.75F));
        }

        if (useDuration < requiredDraw && !player.capabilities.isCreativeMode) {
            return;
        }

        ItemStack ammo = findQuarrel(player);
        boolean creative = player.capabilities.isCreativeMode;

        if (creative || ammo != null) {
            boolean isHeavy = ammo != null && ammo.getItem() == ModItems.quarrel && ammo.getItemDamage() == 1;
            float vel = VELOCITY[meta];

            if (!world.isRemote) {
                if (meta == 2) {
                    // Double Crossbow: fires two quarrels with slight divergence
                    EntityQuarrel bolt1 = new EntityQuarrel(world, player, vel, isHeavy);
                    EntityQuarrel bolt2 = new EntityQuarrel(world, player, vel, isHeavy);
                    bolt2.motionX += (world.rand.nextDouble() - 0.5D) * 0.1D;
                    bolt2.motionZ += (world.rand.nextDouble() - 0.5D) * 0.1D;
                    world.spawnEntityInWorld(bolt1);
                    world.spawnEntityInWorld(bolt2);
                } else {
                    EntityQuarrel bolt = new EntityQuarrel(world, player, vel, isHeavy);
                    world.spawnEntityInWorld(bolt);
                }

                ModLogger.combat("Player %s fired Crossbow (type=%d, vel=%.1f, heavy=%b)",
                    player.getCommandSenderName(), meta, vel, isHeavy);
            }

            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.2F / (itemRand.nextFloat() * 0.2F + 0.9F));

            if (!creative && ammo != null) {
                ammo.stackSize--;
                if (ammo.stackSize <= 0) {
                    player.inventory.consumeInventoryItem(ammo.getItem());
                }
                if (meta == 2 && ammo.stackSize > 0) {
                    ammo.stackSize--;
                    if (ammo.stackSize <= 0) {
                        player.inventory.consumeInventoryItem(ammo.getItem());
                    }
                }
            }

            stack.damageItem(1, player);
        }
    }

    private ItemStack findQuarrel(EntityPlayer player) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && (stack.getItem() == ModItems.quarrel || stack.getItem() == Items.arrow)) {
                return stack;
            }
        }
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        int meta = stack.getItemDamage();
        String specificKey;
        switch (meta) {
            case 1: specificKey = "item.neorism.crossbow_enhanced.name"; break;
            case 2: specificKey = "item.neorism.crossbow_double.name"; break;
            case 3: specificKey = "item.neorism.tech_crossbow.name"; break;
            default: specificKey = "item.neorism.crossbow_normal.name"; break;
        }
        if (StatCollector.canTranslate(specificKey)) {
            return StatCollector.translateToLocal(specificKey).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TYPES];
        icons[0] = register.registerIcon(Neorism.MODID + ":crossbow");
        icons[1] = register.registerIcon(Neorism.MODID + ":crossbow_enhanced");
        icons[2] = register.registerIcon(Neorism.MODID + ":crossbow_double");
        icons[3] = register.registerIcon(Neorism.MODID + ":tech_crossbow");
        itemIcon = icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        int idx = Math.max(0, Math.min(meta, TYPES - 1));
        return icons != null ? icons[idx] : itemIcon;
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
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        int meta = Math.max(0, Math.min(stack.getItemDamage(), TYPES - 1));
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.neorism.crossbow.desc"));
        tooltip.add(EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocalFormatted("tooltip.neorism.crossbow.velocity", VELOCITY[meta]));
        if (meta == 3) {
            int clipAmmo = getClipAmmo(stack);
            boolean heavy = isClipHeavy(stack);
            if (clipAmmo > 0) {
                String typeStr = heavy ? StatCollector.translateToLocal("item.neorism.quarrel.heavy.name")
                                       : StatCollector.translateToLocal("item.neorism.quarrel.normal.name");
                tooltip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.neorism.tech_crossbow.loaded") + ": " 
                    + EnumChatFormatting.YELLOW + clipAmmo + "/" + ItemQuarrelClip.MAX_AMMO + " (" + typeStr + ")");
            } else {
                tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tooltip.neorism.tech_crossbow.empty"));
            }
        }
    }
}
