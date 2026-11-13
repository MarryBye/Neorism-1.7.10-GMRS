package com.marrybye.neorism.client.events;

import java.util.List;

import com.marrybye.neorism.Config;
import com.marrybye.neorism.common.blocks.BlockCoffin;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

/**
 * Client-side ItemTooltipEvent handler providing localized lore and usage tooltips
 * for special Neorism items and functional blocks without touching block classes.
 */
@SideOnly(Side.CLIENT)
public class TooltipEventHandler {

    private static final int TOOLTIP_WRAP_WIDTH = 220;

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onItemTooltip(ItemTooltipEvent event) {
        if (event == null || event.itemStack == null || event.toolTip == null) {
            return;
        }

        ItemStack stack = event.itemStack;
        Item item = stack.getItem();
        if (item == null) {
            return;
        }

        List<String> tooltip = event.toolTip;

        // 1. Plain ItemNeorism & Fluid items without dedicated addInformation overrides
        if (item == ModItems.sovereignBlood) {
            addWrappedTranslation(tooltip, "tooltip.neorism.sovereign_blood", EnumChatFormatting.DARK_RED);
            return;
        }
        if (item == ModItems.vampireBloodBottle) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.vampire_blood_bottle", EnumChatFormatting.GRAY);
            return;
        }
        if (item == ModItems.soulOrbVampire) {
            addWrappedTranslation(tooltip, "tooltip.neorism.soul_orb_vampire", EnumChatFormatting.GRAY);
            return;
        }
        if (item == ModItems.motherCore) {
            addWrappedTranslation(tooltip, "tooltip.neorism.mother_core", EnumChatFormatting.DARK_PURPLE);
            return;
        }
        if (item == ModItems.caseFile144) {
            addWrappedTranslation(tooltip, "tooltip.neorism.case_file_144", EnumChatFormatting.GRAY);
            return;
        }
        if (item == ModItems.purifiedGarlic) {
            addWrappedTranslation(tooltip, "tooltip.neorism.purified_garlic", EnumChatFormatting.GRAY);
            return;
        }
        if (item == ModItems.pureSalt) {
            addWrappedTranslation(tooltip, "tooltip.neorism.pure_salt", EnumChatFormatting.GRAY);
            return;
        }
        if (item == ModItems.bloodInfusedRawIron || item == ModItems.bloodInfusedRawGold
            || item == ModItems.bloodInfusedIron || item == ModItems.bloodInfusedGold
            || item == ModItems.bloodInfusedDiamond || item == ModItems.bloodInfusedNetherite) {
            addWrappedTranslation(tooltip, "tooltip.neorism.blood_infused_material", EnumChatFormatting.GRAY);
            return;
        }
        if (item == ModFluids.bloodBucket) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.blood_bucket", EnumChatFormatting.GRAY);
            return;
        }

        // 2. Functional & special blocks
        if (!(item instanceof ItemBlock)) {
            return;
        }
        Block block = Block.getBlockFromItem(item);
        if (block == null || block == Blocks.air) {
            return;
        }

        if (block == ModBlocks.altarInspiration) {
            addWrappedTranslation(tooltip, "tooltip.neorism.altar_inspiration", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.altarInfusion) {
            addWrappedTranslation(tooltip, "tooltip.neorism.altar_infusion", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.altarPillar) {
            addWrappedTranslation(tooltip, "tooltip.neorism.altar_pillar", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.altarTip) {
            addWrappedTranslation(tooltip, "tooltip.neorism.altar_tip", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.alchemicalCauldron) {
            addWrappedTranslation(tooltip, "tooltip.neorism.alchemical_cauldron", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.hunterTable) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.hunter_table", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.garlicDiffuser) {
            int size = 1 + 2 * Config.garlicDiffuserNormalDist;
            addWrappedTranslation(tooltip, "tooltip.vampirism.garlic_diffuser_normal", EnumChatFormatting.GRAY, size, size);
        } else if (block == ModBlocks.garlicDiffuserLong) {
            int size = 1 + 2 * Config.garlicDiffuserWeakDist;
            addWrappedTranslation(tooltip, "tooltip.vampirism.garlic_diffuser_long", EnumChatFormatting.GRAY, size, size);
        } else if (block == ModBlocks.garlicDiffuserStrong) {
            int size = 1 + 2 * Config.garlicDiffuserEnhancedDist;
            addWrappedTranslation(tooltip, "tooltip.vampirism.garlic_diffuser_strong", EnumChatFormatting.GRAY, size, size);
        } else if (block == ModBlocks.velmorraAltar) {
            addWrappedTranslation(tooltip, "tooltip.neorism.velmorra_altar", EnumChatFormatting.DARK_RED);
        } else if (block == ModBlocks.chalice) {
            addWrappedTranslation(tooltip, "tooltip.neorism.chalice", EnumChatFormatting.DARK_RED);
        } else if (block == ModBlocks.totem || block == ModBlocks.totemTop) {
            addWrappedTranslation(tooltip, "tooltip.neorism.totem", EnumChatFormatting.GRAY);
        } else if (block instanceof BlockCoffin || block == ModBlocks.coffin) {
            addWrappedTranslation(tooltip, "tooltip.neorism.coffin", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.fogDiffuser) {
            addWrappedTranslation(tooltip, "tooltip.neorism.fog_diffuser", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.bloodContainer) {
            addWrappedTranslation(tooltip, "tooltip.neorism.blood_container", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.bloodPedestal) {
            addWrappedTranslation(tooltip, "tooltip.neorism.blood_pedestal", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.vampireBeacon) {
            addWrappedTranslation(tooltip, "tooltip.neorism.vampire_beacon", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.sunscreenBeacon) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.sunscreen_beacon.desc1", EnumChatFormatting.RED);
            addWrappedTranslation(tooltip, "tooltip.vampirism.sunscreen_beacon.desc2", EnumChatFormatting.GRAY, 32);
        } else if (block == ModBlocks.bloodGrinder) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.blood_grinder", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.bloodSieve) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.blood_sieve", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.injectionChair) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.injection_chair", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.vaporStill) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.vapor_still", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.alchemyTable) {
            addWrappedTranslation(tooltip, "tooltip.neorism.alchemy_table", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.hunterWeaponTable) {
            addWrappedTranslation(tooltip, "tooltip.neorism.weapon_table", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.altarCleansing) {
            addWrappedTranslation(tooltip, "tooltip.neorism.altar_cleansing", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.candlestick) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.candle_stick", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.candelabra) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.candelabra", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.chandelier) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.chandelier", EnumChatFormatting.GRAY);
        } else if (block == ModBlocks.ancientTrophy) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.mother_trophy", EnumChatFormatting.DARK_PURPLE);
        } else if (block == ModBlocks.cursedEarth || block == ModBlocks.cursedGrass) {
            addWrappedTranslation(tooltip, "tooltip.vampirism.only_vampire_spawn", EnumChatFormatting.DARK_RED);
        }
    }

    /**
     * Translates a localization key (supporting optional format args and literal "\n" line breaks)
     * and wraps lines neatly to avoid overly wide tooltips.
     */
    @SuppressWarnings("unchecked")
    public static void addWrappedTranslation(List tooltip, String key, EnumChatFormatting color, Object... args) {
        if (!StatCollector.canTranslate(key)) {
            return;
        }
        String raw = (args != null && args.length > 0)
            ? StatCollector.translateToLocalFormatted(key, args)
            : StatCollector.translateToLocal(key);
        if (raw == null || raw.isEmpty()) {
            return;
        }
        raw = raw.replace("\\n", "\n");
        String[] paragraphs = raw.split("\n", -1);

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = (mc != null) ? mc.fontRenderer : null;

        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                tooltip.add("");
                continue;
            }
            if (fr != null) {
                List<String> wrapped = fr.listFormattedStringToWidth(paragraph, TOOLTIP_WRAP_WIDTH);
                for (String line : wrapped) {
                    tooltip.add(color + line);
                }
            } else {
                tooltip.add(color + paragraph);
            }
        }
    }
}
