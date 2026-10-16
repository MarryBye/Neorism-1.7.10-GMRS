package com.marrybye.neorism.client.gui.radial;

import java.util.Collections;
import java.util.List;

/**
 * Data container for radial menu configuration and slot actions.
 */
public class RadialMenu {

    public interface SlotConsumer {
        void accept(int slotIndex);
    }

    private final SlotConsumer onSlotSelected;
    private final List<IRadialMenuSlot> slots;
    private int currentSlot = -1;

    public RadialMenu(SlotConsumer onSlotSelected, List<IRadialMenuSlot> slots) {
        this.onSlotSelected = onSlotSelected;
        this.slots = slots;
    }

    public List<IRadialMenuSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public void setCurrentSlot(int slot) {
        this.currentSlot = slot;
        if (onSlotSelected != null && slot >= 0 && slot < slots.size()) {
            onSlotSelected.accept(slot);
        }
    }

    public int getCurrentSlot() {
        return currentSlot;
    }
}
