package com.marrybye.neorism.client.gui.radial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.items.ItemCrossbow;
import com.marrybye.neorism.common.items.ItemQuarrel;
import com.marrybye.neorism.common.items.ItemQuarrelPouch;
import com.marrybye.neorism.common.core.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

/**
 * Radial screen for selecting quarrel ammo type for equipped crossbow ([C] key).
 * 1:1 port of TeamLapen's SelectAmmoScreen.
 */
@SideOnly(Side.CLIENT)
public class SelectAmmoScreen extends GuiRadialMenu {

    private static final RenderItem itemRender = new RenderItem();
    private final List<AmmoEntry> ammoEntries;

    public static class AmmoEntry implements IRadialMenuSlot {
        public final int quarrelType; // -1 for no restriction / default
        public final ItemStack displayStack;
        public final int count;
        public final String name;

        public AmmoEntry(int quarrelType, ItemStack displayStack, int count, String name) {
            this.quarrelType = quarrelType;
            this.displayStack = displayStack;
            this.count = count;
            this.name = name;
        }

        @Override
        public String getSlotName() {
            return count > 0 ? name + " (" + count + ")" : name;
        }

        @Override
        public com.marrybye.neorism.api.action.IAction getAction() {
            return null;
        }

        @Override
        public ResourceLocation getIcon() {
            return null;
        }
    }

    public SelectAmmoScreen(List<AmmoEntry> entries, KeyBinding triggerKey) {
        super(createRadialMenu(entries), triggerKey);
        this.ammoEntries = entries;
    }

    private static RadialMenu createRadialMenu(final List<AmmoEntry> entries) {
        List<IRadialMenuSlot> slots = new ArrayList<IRadialMenuSlot>();
        for (AmmoEntry e : entries) {
            slots.add(e);
        }
        return new RadialMenu(slots);
    }

    public static void show(KeyBinding triggerKey) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        ItemStack held = player.getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemCrossbow)) {
            return;
        }

        // Count all available quarrels in player inventory
        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack == null) continue;

            if (stack.getItem() instanceof ItemQuarrel) {
                int type = stack.getItemDamage();
                counts.put(type, counts.getOrDefault(type, 0) + stack.stackSize);
            } else if (stack.getItem() instanceof ItemQuarrelPouch) {
                int[] pCounts = ItemQuarrelPouch.getQuarrelCounts(stack);
                for (int t = 0; t < pCounts.length; t++) {
                    if (pCounts[t] > 0) {
                        counts.put(t, counts.getOrDefault(t, 0) + pCounts[t]);
                    }
                }
            }
        }

        List<AmmoEntry> entries = new ArrayList<AmmoEntry>();
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            int type = entry.getKey();
            int count = entry.getValue();
            ItemStack ammoStack = new ItemStack(ModItems.quarrel, 1, type);
            String name = ammoStack.getDisplayName();
            entries.add(new AmmoEntry(type, ammoStack, count, name));
        }

        // Always add "No restriction / Any" option
        ItemStack anyStack = new ItemStack(ModItems.quarrel, 1, 0);
        entries.add(new AmmoEntry(-1, anyStack, 0, "Default / Any"));

        mc.displayGuiScreen(new SelectAmmoScreen(entries, triggerKey));
    }

    @Override
    protected void drawSlotIcon(IRadialMenuSlot slot, int x, int y) {
        if (slot instanceof AmmoEntry) {
            AmmoEntry entry = (AmmoEntry) slot;
            if (entry.displayStack != null) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), entry.displayStack, x, y);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    protected void activateSelected() {
        if (selectedItem != -1 && !ammoEntries.isEmpty()) {
            int numberOfSlices = ammoEntries.size();
            int adjusted = ((selectedItem + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
            adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;

            if (adjusted >= 0 && adjusted < ammoEntries.size()) {
                AmmoEntry chosen = ammoEntries.get(adjusted);
                ItemStack held = mc.thePlayer.getHeldItem();
                if (held != null && held.getItem() instanceof ItemCrossbow) {
                    if (!held.hasTagCompound()) held.setTagCompound(new NBTTagCompound());
                    held.getTagCompound().setInteger("selectedAmmoType", chosen.quarrelType);
                    mc.thePlayer.addChatMessage(new ChatComponentTranslation("Selected ammo: " + chosen.name));
                }
            }
        }
        mc.displayGuiScreen(null);
    }
}
