package com.marrybye.neorism;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Client renderers, entity renderers, keybindings pre-init
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // Client tick handlers, HUD overlays, GUI registrations
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
