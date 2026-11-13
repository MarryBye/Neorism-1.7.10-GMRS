package com.marrybye.neorism.common.integration.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFluids;
import com.marrybye.neorism.common.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NEI Guide Handler explaining Neorism multiblock structures and rituals.
 */
public class NeorismStructureInfoHandler extends TemplateRecipeHandler {

    public class CachedStructureInfo extends CachedRecipe {
        private final List<PositionedStack> components = new ArrayList<PositionedStack>();
        private final List<ItemStack> matchTargets = new ArrayList<ItemStack>();
        public final String titleKey;
        public final String descKey;

        public CachedStructureInfo(String titleKey, String descKey, List<Object> items, List<ItemStack> extraMatches) {
            this.titleKey = titleKey;
            this.descKey = descKey;
            int idx = 0;
            for (Object entry : items) {
                if (entry == null) continue;
                int x = 8 + (idx % 7) * 21;
                int y = 17 + (idx / 7) * 20;
                this.components.add(new PositionedStack(entry, x, y));
                if (entry instanceof ItemStack) {
                    this.matchTargets.add((ItemStack) entry);
                } else if (entry instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<ItemStack> list = (List<ItemStack>) entry;
                    this.matchTargets.addAll(list);
                }
                idx++;
            }
            if (extraMatches != null) {
                this.matchTargets.addAll(extraMatches);
            }
        }

        public boolean matches(ItemStack target) {
            if (target == null) return false;
            for (ItemStack candidate : this.matchTargets) {
                if (candidate == null) continue;
                if (NEIServerUtils.areStacksSameTypeCrafting(candidate, target)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, this.components);
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("nei.neorism.structure_info.title");
    }

    @Override
    public String getGuiTexture() {
        return "neorism:textures/gui/container/altar_of_infusion.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "neorism.structure_info";
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new NeorismStructureInfoHandler();
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(4, 3, 158, 12), getOverlayIdentifier()));
    }

    @Override
    public void drawBackground(int recipe) {
        GuiDraw.drawRect(3, 2, 160, 128, 0xFFC6C6C6);
        GuiDraw.drawRect(4, 3, 158, 126, 0xFFDBDBDB);

        CachedStructureInfo info = (CachedStructureInfo) this.arecipes.get(recipe);
        if (info != null) {
            for (PositionedStack stack : info.components) {
                int sx = stack.relx - 1;
                int sy = stack.rely - 1;
                GuiDraw.drawRect(sx, sy, 18, 18, 0xFF373737);
                GuiDraw.drawRect(sx + 1, sy + 1, 16, 16, 0xFF8B8B8B);
            }
        }
    }

    @Override
    public void drawExtras(int recipe) {
        CachedStructureInfo info = (CachedStructureInfo) this.arecipes.get(recipe);
        if (info == null) return;

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        String title = StatCollector.translateToLocal(info.titleKey);
        fr.drawString(title, 6, 6, 0x8A0303);

        int rows = (info.components.size() + 6) / 7;
        int textY = 19 + rows * 20;
        String body = StatCollector.translateToLocal(info.descKey);

        @SuppressWarnings("unchecked")
        List<String> lines = fr.listFormattedStringToWidth(body, 154);
        int maxBottom = 126;
        int lineStep = (textY + lines.size() * 9 > maxBottom) ? 8 : 9;
        int lineY = textY;
        for (String line : lines) {
            if (lineY + 7 > maxBottom) break;
            fr.drawString(line, 6, lineY, 0x303030);
            lineY += lineStep;
        }
    }

