package com.marrybye.neorism.common.hunter;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

/**
 * Requirements and engine for Hunter leveling progression (levels 1-14).
 * Ported 1:1 from TeamLapen's HunterLeveling.java.
 */
public class HunterLeveling {

    public static final int MAX_LEVEL = 14;

    /**
     * Requirement container for basic training (levels 2-4).
     */
    public static class BasicTrainingRequirement {
        public final int targetLevel;
        public final int fangsRequired;

        public BasicTrainingRequirement(int targetLevel, int fangsRequired) {
            this.targetLevel = targetLevel;
            this.fangsRequired = fangsRequired;
        }
    }

    /**
     * Requirement container for advanced training (levels 5-14).
     */
    public static class AdvancedTrainingRequirement {
        public final int targetLevel;
        public final int intelTier;       // 0 to 9
        public final int ironRequired;
        public final int goldRequired;
        public final int vampireBooksRequired;

        public AdvancedTrainingRequirement(int targetLevel, int intelTier, int ironRequired, int goldRequired, int vampireBooksRequired) {
            this.targetLevel = targetLevel;
            this.intelTier = intelTier;
            this.ironRequired = ironRequired;
            this.goldRequired = goldRequired;
            this.vampireBooksRequired = vampireBooksRequired;
        }
    }

    private static final BasicTrainingRequirement[] BASIC_REQUIREMENTS = {
        null,
        null,                                // Level 1: Obtained via initiation
        new BasicTrainingRequirement(2, 1),   // Level 2: 1 blood/fang
        new BasicTrainingRequirement(3, 5),   // Level 3: 5 fangs
        new BasicTrainingRequirement(4, 12)   // Level 4: 12 fangs
    };

    private static final AdvancedTrainingRequirement[] ADVANCED_REQUIREMENTS = {
        null, null, null, null, null,
        new AdvancedTrainingRequirement(5, 0, 5, 0, 0),
        new AdvancedTrainingRequirement(6, 1, 10, 0, 0),
        new AdvancedTrainingRequirement(7, 2, 15, 0, 0),
        new AdvancedTrainingRequirement(8, 3, 40, 0, 0),
        new AdvancedTrainingRequirement(9, 4, 20, 10, 0),
        new AdvancedTrainingRequirement(10, 5, 20, 20, 1),
        new AdvancedTrainingRequirement(11, 6, 20, 10, 1),
        new AdvancedTrainingRequirement(12, 7, 30, 10, 1),
        new AdvancedTrainingRequirement(13, 8, 40, 20, 2),
        new AdvancedTrainingRequirement(14, 9, 40, 40, 2)
    };

    public static BasicTrainingRequirement getBasicRequirement(int targetLevel) {
        if (targetLevel >= 2 && targetLevel <= 4) {
            return BASIC_REQUIREMENTS[targetLevel];
        }
        return null;
    }

    public static AdvancedTrainingRequirement getAdvancedRequirement(int targetLevel) {
        if (targetLevel >= 5 && targetLevel <= 14) {
            return ADVANCED_REQUIREMENTS[targetLevel];
        }
        return null;
    }

    /**
     * Checks if the player can level up to targetLevel.
     */
    public static boolean canLevelUp(EntityPlayer player, int targetLevel) {
        if (player == null || targetLevel < 1 || targetLevel > MAX_LEVEL) return false;
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null) return false;

        if (targetLevel == 1) {
            return fph.canJoin(ModFactions.HUNTER);
        } else {
            if (!fph.isHunter() || fph.getCurrentLevel() != targetLevel - 1) return false;
        }

