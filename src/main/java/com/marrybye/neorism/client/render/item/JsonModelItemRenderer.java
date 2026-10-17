package com.marrybye.neorism.client.render.item;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marrybye.neorism.common.util.ModLogger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

/**
 * High-performance 3D JSON Model Item Renderer for Forge 1.7.10.
 * Parses Blockbench / modern Minecraft item models with full display transformations
 * and compiles them into cached OpenGL Display Lists.
 */
public class JsonModelItemRenderer implements IItemRenderer {

    public interface IModelResolver {
        String resolveModel(ItemStack stack);
    }

    public interface ITextureResolver {
        ResourceLocation resolveTexture(ItemStack stack);
    }

    public static class Transform {
        public float rx, ry, rz;
        public float tx, ty, tz;
        public float sx = 1.0F, sy = 1.0F, sz = 1.0F;

        public void apply() {
            // Modern translation is in 1/16ths of a block
            GL11.glTranslatef(tx / 16.0F, ty / 16.0F, tz / 16.0F);
            if (rx != 0.0F) GL11.glRotatef(rx, 1.0F, 0.0F, 0.0F);
            if (ry != 0.0F) GL11.glRotatef(ry, 0.0F, 1.0F, 0.0F);
            if (rz != 0.0F) GL11.glRotatef(rz, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(sx, sy, sz);
        }
    }

    private static class CompiledModel {
        int displayList = -1;
        Transform gui;
        Transform firstPerson;
        Transform thirdPerson;
        Transform ground;
    }

    private final IModelResolver modelResolver;
    private final ITextureResolver textureResolver;
    private final Map<String, CompiledModel> modelCache = new HashMap<String, CompiledModel>();

    public JsonModelItemRenderer(final String modelResourcePath, final ResourceLocation defaultTexture) {
        this(new IModelResolver() {
            @Override
            public String resolveModel(ItemStack stack) {
                return modelResourcePath;
            }
        }, new ITextureResolver() {
            @Override
            public ResourceLocation resolveTexture(ItemStack stack) {
                return defaultTexture;
            }
        });
    }

    public JsonModelItemRenderer(final String modelResourcePath, final ResourceLocation[] tierTextures) {
        this(new IModelResolver() {
            @Override
            public String resolveModel(ItemStack stack) {
                return modelResourcePath;
            }
        }, new ITextureResolver() {
            @Override
            public ResourceLocation resolveTexture(ItemStack stack) {
                int dmg = stack != null ? stack.getItemDamage() : 0;
                if (dmg < 0 || dmg >= tierTextures.length) dmg = 0;
                return tierTextures[dmg];
            }
        });
    }

