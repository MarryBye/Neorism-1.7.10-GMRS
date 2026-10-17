package com.marrybye.neorism.common.action;

import com.marrybye.neorism.api.action.IAction;
import com.marrybye.neorism.common.core.ModPotions;
import com.marrybye.neorism.common.skills.SkillHandler;
import com.marrybye.neorism.common.util.Helper;
import com.marrybye.neorism.common.vampire.VampirePlayer;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Base implementation of IAction with common validation and timing logic.
 */
public abstract class ActionBase implements IAction {

    private final String id;
    private final String unlocalizedName;
    private final int cooldown;
    private final int duration;
    private final boolean toggle;
    private final int bloodCost;

    public ActionBase(String id, int cooldown, int duration, boolean toggle, int bloodCost) {
        this.id = id;
        this.unlocalizedName = "action.neorism." + id;
        this.cooldown = cooldown;
        this.duration = duration;
        this.toggle = toggle;
        this.bloodCost = bloodCost;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    @Override
    public int getCooldown(EntityPlayer player) {
        return cooldown;
    }

    @Override
    public int getDuration(EntityPlayer player) {
        return duration;
    }

    @Override
    public boolean isToggle() {
        return toggle;
    }

    @Override
    public int getBloodCost(EntityPlayer player) {
        return bloodCost;
    }

    @Override
    public boolean canUse(EntityPlayer player) {
        if (player == null) return false;
        if (!isUnlocked(player)) return false;

        ActionHandler handler = ActionHandler.get(player);
        if (handler != null && handler.isOnCooldown(this)) return false;

        // Garlic debuff prevents using vampire skills
        if (player.isPotionActive(ModPotions.garlic)) return false;

        if (bloodCost > 0) {
            if (!Helper.isVampire(player)) {
                return false;
            }
            VampirePlayer vp = VampirePlayer.get(player);
            if (vp == null || vp.getBloodStats().getBloodLevel() < bloodCost) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isUnlocked(EntityPlayer player) {
        SkillHandler sh = SkillHandler.get(player);
        return sh != null && sh.isActionUnlocked(this.id);
    }

    @Override
    public void onDeactivated(EntityPlayer player) {
    }

    @Override
    public net.minecraft.util.ResourceLocation getIcon() {
        return new net.minecraft.util.ResourceLocation("neorism", "textures/actions/" + id + ".png");
    }

    @Override
    public void onUpdate(EntityPlayer player, int activeTicksLeft) {
    }
}
