package com.marrybye.neorism.common.achievement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

/**
 * Achievements registry for Neorism (1.7.10 backport of modern Vampirism).
 * Organizes achievements into two primary pages: Vampire Path and Hunter Path.
 */
public class ModAchievements {

    private static final List<Achievement> ALL_ACHIEVEMENTS = new ArrayList<>();
    private static final List<Achievement> VAMPIRE_ACHIEVEMENTS = new ArrayList<>();
    private static final List<Achievement> HUNTER_ACHIEVEMENTS = new ArrayList<>();

    // --- Main / General Achievements ---
    public static Achievement root;
    public static Achievement vampireForest;
    public static Achievement ancientKnowledge;
    public static Achievement domainOfTheDead;
    public static Achievement reopeningOldWounds;
    public static Achievement regicide;

    // --- Vampire Path Achievements ---
    public static Achievement becomeVampire;
    public static Achievement firstBlood;
    public static Achievement baptismOfBlood;
    public static Achievement vampireBat;
    public static Achievement vampireMaxLevel;
    public static Achievement vampireBecomeLord;
    public static Achievement vampireMaxLord;
    public static Achievement extraStorage;
    public static Achievement freezeKill;
    public static Achievement defeatDracula;
    public static Achievement vampireKillMother;
    public static Achievement plagueInc;
    public static Achievement resurrect;
    public static Achievement sniped;
    public static Achievement texasMassacre;
    public static Achievement yuck;

    // --- Hunter Path Achievements ---
    public static Achievement becomeHunter;
    public static Achievement hunterTraining;
    public static Achievement stake;
    public static Achievement hunterCrossbow;
    public static Achievement enhancedCrossbow;
    public static Achievement techCrossbow;
    public static Achievement hunterMaxLevel;
    public static Achievement hunterBecomeLord;
    public static Achievement hunterMaxLord;
    public static Achievement brochette;
    public static Achievement crazedGunman;
    public static Achievement cureVampireVillager;
    public static Achievement hunterKillMother;
    public static Achievement killResurrectedVampire;
    public static Achievement betrayal;
    public static Achievement worthIt;

    // --- Achievement Pages ---
    public static AchievementPage vampirePage;
    public static AchievementPage hunterPage;

    public static void init() {
        registerMainAndVampireAchievements();
        registerHunterAchievements();

        vampirePage = new AchievementPage("Neorism: Vampires", VAMPIRE_ACHIEVEMENTS.toArray(new Achievement[0]));
        hunterPage = new AchievementPage("Neorism: Hunters", HUNTER_ACHIEVEMENTS.toArray(new Achievement[0]));

        AchievementPage.registerAchievementPage(vampirePage);
        AchievementPage.registerAchievementPage(hunterPage);
    }

    private static void registerMainAndVampireAchievements() {
        // Root: Vampirism / Neorism
        root = registerVampire("neorism.vampirism", 0, 0, new ItemStack(Items.glass_bottle), null);

        // General exploration
        vampireForest = registerVampire("vampire_forest", 0, -2, new ItemStack(Blocks.sapling), root);
        domainOfTheDead = registerVampire("domain_of_the_dead", 2, -2, new ItemStack(Blocks.mossy_cobblestone), vampireForest);
        ancientKnowledge = registerVampire("ancient_knowledge", 2, 0, new ItemStack(Items.book), root);
        reopeningOldWounds = registerVampire("reopening_old_wounds", 4, 0, new ItemStack(Items.map), ancientKnowledge);
        regicide = registerVampire("regicide", 6, 0, new ItemStack(Items.skull, 1, 1), reopeningOldWounds).setSpecial();

        // Vampire Path
        becomeVampire = registerVampire("vampire.become_vampire", 0, 2, new ItemStack(Items.rotten_flesh), root);
        firstBlood = registerVampire("vampire.first_blood", -2, 2, new ItemStack(Items.glass_bottle), becomeVampire);
        baptismOfBlood = registerVampire("vampire.baptism_of_blood", 0, 4, new ItemStack(Blocks.redstone_block), becomeVampire);
        vampireBat = registerVampire("vampire.bat", -2, 4, new ItemStack(Items.feather), becomeVampire);
        extraStorage = registerVampire("vampire.extra_storage", -4, 2, new ItemStack(Blocks.chest), firstBlood);
        freezeKill = registerVampire("vampire.freeze_kill", -4, 4, new ItemStack(Blocks.ice), vampireBat);
        yuck = registerVampire("vampire.yuck", 2, 2, new ItemStack(Items.spider_eye), becomeVampire);
        texasMassacre = registerVampire("vampire.texas_massacre", -2, 6, new ItemStack(Items.iron_sword), baptismOfBlood);
        sniped = registerVampire("vampire.sniped", 2, 4, new ItemStack(Items.arrow), becomeVampire);
        resurrect = registerVampire("vampire.resurrect", 2, 6, new ItemStack(Items.golden_apple), baptismOfBlood);

        vampireMaxLevel = registerVampire("vampire.max_level", 0, 6, new ItemStack(Items.nether_star), baptismOfBlood).setSpecial();
        vampireBecomeLord = registerVampire("minion.become_lord", 0, 8, new ItemStack(Items.gold_ingot), vampireMaxLevel);
        vampireMaxLord = registerVampire("vampire.max_lord", 0, 10, new ItemStack(Items.diamond), vampireBecomeLord).setSpecial();

        defeatDracula = registerVampire("vampire.defeat_dracula", 2, 8, new ItemStack(Items.skull), vampireBecomeLord).setSpecial();
        vampireKillMother = registerVampire("vampire.kill_mother", -2, 8, new ItemStack(Items.ghast_tear), vampireBecomeLord).setSpecial();
        plagueInc = registerVampire("vampire.plague_inc", -2, 10, new ItemStack(Items.fermented_spider_eye), vampireMaxLord);
    }

