package com.marrybye.neorism.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * Canonical 3D armor models for Hunter Hats, Vampire Clothing (Crown, Hat, Legs, Boots), and Vampire Cloaks.
 * Ported 1:1 from TeamLapen's VampirismArmorLayerDefinitions.java and CloakModel.java.
 */
@SideOnly(Side.CLIENT)
public class ModelVampirismArmor extends ModelBiped {

    private static ModelVampirismArmor hunterHatBroad;
    private static ModelVampirismArmor hunterHatTall;
    private static ModelVampirismArmor vampireCrown;
    private static ModelVampirismArmor vampireHat;
    private static ModelVampirismArmor vampirePants;
    private static ModelVampirismArmor vampireBoots;
    private static ModelVampirismArmor vampireCloak;

    public ModelVampirismArmor(int texWidth, int texHeight) {
        super(0.0F, 0.0F, texWidth, texHeight);
        this.bipedHead.cubeList.clear();
        this.bipedHeadwear.cubeList.clear();
        this.bipedBody.cubeList.clear();
        this.bipedRightArm.cubeList.clear();
        this.bipedLeftArm.cubeList.clear();
        this.bipedRightLeg.cubeList.clear();
        this.bipedLeftLeg.cubeList.clear();
    }

    public static ModelBiped getHunterHatModel(int meta, EntityLivingBase entity) {
        ModelVampirismArmor model = (meta == 1) ? getHunterHatTall() : getHunterHatBroad();
        model.syncState(entity, 0);
        return model;
    }

    public static ModelBiped getVampireClothingModel(String pieceName, EntityLivingBase entity) {
        ModelVampirismArmor model;
        int slot;
        if ("crown".equals(pieceName)) {
            model = getVampireCrown();
            slot = 0;
        } else if ("hat".equals(pieceName)) {
            model = getVampireHat();
            slot = 0;
        } else if ("legs".equals(pieceName)) {
            model = getVampirePants();
            slot = 2;
        } else {
            model = getVampireBoots();
            slot = 3;
        }
        model.syncState(entity, slot);
        return model;
    }

    public static ModelBiped getVampireCloakModel(EntityLivingBase entity) {
        ModelVampirismArmor model = getVampireCloak();
        model.syncState(entity, 1);
        return model;
    }

    private static ModelVampirismArmor getHunterHatBroad() {
        if (hunterHatBroad == null) {
            hunterHatBroad = new ModelVampirismArmor(64, 64);
            ModelRenderer hat = new ModelRenderer(hunterHatBroad, 0, 0);
            hat.addBox(-8.0F, -8.0F, -8.0F, 16, 1, 16, 0.0F);
            ModelRenderer top = new ModelRenderer(hunterHatBroad, 0, 17);
            top.addBox(-4.0F, -11.0F, -4.0F, 8, 3, 8, 0.0F);
            hat.addChild(top);
            hunterHatBroad.bipedHead.addChild(hat);
        }
        return hunterHatBroad;
    }

    private static ModelVampirismArmor getHunterHatTall() {
        if (hunterHatTall == null) {
            hunterHatTall = new ModelVampirismArmor(64, 64);
            ModelRenderer hat = new ModelRenderer(hunterHatTall, 0, 0);
            hat.addBox(-6.0F, -8.0F, -6.0F, 12, 1, 12, 0.0F);
            ModelRenderer top = new ModelRenderer(hunterHatTall, 0, 13);
            top.addBox(-4.0F, -13.0F, -4.0F, 8, 5, 8, 0.0F);
            hat.addChild(top);
            hunterHatTall.bipedHead.addChild(hat);
        }
        return hunterHatTall;
    }

