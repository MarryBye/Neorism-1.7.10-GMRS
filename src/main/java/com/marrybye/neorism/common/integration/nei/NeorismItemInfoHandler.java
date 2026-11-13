package com.marrybye.neorism.common.integration.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.items.ItemVampireBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * NEI Guide Handler explaining where special Neorism items drop / are obtained and how they are used.
 */
public class NeorismItemInfoHandler extends TemplateRecipeHandler {

    public class CachedItemInfo extends CachedRecipe {
        private final List<PositionedStack> displayStacks = new ArrayList<PositionedStack>();
        private final List<ItemStack> matchTargets = new ArrayList<ItemStack>();
        public final String titleKey;
        public final String dropKey;
        public final String usageKey;

        public CachedItemInfo(String titleKey, String dropKey, String usageKey, List<Object> items) {
            this.titleKey = titleKey;
            this.dropKey = dropKey;
            this.usageKey = usageKey;
            int idx = 0;
            for (Object entry : items) {
                if (entry == null) continue;
                int x = 8 + (idx % 7) * 21;
                int y = 17 + (idx / 7) * 20;
                this.displayStacks.add(new PositionedStack(entry, x, y));
                if (entry instanceof ItemStack) {
                    this.matchTargets.add((ItemStack) entry);
                } else if (entry instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<ItemStack> list = (List<ItemStack>) entry;
                    this.matchTargets.addAll(list);
                }
                idx++;
            }
        }

        public boolean matches(ItemStack target) {
            if (target == null) return false;
            for (ItemStack candidate : this.matchTargets) {
                if (candidate == null) continue;
                if (candidate.getItemDamage() == OreDictionary.WILDCARD_VALUE && candidate.getItem() == target.getItem()) {
                    return true;
                }
                if (NEIServerUtils.areStacksSameTypeCrafting(candidate, target)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, this.displayStacks);
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("nei.neorism.item_info.title");
    }

    @Override
    public String getGuiTexture() {
        return "neorism:textures/gui/container/altar_of_infusion.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "neorism.item_info";
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new NeorismItemInfoHandler();
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(4, 3, 158, 12), getOverlayIdentifier()));
    }

    @Override
    public void drawBackground(int recipe) {
        GuiDraw.drawRect(3, 2, 160, 128, 0xFFC6C6C6);
        GuiDraw.drawRect(4, 3, 158, 126, 0xFFDBDBDB);

        CachedItemInfo info = (CachedItemInfo) this.arecipes.get(recipe);
        if (info != null) {
            for (PositionedStack stack : info.displayStacks) {
                int sx = stack.relx - 1;
                int sy = stack.rely - 1;
                GuiDraw.drawRect(sx, sy, 18, 18, 0xFF373737);
                GuiDraw.drawRect(sx + 1, sy + 1, 16, 16, 0xFF8B8B8B);
            }
        }
    }

    @Override
    public void drawExtras(int recipe) {
        CachedItemInfo info = (CachedItemInfo) this.arecipes.get(recipe);
        if (info == null) return;

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        fr.drawString(StatCollector.translateToLocal(info.titleKey), 6, 6, 0x8A0303);

        int rows = (info.displayStacks.size() + 6) / 7;
        int y = 19 + rows * 20;

        String dropCombined = StatCollector.translateToLocal("nei.neorism.item_info.source_label") + " " + StatCollector.translateToLocal(info.dropKey);
        String usageCombined = StatCollector.translateToLocal("nei.neorism.item_info.usage_label") + " " + StatCollector.translateToLocal(info.usageKey);
        @SuppressWarnings("unchecked")
        List<String> dropLines = fr.listFormattedStringToWidth(dropCombined, 154);
        @SuppressWarnings("unchecked")
        List<String> usageLines = fr.listFormattedStringToWidth(usageCombined, 154);

        int maxBottom = 126;
        int totalLines = dropLines.size() + usageLines.size();
        boolean compact = y + totalLines * 9 + 3 > maxBottom;
        int lineStep = compact ? 8 : 9;
        int sectionGap = compact ? 2 : 3;

        y = drawLines(fr, dropLines, y, lineStep, maxBottom);
        y += sectionGap;
        drawLines(fr, usageLines, y, lineStep, maxBottom);
    }

    private int drawLines(FontRenderer fr, List<String> lines, int startY, int lineStep, int maxBottom) {
        int y = startY;
        for (String line : lines) {
            if (y + 7 > maxBottom) break;
            fr.drawString(line, 6, y, 0x303030);
            y += lineStep;
        }
        return y;
    }

