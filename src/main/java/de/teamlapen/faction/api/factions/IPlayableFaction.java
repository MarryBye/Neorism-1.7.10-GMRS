package de.teamlapen.faction.api.factions;

/**
 * Playable faction that can be joined by players (e.g. Vampires, Hunters).
 */
public interface IPlayableFaction extends IFaction {

    int getMinLevel();

    boolean canPlayerJoin(net.minecraft.entity.player.EntityPlayer player);

    boolean canPlayerLeave(net.minecraft.entity.player.EntityPlayer player);

    enum TitleGender {
        UNKNOWN("unknown"),
        MALE("male"),
        FEMALE("female");

        private final String name;

        TitleGender(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static TitleGender byName(String name) {
            for (TitleGender g : values()) {
                if (g.name.equalsIgnoreCase(name)) {
                    return g;
                }
            }
            return UNKNOWN;
        }
    }
}
