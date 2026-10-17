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
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.events.VampireAtmosphereHandler()
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
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.minion.EntityVampireMinion.class,
            new com.marrybye.neorism.client.render.entity.RenderVampireMinion()
        );
        cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
            com.marrybye.neorism.common.entity.minion.EntityHunterMinion.class,
            new com.marrybye.neorism.client.render.entity.RenderHunterMinion()
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

        registerItemRenderers();

        cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
            com.marrybye.neorism.common.tileentity.TileEntityAltarInfusion.class,
            new com.marrybye.neorism.client.render.tileentity.TileEntityAltarInfusionRenderer()
        );
        cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
            com.marrybye.neorism.common.tileentity.TileEntityBloodPedestal.class,
            new com.marrybye.neorism.client.render.tileentity.TileEntityBloodPedestalRenderer()
        );
        cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
            com.marrybye.neorism.common.tileentity.TileEntityCoffin.class,
            new com.marrybye.neorism.client.render.tileentity.TileEntityCoffinRenderer()
        );
    }

    private void registerItemRenderers() {
        // Pitchfork
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.pitchfork,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                "/assets/neorism/models/item/pitchfork.json",
                new net.minecraft.util.ResourceLocation("neorism", "textures/item/pitchfork.png")
            )
        );

        // Umbrella
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.umbrella,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                "/assets/neorism/models/item/umbrella.json",
                new net.minecraft.util.ResourceLocation("neorism", "textures/item/umbrella.png")
            )
        );

        // Crucifix (Wooden, Iron, Gold)
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.crucifix,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                "/assets/neorism/models/item/crucifix.json",
                new net.minecraft.util.ResourceLocation[]{
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/crucifix_wooden.png"),
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/crucifix_iron.png"),
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/crucifix_gold.png")
                }
            )
        );

        // Hunter Axe (Normal, Enhanced, Ultimate)
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.hunterAxe,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                "/assets/neorism/models/item/hunter_axe.json",
                new net.minecraft.util.ResourceLocation[]{
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/hunter_axe_normal.png"),
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/hunter_axe_enhanced.png"),
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/hunter_axe_ultimate.png")
                }
            )
        );

        // Heart Seeker (Normal, Enhanced, Ultimate)
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.heartSeeker,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                "/assets/neorism/models/item/heart_seeker_model.json",
                new net.minecraft.util.ResourceLocation[]{
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/heart_seeker_normal.png"),
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/heart_seeker_enhanced.png"),
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/heart_seeker_ultimate.png")
                }
            )
        );

        // Heart Striker (Normal, Enhanced, Ultimate)
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.heartStriker,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                "/assets/neorism/models/item/heart_striker_model.json",
                new net.minecraft.util.ResourceLocation[]{
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/heart_striker_normal.png"),
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/heart_striker_enhanced.png"),
                    new net.minecraft.util.ResourceLocation("neorism", "textures/item/heart_striker_ultimate.png")
                }
            )
        );

        // Crossbow (Basic, Enhanced, Double, Tech)
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.crossbow,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                new com.marrybye.neorism.client.render.item.JsonModelItemRenderer.IModelResolver() {
                    @Override
                    public String resolveModel(net.minecraft.item.ItemStack stack) {
                        int meta = stack != null ? stack.getItemDamage() : 0;
                        switch (meta) {
                            case 2:
                                return "/assets/neorism/models/item/double_crossbow.json";
                            case 3:
                                return "/assets/neorism/models/item/tech_crossbow.json";
                            default:
                                return "/assets/neorism/models/item/crossbow.json";
                        }
                    }
                },
                new com.marrybye.neorism.client.render.item.JsonModelItemRenderer.ITextureResolver() {
                    @Override
                    public net.minecraft.util.ResourceLocation resolveTexture(net.minecraft.item.ItemStack stack) {
                        int meta = stack != null ? stack.getItemDamage() : 0;
                        switch (meta) {
                            case 1:
                                return new net.minecraft.util.ResourceLocation("neorism", "textures/item/crossbow_enhanced.png");
                            case 2:
                                return new net.minecraft.util.ResourceLocation("neorism", "textures/item/crossbow_double.png");
                            case 3:
                                return new net.minecraft.util.ResourceLocation("neorism", "textures/item/tech_crossbow.png");
                            default:
                                return new net.minecraft.util.ResourceLocation("neorism", "textures/item/crossbow.png");
                        }
                    }
                }
            )
        );

        // Coffin
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            net.minecraft.item.Item.getItemFromBlock(com.marrybye.neorism.common.core.ModBlocks.coffin),
            new com.marrybye.neorism.client.render.item.ItemCoffinRenderer()
        );
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
