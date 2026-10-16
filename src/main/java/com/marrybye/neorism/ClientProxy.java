package com.marrybye.neorism;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry.registerModid(Neorism.MODID);
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

        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.EntityHolyWaterBottle.class,
            new com.marrybye.neorism.client.render.entity.RenderHolyWaterBottle()
        );

        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.projectile.EntityDarkBloodProjectile.class,
            new com.marrybye.neorism.client.render.entity.RenderDarkBloodProjectile()
        );

        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.projectile.EntityQuarrel.class,
            new com.marrybye.neorism.client.render.entity.RenderQuarrel()
        );

        // Mobs & Bosses
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.vampire.EntityVampire.class,
            new com.marrybye.neorism.client.render.entity.RenderVampire()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.vampire.EntityAdvancedVampire.class,
            new com.marrybye.neorism.client.render.entity.RenderAdvancedVampire()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.vampire.EntityVampireBaron.class,
            new com.marrybye.neorism.client.render.entity.RenderVampireBaron()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.vampire.EntityVampireBaroness.class,
            new com.marrybye.neorism.client.render.entity.RenderVampireBaron()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.vampire.EntityBloodBat.class,
            new com.marrybye.neorism.client.render.entity.RenderBloodBat()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.vampire.EntityDracula.class,
            new com.marrybye.neorism.client.render.entity.RenderDracula()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.vampire.EntityVampireMother.class,
            new com.marrybye.neorism.client.render.entity.RenderVampireMother()
        );

        // Hunters
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.hunter.EntityHunter.class,
            new com.marrybye.neorism.client.render.entity.RenderHunter()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.hunter.EntityAdvancedHunter.class,
            new com.marrybye.neorism.client.render.entity.RenderAdvancedHunter()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.hunter.EntityHunterTrainer.class,
            new com.marrybye.neorism.client.render.entity.RenderHunter()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.hunter.EntityTaskmaster.class,
            new com.marrybye.neorism.client.render.entity.RenderHunter()
        );

        // Converted Mobs
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.converted.EntityConvertedCow.class,
            new com.marrybye.neorism.client.render.entity.RenderConvertedCow()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.converted.EntityConvertedSheep.class,
            new com.marrybye.neorism.client.render.entity.RenderConvertedSheep()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.converted.EntityConvertedHorse.class,
            new com.marrybye.neorism.client.render.entity.RenderConvertedHorse()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.converted.EntityConvertedVillager.class,
            new com.marrybye.neorism.client.render.entity.RenderConvertedVillager()
        );

        net.minecraft.block.Block[] modeledBlocks = new net.minecraft.block.Block[]{
            com.marrybye.neorism.common.core.ModBlocks.bloodPedestal,
            com.marrybye.neorism.common.core.ModBlocks.altarInfusion,
            com.marrybye.neorism.common.core.ModBlocks.altarInspiration,
            com.marrybye.neorism.common.core.ModBlocks.altarPillar,
            com.marrybye.neorism.common.core.ModBlocks.altarTip,
            com.marrybye.neorism.common.core.ModBlocks.bloodContainer,
            com.marrybye.neorism.common.core.ModBlocks.coffin,
            com.marrybye.neorism.common.core.ModBlocks.fogDiffuser,
            com.marrybye.neorism.common.core.ModBlocks.garlicDiffuser,
            com.marrybye.neorism.common.core.ModBlocks.totem
        };
        for (net.minecraft.block.Block block : modeledBlocks) {
            if (block != null) {
                net.minecraft.item.Item item = net.minecraft.item.Item.getItemFromBlock(block);
                if (item != null) {
                    net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
                        item, com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.INSTANCE.get()
                    );
                }
            }
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
