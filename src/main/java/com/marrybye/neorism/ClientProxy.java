package com.marrybye.neorism;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        com.marrybye.neorism.client.input.KeyBindings.init();
        com.marrybye.neorism.client.input.KeyBindings keyHandler = new com.marrybye.neorism.client.input.KeyBindings();
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(keyHandler);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.gui.overlay.BloodBarOverlay(net.minecraft.client.Minecraft.getMinecraft())
        );
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