    private List<CachedStructureInfo> getAllStructures() {
        List<CachedStructureInfo> list = new ArrayList<CachedStructureInfo>();

        // 1. Altar of Inspiration
        List<ItemStack> bloodContainers = Arrays.asList(
            new ItemStack(ModItems.bloodBottle, 1, 5),
            new ItemStack(ModItems.bloodFlask),
            new ItemStack(ModFluids.bloodBucket)
        );
        List<ItemStack> inspirationExtras = new ArrayList<ItemStack>();
        for (int b = 1; b <= 5; b++) inspirationExtras.add(new ItemStack(ModItems.bloodBottle, 1, b));
        list.add(new CachedStructureInfo(
            "nei.neorism.structure.altar_inspiration.title",
            "nei.neorism.structure.altar_inspiration.desc",
            Arrays.<Object>asList(new ItemStack(ModBlocks.altarInspiration), bloodContainers),
            inspirationExtras
        ));

        // 2. Altar of Infusion + Pillars & Tips
        list.add(new CachedStructureInfo(
            "nei.neorism.structure.altar_infusion.title",
            "nei.neorism.structure.altar_infusion.desc",
            Arrays.<Object>asList(
                new ItemStack(ModBlocks.altarInfusion),
                new ItemStack(ModBlocks.altarPillar),
                new ItemStack(ModBlocks.altarTip),
                Arrays.asList(
                    new ItemStack(Blocks.stonebrick),
                    new ItemStack(Blocks.quartz_block),
                    new ItemStack(Blocks.iron_block),
                    new ItemStack(Blocks.gold_block)
                )
            ),
            null
        ));

        // 3. Velmorra Portal & Velmorra Altar
        List<ItemStack> portalExtras = Arrays.asList(new ItemStack(ModItems.ritualKnife, 1, 0));
        list.add(new CachedStructureInfo(
            "nei.neorism.structure.velmorra_portal.title",
            "nei.neorism.structure.velmorra_portal.desc",
            Arrays.<Object>asList(
                new ItemStack(ModBlocks.velmorraAltar),
                new ItemStack(ModItems.ritualKnife, 1, 1),
                new ItemStack(ModBlocks.darkStoneBricks),
                new ItemStack(ModBlocks.darkStoneTiles),
                new ItemStack(ModBlocks.darkStone),
                new ItemStack(Blocks.obsidian)
            ),
            portalExtras
        ));

        // 4. Dracula Blood Chalice Ritual (Velmorra Castle)
        List<ItemStack> chaliceBlood = Arrays.asList(
            new ItemStack(ModItems.bloodBottle, 1, 5),
            new ItemStack(ModItems.vampireBloodBottle),
            new ItemStack(ModItems.pureBlood, 1, 0),
            new ItemStack(ModItems.pureBlood, 1, 4),
            new ItemStack(ModItems.sovereignBlood)
        );
        List<ItemStack> chaliceExtras = new ArrayList<ItemStack>();
        for (int t = 0; t < 5; t++) chaliceExtras.add(new ItemStack(ModItems.pureBlood, 1, t));
        list.add(new CachedStructureInfo(
            "nei.neorism.structure.dracula_chalice.title",
            "nei.neorism.structure.dracula_chalice.desc",
            Arrays.<Object>asList(new ItemStack(ModBlocks.chalice), chaliceBlood),
            chaliceExtras
        ));

        // 5. Faction Totem
        list.add(new CachedStructureInfo(
            "nei.neorism.structure.totem.title",
            "nei.neorism.structure.totem.desc",
            Arrays.<Object>asList(new ItemStack(ModBlocks.totem), new ItemStack(ModBlocks.totemTop)),
            null
        ));

        // 6. Coffin
        List<ItemStack> coffinList = new ArrayList<ItemStack>();
        coffinList.add(new ItemStack(ModBlocks.coffin));
        if (ModBlocks.coffins != null) {
            for (Block c : ModBlocks.coffins) {
                if (c != null) coffinList.add(new ItemStack(c));
            }
        }
        list.add(new CachedStructureInfo(
            "nei.neorism.structure.coffin.title",
            "nei.neorism.structure.coffin.desc",
            Arrays.<Object>asList(coffinList),
            null
        ));

        // 7. Hunter Tent
        list.add(new CachedStructureInfo(
            "nei.neorism.structure.hunter_tent.title",
            "nei.neorism.structure.hunter_tent.desc",
            Arrays.<Object>asList(new ItemStack(ModItems.itemTent), new ItemStack(ModItems.itemTentSpawner)),
            null
        ));

        // 8. Garlic Diffuser (Normal / Long / Strong)
        list.add(new CachedStructureInfo(
            "nei.neorism.structure.garlic_diffuser.title",
            "nei.neorism.structure.garlic_diffuser.desc",
            Arrays.<Object>asList(
                new ItemStack(ModBlocks.garlicDiffuser),
                new ItemStack(ModBlocks.garlicDiffuserLong),
                new ItemStack(ModBlocks.garlicDiffuserStrong),
                new ItemStack(ModItems.purifiedGarlic),
                new ItemStack(ModItems.garlicDiffuserCore, 1, 0)
            ),
            Arrays.asList(
                new ItemStack(ModItems.garlicDiffuserCore, 1, 1),
                new ItemStack(ModItems.garlicDiffuserCore, 1, 2)
            )
        ));

        return list;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            this.arecipes.addAll(getAllStructures());
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (CachedStructureInfo info : getAllStructures()) {
            if (info.matches(result)) {
                this.arecipes.add(info);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (CachedStructureInfo info : getAllStructures()) {
            if (info.matches(ingredient)) {
                this.arecipes.add(info);
            }
        }
    }
}
