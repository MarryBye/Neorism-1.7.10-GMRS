package com.marrybye.neorism.common.hunter;

import java.util.UUID;

import com.marrybye.neorism.api.entity.IHunterEntity;
import com.marrybye.neorism.api.faction.IFaction;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.util.ModLogger;
import com.marrybye.neorism.common.vampire.LevelAttributeModifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Extended entity property managing Hunter progression, training state, attribute buffs, and drain resistance.
 */
public class HunterPlayer implements IExtendedEntityProperties, IHunterEntity {

    public static final String PROPERTY_ID = "neorism:hunter_player";

    public static final UUID HUNTER_SPEED_UUID = UUID.fromString("6a35d980-9ec8-49f9-b883-2f08a54101e5");
    public static final UUID HUNTER_DAMAGE_UUID = UUID.fromString("6a35d980-9ec8-49f9-b883-2f08a54101e6");

    private final EntityPlayer player;

    public HunterPlayer(EntityPlayer player) {
        this.player = player;
    }

    public static HunterPlayer get(EntityPlayer player) {
        if (player == null) return null;
        return (HunterPlayer) player.getExtendedProperties(PROPERTY_ID);
    }

    public static void register(EntityPlayer player) {
        if (player != null && player.getExtendedProperties(PROPERTY_ID) == null) {
            player.registerExtendedProperties(PROPERTY_ID, new HunterPlayer(player));
        }
    }

    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        compound.setTag("NeorismHunterPlayer", tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("NeorismHunterPlayer")) {
            // Placeholder for future persistent hunter-specific cooldowns or skills
        }
    }

    public void copyFrom(HunterPlayer old) {
        // Copy persistent hunter data if needed across respawn
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public IFaction getFaction() {
        return ModFactions.HUNTER;
    }

    @Override
    public EntityLivingBase asEntity() {
        return player;
    }

    public boolean isHunter() {
        return Helper.isHunter(player);
    }

    public int getLevel() {
        return Helper.getLevel(player);
    }

    /**
     * Fraction of blood drain resistance when bitten by a vampire (0.0 = no resistance, 0.70 = 70% reduction).
     */
    public float getDrainResistance() {
        if (!isHunter()) return 0.0F;
        int level = getLevel();
        // 5% per level up to 70% at level 14
        return Math.min(0.70F, level * 0.05F);
    }

    /**
     * Called whenever the player's hunter level changes.
     */
    public void onLevelChanged(int newLevel) {
        applyHunterModifiers(player, newLevel, HunterLeveling.MAX_LEVEL);
        ModLogger.leveling("Applied hunter level %d attribute modifiers to %s (drainResistance: %.0f%%)",
            newLevel, player.getCommandSenderName(), getDrainResistance() * 100.0F);
    }

    /**
     * Applies level-scaled attribute modifiers for Hunters:
     * - Movement Speed: up to +20% with square root curve
     * - Attack Damage: +1.0 at lvl 7+, +2.0 at lvl 14
     */
    public static void applyHunterModifiers(EntityPlayer player, int level, int maxLevel) {
        if (player == null) return;

        // Movement Speed modifier (+20% max)
        LevelAttributeModifier.applyModifier(player, SharedMonsterAttributes.movementSpeed,
            HUNTER_SPEED_UUID, "Hunter Level Speed",
            level, maxLevel, 0.20D, 0.5D, 2, false);

        // Attack Damage modifier
        IAttributeInstance dmgInstance = player.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        if (dmgInstance != null) {
            AttributeModifier existing = dmgInstance.getModifier(HUNTER_DAMAGE_UUID);
            if (existing != null) {
                dmgInstance.removeModifier(existing);
            }
            int bonusDamage = level >= 14 ? 2 : (level >= 7 ? 1 : 0);
            if (bonusDamage > 0) {
                dmgInstance.applyModifier(new AttributeModifier(HUNTER_DAMAGE_UUID, "Hunter Level Damage", bonusDamage, 0));
            }
        }
    }

    /**
     * Cleans up all hunter attribute modifiers when leaving the faction.
     */
    public static void removeHunterModifiers(EntityPlayer player) {
        if (player == null) return;
        IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (speed != null) {
            AttributeModifier mod = speed.getModifier(HUNTER_SPEED_UUID);
            if (mod != null) speed.removeModifier(mod);
        }
        IAttributeInstance dmg = player.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        if (dmg != null) {
            AttributeModifier mod = dmg.getModifier(HUNTER_DAMAGE_UUID);
            if (mod != null) dmg.removeModifier(mod);
        }
    }

    /**
     * Ticked every tick from LivingUpdateEvent for hunters.
     */
    public void onUpdate() {
        if (!isHunter()) return;
        // Periodic hunter checks if needed
    }
}
