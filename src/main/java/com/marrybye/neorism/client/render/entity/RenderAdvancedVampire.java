package com.marrybye.neorism.client.render.entity;

import com.marrybye.neorism.Neorism;
import com.marrybye.neorism.client.model.ModelBiped64;
import com.marrybye.neorism.common.entity.vampire.EntityAdvancedVampire;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Angelica-compatible renderer for advanced vampires with 64x64 skins.
 */
public class RenderAdvancedVampire extends RenderBiped64 {

    private static final ResourceLocation[] SKINS = new ResourceLocation[] {
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/vampire.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/bronzeshadow.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/cheaterpaul.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/cournbread.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/edelvveiss.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/gridexpert.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/lilcatgod.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/mazdallier.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/mistadon.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/pendragon.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/piklach.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/random.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/s_olace.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/spade.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/special_krab.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/supermike.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/the_rebel_t.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/volqonah.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/wildbill.png"),
        new ResourceLocation(Neorism.MODID, "textures/entity/advanced/zinny.png")
    };

    public RenderAdvancedVampire() {
        super(new ModelBiped64(0.0F, false), new ModelBiped64(0.0F, true), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityAdvancedVampire) {
            int skin = ((EntityAdvancedVampire) entity).getSkinType();
            int idx = Math.abs(skin) % SKINS.length;
            return SKINS[idx];
        }
        return SKINS[0];
    }
}
