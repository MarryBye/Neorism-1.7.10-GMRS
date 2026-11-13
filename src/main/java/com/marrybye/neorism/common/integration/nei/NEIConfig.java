package com.marrybye.neorism.common.integration.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;
import com.marrybye.neorism.client.gui.GuiAlchemicalCauldron;
import com.marrybye.neorism.client.gui.GuiAltarInfusion;
import com.marrybye.neorism.client.gui.GuiHunterTable;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.ModLogger;
import net.minecraft.item.ItemStack;

/**
 * Standard IConfigureNEI plugin for NotEnoughItems.
 */
public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        ModLogger.debug("Loading Neorism NEI plugin configuration...");

        // Hide technical / internal blocks that should not be crafted or browsed in NEI
        hideTechnicalBlock(ModBlocks.tent);
        hideTechnicalBlock(ModBlocks.velmorraPortal);
        hideTechnicalBlock(ModBlocks.darkSpruceDoor);
        hideTechnicalBlock(ModBlocks.cursedSpruceDoor);
        hideTechnicalBlock(ModFluids.blockBlood);

        // 1. Alchemical Cauldron
        AlchemicalCauldronRecipeHandler cauldronHandler = new AlchemicalCauldronRecipeHandler();
        API.registerRecipeHandler(cauldronHandler);
        API.registerUsageHandler(cauldronHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.alchemicalCauldron), cauldronHandler.getOverlayIdentifier());
        API.registerGuiOverlay(GuiAlchemicalCauldron.class, cauldronHandler.getOverlayIdentifier());
        API.registerGuiOverlayHandler(GuiAlchemicalCauldron.class, new DefaultOverlayHandler(), cauldronHandler.getOverlayIdentifier());

        // 2. Hunter Table
        HunterTableRecipeHandler hunterTableHandler = new HunterTableRecipeHandler();
        API.registerRecipeHandler(hunterTableHandler);
        API.registerUsageHandler(hunterTableHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.hunterTable), hunterTableHandler.getOverlayIdentifier());
        API.registerGuiOverlay(GuiHunterTable.class, hunterTableHandler.getOverlayIdentifier());
        API.registerGuiOverlayHandler(GuiHunterTable.class, new DefaultOverlayHandler(), hunterTableHandler.getOverlayIdentifier());

        // 3. Altar of Infusion
        AltarInfusionRecipeHandler infusionHandler = new AltarInfusionRecipeHandler();
        API.registerRecipeHandler(infusionHandler);
        API.registerUsageHandler(infusionHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.altarInfusion), infusionHandler.getOverlayIdentifier());
        API.registerGuiOverlay(GuiAltarInfusion.class, infusionHandler.getOverlayIdentifier());
        API.registerGuiOverlayHandler(GuiAltarInfusion.class, new DefaultOverlayHandler(), infusionHandler.getOverlayIdentifier());

        // 4. Altar of Inspiration
        AltarInspirationRecipeHandler inspirationHandler = new AltarInspirationRecipeHandler();
        API.registerRecipeHandler(inspirationHandler);
        API.registerUsageHandler(inspirationHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.altarInspiration), inspirationHandler.getOverlayIdentifier());

        // 5. Multiblock & Structure Guide
        NeorismStructureInfoHandler structureHandler = new NeorismStructureInfoHandler();
        API.registerRecipeHandler(structureHandler);
        API.registerUsageHandler(structureHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.altarInspiration), structureHandler.getOverlayIdentifier());
        API.addRecipeCatalyst(new ItemStack(ModBlocks.altarInfusion), structureHandler.getOverlayIdentifier());
        API.addRecipeCatalyst(new ItemStack(ModBlocks.velmorraAltar), structureHandler.getOverlayIdentifier());
        API.addRecipeCatalyst(new ItemStack(ModBlocks.chalice), structureHandler.getOverlayIdentifier());
        API.addRecipeCatalyst(new ItemStack(ModBlocks.totem), structureHandler.getOverlayIdentifier());
        API.addRecipeCatalyst(new ItemStack(ModBlocks.coffin), structureHandler.getOverlayIdentifier());
        API.addRecipeCatalyst(new ItemStack(ModItems.itemTent), structureHandler.getOverlayIdentifier());
        API.addRecipeCatalyst(new ItemStack(ModBlocks.garlicDiffuser), structureHandler.getOverlayIdentifier());

        // 6. Special Item Drop & Progression Guide
        NeorismItemInfoHandler itemInfoHandler = new NeorismItemInfoHandler();
        API.registerRecipeHandler(itemInfoHandler);
        API.registerUsageHandler(itemInfoHandler);
        API.addRecipeCatalyst(new ItemStack(ModItems.caseFile144), itemInfoHandler.getOverlayIdentifier());

        ModLogger.debug("Neorism NEI plugin loaded: 6 recipe/info handlers registered");
    }

    private void hideTechnicalBlock(net.minecraft.block.Block block) {
        if (block != null) {
            API.hideItem(new ItemStack(block));
        }
    }

    @Override
    public String getName() {
        return "Neorism NEI Integration";
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }
}