    private static ModelVampirismArmor getVampireCrown() {
        if (vampireCrown == null) {
            vampireCrown = new ModelVampirismArmor(64, 32);

            addBox(vampireCrown, vampireCrown.bipedHead, 0, 4, -4.0F, -7.7F, -5.0F, 8, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 0, 2, 1.7F, -8.8F, -5.0F, 2, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 12, 2, -3.7F, -8.8F, -5.0F, 2, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 6, 2, -1.0F, -8.8F, -5.0F, 2, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 1, 0, 2.25F, -9.8F, -5.0F, 1, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 13, 0, -3.2F, -9.8F, -5.0F, 1, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 7, 0, -0.5F, -9.8F, -5.0F, 1, 1, 1, 0.5F);

            addBox(vampireCrown, vampireCrown.bipedHead, 18, 4, -4.0F, -7.7F, 4.0F, 8, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 30, 2, 1.7F, -8.8F, 4.0F, 2, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 18, 2, -3.7F, -8.8F, 4.0F, 2, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 24, 2, -1.0F, -8.8F, 4.0F, 2, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 31, 0, 2.25F, -9.8F, 4.0F, 1, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 19, 0, -3.2F, -9.8F, 4.0F, 1, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 25, 0, -0.5F, -9.8F, 4.0F, 1, 1, 1, 0.5F);

            addBox(vampireCrown, vampireCrown.bipedHead, 0, 6, 4.0F, -7.7F, -4.0F, 1, 1, 8, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 12, 17, 4.0F, -8.8F, -3.7F, 1, 1, 2, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 0, 17, 4.0F, -8.8F, 1.7F, 1, 1, 2, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 6, 17, 4.0F, -8.8F, -1.0F, 1, 1, 2, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 13, 15, 4.0F, -9.8F, -3.25F, 1, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 1, 15, 4.0F, -9.8F, 2.2F, 1, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 7, 15, 4.0F, -9.8F, -0.5F, 1, 1, 1, 0.5F);

            addBox(vampireCrown, vampireCrown.bipedHead, 18, 6, -5.0F, -7.7F, -4.0F, 1, 1, 8, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 18, 17, -5.0F, -8.8F, -3.7F, 1, 1, 2, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 30, 17, -5.0F, -8.8F, 1.7F, 1, 1, 2, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 24, 17, -5.0F, -8.8F, -1.0F, 1, 1, 2, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 19, 15, -5.0F, -9.8F, -3.25F, 1, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 31, 15, -5.0F, -9.8F, 2.2F, 1, 1, 1, 0.5F);
            addBox(vampireCrown, vampireCrown.bipedHead, 25, 15, -5.0F, -9.8F, -0.5F, 1, 1, 1, 0.5F);
        }
        return vampireCrown;
    }

    private static ModelVampirismArmor getVampireHat() {
        if (vampireHat == null) {
            vampireHat = new ModelVampirismArmor(64, 32);
            ModelRenderer hat = new ModelRenderer(vampireHat, 18, 1);
            hat.addBox(-1.5F, -8.0F, -3.5F, 6, 0, 6, 0.5F);
            ModelRenderer tip = new ModelRenderer(vampireHat, 2, 1);
            tip.addBox(-2.4F, -13.5F, -3.0F, 3, 6, 3, 0.5F);
            tip.rotateAngleX = -0.1222F;
            tip.rotateAngleZ = 0.2775F;
            hat.addChild(tip);
            vampireHat.bipedHead.addChild(hat);
        }
        return vampireHat;
    }

