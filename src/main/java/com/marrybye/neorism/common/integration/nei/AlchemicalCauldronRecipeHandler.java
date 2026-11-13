package com.marrybye.neorism.common.integration.nei;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.client.gui.GuiAlchemicalCauldron;
import com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron;
import com.marrybye.neorism.common.tileentity.TileEntityAlchemicalCauldron.CauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NEI Recipe Handler for Hunter Alchemical Cauldron.
 * Dynamically mirrors all recipes registered in {@link TileEntityAlchemicalCauldron}.
 */
public class AlchemicalCauldronRecipeHandler extends TemplateRecipeHandler {

    private static final ResourceLocation LIT_PROGRESS = new ResourceLocation("neorism", "textures/gui/sprites/container/alchemical_cauldron/lit_progress.png");
    private static final ResourceLocation BURN_PROGRESS = new ResourceLocation("neorism", "textures/gui/sprites/container/alchemical_cauldron/burn_progress.png");
    private static final ResourceLocation BUBBLES_PROGRESS = new ResourceLocation("neorism", "textures/gui/sprites/container/alchemical_cauldron/bubbles_progress.png");

    private static final List<ItemStack> FUEL_SAMPLES = Arrays.asList(
        new ItemStack(Items.coal, 1, 0),
        new ItemStack(Items.coal, 1, 1)
    );

    public class CachedCauldronRecipe extends CachedRecipe {
        private final PositionedStack fluid;
        private final PositionedStack ingredient;
        private final PositionedStack result;
        private final PositionedStack fuel;

        public CachedCauldronRecipe(CauldronRecipe recipe) {
            this.fluid = recipe.fluidInputs.isEmpty() ? null : new PositionedStack(recipe.fluidInputs, 51, 6);
            this.ingredient = recipe.ingredients.isEmpty() ? null : new PositionedStack(recipe.ingredients, 74, 6);
            this.result = new PositionedStack(recipe.result.copy(), 111, 24);
            this.fuel = new PositionedStack(FUEL_SAMPLES, 51, 42);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> list = new ArrayList<PositionedStack>(2);
            if (this.fluid != null) list.add(this.fluid);
            if (this.ingredient != null) list.add(this.ingredient);
            return getCycledIngredients(cycleticks / 20, list);
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }

        @Override
        public PositionedStack getOtherStack() {
            List<PositionedStack> list = new ArrayList<PositionedStack>(1);
            list.add(this.fuel);
            list = getCycledIngredients(cycleticks / 20, list);
            return list.get(0);
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("tile.neorism.alchemical_cauldron.name");
    }

    @Override
    public String getGuiTexture() {
        return "neorism:textures/gui/container/alchemical_cauldron.png";
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
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 24, 24, 16), getOverlayIdentifier()));
    }

    @Override
    public void drawExtras(int recipe) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int burnH = 14 - ((this.cycleticks / 3) % 15);
        if (burnH > 0) {
            mc.getTextureManager().bindTexture(LIT_PROGRESS);
            Gui.func_146110_a(51, 25 + 14 - burnH, 0.0F, (float) (14 - burnH), 14, burnH, 14.0F, 14.0F);
        }

        int cookW = ((this.cycleticks % 40) * 24) / 40;
        if (cookW > 0) {
            mc.getTextureManager().bindTexture(BURN_PROGRESS);
            Gui.func_146110_a(74, 24, 0.0F, 0.0F, cookW, 16, 24.0F, 16.0F);
        }

        int bubbleH = ((this.cycleticks % 40) * 29) / 40;
        if (bubbleH > 0) {
            mc.getTextureManager().bindTexture(BUBBLES_PROGRESS);
            Gui.func_146110_a(137, 17 + 29 - bubbleH, 0.0F, (float) (29 - bubbleH), 12, bubbleH, 12.0F, 29.0F);
        }
    }

    private List<CachedCauldronRecipe> getCraftingList() {
        List<CauldronRecipe> recipes = TileEntityAlchemicalCauldron.getRecipes();
        List<CachedCauldronRecipe> list = new ArrayList<CachedCauldronRecipe>(recipes.size());
        for (CauldronRecipe recipe : recipes) {
            if (recipe.result != null) {
                list.add(new CachedCauldronRecipe(recipe));
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
