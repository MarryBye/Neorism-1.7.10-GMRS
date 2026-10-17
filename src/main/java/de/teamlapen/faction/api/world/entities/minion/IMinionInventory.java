package de.teamlapen.faction.api.world.entities.minion;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Minion inventory managing main storage slots (9-15), hands, and armor slots.
 */
public interface IMinionInventory extends IInventory {

    void addItemStack(ItemStack stack);

    int getAvailableSize();

    ItemStack[] getInventoryArmor();

    ItemStack[] getInventoryHands();
}
