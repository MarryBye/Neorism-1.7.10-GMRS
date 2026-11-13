package com.marrybye.neorism.common.integration.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.vampire.VampireLeveling;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * NEI Recipe Handler for Vampire Altar of Inspiration (Vampire Levels 2-4).
 */
public class AltarInspirationRecipeHandler extends TemplateRecipeHandler {

    private static final List<ItemStack> BLOOD_CONTAINERS = Arrays.asList(
        new ItemStack(ModItems.bloodBottle, 1, 5),
        new ItemStack(ModItems.bloodFlask),
        new ItemStack(ModFluids.bloodBucket)
    );

    public class CachedInspirationRecipe extends CachedRecipe {
        private final PositionedStack input;
        private final PositionedStack altarStack;
        public final int targetLevel;
        public final int bloodMb;

        public CachedInspirationRecipe(VampireLeveling.AltarInspirationRequirement req) {
            this.targetLevel = req.getTargetLevel();
            this.bloodMb = req.getBloodAmount() * 100;
            this.input = new PositionedStack(BLOOD_CONTAINERS, 37, 18);
            this.altarStack = new PositionedStack(new ItemStack(ModBlocks.altarInspiration), 113, 18);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, Collections.singletonList(this.input));
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
        return StatCollector.translateToLocal("tile.neorism.altar_inspiration.name");
    }

    @Override
    public String getGuiTexture() {
        return "neorism:textures/gui/container/altar_of_infusion.png";
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
        this.transferRects.add(new RecipeTransferRect(new Rectangle(64, 18, 38, 16), getOverlayIdentifier()));
    }

    @Override
    public void drawBackground(int recipe) {
        GuiDraw.drawRect(4, 2, 158, 60, 0xFFC6C6C6);
        GuiDraw.drawRect(5, 3, 156, 58, 0xFFDBDBDB);
        // Slot boxes
        drawSlotBox(36, 17);
        drawSlotBox(112, 17);
    }

    private void drawSlotBox(int x, int y) {
        GuiDraw.drawRect(x, y, 18, 18, 0xFF373737);
        GuiDraw.drawRect(x + 1, y + 1, 16, 16, 0xFF8B8B8B);
    }

    @Override
    public void drawExtras(int recipe) {
        CachedInspirationRecipe r = (CachedInspirationRecipe) this.arecipes.get(recipe);
        if (r != null) {
            String lvlText = StatCollector.translateToLocalFormatted("nei.neorism.altar_inspiration.level", r.targetLevel);
            String bloodText = StatCollector.translateToLocalFormatted("nei.neorism.altar_inspiration.blood", r.bloodMb);
            String hintText = StatCollector.translateToLocal("nei.neorism.altar_inspiration.hint");

            GuiDraw.drawString("->", 79, 22, 0x555555, false);
            GuiDraw.drawString(lvlText, 8, 6, 0x8A0303, false);
            GuiDraw.drawString(bloodText, 88, 6, 0x404040, false);
            GuiDraw.drawString(hintText, 8, 44, 0x555555, false);
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
        // Altar of Inspiration advances player Vampire Level, never crafts an item.
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null) return;
        Item item = ingredient.getItem();
        boolean matches = item == ModItems.bloodBottle
            || item == ModItems.bloodFlask
            || item == ModFluids.bloodBucket
            || item == Item.getItemFromBlock(ModBlocks.altarInspiration);

        if (matches) {
            this.arecipes.addAll(getCraftingList());
        }
    }
}
