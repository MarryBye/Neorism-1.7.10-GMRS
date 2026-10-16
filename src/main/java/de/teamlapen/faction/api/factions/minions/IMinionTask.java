package de.teamlapen.faction.api.factions.minions;

/**
 * Task assigned to a minion (Defend, Collect, Follow, Guard).
 */
public interface IMinionTask {

    String getTaskId();

    String getTranslationKey();

    void executeTask(IMinionEntity minion);

    boolean isComplete(IMinionEntity minion);
}
