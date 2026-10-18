package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelBiped64;
import com.marrybye.neorism.common.entity.minion.EntityHunterMinion;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Renderer for Hunter Minion entities with 64x64 skin variations.
 */
public class RenderHunterMinion extends RenderBiped {

    private static final ResourceLocation[] SKINS = new ResourceLocation[] {
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter1.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter2.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter3.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter4.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter5_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter6_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter7_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter8_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter9.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter10.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter11.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter12_slim.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter13.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter14.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter15.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/hunter/hunter16.png")
    };

    private final ModelBiped64 defaultModel = new ModelBiped64(0.0F, false);
    private final ModelBiped64 slimModel = new ModelBiped64(0.0F, true);

    public RenderHunterMinion() {
        super(new ModelBiped64(0.0F, false), 0.5F);
    }

    @Override
    public void doRender(net.minecraft.entity.EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        ResourceLocation tex = getEntityTexture(entity);
        ModelBiped64 active = (tex != null && tex.getResourcePath().endsWith("_slim.png")) ? this.slimModel : this.defaultModel;
        this.mainModel = active;
        this.modelBipedMain = active;
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityHunterMinion) {
            int skin = ((EntityHunterMinion) entity).getSkinType();
            int idx = Math.abs(skin) % SKINS.length;
            return SKINS[idx];
        }
        return SKINS[0];
    }
}