    private static ModelVampirismArmor getVampirePants() {
        if (vampirePants == null) {
            vampirePants = new ModelVampirismArmor(32, 32);
            ModelRenderer belt = new ModelRenderer(vampirePants, 4, 16);
            belt.addBox(-4.0F, 7.0F, -2.0F, 8, 5, 4, 0.25F);
            vampirePants.bipedBody.addChild(belt);

            ModelRenderer rightPants = new ModelRenderer(vampirePants, 0, 0);
            rightPants.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F);
            vampirePants.bipedRightLeg.addChild(rightPants);

            ModelRenderer leftPants = new ModelRenderer(vampirePants, 16, 0);
            leftPants.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F);
            vampirePants.bipedLeftLeg.addChild(leftPants);
        }
        return vampirePants;
    }

    private static ModelVampirismArmor getVampireBoots() {
        if (vampireBoots == null) {
            vampireBoots = new ModelVampirismArmor(32, 16);
            ModelRenderer rightToes = new ModelRenderer(vampireBoots, 2, 9);
            rightToes.addBox(-2.0F, 10.0F, -4.0F, 4, 2, 2, 0.3F);
            vampireBoots.bipedRightLeg.addChild(rightToes);

            ModelRenderer rightBoot = new ModelRenderer(vampireBoots, 0, 0);
            rightBoot.addBox(-2.0F, 7.0F, -2.0F, 4, 5, 4, 0.3F);
            vampireBoots.bipedRightLeg.addChild(rightBoot);

            ModelRenderer leftToes = new ModelRenderer(vampireBoots, 18, 9);
            leftToes.addBox(-2.0F, 10.0F, -4.0F, 4, 2, 2, 0.3F);
            vampireBoots.bipedLeftLeg.addChild(leftToes);

            ModelRenderer leftBoot = new ModelRenderer(vampireBoots, 16, 0);
            leftBoot.addBox(-2.0F, 7.0F, -2.0F, 4, 5, 4, 0.3F);
            vampireBoots.bipedLeftLeg.addChild(leftBoot);
        }
        return vampireBoots;
    }

    private static ModelVampirismArmor getVampireCloak() {
        if (vampireCloak == null) {
            vampireCloak = new ModelVampirismArmor(32, 32);
            ModelRenderer holder = new ModelRenderer(vampireCloak, 1, 0);
            holder.addBox(-5.0F, -1.0F, -0.02F, 10, 3, 0, 0.0F);
            holder.rotateAngleX = -1.5708F;
            holder.rotateAngleY = 3.1416F;
            vampireCloak.bipedBody.addChild(holder);

            ModelRenderer cloak = new ModelRenderer(vampireCloak, 0, 3);
            cloak.addBox(-5.0F, -0.02F, -1.0F, 10, 16, 1, 0.0F);
            cloak.setRotationPoint(0.0F, 0.0F, 2.0F);
            cloak.rotateAngleX = -0.07F;
            cloak.rotateAngleY = 3.1416F;
            vampireCloak.bipedBody.addChild(cloak);
        }
        return vampireCloak;
    }

    private static void addBox(ModelBiped model, ModelRenderer parent, int texU, int texV, float x, float y, float z, int w, int h, int d, float scale) {
        ModelRenderer part = new ModelRenderer(model, texU, texV);
        part.addBox(x, y, z, w, h, d, scale);
        parent.addChild(part);
    }

    private void syncState(EntityLivingBase entity, int armorType) {
        this.bipedHead.showModel = (armorType == 0);
        this.bipedHeadwear.showModel = false;
        this.bipedBody.showModel = (armorType == 1 || armorType == 2);
        this.bipedRightArm.showModel = false;
        this.bipedLeftArm.showModel = false;
        this.bipedRightLeg.showModel = (armorType == 2 || armorType == 3);
        this.bipedLeftLeg.showModel = (armorType == 2 || armorType == 3);

        if (entity != null) {
            this.isSneak = entity.isSneaking();
            this.isRiding = entity.isRiding();
            this.isChild = entity.isChild();
            this.heldItemRight = entity.getHeldItem() != null ? 1 : 0;
            this.aimedBow = false;
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                ItemStack inUse = player.getItemInUse();
                if (inUse != null && player.getItemInUseCount() > 0) {
                    EnumAction action = inUse.getItemUseAction();
                    if (action == EnumAction.block) {
                        this.heldItemRight = 3;
                    } else if (action == EnumAction.bow) {
                        this.aimedBow = true;
                    }
                }
            }
        }
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        boolean cullWasEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_CULL_FACE);
        super.render(entity, f, f1, f2, f3, f4, f5);
        if (cullWasEnabled) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }
}
