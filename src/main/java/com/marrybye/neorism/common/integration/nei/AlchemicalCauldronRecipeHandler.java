package com.marrybye.neorism.common.integration.nei;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.client.gui.GuiAlchemicalCauldron;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * NEI Recipe Handler for Hunter Alchemical Cauldron.
 */
public class AlchemicalCauldronRecipeHandler extends TemplateRecipeHandler {

    public class CachedCauldronRecipe extends CachedRecipe {
        private final PositionedStack fluid;
        private final PositionedStack ingredient;
        private final PositionedStack result;

        public CachedCauldronRecipe(ItemStack fluidStack, ItemStack ingredientStack, ItemStack resultStack) {
            this.fluid = new PositionedStack(fluidStack, 46, 6);
            this.ingredient = new PositionedStack(ingredientStack, 72, 6);
            this.result = new PositionedStack(resultStack, 111, 24);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> list = new ArrayList<PositionedStack>();
            if (this.fluid != null) list.add(this.fluid);
            if (this.ingredient != null) list.add(this.ingredient);
            return list;
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("tile.neorism.alchemical_cauldron.name");
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/furnace.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "neorism.alchemical_cauldron";
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiAlchemicalCauldron.class;
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new AlchemicalCauldronRecipeHandler();
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 17), getOverlayIdentifier()));
    }

    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(74, 23, 176, 14, 24, 17, 40, 0); // arrow
        this.drawProgressBar(46, 25, 176, 0, 14, 14, 48, 2);  // fire flame
    }

    private List<CachedCauldronRecipe> getCraftingList() {
        List<CachedCauldronRecipe> list = new ArrayList<CachedCauldronRecipe>();

        ItemStack waterBottle = new ItemStack(Items.potionitem, 1, 0);
        ItemStack glassBottle = new ItemStack(Items.glass_bottle, 1, 0);

        // Holy water
        list.add(new CachedCauldronRecipe(waterBottle, new ItemStack(ModItems.garlic), new ItemStack(ModItems.holyWaterBottle, 1, 0)));
        list.add(new CachedCauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 0), new ItemStack(ModItems.purifiedGarlic), new ItemStack(ModItems.holyWaterBottle, 1, 1)));
        list.add(new CachedCauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 1), new ItemStack(ModItems.pureBlood, 1, 0), new ItemStack(ModItems.holyWaterBottle, 1, 2)));

        // Splash holy water
        list.add(new CachedCauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 0), new ItemStack(Items.gunpowder), new ItemStack(ModItems.holyWaterSplashBottle, 1, 0)));
        list.add(new CachedCauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 1), new ItemStack(Items.gunpowder), new ItemStack(ModItems.holyWaterSplashBottle, 1, 1)));
        list.add(new CachedCauldronRecipe(new ItemStack(ModItems.holyWaterBottle, 1, 2), new ItemStack(Items.gunpowder), new ItemStack(ModItems.holyWaterSplashBottle, 1, 2)));

        // Purified Garlic
        list.add(new CachedCauldronRecipe(glassBottle, new ItemStack(ModItems.garlic), new ItemStack(ModItems.purifiedGarlic, 1, 0)));

        // Blood Infused metals
        list.add(new CachedCauldronRecipe(new ItemStack(ModItems.pureBlood, 1, 0), new ItemStack(Items.iron_ingot), new ItemStack(ModItems.bloodInfusedIron, 1, 0)));
        list.add(new CachedCauldronRecipe(new ItemStack(ModItems.pureBlood, 1, 0), new ItemStack(Items.gold_ingot), new ItemStack(ModItems.bloodInfusedGold, 1, 0)));
        list.add(new CachedCauldronRecipe(new ItemStack(ModItems.pureBlood, 1, 0), new ItemStack(Items.diamond), new ItemStack(ModItems.bloodInfusedDiamond, 1, 0)));

        // Alchemical Weapon Oils
        list.add(new CachedCauldronRecipe(waterBottle, new ItemStack(ModItems.vampireFang), new ItemStack(ModItems.oilBottle, 1, 0)));
        list.add(new CachedCauldronRecipe(waterBottle, new ItemStack(ModBlocks.vampireOrchid), new ItemStack(ModItems.oilBottle, 1, 1)));
        list.add(new CachedCauldronRecipe(waterBottle, new ItemStack(ModBlocks.cursedRoots), new ItemStack(ModItems.oilBottle, 1, 2)));
        list.add(new CachedCauldronRecipe(waterBottle, new ItemStack(ModBlocks.cursedEarth), new ItemStack(ModItems.oilBottle, 1, 3)));

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
        for (CachedCauldronRecipe r : getCraftingList()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(r.getResult().item, result)) {
                this.arecipes.add(r);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (CachedCauldronRecipe r : getCraftingList()) {
            if (r.contains(r.getIngredients(), ingredient)) {
                this.arecipes.add(r);
            }
        }
    }
}
