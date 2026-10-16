package com.marrybye.neorism.client.input;

import com.marrybye.neorism.client.gui.FactionMenuScreen;
import com.marrybye.neorism.client.gui.radial.SelectActionRadialScreen;
import com.marrybye.neorism.client.gui.skills.SkillsScreen;
import com.marrybye.neorism.common.network.PacketHandler;
import com.marrybye.neorism.common.network.packets.BiteRequestPacket;
import com.marrybye.neorism.common.util.Helper;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

/**
 * Keybindings handler for Neorism actions (Biting, Radial Action Menu, Skills, Faction Dashboard).
 */
@SideOnly(Side.CLIENT)
public class KeyBindings {

    public static KeyBinding keyBite;
    public static KeyBinding keyAction1;
    public static KeyBinding keyAction2;
    public static KeyBinding keySkills;
    public static KeyBinding keyFactionMenu;

    private static boolean wasBiteKeyDown = false;
    private static boolean wasAction1KeyDown = false;
    private static boolean wasAction2KeyDown = false;
    private static boolean wasSkillsKeyDown = false;
    private static boolean wasFactionMenuKeyDown = false;

    public static void init() {
        keyBite = new KeyBinding("key.neorism.suck_blood", Keyboard.KEY_V, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keyBite);

        keyAction1 = new KeyBinding("key.neorism.action1", Keyboard.KEY_R, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keyAction1);

        keyAction2 = new KeyBinding("key.neorism.action2", Keyboard.KEY_Z, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keyAction2);

        keySkills = new KeyBinding("key.neorism.skills", Keyboard.KEY_K, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keySkills);

        keyFactionMenu = new KeyBinding("key.neorism.faction_menu", Keyboard.KEY_P, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keyFactionMenu);
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
            wasFactionMenuKeyDown = false;
            return;
        }

        // Bite Key
        boolean isKeyDown = keyBite.getIsKeyPressed();
        if (isKeyDown && !wasBiteKeyDown) {
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
            if (Helper.isVampire(mc.thePlayer)) {
                PacketHandler.sendToServer(new BiteRequestPacket(
                    0,
                    BiteRequestPacket.ACTION_STOP
                ));
            }
        }
        wasBiteKeyDown = isKeyDown;

        // Action 1 Key (Hold [R] opens Radial Action Rose)
        boolean isAction1Down = keyAction1.getIsKeyPressed();
        if (isAction1Down && !wasAction1KeyDown) {
            if (mc.currentScreen == null) {
                SelectActionRadialScreen.show(keyAction1);
            }
        }
        wasAction1KeyDown = isAction1Down;

        // Action 2 Key (Secondary Action trigger / hotkey)
        boolean isAction2Down = keyAction2.getIsKeyPressed();
        if (isAction2Down && !wasAction2KeyDown) {
            PacketHandler.sendToServer(new com.marrybye.neorism.common.network.packets.ActionKeyPacket(1));
        }
        wasAction2KeyDown = isAction2Down;

        // Skill Tree Key ([K])
        boolean isSkillsDown = keySkills.getIsKeyPressed();
        if (isSkillsDown && !wasSkillsKeyDown) {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new SkillsScreen(mc.thePlayer));
            }
        }
        wasSkillsKeyDown = isSkillsDown;

        // Faction Menu Key ([P])
        boolean isFactionMenuDown = keyFactionMenu.getIsKeyPressed();
        if (isFactionMenuDown && !wasFactionMenuKeyDown) {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new FactionMenuScreen(mc.thePlayer));
            }
        }
        wasFactionMenuKeyDown = isFactionMenuDown;
    }
}
