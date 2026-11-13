package com.marrybye.neorism.common.integration.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.client.gui.GuiAltarInfusion;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.vampire.VampireLeveling;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NEI Recipe Handler for Vampire Altar of Infusion Rituals (Vampire Levels 5-14).
 */
public class AltarInfusionRecipeHandler extends TemplateRecipeHandler {

    public class CachedInfusionRecipe extends CachedRecipe {
        private final List<PositionedStack> inputs = new ArrayList<PositionedStack>();
        private final PositionedStack altarStack;
        public final int targetLevel;
        public final int pureBloodTier;
        public final int pureBloodQuantity;
        public final int structurePoints;

        public CachedInfusionRecipe(VampireLeveling.AltarInfusionRequirement req) {
            this.targetLevel = req.getTargetLevel();
            this.pureBloodTier = req.getPureBloodLevel();
            this.pureBloodQuantity = req.getPureBloodQuantity();
            this.structurePoints = req.getRequiredStructurePoints();

            // Slot 0: Pure Blood (GUI 44, 34 -> NEI 39, 23)
            if (req.getPureBloodQuantity() > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.pureBlood, req.getPureBloodQuantity(), req.getPureBloodLevel()), 39, 23));
            }

            // Slot 1: Human Heart (GUI 80, 34 -> NEI 75, 23)
            if (req.getHumanHeartQuantity() > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.humanHeart, req.getHumanHeartQuantity()), 75, 23));
            }

            // Slot 2: Vampire Book (GUI 116, 34 -> NEI 111, 23)
            if (req.getVampireBookQuantity() > 0) {
                inputs.add(new PositionedStack(new ItemStack(ModItems.vampireBook, req.getVampireBookQuantity()), 111, 23));
            }

            // Visual indicator of Altar of Infusion (NOT returned from getResult() so R on ingredients never matches a fake item)
            this.altarStack = new PositionedStack(new ItemStack(ModBlocks.altarInfusion), 141, 23);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return this.inputs;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            return Collections.singletonList(this.altarStack);
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("tile.neorism.altar_infusion.name");
    }

    @Override
    public String getGuiTexture() {
        return "neorism:textures/gui/container/altar_of_infusion.png";
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
        this.transferRects.add(new RecipeTransferRect(new Rectangle(136, 20, 24, 22), getOverlayIdentifier()));
    }

    @Override
    public void drawExtras(int recipe) {
        CachedInfusionRecipe r = (CachedInfusionRecipe) this.arecipes.get(recipe);
        if (r != null) {
            String lvlText = StatCollector.translateToLocalFormatted("nei.neorism.altar_infusion.level", r.targetLevel);
            String bloodTier = r.pureBloodQuantity > 0 ? String.valueOf(r.pureBloodTier + 1) : "-";
            String bloodText = StatCollector.translateToLocalFormatted("nei.neorism.altar_infusion.blood_tier", bloodTier);
            String ptsText = StatCollector.translateToLocalFormatted("nei.neorism.altar_infusion.structure_points", r.structurePoints);
            String pillarHint = StatCollector.translateToLocal("nei.neorism.altar_infusion.pillar_hint");

            GuiDraw.drawString(lvlText, 8, 6, 0x8A0303, false);
            GuiDraw.drawString(bloodText, 92, 6, 0x404040, false);
            GuiDraw.drawString(ptsText, 8, 44, 0x404040, false);
            GuiDraw.drawString(pillarHint, 8, 54, 0x555555, false);
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
        // Altar Infusion advances player Vampire Level, never crafts an item.
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null) return;
        Item item = ingredient.getItem();
        boolean isAltarPart = item == Item.getItemFromBlock(ModBlocks.altarInfusion)
            || item == Item.getItemFromBlock(ModBlocks.altarPillar)
            || item == Item.getItemFromBlock(ModBlocks.altarTip);

        for (CachedInfusionRecipe r : getCraftingList()) {
            if (isAltarPart || r.contains(r.getIngredients(), ingredient)) {
                this.arecipes.add(r);
            } else if (item == ModItems.pureBlood && r.pureBloodQuantity > 0 && ingredient.getItemDamage() >= r.pureBloodTier) {
                this.arecipes.add(r);
            }
        }
    }
}
