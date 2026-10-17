package com.marrybye.neorism.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelCoffin;
import com.marrybye.neorism.common.tileentity.TileEntityCoffin;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * TESR for the Vampire Coffin.
 * Renders the full 2-block 3D coffin with animated lid when sleeping.
 */
public class TileEntityCoffinRenderer extends TileEntitySpecialRenderer {

    public static final ResourceLocation TEXTURE =
        new ResourceLocation(Neorism.MODID, "textures/entity/coffin/coffin_base.png");

    private final ModelCoffin model = new ModelCoffin();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        if (!(te instanceof TileEntityCoffin)) return;
        TileEntityCoffin coffin = (TileEntityCoffin) te;

        int meta = te.hasWorldObj() ? te.getBlockMetadata() : 0;
        // Only render from foot block
        if ((meta & 8) != 0) return;

        int dir = meta & 3;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glScalef(1.0F, -1.0F, -1.0F);

        float rotAngle = 0.0F;
        if (dir == 0) rotAngle = 180.0F; // South
        else if (dir == 1) rotAngle = 90.0F;  // West
        else if (dir == 2) rotAngle = 0.0F;   // North
        else if (dir == 3) rotAngle = 270.0F; // East

        GL11.glRotatef(rotAngle, 0.0F, 1.0F, 0.0F);

        // Adjust origin so coffin sits evenly across the 2 blocks
        GL11.glTranslatef(0.0F, 0.05F, 0.5F);

        bindTexture(TEXTURE);
        model.render(coffin.getLidAngle(partialTick), 0.0625F);

        GL11.glPopMatrix();
    }
}
