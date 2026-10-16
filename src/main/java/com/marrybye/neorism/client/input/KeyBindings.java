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
    private static boolean wasBiteKeyDown = false;

    public static void init() {
        keyBite = new KeyBinding("key.neorism.suck_blood", Keyboard.KEY_V, "key.category.neorism.main");
        ClientRegistry.registerKeyBinding(keyBite);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            wasBiteKeyDown = false;
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
    }
}
