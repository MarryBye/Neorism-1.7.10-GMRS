package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelDraculaPhase1;
import com.marrybye.neorism.client.model.ModelDraculaPhase2;
import com.marrybye.neorism.client.model.ModelDraculaPhase3;
import com.marrybye.neorism.common.entity.vampire.EntityDracula;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Angelica-compatible renderer for Lord Dracula with canonical 256x256 phase models and textures.
 */
public class RenderDracula extends RenderLiving {

    private static final ResourceLocation PHASE_1 =
        new ResourceLocation(Neorism.MODID, "textures/entity/dracula/phase1.png");
    private static final ResourceLocation PHASE_2 =
        new ResourceLocation(Neorism.MODID, "textures/entity/dracula/phase2.png");
    private static final ResourceLocation PHASE_3 =
        new ResourceLocation(Neorism.MODID, "textures/entity/dracula/phase3.png");

    private final ModelBase phase1Model = new ModelDraculaPhase1();
    private final ModelBase phase2Model = new ModelDraculaPhase2();
    private final ModelBase phase3Model = new ModelDraculaPhase3();

    public RenderDracula() {
        super(new ModelDraculaPhase1(), 0.5F);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity instanceof EntityDracula) {
            int phase = ((EntityDracula) entity).getPhase();
            if (phase == 2) {
                this.mainModel = this.phase2Model;
            } else if (phase == 3) {
                this.mainModel = this.phase3Model;
            } else {
                this.mainModel = this.phase1Model;
            }
        }
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);
        // Canonical scale from TeamLapen's DraculaRenderer.java (0.62F)
        GL11.glScalef(0.62F, 0.62F, 0.62F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityDracula) {
            int phase = ((EntityDracula) entity).getPhase();
            if (phase == 2) return PHASE_2;
            if (phase == 3) return PHASE_3;
        }
        return PHASE_1;
    }
}