    private static void registerHunterAchievements() {
        // Hunter Root
        becomeHunter = registerHunter("hunter.become_hunter", 0, 0, new ItemStack(Items.wooden_sword), null);
        hunterTraining = registerHunter("hunter.mainline", 0, 2, new ItemStack(Items.iron_helmet), becomeHunter);
        stake = registerHunter("hunter.stake", -2, 2, new ItemStack(Items.stick), becomeHunter);
        brochette = registerHunter("hunter.brochette", -4, 2, new ItemStack(Items.iron_sword), stake);
        cureVampireVillager = registerHunter("hunter.cure_vampire_villager", 2, 2, new ItemStack(Items.golden_apple), becomeHunter);

        hunterCrossbow = registerHunter("hunter.hunter_crossbow", 0, 4, new ItemStack(Items.bow), hunterTraining);
        enhancedCrossbow = registerHunter("hunter.enhanced_crossbow", -2, 4, new ItemStack(Items.bow), hunterCrossbow);
        techCrossbow = registerHunter("hunter.tech_crossbow", -4, 4, new ItemStack(Items.repeater), enhancedCrossbow);
        crazedGunman = registerHunter("hunter.crazed_gunman", -2, 6, new ItemStack(Items.arrow), hunterCrossbow);

        hunterMaxLevel = registerHunter("hunter.max_level", 0, 6, new ItemStack(Items.nether_star), hunterCrossbow).setSpecial();
        hunterBecomeLord = registerHunter("hunter.become_lord", 0, 8, new ItemStack(Items.gold_ingot), hunterMaxLevel);
        hunterMaxLord = registerHunter("hunter.max_lord", 0, 10, new ItemStack(Items.diamond), hunterBecomeLord).setSpecial();

        hunterKillMother = registerHunter("hunter.kill_mother", -2, 8, new ItemStack(Items.ghast_tear), hunterBecomeLord).setSpecial();
        killResurrectedVampire = registerHunter("hunter.kill_resurrected_vampire", 2, 8, new ItemStack(Items.skull), hunterBecomeLord);
        betrayal = registerHunter("hunter.betrayal", 2, 6, new ItemStack(Items.bone), hunterMaxLevel);
        worthIt = registerHunter("hunter.worth_it", 2, 10, new ItemStack(Items.emerald), hunterMaxLord).setSpecial();
    }

    private static Achievement registerVampire(String name, int column, int row, ItemStack icon, Achievement parent) {
        Achievement achievement = new Achievement("achievement." + name, name, column, row, icon, parent);
        achievement.registerStat();
        ALL_ACHIEVEMENTS.add(achievement);
        VAMPIRE_ACHIEVEMENTS.add(achievement);
        return achievement;
    }

    private static Achievement registerHunter(String name, int column, int row, ItemStack icon, Achievement parent) {
        Achievement achievement = new Achievement("achievement." + name, name, column, row, icon, parent);
        achievement.registerStat();
        ALL_ACHIEVEMENTS.add(achievement);
        HUNTER_ACHIEVEMENTS.add(achievement);
        return achievement;
    }

    public static List<Achievement> getAllAchievements() {
        return ALL_ACHIEVEMENTS;
    }
}
