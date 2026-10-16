package com.marrybye.neorism.client.input;

import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.BiteRequestPacket;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

/**
 * Keybindings handler for Neorism actions (Biting, Skills).
 */
@SideOnly(Side.CLIENT)
public class KeyBindings {

    public static KeyBinding keyBite;
    public static KeyBinding keyAction1;
    public static KeyBinding keyAction2;
    public static KeyBinding keySkills;
    private static boolean wasBiteKeyDown = false;
    private static boolean wasAction1KeyDown = false;
    private static boolean wasAction2KeyDown = false;
    private static boolean wasSkillsKeyDown = false;

    public static void init() {
        keyBite = new KeyBinding("key.neorism.suck_blood", Keyboard.KEY_V, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keyBite);

        keyAction1 = new KeyBinding("key.neorism.action1", Keyboard.KEY_R, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keyAction1);

        keyAction2 = new KeyBinding("key.neorism.action2", Keyboard.KEY_Z, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keyAction2);

        keySkills = new KeyBinding("key.neorism.skills", Keyboard.KEY_K, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keySkills);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            wasBiteKeyDown = false;
            wasAction1KeyDown = false;
            wasAction2KeyDown = false;
            wasSkillsKeyDown = false;
            return;
        }

        boolean isKeyDown = keyBite.getIsKeyPressed();

        if (isKeyDown && !wasBiteKeyDown) {
            // Key just pressed
            if (Helper.isVampire(mc.thePlayer)) {
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    if (mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
                        PacketHandler.sendToServer(new BiteRequestPacket(
                            mc.objectMouseOver.entityHit.getEntityId(),
                            BiteRequestPacket.ACTION_START
                        ));
                    }
                }
            }
        } else if (!isKeyDown && wasBiteKeyDown) {
            // Key just released
            if (Helper.isVampire(mc.thePlayer)) {
                PacketHandler.sendToServer(new BiteRequestPacket(
                    0,
                    BiteRequestPacket.ACTION_STOP
                ));
            }
        }

        wasBiteKeyDown = isKeyDown;

        // Action 1 Key (Primary Action)
        boolean isAction1Down = keyAction1.getIsKeyPressed();
        if (isAction1Down && !wasAction1KeyDown) {
            PacketHandler.sendToServer(new com.marrybye.neorism.common.network.packets.ActionKeyPacket(0));
        }
        wasAction1KeyDown = isAction1Down;

        // Action 2 Key (Secondary Action)
        boolean isAction2Down = keyAction2.getIsKeyPressed();
        if (isAction2Down && !wasAction2KeyDown) {
            PacketHandler.sendToServer(new com.marrybye.neorism.common.network.packets.ActionKeyPacket(1));
        }
        wasAction2KeyDown = isAction2Down;

        // Skill Tree Key
        boolean isSkillsDown = keySkills.getIsKeyPressed();
        if (isSkillsDown && !wasSkillsKeyDown) {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new com.marrybye.neorism.client.gui.GuiSkillTree());
            }
        }
        wasSkillsKeyDown = isSkillsDown;
    }
}
