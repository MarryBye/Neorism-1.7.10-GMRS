package com.marrybye.neorism.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Sound definitions and helper dispatchers for Neorism.
 */
public class ModSounds {

    public static final String VAMPIRE_SCREAM = "neorism:entity.vampire_scream";
    public static final String SCREECH = VAMPIRE_SCREAM;
    public static final String VAMPIRE_BITE = "neorism:entity.vampire_bite";
    public static final String BITE = VAMPIRE_BITE;
    public static final String VAMPIRE_FEEDING = "neorism:entity.vampire_feeding";
    public static final String BLOOD_DRIP = "neorism:ambient.blood_drip";
    public static final String COFFIN_LID = "neorism:block.coffin_lid";
    public static final String BAT_SWARM = "neorism:entity.bat_swarm";
    public static final String BOILING = "neorism:block.boiling";
    public static final String BLOOD_SQUEEZE = "neorism:block.blood_squeeze";
    public static final String BLESSING_MUSIC = "neorism:block.blessing_music";
    public static final String BLOOD_PROJECTILE_HIT = "neorism:fx.blood_projectile_hit";
    public static final String SPHERE_SPINNING = "neorism:block.sphere_spinning";
    public static final String BEAM_ENTER_PLAYER = "neorism:block.beam_enter_player";
    public static final String BEAM_ENTER = BEAM_ENTER_PLAYER;
    public static final String WEAPON_TABLE_CRAFTING = "neorism:block.weapon_table_crafting";
    public static final String STAKE = "neorism:fx.stake";
    public static final String TELEPORT_AWAY = "neorism:fx.teleport_away";
    public static final String TELEPORT_HERE = "neorism:fx.teleport_here";
    public static final String FREEZE = "neorism:fx.freeze";
    public static final String VAPOR_STILL_CRAFTING = "neorism:block.vapor_still_crafting";
    public static final String MOTHER_DEATH = "neorism:fx.mother_death";
    public static final String MOTHER_AMBIENT = "neorism:ambient.mother";
    public static final String VAMPIRE_FOREST_AMBIENT = "neorism:ambient.forest";
    public static final String DRACULA_AMBIENT = "neorism:entity.dracula.ambient";
    public static final String DRACULA_HURT = "neorism:entity.dracula.hurt";
    public static final String DRACULA_DEATH = "neorism:entity.dracula.death";
    public static final String DRACULA_TELEPORT = "neorism:entity.dracula.teleport";
    public static final String DRACULA_TRANSFORM = "neorism:entity.dracula.transform";
    public static final String RAGE = "neorism:fx.rage";
    public static final String HEARTBEAT = "neorism:fx.heartbeat";
    public static final String SIZZLE = "random.fizz";
    public static final String FIZZ = "random.fizz";
    public static final String LEVEL_UP = "random.levelup";

    public static void playAtEntity(Entity entity, String soundName, float volume, float pitch) {
        if (entity != null && entity.worldObj != null) {
            entity.worldObj.playSoundAtEntity(entity, soundName, volume, pitch);
        }
    }

    public static void playSound(World world, double x, double y, double z, String soundName, float volume, float pitch) {
        if (world != null) {
            world.playSoundEffect(x, y, z, soundName, volume, pitch);
        }
    }

    public static void playAt(World world, double x, double y, double z, String soundName, float volume, float pitch) {
        playSound(world, x, y, z, soundName, volume, pitch);
    }
}
