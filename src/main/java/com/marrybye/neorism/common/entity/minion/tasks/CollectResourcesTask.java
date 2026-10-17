package com.marrybye.neorism.common.entity.minion.tasks;

import com.marrybye.neorism.common.core.ModBlocks;
import com.marrybye.neorism.common.core.ModFactions;
import com.marrybye.neorism.common.core.ModItems;
import com.marrybye.neorism.common.entity.minion.MinionData;
import com.marrybye.neorism.common.items.ItemBloodBottle;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CollectResourcesTask implements IMinionTask {

    private final String id;
    private final String translationKey;
    private final IFaction faction;
    private final int baseCooldown;
    private final List<WeightedDrop> drops = new ArrayList<WeightedDrop>();
    private final Random rng = new Random();

    public CollectResourcesTask(String id, String translationKey, IFaction faction, int baseCooldown) {
        this.id = id;
        this.translationKey = translationKey;
        this.faction = faction;
        this.baseCooldown = baseCooldown;
    }

    public static CollectResourcesTask createCollectBlood() {
        CollectResourcesTask task = new CollectResourcesTask(
            "collect_blood", "minion_task.vampirism.collect_blood", ModFactions.VAMPIRE, 1200
        );
        ItemStack bloodBottle = new ItemStack(ModItems.bloodBottle);
        bloodBottle.setItemDamage(ItemBloodBottle.MAX_BLOOD);
        task.addDrop(bloodBottle, 20);
        task.addDrop(new ItemStack(ModItems.humanHeart), 5);
        task.addDrop(new ItemStack(Items.iron_ingot), 12);
        task.addDrop(new ItemStack(Items.gold_nugget, 3), 10);
        return task;
    }

    public static CollectResourcesTask createCollectHunterItems() {
        CollectResourcesTask task = new CollectResourcesTask(
            "collect_hunter_items", "minion_task.vampirism.collect_hunter_items", ModFactions.HUNTER, 1200
        );
        task.addDrop(new ItemStack(ModItems.garlicBread), 10);
        task.addDrop(new ItemStack(Items.iron_ingot), 20);
        task.addDrop(new ItemStack(Items.gold_nugget, 4), 10);
        task.addDrop(new ItemStack(ModItems.garlic, 2), 15);
        task.addDrop(new ItemStack(Items.coal, 4), 20);
        return task;
    }

    public void addDrop(ItemStack stack, int weight) {
        drops.add(new WeightedDrop(stack, weight));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public IChatComponent getName() {
        return new ChatComponentTranslation(translationKey);
    }

    @Override
    public boolean isAvailable(ILordPlayer lord) {
        return true;
    }

    @Override
    public IMinionTaskDesc activateTask(EntityPlayer lord, IMinionEntity minion, Object minionData) {
        if (minion != null) {
            minion.recallMinion();
        }
        if (lord != null) {
            lord.addChatMessage(new ChatComponentTranslation("minion_task.neorism.collect.start"));
        }
        return new Desc(baseCooldown);
    }

    @Override
    public void deactivateTask(IMinionTaskDesc desc) {
    }

    @Override
    public void tickActive(IMinionTaskDesc desc, IMinionEntity minion, Object minionData) {
        tickBackground(desc, minionData);
    }

    @Override
    public void tickBackground(IMinionTaskDesc desc, Object minionData) {
        if (desc instanceof Desc && minionData instanceof MinionData) {
            Desc d = (Desc) desc;
            MinionData data = (MinionData) minionData;
            d.cooldown--;
            if (d.cooldown <= 0) {
                d.cooldown = baseCooldown;
                ItemStack reward = getRandomDrop();
                if (reward != null) {
                    data.getInventory().addItemStack(reward.copy());
                }
            }
        }
    }

    private ItemStack getRandomDrop() {
        int totalWeight = 0;
        for (WeightedDrop drop : drops) {
            totalWeight += drop.weight;
        }
        if (totalWeight <= 0) return null;
        int roll = rng.nextInt(totalWeight);
        int current = 0;
        for (WeightedDrop drop : drops) {
            current += drop.weight;
            if (roll < current) {
                return drop.stack;
            }
        }
        return null;
    }

    public class Desc implements IMinionTaskDesc {

        public int cooldown;

        public Desc() {
            this(baseCooldown);
        }

        public Desc(int cooldown) {
            this.cooldown = cooldown;
        }

        @Override
        public IMinionTask getTask() {
            return CollectResourcesTask.this;
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setString("Task", id);
            tag.setInteger("Cooldown", cooldown);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            this.cooldown = tag.getInteger("Cooldown");
            if (this.cooldown <= 0) this.cooldown = baseCooldown;
        }
    }

    private static class WeightedDrop {
        final ItemStack stack;
        final int weight;

        WeightedDrop(ItemStack stack, int weight) {
            this.stack = stack;
            this.weight = weight;
        }
    }
}
