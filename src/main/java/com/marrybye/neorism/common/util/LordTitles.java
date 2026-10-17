package com.marrybye.neorism.common.util;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordTitleProvider;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

/**
 * Lord title provider for Vampire and Hunter factions matching canonical TeamLapen implementation.
 */
public class LordTitles {

    public static final VampireTitles VAMPIRE = new VampireTitles();
    public static final HunterTitles HUNTER = new HunterTitles();

    private static IChatComponent title(String faction, int level, String suffix) {
        return new ChatComponentTranslation("lord_title.vampirism." + faction + "." + level + suffix);
    }

    public static class VampireTitles implements ILordTitleProvider {

        @Override
        public IChatComponent getLordTitle(int level, IPlayableFaction.TitleGender titleGender) {
            String gender = titleGender == IPlayableFaction.TitleGender.FEMALE ? "female" : "male";
            return title("vampire." + gender, level, "");
        }

        @Override
        public IChatComponent getShort(int level, IPlayableFaction.TitleGender titleGender) {
            String gender = titleGender == IPlayableFaction.TitleGender.FEMALE ? "female" : "male";
            return title("vampire." + gender, level, ".short");
        }
    }

    public static class HunterTitles implements ILordTitleProvider {

        @Override
        public IChatComponent getLordTitle(int level, IPlayableFaction.TitleGender titleGender) {
            return title("hunter", level, "");
        }

        @Override
        public IChatComponent getShort(int level, IPlayableFaction.TitleGender titleGender) {
            return title("hunter", level, ".short");
        }
    }
}