    private List<CachedItemInfo> getAllEntries() {
        List<CachedItemInfo> list = new ArrayList<CachedItemInfo>();

        // 1. Sovereign Blood
        list.add(new CachedItemInfo(
            "item.neorism.sovereign_blood.name",
            "nei.neorism.item.sovereign_blood.drop",
            "nei.neorism.item.sovereign_blood.usage",
            Collections.<Object>singletonList(new ItemStack(ModItems.sovereignBlood))
        ));

        // 2. Pure Blood (0..4)
        List<ItemStack> pureBloodTiers = new ArrayList<ItemStack>(5);
        for (int i = 0; i < 5; i++) pureBloodTiers.add(new ItemStack(ModItems.pureBlood, 1, i));
        list.add(new CachedItemInfo(
            "item.neorism.pure_blood.name",
            "nei.neorism.item.pure_blood.drop",
            "nei.neorism.item.pure_blood.usage",
            Collections.<Object>singletonList(pureBloodTiers)
        ));

        // 3. Vampire Blood Bottle
        list.add(new CachedItemInfo(
            "item.neorism.vampire_blood_bottle.name",
            "nei.neorism.item.vampire_blood_bottle.drop",
            "nei.neorism.item.vampire_blood_bottle.usage",
            Collections.<Object>singletonList(new ItemStack(ModItems.vampireBloodBottle))
        ));

        // 4. Mother Core
        list.add(new CachedItemInfo(
            "item.neorism.mother_core.name",
            "nei.neorism.item.mother_core.drop",
            "nei.neorism.item.mother_core.usage",
            Collections.<Object>singletonList(new ItemStack(ModItems.motherCore))
        ));

        // 5. Soul Orb Vampire
        list.add(new CachedItemInfo(
            "item.neorism.soul_orb_vampire.name",
            "nei.neorism.item.soul_orb_vampire.drop",
            "nei.neorism.item.soul_orb_vampire.usage",
            Collections.<Object>singletonList(new ItemStack(ModItems.soulOrbVampire))
        ));

        // 6. Vampire Fang
        list.add(new CachedItemInfo(
            "item.neorism.vampire_fang.name",
            "nei.neorism.item.vampire_fang.drop",
            "nei.neorism.item.vampire_fang.usage",
            Collections.<Object>singletonList(new ItemStack(ModItems.vampireFang))
        ));

        // 7. Human Heart & Weak Human Heart
        list.add(new CachedItemInfo(
            "item.neorism.human_heart.name",
            "nei.neorism.item.human_heart.drop",
            "nei.neorism.item.human_heart.usage",
            Arrays.<Object>asList(new ItemStack(ModItems.humanHeart), new ItemStack(ModItems.weakHumanHeart))
        ));

        // 8. Vampire Book (all tiers 0..4)
        List<ItemStack> bookTiers = new ArrayList<ItemStack>(ItemVampireBook.TIERS);
        for (int t = 0; t < ItemVampireBook.TIERS; t++) bookTiers.add(new ItemStack(ModItems.vampireBook, 1, t));
        list.add(new CachedItemInfo(
            "item.neorism.vampire_book.name",
            "nei.neorism.item.vampire_book.drop",
            "nei.neorism.item.vampire_book.usage",
            Collections.<Object>singletonList(bookTiers)
        ));

        // 9. Garlic & Purified Garlic
        list.add(new CachedItemInfo(
            "item.neorism.garlic.name",
            "nei.neorism.item.garlic.drop",
            "nei.neorism.item.garlic.usage",
            Arrays.<Object>asList(new ItemStack(ModItems.garlic), new ItemStack(ModItems.purifiedGarlic))
        ));

        // 10. Case File 144
        list.add(new CachedItemInfo(
            "item.neorism.case_file_144.name",
            "nei.neorism.item.case_file_144.drop",
            "nei.neorism.item.case_file_144.usage",
            Collections.<Object>singletonList(new ItemStack(ModItems.caseFile144))
        ));

        // 11. Hunter Intel (0..9)
        List<ItemStack> intelTiers = new ArrayList<ItemStack>(10);
        for (int i = 0; i < 10; i++) intelTiers.add(new ItemStack(ModItems.hunterIntel, 1, i));
        list.add(new CachedItemInfo(
            "item.neorism.hunter_intel.name",
            "nei.neorism.item.hunter_intel.drop",
            "nei.neorism.item.hunter_intel.usage",
            Collections.<Object>singletonList(intelTiers)
        ));

        // 12. Syringes & Injections
        list.add(new CachedItemInfo(
            "nei.neorism.item.injections.title",
            "nei.neorism.item.injections.drop",
            "nei.neorism.item.injections.usage",
            Arrays.<Object>asList(
                new ItemStack(ModItems.injectionGarlic),
                new ItemStack(ModItems.injectionSanguinare),
                new ItemStack(ModItems.syringeEmpty),
                new ItemStack(ModItems.syringeBlood),
                new ItemStack(ModItems.serumInjection)
            )
        ));

        // 13. Ritual Knife (0 & 1)
        list.add(new CachedItemInfo(
            "item.neorism.ritual_knife.name",
            "nei.neorism.item.ritual_knife.drop",
            "nei.neorism.item.ritual_knife.usage",
            Arrays.<Object>asList(new ItemStack(ModItems.ritualKnife, 1, 0), new ItemStack(ModItems.ritualKnife, 1, 1))
        ));

        // 14. Velmorra Altar & Blood Chalice
        list.add(new CachedItemInfo(
            "nei.neorism.item.velmorra_ritual.title",
            "nei.neorism.item.velmorra_ritual.drop",
            "nei.neorism.item.velmorra_ritual.usage",
            Arrays.<Object>asList(new ItemStack(ModBlocks.velmorraAltar), new ItemStack(ModBlocks.chalice))
        ));

        return list;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            this.arecipes.addAll(getAllEntries());
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (CachedItemInfo info : getAllEntries()) {
            if (info.matches(result)) {
                this.arecipes.add(info);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (CachedItemInfo info : getAllEntries()) {
            if (info.matches(ingredient)) {
                this.arecipes.add(info);
            }
        }
    }
}
