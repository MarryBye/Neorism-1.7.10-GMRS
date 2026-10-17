package com.marrybye.neorism.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

/**
 * Utility for synchronizing faction levels and lord levels with scoreboard objectives.
 */
public class ScoreboardUtil {

    public static final String CRITERIA_FACTION = "neorism:faction";
    public static final String CRITERIA_VAMPIRE = "vampirism:vampire";
    public static final String CRITERIA_HUNTER = "vampirism:hunter";
    public static final String CRITERIA_LORD = "neorism:lord";

    public static void updateScore(EntityPlayer player, String objectiveName, int value) {
        if (player == null || player.worldObj.isRemote) return;
        Scoreboard scoreboard = player.getWorldScoreboard();
        if (scoreboard != null) {
            ScoreObjective obj = scoreboard.getObjective(objectiveName);
            if (obj != null) {
                scoreboard.func_96529_a(player.getCommandSenderName(), obj).setScorePoints(value);
            }
        }
    }
}