    public JsonModelItemRenderer(IModelResolver modelResolver, ITextureResolver textureResolver) {
        this.modelResolver = modelResolver;
        this.textureResolver = textureResolver;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        switch (helper) {
            case ENTITY_ROTATION:
            case ENTITY_BOBBING:
                return true;
            case EQUIPPED_BLOCK:
                return true; // Bypass Forge's 2D flat item slicing for both first and third person
            default:
                return false;
        }
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        String modelPath = modelResolver != null ? modelResolver.resolveModel(item) : null;
        if (modelPath == null) return;

        CompiledModel model = getOrCompile(modelPath);
        if (model == null || model.displayList < 0) return;

        ResourceLocation texture = textureResolver != null ? textureResolver.resolveTexture(item) : null;
        if (texture != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-0.2F, -0.2F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        switch (type) {
            case INVENTORY:
                GL11.glTranslatef(8.0F, 8.0F, 0.0F);
                GL11.glScalef(16.0F, -16.0F, 16.0F);
                if (model.gui != null) {
                    model.gui.apply();
                } else {
                    GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(225.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(0.625F, 0.625F, 0.625F);
                }
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                break;
            case EQUIPPED:
                // Undo RenderBiped's EQUIPPED_BLOCK inversion and position weapon naturally in entity hand
                GL11.glTranslatef(0.0F, -0.1875F, 0.3125F);
                GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-1.0F, -1.0F, 1.0F);

                GL11.glTranslatef(0.1F, 0.3F, 0.0F);
                GL11.glRotatef(-45.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(1.4F, 1.4F, 1.4F);

                if (model.thirdPerson != null) {
                    model.thirdPerson.apply();
                }
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(1.5F, 1.5F, 1.5F);
                if (model.firstPerson != null) {
                    model.firstPerson.apply();
                } else {
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glScalef(0.8F, 0.8F, 0.8F);
                }
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                break;
            case ENTITY:
                GL11.glTranslatef(0.0F, 0.2F, 0.0F);
                if (model.ground != null) {
                    model.ground.apply();
                } else {
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                }
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                break;
            default:
                break;
        }

        GL11.glCallList(model.displayList);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    private CompiledModel getOrCompile(String modelResourcePath) {
        CompiledModel model = modelCache.get(modelResourcePath);
        if (model != null) return model;

        model = new CompiledModel();
        modelCache.put(modelResourcePath, model);

        try {
            InputStream stream = getClass().getResourceAsStream(modelResourcePath);
            if (stream == null) {
                String path = modelResourcePath.startsWith("/") ? modelResourcePath : "/" + modelResourcePath;
                stream = getClass().getResourceAsStream(path);
            }

            if (stream == null) {
                ModLogger.debug("JsonModelItemRenderer: Could not find model resource: %s", modelResourcePath);
                return model;
            }

            JsonObject root = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            
            // Parse display transforms
            if (root.has("display")) {
                JsonObject display = root.getAsJsonObject("display");
                if (display.has("gui")) model.gui = parseTransform(display.getAsJsonObject("gui"));
                if (display.has("firstperson_righthand")) model.firstPerson = parseTransform(display.getAsJsonObject("firstperson_righthand"));
                else if (display.has("firstperson")) model.firstPerson = parseTransform(display.getAsJsonObject("firstperson"));
                
                if (display.has("thirdperson_righthand")) model.thirdPerson = parseTransform(display.getAsJsonObject("thirdperson_righthand"));
                else if (display.has("thirdperson")) model.thirdPerson = parseTransform(display.getAsJsonObject("thirdperson"));
                
                if (display.has("ground")) model.ground = parseTransform(display.getAsJsonObject("ground"));
            }

            JsonArray elements = root.getAsJsonArray("elements");
            if (elements == null || elements.size() == 0) return model;

            model.displayList = GL11.glGenLists(1);
            GL11.glNewList(model.displayList, GL11.GL_COMPILE);
            Tessellator tess = Tessellator.instance;

            for (int i = 0; i < elements.size(); i++) {
                JsonObject elem = elements.get(i).getAsJsonObject();
                JsonArray from = elem.getAsJsonArray("from");
                JsonArray to = elem.getAsJsonArray("to");

                float x1 = from.get(0).getAsFloat() / 16.0F;
                float y1 = from.get(1).getAsFloat() / 16.0F;
                float z1 = from.get(2).getAsFloat() / 16.0F;

                float x2 = to.get(0).getAsFloat() / 16.0F;
                float y2 = to.get(1).getAsFloat() / 16.0F;
                float z2 = to.get(2).getAsFloat() / 16.0F;

                boolean hasRot = elem.has("rotation");
                if (hasRot) {
                    JsonObject rot = elem.getAsJsonObject("rotation");
                    JsonArray origin = rot.getAsJsonArray("origin");
                    float ox = origin.get(0).getAsFloat() / 16.0F;
                    float oy = origin.get(1).getAsFloat() / 16.0F;
                    float oz = origin.get(2).getAsFloat() / 16.0F;
                    float angle = rot.get("angle").getAsFloat();
                    String axis = rot.get("axis").getAsString();

                    GL11.glPushMatrix();
                    GL11.glTranslatef(ox, oy, oz);
                    if ("x".equalsIgnoreCase(axis)) GL11.glRotatef(angle, 1, 0, 0);
                    else if ("y".equalsIgnoreCase(axis)) GL11.glRotatef(angle, 0, 1, 0);
                    else if ("z".equalsIgnoreCase(axis)) GL11.glRotatef(angle, 0, 0, 1);
                    GL11.glTranslatef(-ox, -oy, -oz);
                }

                JsonObject faces = elem.getAsJsonObject("faces");
                if (faces != null) {
                    renderFaces(tess, faces, x1, y1, z1, x2, y2, z2);
                }

                if (hasRot) {
                    GL11.glPopMatrix();
                }
            }

            GL11.glEndList();
            ModLogger.debug("Successfully compiled 3D JSON model display list for %s", modelResourcePath);

        } catch (Throwable t) {
            ModLogger.debug("Failed to compile 3D model for %s: %s", modelResourcePath, t.getMessage());
            model.displayList = -1;
        }

        return model;
    }

    private Transform parseTransform(JsonObject obj) {
        Transform t = new Transform();
        if (obj.has("rotation")) {
            JsonArray arr = obj.getAsJsonArray("rotation");
            t.rx = arr.get(0).getAsFloat();
            t.ry = arr.get(1).getAsFloat();
            t.rz = arr.get(2).getAsFloat();
        }
        if (obj.has("translation")) {
            JsonArray arr = obj.getAsJsonArray("translation");
            t.tx = arr.get(0).getAsFloat();
            t.ty = arr.get(1).getAsFloat();
            t.tz = arr.get(2).getAsFloat();
        }
        if (obj.has("scale")) {
            JsonArray arr = obj.getAsJsonArray("scale");
            t.sx = arr.get(0).getAsFloat();
            t.sy = arr.get(1).getAsFloat();
            t.sz = arr.get(2).getAsFloat();
        }
        return t;
    }

    private void renderFaces(Tessellator tess, JsonObject faces, float x1, float y1, float z1, float x2, float y2, float z2) {
        // North (Z-)
        if (faces.has("north")) {
            float[] uv = getUV(faces.getAsJsonObject("north"));
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 0.0F, -1.0F);
            tess.addVertexWithUV(x1, y1, z1, uv[0], uv[3]);
            tess.addVertexWithUV(x2, y1, z1, uv[2], uv[3]);
            tess.addVertexWithUV(x2, y2, z1, uv[2], uv[1]);
            tess.addVertexWithUV(x1, y2, z1, uv[0], uv[1]);
            tess.draw();
        }
        // South (Z+)
        if (faces.has("south")) {
            float[] uv = getUV(faces.getAsJsonObject("south"));
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 0.0F, 1.0F);
            tess.addVertexWithUV(x2, y1, z2, uv[2], uv[3]);
            tess.addVertexWithUV(x1, y1, z2, uv[0], uv[3]);
            tess.addVertexWithUV(x1, y2, z2, uv[0], uv[1]);
            tess.addVertexWithUV(x2, y2, z2, uv[2], uv[1]);
            tess.draw();
        }
        // West (X-)
        if (faces.has("west")) {
            float[] uv = getUV(faces.getAsJsonObject("west"));
            tess.startDrawingQuads();
            tess.setNormal(-1.0F, 0.0F, 0.0F);
            tess.addVertexWithUV(x1, y1, z2, uv[2], uv[3]);
            tess.addVertexWithUV(x1, y1, z1, uv[0], uv[3]);
            tess.addVertexWithUV(x1, y2, z1, uv[0], uv[1]);
            tess.addVertexWithUV(x1, y2, z2, uv[2], uv[1]);
            tess.draw();
        }
        // East (X+)
        if (faces.has("east")) {
            float[] uv = getUV(faces.getAsJsonObject("east"));
            tess.startDrawingQuads();
            tess.setNormal(1.0F, 0.0F, 0.0F);
            tess.addVertexWithUV(x2, y1, z1, uv[2], uv[3]);
            tess.addVertexWithUV(x2, y1, z2, uv[0], uv[3]);
            tess.addVertexWithUV(x2, y2, z2, uv[0], uv[1]);
            tess.addVertexWithUV(x2, y2, z1, uv[2], uv[1]);
            tess.draw();
        }
        // Up (Y+)
        if (faces.has("up")) {
            float[] uv = getUV(faces.getAsJsonObject("up"));
            tess.startDrawingQuads();
            tess.setNormal(0.0F, 1.0F, 0.0F);
            tess.addVertexWithUV(x1, y2, z2, uv[0], uv[3]);
            tess.addVertexWithUV(x2, y2, z2, uv[2], uv[3]);
            tess.addVertexWithUV(x2, y2, z1, uv[2], uv[1]);
            tess.addVertexWithUV(x1, y2, z1, uv[0], uv[1]);
            tess.draw();
        }
        // Down (Y-)
        if (faces.has("down")) {
            float[] uv = getUV(faces.getAsJsonObject("down"));
            tess.startDrawingQuads();
            tess.setNormal(0.0F, -1.0F, 0.0F);
            tess.addVertexWithUV(x1, y1, z1, uv[0], uv[1]);
            tess.addVertexWithUV(x2, y1, z1, uv[2], uv[1]);
            tess.addVertexWithUV(x2, y1, z2, uv[2], uv[3]);
            tess.addVertexWithUV(x1, y1, z2, uv[0], uv[3]);
            tess.draw();
        }
    }

    private float[] getUV(JsonObject face) {
        if (face.has("uv")) {
            JsonArray uvArr = face.getAsJsonArray("uv");
            return new float[]{
                uvArr.get(0).getAsFloat() / 16.0F,
                uvArr.get(1).getAsFloat() / 16.0F,
                uvArr.get(2).getAsFloat() / 16.0F,
                uvArr.get(3).getAsFloat() / 16.0F
            };
        }
        return new float[]{0.0F, 0.0F, 1.0F, 1.0F};
    }
}
