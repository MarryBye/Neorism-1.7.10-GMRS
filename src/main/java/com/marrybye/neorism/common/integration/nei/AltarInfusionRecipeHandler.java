package com.marrybye.neorism.common.integration.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.client.gui.GuiAltarInfusion;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.vampire.VampireLeveling;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * NEI Recipe Handler for Vampire Altar of Infusion Rituals.
 */
public class AltarInfusionRecipeHandler extends TemplateRecipeHandler {

    public class CachedInfusionRecipe extends CachedRecipe {
        private final List<PositionedStack> inputs = new ArrayList<PositionedStack>();
        private final PositionedStack result;
        public final int targetLevel;
        public final int pureBloodTier;
        public final int hearts;

        public CachedInfusionRecipe(VampireLeveling.AltarInfusionRequirement req) {
            this.targetLevel = req.getTargetLevel();
            this.pureBloodTier = req.getPureBloodLevel();
            this.hearts = req.getHumanHeartQuantity();

            // Pure blood flask (slot 0)
            if (req.getPureBloodQuantity() > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.pureBlood, req.getPureBloodQuantity(), req.getPureBloodLevel()), 25, 6));
            }

            // Human hearts (slot 1)
            if (req.getHumanHeartQuantity() > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.humanHeart, req.getHumanHeartQuantity()), 43, 6));
            }

            // Vampire book (slot 2)
            if (req.getVampireBookQuantity() > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.vampireBook, req.getVampireBookQuantity()), 34, 24));
            }

            // Ritual reward: represented by next level pure blood flask
            this.result = new PositionedStack(new ItemStack(ModItems.pureBlood, 1, Math.min(4, req.getPureBloodLevel())), 119, 15);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return this.inputs;
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("tile.neorism.altar_infusion.name");
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "neorism.altar_infusion";
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiAltarInfusion.class;
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new AltarInfusionRecipeHandler();
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 15, 24, 17), getOverlayIdentifier()));
    }

    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(74, 15, 176, 14, 24, 17, 40, 0); // arrow
        CachedInfusionRecipe r = (CachedInfusionRecipe) this.arecipes.get(recipe);
        if (r != null) {
            String ritualText = "Vampire Level " + r.targetLevel;
            String bloodText = "Blood Tier: " + (r.pureBloodTier + 1);
            GuiDraw.drawString(ritualText, 25, 46, 0x8A0303, false);
            GuiDraw.drawString(bloodText, 100, 46, 0x404040, false);
        }
    }

    private List<CachedInfusionRecipe> getCraftingList() {
        List<CachedInfusionRecipe> list = new ArrayList<CachedInfusionRecipe>();
        for (int lvl = 5; lvl <= 14; lvl++) {
            VampireLeveling.AltarInfusionRequirement req = VampireLeveling.getInfusionRequirement(lvl);
            if (req != null) {
                list.add(new CachedInfusionRecipe(req));
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
        for (CachedInfusionRecipe r : getCraftingList()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(r.getResult().item, result)) {
                this.arecipes.add(r);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (CachedInfusionRecipe r : getCraftingList()) {
            if (r.contains(r.getIngredients(), ingredient)) {
                this.arecipes.add(r);
            }
        }
    }
}
