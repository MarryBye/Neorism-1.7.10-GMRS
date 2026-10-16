package com.marrybye.neorism.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.common.tileentity.TileEntityBloodPedestal;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * TESR for the Blood Pedestal.
 * Renders the stored catalyst / pure blood item floating and rotating smoothly above the pedestal.
 * Ported from TeamLapen's PedestalRenderer.java.
 */
public class TileEntityBloodPedestalRenderer extends TileEntitySpecialRenderer {

    private EntityItem dummyItem;

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (!(te instanceof TileEntityBloodPedestal)) return;
        TileEntityBloodPedestal pedestal = (TileEntityBloodPedestal) te;

        ItemStack stack = pedestal.getStoredItem();
        if (stack != null && stack.getItem() != null) {
            if (dummyItem == null) {
                dummyItem = new EntityItem(te.getWorldObj());
            }
            dummyItem.worldObj = te.getWorldObj();
            dummyItem.setEntityItemStack(stack);
            dummyItem.hoverStart = 0.0F;
            dummyItem.age = 0;

            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5D, y + 0.72D, z + 0.5D);

            float rot = (pedestal.ticksExisted + partialTick) * 1.5F;
            GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);

            RenderItem.renderInFrame = true;
            RenderManager.instance.renderEntityWithPosYaw(dummyItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            RenderItem.renderInFrame = false;

            GL11.glPopMatrix();
        }
    }
}
