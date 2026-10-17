package com.marrybye.neorism.client.render.item;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.client.model.ModelCoffin;
import com.marrybye.neorism.client.render.tileentity.TileEntityCoffinRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

/**
 * 3D Item Renderer for the Coffin in inventory, in-hand, and world drop.
 */
public class ItemCoffinRenderer implements IItemRenderer {

    private final ModelCoffin model = new ModelCoffin();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(TileEntityCoffinRenderer.TEXTURE);

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.5F, 0.3F, 0.5F);
            GL11.glScalef(0.35F, -0.35F, -0.35F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        } else if (type == ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.5F, 0.4F, 0.5F);
            GL11.glScalef(0.3F, -0.3F, -0.3F);
            GL11.glRotatef(15.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        } else if (type == ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0.0F, -0.15F, 0.0F);
            GL11.glScalef(0.28F, -0.28F, -0.28F);
            GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        } else if (type == ItemRenderType.ENTITY) {
            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
            GL11.glScalef(0.3F, -0.3F, -0.3F);
        }

        model.render(0.0F, 0.0625F);

        GL11.glPopMatrix();
    }
}
