package com.marrybye.neorism.client.input;

import org.lwjgl.input.Keyboard;

import com.marrybye.neorism.client.gui.FactionMenuScreen;
import com.marrybye.neorism.client.gui.GuiMinion;
import com.marrybye.neorism.client.gui.radial.SelectActionRadialScreen;
import com.marrybye.neorism.client.gui.radial.SelectAmmoScreen;
import com.marrybye.neorism.client.gui.skills.SkillsScreen;
import com.marrybye.neorism.common.faction.FactionPlayerHandler;
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;

/**
 * 8 Canonical Keybindings for Neorism (100% TeamLapen parity):
 * 1. Open Action Radial Menu ([R])
 * 2. Open Faction Menu ([P])
 * 3. Open Minion Radial Menu ([M])
 * 4. Open Skill Screen ([K])
 * 5. Select Quarrel Type ([C])
 * 6. Suck Blood ([V])
 * 7. Toggle Vampire Vision ([N])
 * 8. Toggle Wings ([COMMA])
 */
@SideOnly(Side.CLIENT)
public class KeyBindings {

    public static final String CATEGORY = "key.category.neorism.main";

    public static KeyBinding keyAction;
    public static KeyBinding keyFactionMenu;
    public static KeyBinding keyMinion;
    public static KeyBinding keySkills;
    public static KeyBinding keySelectAmmo;
    public static KeyBinding keyBite;
    public static KeyBinding keyVision;
    public static KeyBinding keyWings;

    // Backwards-compatible aliases
    public static KeyBinding keyAction1;
    public static KeyBinding keyAction2;

    private static boolean wasBiteKeyDown = false;
    private static boolean wasActionKeyDown = false;
    private static boolean wasFactionMenuKeyDown = false;
    private static boolean wasMinionKeyDown = false;
    private static boolean wasSkillsKeyDown = false;
    private static boolean wasSelectAmmoKeyDown = false;
    private static boolean wasVisionKeyDown = false;
    private static boolean wasWingsKeyDown = false;

    public static void init() {
        keyAction = new KeyBinding("key.neorism.action", Keyboard.KEY_R, CATEGORY);
        ClientRegistry.registerKeyBinding(keyAction);
        keyAction1 = keyAction;

        keyFactionMenu = new KeyBinding("key.neorism.faction_menu", Keyboard.KEY_P, CATEGORY);
        ClientRegistry.registerKeyBinding(keyFactionMenu);

        keyMinion = new KeyBinding("key.neorism.minion", Keyboard.KEY_M, CATEGORY);
        ClientRegistry.registerKeyBinding(keyMinion);

        keySkills = new KeyBinding("key.neorism.skills", Keyboard.KEY_K, CATEGORY);
        ClientRegistry.registerKeyBinding(keySkills);

        keySelectAmmo = new KeyBinding("key.neorism.select_ammo", Keyboard.KEY_C, CATEGORY);
        ClientRegistry.registerKeyBinding(keySelectAmmo);

        keyBite = new KeyBinding("key.neorism.suck_blood", Keyboard.KEY_V, CATEGORY);
        ClientRegistry.registerKeyBinding(keyBite);

        keyVision = new KeyBinding("key.neorism.toggle_vision", Keyboard.KEY_N, CATEGORY);
        ClientRegistry.registerKeyBinding(keyVision);

        keyWings = new KeyBinding("key.neorism.toggle_wings", Keyboard.KEY_COMMA, CATEGORY);
        ClientRegistry.registerKeyBinding(keyWings);

        // Secondary action shortcut ([Z])
        keyAction2 = new KeyBinding("key.neorism.action2", Keyboard.KEY_Z, CATEGORY);
        ClientRegistry.registerKeyBinding(keyAction2);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            wasBiteKeyDown = false;
            wasActionKeyDown = false;
            wasFactionMenuKeyDown = false;
            wasMinionKeyDown = false;
            wasSkillsKeyDown = false;
            wasSelectAmmoKeyDown = false;
            wasVisionKeyDown = false;
            wasWingsKeyDown = false;
            return;
        }

