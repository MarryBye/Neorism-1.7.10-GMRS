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
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.gui.overlay.BloodBarOverlay(mc)
        );
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.gui.overlay.SunOverlay(mc)
        );
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.gui.overlay.RageOverlay(mc)
        );
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.gui.overlay.BatOverlay(mc)
        );
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.gui.overlay.ActionCooldownOverlay(mc)
        );
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.events.BloodVisionHandler()
        );
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.events.VampireAtmosphereHandler()
        );
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
            new com.marrybye.neorism.client.render.VampireAppearanceRenderHandler()
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

        java.util.List<net.minecraft.block.Block> modeledList = new java.util.ArrayList<net.minecraft.block.Block>();
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.bloodPedestal);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.altarInfusion);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.altarInspiration);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.altarPillar);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.altarTip);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.bloodContainer);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.fogDiffuser);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.garlicDiffuser);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.garlicDiffuserLong);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.garlicDiffuserStrong);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.totem);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.totemTop);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.hunterTable);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.hunterWeaponTable);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.alchemicalCauldron);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.candlestick);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.candelabra);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.chandelier);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.vampireSoulLantern);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.cross);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.tombstoneShort);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.tombstoneMedium);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.tombstoneCross);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.graveCage);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.vampireRack);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.throne);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.batCage);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.ancientTrophy);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.velmorraAltar);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.vaporStill);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.alchemyTable);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.injectionChair);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.altarCleansing);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.sunscreenBeacon);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.chalice);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.bloodGrinder);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.bloodSieve);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.infuser);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.vampireBeacon);
        modeledList.add(com.marrybye.neorism.common.core.ModBlocks.coffin);
        if (com.marrybye.neorism.common.core.ModBlocks.coffins != null) {
            for (net.minecraft.block.Block c : com.marrybye.neorism.common.core.ModBlocks.coffins) {
                if (c != null) modeledList.add(c);
            }
        }
        for (net.minecraft.block.Block block : modeledList) {
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
        cpw.mods.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
            com.marrybye.neorism.common.tileentity.TileEntityTotem.class,
            new com.marrybye.neorism.client.render.tileentity.TileEntityTotemRenderer()
        );
    }

    private void registerItemRenderers() {
        // Umbrella (Sun protection) - 3D JSON model
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.umbrella,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                "/assets/neorism/models/item/umbrella.json",
                new net.minecraft.util.ResourceLocation("neorism", "textures/item/umbrella.png")
            )
        );

        // Pitchfork - 3D JSON model
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.pitchfork,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                "/assets/neorism/models/item/pitchfork.json",
                new net.minecraft.util.ResourceLocation("neorism", "textures/item/pitchfork.png")
            )
        );

        // Crucifixes (Wooden, Iron, Golden) - 3D JSON models
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.crucifix,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                new com.marrybye.neorism.client.render.item.JsonModelItemRenderer.IModelResolver() {
                    @Override
                    public String resolveModel(net.minecraft.item.ItemStack stack) {
                        int dmg = stack != null ? stack.getItemDamage() : 0;
                        switch (dmg) {
                            case 1: return "/assets/neorism/models/item/crucifix_enhanced.json";
                            case 2: return "/assets/neorism/models/item/crucifix_ultimate.json";
                            default: return "/assets/neorism/models/item/crucifix_normal.json";
                        }
                    }
                }, null
            )
        );

        // Hunter Axes (Normal, Enhanced, Ultimate) - 3D JSON models
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.hunterAxe,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                new com.marrybye.neorism.client.render.item.JsonModelItemRenderer.IModelResolver() {
                    @Override
                    public String resolveModel(net.minecraft.item.ItemStack stack) {
                        int dmg = stack != null ? stack.getItemDamage() : 0;
                        switch (dmg) {
                            case 1: return "/assets/neorism/models/item/hunter_axe_enhanced.json";
                            case 2: return "/assets/neorism/models/item/hunter_axe_ultimate.json";
                            default: return "/assets/neorism/models/item/hunter_axe_normal.json";
                        }
                    }
                }, null
            )
        );

        // Heart Striker (Normal, Enhanced, Ultimate) - 3D JSON models
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.heartStriker,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                new com.marrybye.neorism.client.render.item.JsonModelItemRenderer.IModelResolver() {
                    @Override
                    public String resolveModel(net.minecraft.item.ItemStack stack) {
                        int dmg = stack != null ? stack.getItemDamage() : 0;
                        switch (dmg) {
                            case 1: return "/assets/neorism/models/item/diamond_heart_striker.json";
                            case 2: return "/assets/neorism/models/item/netherite_heart_striker.json";
                            default: return "/assets/neorism/models/item/iron_heart_striker.json";
                        }
                    }
                }, null
            )
        );

        // Heart Seeker (Normal, Enhanced, Ultimate) - 3D JSON models
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.heartSeeker,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                new com.marrybye.neorism.client.render.item.JsonModelItemRenderer.IModelResolver() {
                    @Override
                    public String resolveModel(net.minecraft.item.ItemStack stack) {
                        int dmg = stack != null ? stack.getItemDamage() : 0;
                        switch (dmg) {
                            case 1: return "/assets/neorism/models/item/diamond_heart_seeker.json";
                            case 2: return "/assets/neorism/models/item/netherite_heart_seeker.json";
                            default: return "/assets/neorism/models/item/iron_heart_seeker.json";
                        }
                    }
                }, null
            )
        );

        // Crossbows (6 variants) - 3D JSON models
        net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(
            com.marrybye.neorism.common.core.ModItems.crossbow,
            new com.marrybye.neorism.client.render.item.JsonModelItemRenderer(
                new com.marrybye.neorism.client.render.item.JsonModelItemRenderer.IModelResolver() {
                    @Override
                    public String resolveModel(net.minecraft.item.ItemStack stack) {
                        int dmg = stack != null ? stack.getItemDamage() : 0;
                        switch (dmg) {
                            case 1: return "/assets/neorism/models/item/basic_double_crossbow.json";
                            case 2: return "/assets/neorism/models/item/enhanced_crossbow.json";
                            case 3: return "/assets/neorism/models/item/enhanced_double_crossbow.json";
                            case 4: return "/assets/neorism/models/item/basic_tech_crossbow.json";
                            case 5: return "/assets/neorism/models/item/enhanced_tech_crossbow.json";
                            default: return "/assets/neorism/models/item/basic_crossbow.json";
                        }
                    }
                }, null
            )
        );
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
