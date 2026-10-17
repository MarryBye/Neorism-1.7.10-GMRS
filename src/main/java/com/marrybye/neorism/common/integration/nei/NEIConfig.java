package com.marrybye.neorism.common.integration.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import com.marrybye.neorism.client.gui.GuiAlchemicalCauldron;
import com.marrybye.neorism.client.gui.GuiAltarInfusion;
import com.marrybye.neorism.client.gui.GuiHunterTable;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.util.ModLogger;
import net.minecraft.item.ItemStack;

/**
 * Standard IConfigureNEI plugin for NotEnoughItems.
 */
public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        ModLogger.debug("Loading Neorism NEI plugin configuration...");

        // Alchemical Cauldron
        AlchemicalCauldronRecipeHandler cauldronHandler = new AlchemicalCauldronRecipeHandler();
        API.registerRecipeHandler(cauldronHandler);
        API.registerUsageHandler(cauldronHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.alchemicalCauldron), cauldronHandler.getOverlayIdentifier());
        API.registerGuiOverlay(GuiAlchemicalCauldron.class, cauldronHandler.getOverlayIdentifier());

        // Hunter Table
        HunterTableRecipeHandler hunterTableHandler = new HunterTableRecipeHandler();
        API.registerRecipeHandler(hunterTableHandler);
        API.registerUsageHandler(hunterTableHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.hunterTable), hunterTableHandler.getOverlayIdentifier());
        API.registerGuiOverlay(GuiHunterTable.class, hunterTableHandler.getOverlayIdentifier());

        // Altar of Infusion
        AltarInfusionRecipeHandler infusionHandler = new AltarInfusionRecipeHandler();
        API.registerRecipeHandler(infusionHandler);
        API.registerUsageHandler(infusionHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.altarInfusion), infusionHandler.getOverlayIdentifier());
        API.registerGuiOverlay(GuiAltarInfusion.class, infusionHandler.getOverlayIdentifier());

        // Altar of Inspiration
        AltarInspirationRecipeHandler inspirationHandler = new AltarInspirationRecipeHandler();
        API.registerRecipeHandler(inspirationHandler);
        API.registerUsageHandler(inspirationHandler);
        API.addRecipeCatalyst(new ItemStack(ModBlocks.altarInspiration), inspirationHandler.getOverlayIdentifier());

        ModLogger.debug("Neorism NEI plugin loaded: 4 recipe handlers registered");
    }

    @Override
    public String getName() {
        return "Neorism NEI Integration";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
