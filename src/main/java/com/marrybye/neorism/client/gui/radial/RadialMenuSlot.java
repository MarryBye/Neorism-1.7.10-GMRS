package com.marrybye.neorism.client.gui.radial;

import com.marrybye.neorism.api.action.IAction;
import net.minecraft.util.ResourceLocation;

/**
 * Concrete slot in the Radial Menu.
 */
public class RadialMenuSlot implements IRadialMenuSlot {

    private final String name;
    private final IAction action;
    private final ResourceLocation icon;

    public RadialMenuSlot(String name, IAction action, ResourceLocation icon) {
        this.name = name;
        this.action = action;
        this.icon = icon;
    }

    @Override
    public String getSlotName() {
        return name;
    }

    @Override
    public IAction getAction() {
        return action;
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }
}
