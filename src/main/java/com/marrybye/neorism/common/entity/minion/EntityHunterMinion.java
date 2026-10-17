package com.marrybye.neorism.common.entity.minion;

import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import de.teamlapen.faction.api.factions.IFaction;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

/**
 * Hunter minion entity serving Hunter Lords.
 */
public class EntityHunterMinion extends EntityMinionBase {

    public EntityHunterMinion(World world) {
        super(world);
    }

    @Override
    public IFaction getFaction() {
        return ModFactions.HUNTER;
    }

    @Override
    protected Item getDropItem() {
        return Items.arrow;
    }
}
