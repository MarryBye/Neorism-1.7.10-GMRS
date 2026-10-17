package com.marrybye.neorism.api.items;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/**
 * Interface for items that exist in different tiers (Normal, Enhanced, Ultimate).
 * Canonical backport of TeamLapen's IItemWithTier.
 */
public interface IItemWithTier {

    Tier getVampirismTier();

    default Tier getVampirismTier(ItemStack stack) {
        return getVampirismTier();
    }

    default void addTierInformation(List<String> tooltip) {
        addTierInformation((ItemStack) null, tooltip);
    }

    default void addTierInformation(ItemStack stack, List tooltip) {
        Tier tier = stack != null ? getVampirismTier(stack) : getVampirismTier();
        if (tier != null && tier != Tier.NORMAL) {
            EnumChatFormatting format = tier == Tier.ENHANCED ? EnumChatFormatting.YELLOW : EnumChatFormatting.AQUA;
            String key = "tooltip.neorism.tier." + tier.getName();
            if (StatCollector.canTranslate(key)) {
                tooltip.add(format + StatCollector.translateToLocal(key));
            } else {
                tooltip.add(format + tier.getName().substring(0, 1).toUpperCase() + tier.getName().substring(1));
            }
        }
    }

    enum Tier {
        NORMAL("normal"),
        ENHANCED("enhanced"),
        ULTIMATE("ultimate");

        private final String name;

        Tier(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
