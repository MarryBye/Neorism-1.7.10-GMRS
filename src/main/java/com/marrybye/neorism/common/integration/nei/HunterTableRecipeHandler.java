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

            // Slot 0: Book (GUI 15, 28 -> NEI 10, 17)
            inputs.add(new PositionedStack(new ItemStack(Items.book, req.bookQuantity), 10, 17));

            // Slot 1: Vampire Fangs (GUI 42, 28 -> NEI 37, 17)
            if (req.vampireFangQuantity > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.vampireFang, req.vampireFangQuantity), 37, 17));
            }

            // Slot 2: Pure Blood Flask (GUI 69, 28 -> NEI 64, 17)
            if (req.pureBloodQuantity > 0 && req.pureBloodLevel >= 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.pureBlood, req.pureBloodQuantity, req.pureBloodLevel), 64, 17));
            }

            // Slot 3: Vampire Book (GUI 96, 28 -> NEI 91, 17)
            if (req.vampireBookQuantity > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.vampireBook, req.vampireBookQuantity), 91, 17));
            }

            // Slot 4: Hunter Intel (GUI 146, 28 -> NEI 141, 17)
            this.result = new PositionedStack(new ItemStack(ModItems.hunterIntel, 1, req.resultIntelTier), 141, 17);
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
        return "neorism:textures/gui/container/hunter_table.png";
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
        this.transferRects.add(new RecipeTransferRect(new Rectangle(113, 17, 24, 16), getOverlayIdentifier()));
    }

    @Override
    public void drawExtras(int recipe) {
        CachedHunterTableRecipe r = (CachedHunterTableRecipe) this.arecipes.get(recipe);
        if (r != null) {
            String tierText = StatCollector.translateToLocalFormatted("nei.neorism.hunter_table.tier", r.requiredTableTier + 1);
            String levelText = StatCollector.translateToLocalFormatted("nei.neorism.hunter_table.level", r.targetLevel);
            GuiDraw.drawString(tierText, 10, 48, 0x404040, false);
            GuiDraw.drawString(levelText, 95, 48, 0x006600, false);
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
