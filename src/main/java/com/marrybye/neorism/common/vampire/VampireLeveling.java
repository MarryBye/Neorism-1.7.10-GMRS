package com.marrybye.neorism.common.vampire;

/**
 * Requirements and calculations for Vampire leveling progression (levels 1-14).
 * Ported 1:1 from modern Vampirism VampireLeveling.java.
 */
public class VampireLeveling {

    public static final int MAX_LEVEL = 14;

    private static final AltarInspirationRequirement LEVEL_2 = new AltarInspirationRequirement(2, 40);
    private static final AltarInspirationRequirement LEVEL_3 = new AltarInspirationRequirement(3, 70);
    private static final AltarInspirationRequirement LEVEL_4 = new AltarInspirationRequirement(4, 100);

    private static final AltarInfusionRequirement LEVEL_5 = new AltarInfusionRequirement(5, 0, 0, 5, 1);
    private static final AltarInfusionRequirement LEVEL_6 = new AltarInfusionRequirement(6, 0, 1, 5, 1);
    private static final AltarInfusionRequirement LEVEL_7 = new AltarInfusionRequirement(7, 0, 1, 10, 1);
    private static final AltarInfusionRequirement LEVEL_8 = new AltarInfusionRequirement(8, 1, 1, 10, 1);
    private static final AltarInfusionRequirement LEVEL_9 = new AltarInfusionRequirement(9, 1, 1, 10, 1);
    private static final AltarInfusionRequirement LEVEL_10 = new AltarInfusionRequirement(10, 2, 1, 15, 1);
    private static final AltarInfusionRequirement LEVEL_11 = new AltarInfusionRequirement(11, 2, 1, 15, 1);
    private static final AltarInfusionRequirement LEVEL_12 = new AltarInfusionRequirement(12, 3, 1, 20, 1);
    private static final AltarInfusionRequirement LEVEL_13 = new AltarInfusionRequirement(13, 3, 2, 20, 1);
    private static final AltarInfusionRequirement LEVEL_14 = new AltarInfusionRequirement(14, 4, 2, 25, 1);

    private static final Object[] LEVEL_REQUIREMENTS = {
        null, null, LEVEL_2, LEVEL_3, LEVEL_4,
        LEVEL_5, LEVEL_6, LEVEL_7, LEVEL_8, LEVEL_9,
        LEVEL_10, LEVEL_11, LEVEL_12, LEVEL_13, LEVEL_14
    };

    private static final int[] PASSIVE_LEVELING_BLOOD = {0, 0, 100, 175};

    public static boolean canLevelPassively(int currentLevel) {
        return currentLevel > 0 && currentLevel < 3;
    }

    public static int getPassiveLevelingBlood(int targetLevel) {
        if (targetLevel < 0 || targetLevel >= PASSIVE_LEVELING_BLOOD.length) return 0;
        return PASSIVE_LEVELING_BLOOD[targetLevel];
    }

    public static AltarInspirationRequirement getInspirationRequirement(int targetLevel) {
        if (targetLevel >= 2 && targetLevel <= 4) {
            return (AltarInspirationRequirement) LEVEL_REQUIREMENTS[targetLevel];
        }
        return null;
    }

    public static AltarInfusionRequirement getInfusionRequirement(int targetLevel) {
        if (targetLevel >= 5 && targetLevel <= 14) {
            return (AltarInfusionRequirement) LEVEL_REQUIREMENTS[targetLevel];
        }
        return null;
    }

    /**
     * Requirement for leveling up at the Altar of Inspiration (levels 2-4).
     */
    public static class AltarInspirationRequirement {
        private final int targetLevel;
        private final int bloodAmount;

        public AltarInspirationRequirement(int targetLevel, int bloodAmount) {
            this.targetLevel = targetLevel;
            this.bloodAmount = bloodAmount;
        }

        public int getTargetLevel() {
            return targetLevel;
        }

        public int getBloodAmount() {
            return bloodAmount;
        }
    }

    /**
     * Requirement for leveling up at the Altar of Infusion (levels 5-14).
     */
    public static class AltarInfusionRequirement {
        private final int targetLevel;
        private final int pureBloodLevel;
        private final int pureBloodQuantity;
        private final int humanHeartQuantity;
        private final int vampireBookQuantity;

        public AltarInfusionRequirement(int targetLevel, int pureBloodLevel, int pureBloodQuantity, int humanHeartQuantity, int vampireBookQuantity) {
            this.targetLevel = targetLevel;
            this.pureBloodLevel = pureBloodLevel;
            this.pureBloodQuantity = pureBloodQuantity;
            this.humanHeartQuantity = humanHeartQuantity;
            this.vampireBookQuantity = vampireBookQuantity;
        }

        public int getTargetLevel() {
            return targetLevel;
        }

        public int getPureBloodLevel() {
            return pureBloodLevel;
        }

        public int getPureBloodQuantity() {
            return pureBloodQuantity;
        }

        public int getHumanHeartQuantity() {
            return humanHeartQuantity;
        }

        public int getVampireBookQuantity() {
            return vampireBookQuantity;
        }

        /**
         * Calculates required multiblock structure points for the ritual.
         * Exact formula: 8 + (54 - 8) * ((targetLevel - 4) / 2) / 5
         */
        public int getRequiredStructurePoints() {
            int t = (this.targetLevel - 4) / 2;
            return (int) (8 + (54 - 8) * t / 5.0F);
        }
    }
}
