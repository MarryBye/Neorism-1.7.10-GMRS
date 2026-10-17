package com.marrybye.neorism.common.entity.minion.tasks;

import de.teamlapen.faction.api.world.entities.minion.IMinionTask;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry of available minion tasks.
 */
public class ModMinionTasks {

    private static final Map<String, IMinionTask> TASKS = new LinkedHashMap<String, IMinionTask>();

    public static final FollowLordTask FOLLOW_LORD = register(new FollowLordTask());
    public static final StayTask STAY = register(new StayTask());
    public static final DefendAreaTask DEFEND_AREA = register(new DefendAreaTask());
    public static final ProtectLordTask PROTECT_LORD = register(new ProtectLordTask());
    public static final CollectResourcesTask COLLECT_BLOOD = register(CollectResourcesTask.createCollectBlood());
    public static final CollectResourcesTask COLLECT_HUNTER_ITEMS = register(CollectResourcesTask.createCollectHunterItems());

    public static <T extends IMinionTask> T register(T task) {
        TASKS.put(task.getId(), task);
        return task;
    }

    public static IMinionTask getTask(String id) {
        return TASKS.get(id);
    }

    public static Map<String, IMinionTask> getAllTasks() {
        return Collections.unmodifiableMap(TASKS);
    }
}