        if (targetLevel <= 4) {
            BasicTrainingRequirement req = getBasicRequirement(targetLevel);
            if (req == null) return false;
            int count = countItem(player, ModItems.vampireFang);
            return count >= req.fangsRequired;
        } else {
            AdvancedTrainingRequirement req = getAdvancedRequirement(targetLevel);
            if (req == null) return false;

            // Check Intel
            if (!hasHunterIntel(player, req.intelTier)) return false;
            // Check Iron
            if (req.ironRequired > 0 && countItem(player, Items.iron_ingot) < req.ironRequired) return false;
            // Check Gold
            if (req.goldRequired > 0 && countItem(player, Items.gold_ingot) < req.goldRequired) return false;
            // Check Vampire Books
            if (req.vampireBooksRequired > 0 && countItem(player, ModItems.vampireBook) < req.vampireBooksRequired) {
                return false;
            }
            return true;
        }
    }

    /**
     * Attempts to level up the player, consuming training resources from inventory.
     */
    public static boolean tryLevelUp(EntityPlayer player) {
        if (player == null) return false;
        FactionPlayerHandler fph = FactionPlayerHandler.get(player);
        if (fph == null) return false;

        int currentLvl = fph.isHunter() ? fph.getCurrentLevel() : 0;
        int targetLvl = currentLvl + 1;
        if (targetLvl > MAX_LEVEL) return false;

        if (!canLevelUp(player, targetLvl)) {
            player.addChatMessage(new ChatComponentTranslation("chat.neorism.cannot_level_up", targetLvl));
            return false;
        }

        if (targetLvl == 1) {
            fph.setFaction(ModFactions.HUNTER, 1);
        } else if (targetLvl <= 4) {
            BasicTrainingRequirement req = getBasicRequirement(targetLvl);
            consumeItem(player, ModItems.vampireFang, req.fangsRequired);
            fph.setLevel(targetLvl);
        } else {
            AdvancedTrainingRequirement req = getAdvancedRequirement(targetLvl);
            consumeHunterIntel(player, req.intelTier);
            if (req.ironRequired > 0) {
                consumeItem(player, Items.iron_ingot, req.ironRequired);
            }
            if (req.goldRequired > 0) {
                consumeItem(player, Items.gold_ingot, req.goldRequired);
            }
            if (req.vampireBooksRequired > 0) {
                consumeItem(player, ModItems.vampireBook, req.vampireBooksRequired);
            }
            fph.setLevel(targetLvl);
        }

        HunterPlayer hp = HunterPlayer.get(player);
        if (hp != null) {
            hp.onLevelChanged(targetLvl);
        }

        player.addChatMessage(new ChatComponentTranslation("chat.neorism.hunter_leveled_up", targetLvl));
        ModLogger.leveling("Player %s leveled up to Hunter Level %d", player.getCommandSenderName(), targetLvl);

        return true;
    }

    public static int countItem(EntityPlayer player, Item item) {
        if (player == null || item == null) return 0;
        int total = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == item) {
                total += stack.stackSize;
            }
        }
        return total;
    }

    public static void consumeItem(EntityPlayer player, Item item, int amount) {
        if (player == null || item == null || amount <= 0) return;
        int remaining = amount;
        for (int i = 0; i < player.inventory.mainInventory.length && remaining > 0; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                int toTake = Math.min(remaining, stack.stackSize);
                stack.stackSize -= toTake;
                remaining -= toTake;
                if (stack.stackSize <= 0) {
                    player.inventory.mainInventory[i] = null;
                }
            }
        }
        player.inventoryContainer.detectAndSendChanges();
    }

    public static boolean hasHunterIntel(EntityPlayer player, int tier) {
        if (player == null) return false;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == ModItems.hunterIntel && stack.getItemDamage() >= tier) {
                return true;
            }
        }
        return false;
    }

    public static void consumeHunterIntel(EntityPlayer player, int tier) {
        if (player == null) return;
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == ModItems.hunterIntel && stack.getItemDamage() == tier) {
                stack.stackSize--;
                if (stack.stackSize <= 0) {
                    player.inventory.mainInventory[i] = null;
                }
                player.inventoryContainer.detectAndSendChanges();
                return;
            }
        }
    }
}