        // 1. Suck Blood ([V])
        boolean isBiteDown = keyBite.getIsKeyPressed();
        if (isBiteDown && !wasBiteKeyDown) {
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
        } else if (!isBiteDown && wasBiteKeyDown) {
            if (Helper.isVampire(mc.thePlayer)) {
                PacketHandler.sendToServer(new BiteRequestPacket(0, BiteRequestPacket.ACTION_STOP));
            }
        }
        wasBiteKeyDown = isBiteDown;

        // 2. Open Action Radial Menu ([R])
        boolean isActionDown = keyAction.getIsKeyPressed();
        if (isActionDown && !wasActionKeyDown) {
            if (mc.currentScreen == null) {
                SelectActionRadialScreen.show(keyAction);
            }
        }
        wasActionKeyDown = isActionDown;

        // 3. Open Faction Menu ([P])
        boolean isFactionMenuDown = keyFactionMenu.getIsKeyPressed();
        if (isFactionMenuDown && !wasFactionMenuKeyDown) {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new FactionMenuScreen(mc.thePlayer));
            }
        }
        wasFactionMenuKeyDown = isFactionMenuDown;

        // 4. Open Minion Radial / Management Menu ([M])
        boolean isMinionDown = keyMinion.getIsKeyPressed();
        if (isMinionDown && !wasMinionKeyDown) {
            if (mc.currentScreen == null) {
                MovingObjectPosition mop = mc.objectMouseOver;
                if (mop != null && mop.entityHit instanceof com.marrybye.neorism.common.entity.minion.EntityMinionBase) {
                    mc.displayGuiScreen(new GuiMinion(mc.thePlayer.inventory, (com.marrybye.neorism.common.entity.minion.EntityMinionBase) mop.entityHit));
                } else {
                    mc.thePlayer.addChatMessage(new ChatComponentTranslation("gui.neorism.minion_radial.no_minion_tasks"));
                }
            }
        }
        wasMinionKeyDown = isMinionDown;

        // 5. Open Skill Screen ([K])
        boolean isSkillsDown = keySkills.getIsKeyPressed();
        if (isSkillsDown && !wasSkillsKeyDown) {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new SkillsScreen(mc.thePlayer));
            }
        }
        wasSkillsKeyDown = isSkillsDown;

        // 6. Select Quarrel Type ([C])
        boolean isSelectAmmoDown = keySelectAmmo.getIsKeyPressed();
        if (isSelectAmmoDown && !wasSelectAmmoKeyDown) {
            if (mc.currentScreen == null) {
                SelectAmmoScreen.show(keySelectAmmo);
            }
        }
        wasSelectAmmoKeyDown = isSelectAmmoDown;

        // 7. Toggle Vampire Vision ([N])
        boolean isVisionDown = keyVision.getIsKeyPressed();
        if (isVisionDown && !wasVisionKeyDown) {
            if (Helper.isVampire(mc.thePlayer)) {
                if (mc.thePlayer.isPotionActive(Potion.nightVision.id)) {
                    mc.thePlayer.removePotionEffect(Potion.nightVision.id);
                } else {
                    mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 999999, 0, true));
                }
            }
        }
        wasVisionKeyDown = isVisionDown;

        // 8. Toggle Wings ([COMMA])
        boolean isWingsDown = keyWings.getIsKeyPressed();
        if (isWingsDown && !wasWingsKeyDown) {
            if (Helper.isVampire(mc.thePlayer)) {
                FactionPlayerHandler fph = FactionPlayerHandler.get(mc.thePlayer);
                if (fph != null && (fph.getCurrentLevel() >= 14 || fph.getLordLevel() > 0 || mc.thePlayer.capabilities.isCreativeMode)) {
                    mc.thePlayer.capabilities.allowFlying = !mc.thePlayer.capabilities.allowFlying;
                    if (!mc.thePlayer.capabilities.allowFlying) {
                        mc.thePlayer.capabilities.isFlying = false;
                    }
                    mc.thePlayer.sendPlayerAbilities();
                }
            }
        }
        wasWingsKeyDown = isWingsDown;

        // Secondary Action ([Z])
        if (keyAction2 != null && keyAction2.isPressed()) {
            PacketHandler.sendToServer(new com.marrybye.neorism.common.network.packets.ActionKeyPacket(1));
        }
    }
}
