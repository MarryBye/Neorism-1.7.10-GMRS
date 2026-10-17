package com.marrybye.neorism.common.integration.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.vampire.VampireLeveling;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * NEI Recipe Handler for Vampire Altar of Inspiration.
 */
public class AltarInspirationRecipeHandler extends TemplateRecipeHandler {

    public class CachedInspirationRecipe extends CachedRecipe {
        private final PositionedStack input;
        private final PositionedStack result;
        public final int targetLevel;
        public final int bloodRequired;

        public CachedInspirationRecipe(VampireLeveling.AltarInspirationRequirement req) {
            this.targetLevel = req.getTargetLevel();
            this.bloodRequired = req.getBloodAmount();

            // Input: Blood Flask
            this.input = new PositionedStack(new ItemStack(ModItems.bloodFlask), 51, 6);

            // Result: Vampire Book representing the level progression
            this.result = new PositionedStack(new ItemStack(ModItems.vampireBook), 111, 24);
        }

        @Override
        public PositionedStack getIngredient() {
            return this.input;
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("tile.neorism.altar_inspiration.name");
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/furnace.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "neorism.altar_inspiration";
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new AltarInspirationRecipeHandler();
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 17), getOverlayIdentifier()));
    }

    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(74, 23, 176, 14, 24, 17, 40, 0); // arrow
        this.drawProgressBar(51, 25, 176, 0, 14, 14, 48, 2);  // fire flame
        CachedInspirationRecipe r = (CachedInspirationRecipe) this.arecipes.get(recipe);
        if (r != null) {
            String lvlText = "Vampire Level " + r.targetLevel;
            String bloodText = "Blood: " + r.bloodRequired;
            GuiDraw.drawString(lvlText, 25, 48, 0x8A0303, false);
            GuiDraw.drawString(bloodText, 105, 48, 0x404040, false);
        }
    }

    private List<CachedInspirationRecipe> getCraftingList() {
        List<CachedInspirationRecipe> list = new ArrayList<CachedInspirationRecipe>();
        for (int lvl = 2; lvl <= 4; lvl++) {
            VampireLeveling.AltarInspirationRequirement req = VampireLeveling.getInspirationRequirement(lvl);
            if (req != null) {
                list.add(new CachedInspirationRecipe(req));
            }
        }
        return list;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            this.arecipes.addAll(getCraftingList());
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (CachedInspirationRecipe r : getCraftingList()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(r.getResult().item, result)) {
                this.arecipes.add(r);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (CachedInspirationRecipe r : getCraftingList()) {
            if (r.contains(r.getIngredients(), ingredient)) {
                this.arecipes.add(r);
            }
        }
    }
}
