package com.marrybye.neorism.common.integration.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.client.gui.GuiHunterTable;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.hunter.HunterLeveling;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * NEI Recipe Handler for Hunter Research Table.
 */
public class HunterTableRecipeHandler extends TemplateRecipeHandler {

    public class CachedHunterTableRecipe extends CachedRecipe {
        private final List<PositionedStack> inputs = new ArrayList<PositionedStack>();
        private final PositionedStack result;
        public final int requiredTableTier;
        public final int targetLevel;

        public CachedHunterTableRecipe(HunterLeveling.HunterTableRequirement req, int targetLevel) {
            this.requiredTableTier = req.requiredTableTier;
            this.targetLevel = targetLevel;

            // Slot 0: Book
            inputs.add(new PositionedStack(new ItemStack(Items.book), 25, 6));

            // Slot 1: Vampire Fangs
            if (req.vampireFangQuantity > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.vampireFang, req.vampireFangQuantity), 43, 6));
            }

            // Slot 2: Pure Blood Flask
            if (req.pureBloodQuantity > 0 && req.pureBloodLevel >= 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.pureBlood, req.pureBloodQuantity, req.pureBloodLevel), 25, 24));
            }

            // Slot 3: Vampire Book
            if (req.vampireBookQuantity > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.vampireBook, req.vampireBookQuantity), 43, 24));
            }

            // Result: Hunter Intel
            this.result = new PositionedStack(new ItemStack(ModItems.hunterIntel, 1, req.resultIntelTier), 119, 15);
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
        return StatCollector.translateToLocal("tile.neorism.hunter_table.name");
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "neorism.hunter_table";
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiHunterTable.class;
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new HunterTableRecipeHandler();
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 15, 24, 17), getOverlayIdentifier()));
    }

    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(74, 15, 176, 14, 24, 17, 40, 0); // arrow
        CachedHunterTableRecipe r = (CachedHunterTableRecipe) this.arecipes.get(recipe);
        if (r != null) {
            String tierText = "Table Tier: " + (r.requiredTableTier + 1);
            String levelText = "For Lvl: " + r.targetLevel;
            GuiDraw.drawString(tierText, 25, 46, 0x404040, false);
            GuiDraw.drawString(levelText, 100, 46, 0x006600, false);
        }
    }

    private List<CachedHunterTableRecipe> getCraftingList() {
        List<CachedHunterTableRecipe> list = new ArrayList<CachedHunterTableRecipe>();
        for (int lvl = 5; lvl <= 14; lvl++) {
            HunterLeveling.HunterTableRequirement req = HunterLeveling.getTableRequirement(lvl);
            if (req != null) {
                list.add(new CachedHunterTableRecipe(req, lvl));
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
        for (CachedHunterTableRecipe r : getCraftingList()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(r.getResult().item, result)) {
                this.arecipes.add(r);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (CachedHunterTableRecipe r : getCraftingList()) {
            if (r.contains(r.getIngredients(), ingredient)) {
                this.arecipes.add(r);
            }
        }
    }
}
