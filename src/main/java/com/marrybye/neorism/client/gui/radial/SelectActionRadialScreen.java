package com.marrybye.neorism.client.gui.radial;

import java.util.ArrayList;
import java.util.List;

import com.marrybye.neorism.api.action.IAction;
import com.marrybye.neorism.common.action.ActionHandler;
import com.marrybye.neorism.common.action.ActionRegistry;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.ToggleActionPacket;
import com.marrybye.neorism.common.skills.SkillHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Dedicated radial screen for selecting and activating faction actions (Vampire / Hunter abilities).
 */
@SideOnly(Side.CLIENT)
public class SelectActionRadialScreen extends GuiRadialMenu {

    private final EntityPlayer player;

    public SelectActionRadialScreen(List<IAction> actions, KeyBinding triggerKey) {
        super(createRadialMenu(actions), triggerKey);
        this.player = Minecraft.getMinecraft().thePlayer;
    }

    public static void show(KeyBinding triggerKey) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        SkillHandler sh = SkillHandler.get(mc.thePlayer);
        if (sh == null) return;

        List<IAction> available = new ArrayList<IAction>();
        for (IAction action : ActionRegistry.getAllActions()) {
            if (sh.isActionUnlocked(action.getId()) || action.isUnlocked(mc.thePlayer)) {
                available.add(action);
            }
        }

        if (available.isEmpty()) {
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("gui.factionapi.action_radial.no_actions"));
            return;
        }

        mc.displayGuiScreen(new SelectActionRadialScreen(available, triggerKey));
    }

    private static RadialMenu createRadialMenu(final List<IAction> actions) {
        List<IRadialMenuSlot> slots = new ArrayList<IRadialMenuSlot>();
        for (IAction action : actions) {
            String name = StatCollector.translateToLocal(action.getUnlocalizedName());
            ResourceLocation icon = action.getIcon();
            if (icon == null) {
                icon = new ResourceLocation("neorism", "textures/actions/" + action.getId() + ".png");
            }
            slots.add(new RadialMenuSlot(name, action, icon));
        }

        return new RadialMenu(new RadialMenu.SlotConsumer() {
            @Override
            public void accept(int slotIndex) {
                if (slotIndex >= 0 && slotIndex < actions.size()) {
                    IAction action = actions.get(slotIndex);
                    PacketHandler.sendToServer(new ToggleActionPacket(action.getId()));
                }
            }
        }, slots);
    }

    @Override
    protected void drawSlotDecorations(int centerX, int centerY, float radiusIn, float radiusOut, int numberOfSlices) {
        ActionHandler ah = ActionHandler.get(player);
        if (ah == null) return;

        for (int i = 0; i < numberOfSlices; i++) {
            IRadialMenuSlot slot = slots.get(i);
            IAction action = slot.getAction();
            if (action == null) continue;

            float sliceLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360.0f;
            float sliceRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360.0f;

            if (ah.isActive(action)) {
                // Active action highlight (golden/green active aura)
                drawSlice(centerX, centerY, 0, radiusIn, radiusOut, sliceLeft, sliceRight, 80, 220, 100, 90);
            } else if (ah.isOnCooldown(action)) {
                int cdRemain = ah.getCooldownRemaining(action);
                int cdTotal = action.getCooldown(player);
                float fraction = cdTotal > 0 ? (float) cdRemain / cdTotal : 0.5f;
                float cooldownRadius = radiusIn + (radiusOut - radiusIn) * fraction;
                // Red/dark cooldown shade
                drawSlice(centerX, centerY, 0, radiusIn, cooldownRadius, sliceLeft, sliceRight, 200, 50, 50, 110);
            }
        }
    }
}
