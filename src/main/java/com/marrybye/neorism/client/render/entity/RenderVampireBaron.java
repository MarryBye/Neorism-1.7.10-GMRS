package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelBaron;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaron;
import com.marrybye.neorism.common.entity.vampire.EntityVampireBaroness;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Angelica-compatible renderer for Vampire Baron and Baroness minibosses (64x64 with claws).
 */
public class RenderVampireBaron extends RenderBiped64 {

    private static final ResourceLocation TEXTURE_BARON =
        new ResourceLocation(Neorism.MODID, "textures/entity/baron/baron.png");
    private static final ResourceLocation TEXTURE_BARON_ENRAGED =
        new ResourceLocation(Neorism.MODID, "textures/entity/baron/baron_enraged.png");
    private static final ResourceLocation TEXTURE_BARONESS =
        new ResourceLocation(Neorism.MODID, "textures/entity/baron/baroness.png");
    private static final ResourceLocation TEXTURE_BARONESS_ENRAGED =
        new ResourceLocation(Neorism.MODID, "textures/entity/baron/baroness_enraged.png");

    public RenderVampireBaron() {
        super(new ModelBaron(false), new ModelBaron(true), 0.6F);
    }

    private boolean isLadyEntity(Entity entity) {
        return (entity instanceof EntityVampireBaroness)
            || (entity instanceof EntityVampireBaron && ((EntityVampireBaron) entity).isLady());
    }

    @Override
    protected boolean isSlim(EntityLiving entity) {
        return isLadyEntity(entity);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);
        // Slightly larger than standard biped
        GL11.glScalef(1.15F, 1.15F, 1.15F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        boolean lady = isLadyEntity(entity);
        boolean enraged = (entity instanceof EntityVampireBaron) && ((EntityVampireBaron) entity).isEnraged();
        if (lady) {
            return enraged ? TEXTURE_BARONESS_ENRAGED : TEXTURE_BARONESS;
        }
        return enraged ? TEXTURE_BARON_ENRAGED : TEXTURE_BARON;
    }
}
