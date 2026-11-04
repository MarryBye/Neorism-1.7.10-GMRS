package com.marrybye.neorism.common.items;

import java.util.List;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.common.core.ModCreativeTabs;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaron;
import com.marrybye.neorism.common.util.ModLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Blood Finder / Baron Dowsing Instrument.
 * Searches and tracks Vampire Barons across the Vampire Forest by level (0..4).
 * Uses canonical pure blood resonance technology without adding custom textures or models.
 */
public class ItemBloodFinder extends ItemNeorism {

    public ItemBloodFinder() {
        super("blood_finder", ModCreativeTabs.tabVampirism);
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(net.minecraft.client.renderer.texture.IIconRegister register) {
        this.itemIcon = register.registerIcon(Neorism.MODID + ":garlic_finder");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        // Radiant dark crimson blood tint on the brass dial
        return 0xD62828;
    }

    public static int getTargetLevel(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("targetLevel")) {
            return stack.getTagCompound().getInteger("targetLevel");
        }
        return -1; // -1 = Any Level
    }

    public static void setTargetLevel(ItemStack stack, int level) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("targetLevel", level);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }

        // Shift + Right Click: Cycle target baron level (-1 -> 0 -> 1 -> 2 -> 3 -> 4 -> -1)
        if (player.isSneaking()) {
            int current = getTargetLevel(stack);
            int next = (current + 2) % 6 - 1; // cycles -1, 0, 1, 2, 3, 4
            setTargetLevel(stack, next);

            String levelName = next < 0 ? "Любой (1-5)" : String.format("Уровень %d (для Чистой крови %d)", next + 1, next);
            player.addChatMessage(new ChatComponentText(
                EnumChatFormatting.DARK_RED + "[Blood Finder] " + EnumChatFormatting.YELLOW + "Целевой ранг: " + EnumChatFormatting.GOLD + levelName));
            world.playSoundAtEntity(player, "random.click", 0.6F, 1.4F);
            return stack;
        }

        // Right Click: Scan loaded radius for Vampire Barons
        int targetLvl = getTargetLevel(stack);
        double searchRadius = 160.0D;
        AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox(
            player.posX - searchRadius, player.posY - 48.0D, player.posZ - searchRadius,
            player.posX + searchRadius, player.posY + 64.0D, player.posZ + searchRadius
        );

        @SuppressWarnings("unchecked")
        List<EntityVampireBaron> barons = world.getEntitiesWithinAABB(EntityVampireBaron.class, searchBox);
        EntityVampireBaron best = null;
        double bestDistSq = Double.MAX_VALUE;

        for (EntityVampireBaron baron : barons) {
            if (baron == null || !baron.isEntityAlive()) continue;
            if (targetLvl >= 0 && baron.getEntityLevel() != targetLvl) continue;

            double distSq = player.getDistanceSqToEntity(baron);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                best = baron;
            }
        }

        if (best != null) {
            int dist = (int) Math.round(Math.sqrt(bestDistSq));
            int bLevel = best.getEntityLevel() + 1; // 1-based display
            double dx = best.posX - player.posX;
            double dz = best.posZ - player.posZ;
            String direction = getDirectionString(dx, dz);

            player.addChatMessage(new ChatComponentText(
                String.format(EnumChatFormatting.RED + "[Blood Finder] " + EnumChatFormatting.WHITE + "Барон вампиров " +
                    EnumChatFormatting.GOLD + "(Ранг %d)" + EnumChatFormatting.WHITE + ": ~" +
                    EnumChatFormatting.YELLOW + "%dм " + EnumChatFormatting.AQUA + "[%s] " +
                    EnumChatFormatting.GRAY + "(Y=%d)",
                    bLevel, dist, direction, (int) best.posY)));

            world.playSoundAtEntity(player, "random.orb", 1.0F, 0.7F);

            // Emit a visible beacon column of blood particles above the baron
            if (world instanceof WorldServer) {
                WorldServer ws = (WorldServer) world;
                for (double py = 1.0D; py <= 8.0D; py += 0.5D) {
                    ws.func_147487_a("reddust", best.posX, best.posY + py, best.posZ, 4, 0.2D, 0.2D, 0.2D, 0.0D);
                    ws.func_147487_a("flame", best.posX, best.posY + py, best.posZ, 2, 0.1D, 0.1D, 0.1D, 0.01D);
                }
            }
        } else {
            String targetDesc = targetLvl < 0 ? "баронов любого уровня" : String.format("баронов ранга %d", targetLvl + 1);
            player.addChatMessage(new ChatComponentText(
                EnumChatFormatting.DARK_GRAY + "[Blood Finder] " + EnumChatFormatting.GRAY + "Поблизости не обнаружено " + targetDesc + " (продолжайте поиски в вампирском лесу)."));
            world.playSoundAtEntity(player, "mob.fizz", 0.4F, 1.2F);
        }

        return stack;
    }

    public static String getDirectionString(double dx, double dz) {
        double angle = Math.toDegrees(Math.atan2(-dx, dz));
        if (angle < 0) angle += 360.0D;
        if (angle >= 337.5D || angle < 22.5D) return "Север";
        if (angle >= 22.5D && angle < 67.5D) return "Северо-Восток";
        if (angle >= 67.5D && angle < 112.5D) return "Восток";
        if (angle >= 112.5D && angle < 157.5D) return "Юго-Восток";
        if (angle >= 157.5D && angle < 202.5D) return "Юг";
        if (angle >= 202.5D && angle < 247.5D) return "Юго-Запад";
        if (angle >= 247.5D && angle < 292.5D) return "Запад";
        return "Северо-Запад";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int targetLvl = getTargetLevel(stack);
        String levelName = targetLvl < 0 ? "Любой (1-5)" : String.format("Ранг %d (Чистая кровь %d)", targetLvl + 1, targetLvl);
        list.add(EnumChatFormatting.GRAY + "Отслеживание: " + EnumChatFormatting.GOLD + levelName);
        list.add(EnumChatFormatting.DARK_GRAY + "ПКМ: Сканировать местность в радиусе 160м");
        list.add(EnumChatFormatting.DARK_GRAY + "Shift + ПКМ: Сменить искомый ранг Барона");
    }
}
