package com.marrybye.neorism.common.skills;

import java.util.*;

import com.marrybye.neorism.common.action.ActionRegistry;
import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.util.ModLogger;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Central registry of all Skill Trees, Skill Segments, and Skills for Neorism.
 * Matches TeamLapen Vampirism & FactionAPI 1:1.
 */
public class SkillRegistry {

    private static final Map<String, ISkill> SKILLS = new HashMap<String, ISkill>();
    private static final Map<String, ISkillSegment> SEGMENTS = new HashMap<String, ISkillSegment>();
    private static final Map<String, ISkillTree> TREES = new LinkedHashMap<String, ISkillTree>();
    private static final SkillTreeGraph GRAPH = new SkillTreeGraph();

    public static void init() {
        SKILLS.clear();
        SEGMENTS.clear();
        TREES.clear();

        initVampireTrees();
        initHunterTrees();

        GRAPH.buildGraphs();
        ModLogger.info("SkillRegistry initialized: %d skills, %d segments, %d trees",
            SKILLS.size(), SEGMENTS.size(), TREES.size());
    }

    private static void initVampireTrees() {
        IPlayableFaction vampireFaction = (IPlayableFaction) ModFactions.VAMPIRE;

        // Tree 1: Vampire Level
        SkillTree vampireLevel = new SkillTree(
            "vampire_level",
            vampireFaction,
            new ItemStack(ModItems.vampireFang != null ? ModItems.vampireFang : Items.apple),
            new ResourceLocation("factionapi", "textures/gui/skills/backgrounds/level.png")
        );

        // Core Skills
        Skill sLevelRoot = regSkill(new Skill("vampire_level_root", vampireFaction, 1, 0, true, "vampire"));
        Skill sNightVision = regSkill(new Skill("night_vision", vampireFaction, 1, 1, false, "night_vision", ActionRegistry.getAction("night_vision")));
        Skill sRegen = regSkill(new Skill("vampire_regen", vampireFaction, 1, 1, false, "vampire", ActionRegistry.getAction("vampire_regen")));
        Skill sFledgling = regSkill(new Skill("no_longer_fledgling", vampireFaction, 2, 1, true, "vampire"));

        // Branch 1: Combat / Rage
        Skill sRage = regSkill(new Skill("vampire_rage", vampireFaction, 3, 1, false, "vampire_rage", ActionRegistry.getAction("vampire_rage")));
        Skill sLessThirst = regSkill(new Skill("less_blood_thirst", vampireFaction, 4, 1, true, "less_blood_thirst"));
        Skill sBloodCharge = regSkill(new Skill("blood_charge", vampireFaction, 5, 1, true, "vampire"));
        Skill sBloodVision = regSkill(new Skill("blood_vision", vampireFaction, 6, 1, true, "vampire"));
        Skill sGarlicVision = regSkill(new Skill("garlic_blood_vision", vampireFaction, 7, 1, true, "vampire"));
        Skill sHalfInvuln = regSkill(new Skill("half_invulnerable", vampireFaction, 8, 1, false, "vampire", ActionRegistry.getAction("vampire_rage")));
        Skill sFinisher = regSkill(new Skill("sword_finisher", vampireFaction, 9, 2, true, "sword_finisher"));

        // Branch 2: Biter / Agility
        Skill sAdvancedBiter = regSkill(new Skill("advanced_biter", vampireFaction, 3, 1, true, "vampire"));
        Skill sHissing = regSkill(new Skill("hissing", vampireFaction, 4, 1, false, "vampire", ActionRegistry.getAction("vampire_rage")));
        Skill sWaterResist = regSkill(new Skill("water_resistance", vampireFaction, 5, 1, true, "water_resistance"));
        Skill sAtkSpeed = regSkill(new Skill("vampire_attack_speed", vampireFaction, 6, 1, true, "vampire_attack_speed"));
        Skill sAtkDamage = regSkill(new Skill("vampire_attack_damage", vampireFaction, 6, 1, true, "vampire_attack_damage"));
        Skill sLessSun = regSkill(new Skill("less_sundamage", vampireFaction, 7, 1, true, "less_sundamage"));
        Skill sSpeed = regSkill(new Skill("vampire_speed", vampireFaction, 8, 1, true, "vampire_speed"));
        Skill sJump = regSkill(new Skill("vampire_jump", vampireFaction, 8, 1, true, "vampire"));
        Skill sDarkStalker = regSkill(new Skill("dark_stalker", vampireFaction, 9, 1, true, "vampire"));
        Skill sInvis = regSkill(new Skill("vampire_invisibility", vampireFaction, 10, 2, false, "vampire_invisibility", ActionRegistry.getAction("invisibility")));

        // Branch 3: Disguise / Spells
        Skill sDisguise = regSkill(new Skill("vampire_disguise", vampireFaction, 3, 1, false, "vampire", ActionRegistry.getAction("bat_form")));
        Skill sSummonBats = regSkill(new Skill("summon_bats", vampireFaction, 4, 1, false, "vampire", ActionRegistry.getAction("summon_bats")));
        Skill sSunscreen = regSkill(new Skill("sunscreen", vampireFaction, 5, 1, false, "less_sundamage", ActionRegistry.getAction("sunscreen")));
        Skill sDarkProj = regSkill(new Skill("dark_blood_projectile", vampireFaction, 6, 1, false, "vampire", ActionRegistry.getAction("dark_blood_projectile")));
        Skill sFreeze = regSkill(new Skill("freeze", vampireFaction, 7, 1, false, "vampire", ActionRegistry.getAction("freeze")));
        Skill sTeleport = regSkill(new Skill("teleport", vampireFaction, 8, 2, false, "vampire", ActionRegistry.getAction("teleport")));
        Skill sNeonatal = regSkill(new Skill("neonatal_decrease", vampireFaction, 4, 1, true, "neonatal_decrease"));

        // Register Segments in Graph
        regSeg(vampireLevel, new SkillSegment("vampire/level_root", sLevelRoot));
        regSeg(vampireLevel, new SkillSegment("vampire/night_vision", sNightVision).parents("vampire/level_root"));
        regSeg(vampireLevel, new SkillSegment("vampire/regeneration", sRegen).parents("vampire/night_vision"));
        regSeg(vampireLevel, new SkillSegment("vampire/no_longer_fledgling", sFledgling).parents("vampire/regeneration"));

        // Branch 1
        regSeg(vampireLevel, new SkillSegment("vampire/vampire_rage", sRage).parents("vampire/no_longer_fledgling"));
        regSeg(vampireLevel, new SkillSegment("vampire/frugal_vampire", sLessThirst).parents("vampire/vampire_rage"));
        regSeg(vampireLevel, new SkillSegment("vampire/blood_charge", sBloodCharge).parents("vampire/frugal_vampire"));
        regSeg(vampireLevel, new SkillSegment("vampire/blood_vision", sBloodVision).parents("vampire/blood_charge"));
        regSeg(vampireLevel, new SkillSegment("vampire/garlic_blood_vision", sGarlicVision).parents("vampire/blood_vision"));
        regSeg(vampireLevel, new SkillSegment("vampire/damage_limiter", sHalfInvuln).parents("vampire/blood_vision"));
        regSeg(vampireLevel, new SkillSegment("vampire/finisher", sFinisher).parents("vampire/damage_limiter"));

        // Branch 2
        regSeg(vampireLevel, new SkillSegment("vampire/advanced_biter", sAdvancedBiter).parents("vampire/no_longer_fledgling"));
        regSeg(vampireLevel, new SkillSegment("vampire/hissing", sHissing).parents("vampire/advanced_biter"));
        regSeg(vampireLevel, new SkillSegment("vampire/water_resistance", sWaterResist).parents("vampire/advanced_biter"));
        regSeg(vampireLevel, new SkillSegment("vampire/attack_speed", sAtkSpeed).parents("vampire/water_resistance"));
        regSeg(vampireLevel, new SkillSegment("vampire/damage", sAtkDamage).parents("vampire/water_resistance"));
        regSeg(vampireLevel, new SkillSegment("vampire/tough_skin", sLessSun).parents("vampire/damage", "vampire/attack_speed"));
        regSeg(vampireLevel, new SkillSegment("vampire/movement_speed", sSpeed).parents("vampire/tough_skin"));
        regSeg(vampireLevel, new SkillSegment("vampire/jump", sJump).parents("vampire/tough_skin"));
        regSeg(vampireLevel, new SkillSegment("vampire/dark_stalker", sDarkStalker).parents("vampire/tough_skin"));
        regSeg(vampireLevel, new SkillSegment("vampire/invisibility", sInvis).parents("vampire/dark_stalker", "vampire/jump", "vampire/movement_speed"));

        // Branch 3
        regSeg(vampireLevel, new SkillSegment("vampire/human_disguise", sDisguise).parents("vampire/no_longer_fledgling"));
        regSeg(vampireLevel, new SkillSegment("vampire/summon_bats", sSummonBats).parents("vampire/human_disguise"));
        regSeg(vampireLevel, new SkillSegment("vampire/sunscreen", sSunscreen).parents("vampire/summon_bats"));
        regSeg(vampireLevel, new SkillSegment("vampire/dark_blood_projectile", sDarkProj).parents("vampire/sunscreen"));
        regSeg(vampireLevel, new SkillSegment("vampire/freeze", sFreeze).parents("vampire/dark_blood_projectile"));
        regSeg(vampireLevel, new SkillSegment("vampire/teleport", sTeleport).parents("vampire/freeze"));
        regSeg(vampireLevel, new SkillSegment("vampire/fast_recovery_or_resurrection", sNeonatal).parents("vampire/human_disguise"));

        regTree(vampireLevel);

        // Tree 2: Vampire Lord
        SkillTree vampireLord = new SkillTree(
            "vampire_lord",
            vampireFaction,
            new ItemStack(ModItems.vampireCrown != null ? ModItems.vampireCrown : Items.gold_ingot),
            new ResourceLocation("factionapi", "textures/gui/skills/backgrounds/level.png")
        );
        Skill sLordRoot = regSkill(new Skill("vampire_lord_root", vampireFaction, 1, 0, true, "vampire_lord"));
        Skill sBetterMinions = regSkill(new Skill("vampire_minion_stats_increase", vampireFaction, 1, 1, true, "vampire_minion_stats_increase"));
        Skill sLordSpeed = regSkill(new Skill("vampire_lord_speed", vampireFaction, 2, 1, true, "vampire_speed"));
        Skill sMinionCollect = regSkill(new Skill("vampire_minion_collect", vampireFaction, 3, 1, true, "vampire_minion_collect"));
        Skill sMinionRecovery = regSkill(new Skill("vampire_minion_recovery", vampireFaction, 4, 1, true, "vampire"));

        regSeg(vampireLord, new SkillSegment("vampire/lord_root", sLordRoot));
        regSeg(vampireLord, new SkillSegment("vampire/better_minions", sBetterMinions).parents("vampire/lord_root"));
        regSeg(vampireLord, new SkillSegment("vampire/lord_movement_or_attack_speed", sLordSpeed).parents("vampire/lord_root").after("vampire/better_minions"));
        regSeg(vampireLord, new SkillSegment("vampire/blood_collection", sMinionCollect).parents("vampire/lord_root").after("vampire/lord_movement_or_attack_speed"));
        regSeg(vampireLord, new SkillSegment("vampire/minion_recovery", sMinionRecovery).parents("vampire/lord_root").after("vampire/blood_collection"));

        regTree(vampireLord);
    }

