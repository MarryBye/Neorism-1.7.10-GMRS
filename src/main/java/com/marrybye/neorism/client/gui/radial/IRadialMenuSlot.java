package com.marrybye.neorism.client.gui.radial;

import com.marrybye.neorism.api.action.IAction;
import net.minecraft.util.ResourceLocation;

/**
 * Interface representing a slot/slice in the Radial Menu.
 */
public interface IRadialMenuSlot {

    String getSlotName();

    IAction getAction();

    ResourceLocation getIcon();
}