    private static void initHunterTrees() {
        IPlayableFaction hunterFaction = (IPlayableFaction) ModFactions.HUNTER;

        // Tree 1: Hunter Level
        SkillTree hunterLevel = new SkillTree(
            "hunter_level",
            hunterFaction,
            new ItemStack(ModItems.stake != null ? ModItems.stake : Items.stick),
            new ResourceLocation("factionapi", "textures/gui/skills/backgrounds/level.png")
        );

        Skill sHunterRoot = regSkill(new Skill("hunter_level_root", hunterFaction, 1, 0, true, "hunter"));
        Skill sStake1 = regSkill(new Skill("stake1", hunterFaction, 1, 1, true, "stake1"));
        Skill sDisguise = regSkill(new Skill("hunter_disguise", hunterFaction, 2, 1, false, "hunter", ActionRegistry.getAction("hunter_awareness")));
        Skill sAlchemy = regSkill(new Skill("master_alchemist", hunterFaction, 3, 1, true, "master_alchemist"));
        Skill sPurifiedGarlic = regSkill(new Skill("purified_garlic", hunterFaction, 4, 1, true, "purified_garlic"));
        Skill sCrucifix = regSkill(new Skill("ultimate_crucifix", hunterFaction, 5, 1, true, "ultimate_crucifix"));
        Skill sAwareness = regSkill(new Skill("awareness_hunter", hunterFaction, 6, 1, false, "hunter", ActionRegistry.getAction("hunter_awareness")));

        Skill sMultitask = regSkill(new Skill("multitask_brewing", hunterFaction, 3, 1, true, "multitask_brewing"));
        Skill sSwiftBrew = regSkill(new Skill("swift_brewing", hunterFaction, 4, 1, true, "swift_brewing"));
        Skill sMasterBrew = regSkill(new Skill("master_brewer", hunterFaction, 5, 1, true, "master_brewer"));
        Skill sPotionResist = regSkill(new Skill("potion_resistance_hunter", hunterFaction, 6, 1, false, "hunter", ActionRegistry.getAction("potion_resistance")));

        Skill sTech = regSkill(new Skill("weapon_table", hunterFaction, 3, 1, true, "weapon_table"));
        Skill sAtkDamage = regSkill(new Skill("hunter_attack_damage", hunterFaction, 4, 1, true, "hunter_attack_damage"));
        Skill sAtkSpeed = regSkill(new Skill("hunter_attack_speed", hunterFaction, 4, 1, true, "hunter_attack_speed"));
        Skill sMasterCraft = regSkill(new Skill("master_craftsmanship", hunterFaction, 5, 1, true, "master_craftsmanship"));
        Skill sStake2 = regSkill(new Skill("stake2", hunterFaction, 6, 2, true, "stake2"));

        regSeg(hunterLevel, new SkillSegment("hunter/level_root", sHunterRoot));
        regSeg(hunterLevel, new SkillSegment("hunter/stake", sStake1).parents("hunter/level_root"));
        regSeg(hunterLevel, new SkillSegment("hunter/disguise", sDisguise).parents("hunter/stake"));

        // Alchemy path
        regSeg(hunterLevel, new SkillSegment("hunter/basic_alchemy", sAlchemy).parents("hunter/disguise"));
        regSeg(hunterLevel, new SkillSegment("hunter/garlic_diffuser", sPurifiedGarlic).parents("hunter/basic_alchemy"));
        regSeg(hunterLevel, new SkillSegment("hunter/crucifix_wielder", sCrucifix).parents("hunter/basic_alchemy").after("hunter/garlic_diffuser"));
        regSeg(hunterLevel, new SkillSegment("hunter/hunter_awareness", sAwareness).parents("hunter/crucifix_wielder"));

        // Brewing path
        regSeg(hunterLevel, new SkillSegment("hunter/multitask_brewing", sMultitask).parents("hunter/disguise").after("hunter/basic_alchemy"));
        regSeg(hunterLevel, new SkillSegment("hunter/swift_brewing", sSwiftBrew).parents("hunter/multitask_brewing"));
        regSeg(hunterLevel, new SkillSegment("hunter/master_brewer", sMasterBrew).parents("hunter/swift_brewing"));
        regSeg(hunterLevel, new SkillSegment("hunter/potion_resistance", sPotionResist).parents("hunter/master_brewer"));

        // Weapon Tech path
        regSeg(hunterLevel, new SkillSegment("hunter/basic_technology", sTech).parents("hunter/disguise"));
        regSeg(hunterLevel, new SkillSegment("hunter/attack_damage", sAtkDamage).parents("hunter/basic_technology"));
        regSeg(hunterLevel, new SkillSegment("hunter/attack_speed", sAtkSpeed).parents("hunter/basic_technology").after("hunter/attack_damage"));
        regSeg(hunterLevel, new SkillSegment("hunter/master_craftsmanship", sMasterCraft).parents("hunter/attack_damage", "hunter/attack_speed"));
        regSeg(hunterLevel, new SkillSegment("hunter/stake2", sStake2).parents("hunter/master_craftsmanship"));

        regTree(hunterLevel);

        // Tree 2: Hunter Lord
        SkillTree hunterLord = new SkillTree(
            "hunter_lord",
            hunterFaction,
            new ItemStack(ModItems.stake != null ? ModItems.stake : Items.iron_ingot),
            new ResourceLocation("factionapi", "textures/gui/skills/backgrounds/level.png")
        );
        Skill sHunterLordRoot = regSkill(new Skill("hunter_lord_root", hunterFaction, 1, 0, true, "hunter_lord"));
        Skill sHunterMinionStats = regSkill(new Skill("hunter_minion_stats_increase", hunterFaction, 1, 1, true, "hunter_minion_stats_increase"));
        Skill sHunterCrossbows = regSkill(new Skill("minion_tech_crossbows", hunterFaction, 2, 1, true, "minion_tech_crossbows"));
        Skill sHunterCollect = regSkill(new Skill("hunter_minion_collect", hunterFaction, 3, 1, true, "hunter_minion_collect"));

        regSeg(hunterLord, new SkillSegment("hunter/lord_root", sHunterLordRoot));
        regSeg(hunterLord, new SkillSegment("hunter/better_minions", sHunterMinionStats).parents("hunter/lord_root"));
        regSeg(hunterLord, new SkillSegment("hunter/minion_tech_crossbows", sHunterCrossbows).parents("hunter/lord_root").after("hunter/better_minions"));
        regSeg(hunterLord, new SkillSegment("hunter/blood_collection", sHunterCollect).parents("hunter/lord_root").after("hunter/minion_tech_crossbows"));

        regTree(hunterLord);
    }

    private static Skill regSkill(Skill skill) {
        SKILLS.put(skill.getSkillId(), skill);
        return skill;
    }

    private static void regSeg(SkillTree tree, SkillSegment segment) {
        tree.addSegment(segment);
        SEGMENTS.put(segment.getId(), segment);
    }

    private static void regTree(SkillTree tree) {
        TREES.put(tree.getTreeId(), tree);
        GRAPH.registerTree(tree);
    }

    public static ISkill getSkill(String id) {
        return SKILLS.get(id);
    }

    public static ISkillSegment getSegment(String id) {
        return SEGMENTS.get(id);
    }

    public static ISkillTree getTree(String id) {
        return TREES.get(id);
    }

    public static Collection<ISkillTree> getTreesForFaction(IPlayableFaction faction) {
        List<ISkillTree> list = new ArrayList<ISkillTree>();
        for (ISkillTree t : TREES.values()) {
            if (t.getFaction() != null && t.getFaction().equals(faction)) {
                list.add(t);
            }
        }
        return list;
    }

    public static SkillTreeGraph getGraph() {
        return GRAPH;
    }
}
