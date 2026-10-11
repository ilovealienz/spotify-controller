package com.tiji.spotify_controller.util;

//#if MC>=12107
//$$ import net.minecraft.client.renderer.RenderPipelines;
//#else
import net.minecraft.client.renderer.RenderType;
//#endif

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.Font;

public class SafeDrawer {
    public static void drawImage(GuiGraphics context,
                                 ResourceLocation sprite,
                                 int x,
                                 int y,
                                 float u,
                                 float v,
                                 int width,
                                 int height,
                                 int regionWith,
                                 int regionHeight,
                                 int textureWidth,
                                 int textureHeight) {
        //#if MC>=12102
        context.blit(
                //#if MC>=12107
                //$$ RenderPipelines.GUI_TEXTURED,
                //#else
                RenderType::guiTextured,
                //#endif
                sprite,
                x, y, u, v,
                width, height,
                regionWith, regionHeight, textureWidth, textureHeight
        );
        //#else
        //$$ context.blit(
        //$$         sprite,
        //$$         x, y,
        //$$         width, height, u, v,
        //$$         regionWith, regionHeight, textureWidth, textureHeight
        //$$ );
        //#endif
    }

    public static void drawImage(GuiGraphics context,
                                 ResourceLocation sprite,
                                 int x,
                                 int y,
                                 float u,
                                 float v,
                                 int width,
                                 int height,
                                 int regionWith,
                                 int regionHeight,
                                 int textureWidth,
                                 int textureHeight,
                                 int tint) {
        //#if MC>=12102
        context.blit(
                //#if MC>=12106
                //$$ RenderPipelines.GUI_TEXTURED,
                //#else
                RenderType::guiTextured,
                //#endif
                sprite,
                x, y, u, v,
                width, height,
                regionWith, regionHeight, textureWidth, textureHeight,
                tint
        );
        //#else
        //$$ RenderSystem.enableBlend();
        //$$ RenderSystem.defaultBlendFunc();
        //$$ float a = (tint >> 24 & 0xFF) / 255.0f;
        //$$ float r = (tint       & 0xFF) / 255.0f;
        //$$ float g = (tint >> 8  & 0xFF) / 255.0f;
        //$$ float b = (tint >> 16 & 0xFF) / 255.0f;
        //$$
        //$$ context.setColor(r, g, b, a);
        //$$ context.blit(
        //$$         sprite,
        //$$         x, y,
        //$$         width, height, u, v,
        //$$         regionWith, regionHeight, textureWidth, textureHeight
        //$$ );
        //$$ context.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        //$$ RenderSystem.disableBlend();
        //#endif
    }

    public static void drawImage(GuiGraphics context,
                                 ResourceLocation sprite,
                                 int x,
                                 int y,
                                 float u,
                                 float v,
                                 int width,
                                 int height) {
        drawImage(context, sprite, x, y, u, v, width, height, 1, 1, 1, 1);
    }

    public static void drawImage(GuiGraphics context,
                                 ResourceLocation sprite,
                                 int x,
                                 int y,
                                 float u,
                                 float v,
                                 int width,
                                 int height,
                                 int color) {
        drawImage(context, sprite, x, y, u, v, width, height, 1, 1, 1, 1, color);
    }

    // Scissor in the current pose's coordinates. 1.21.6+ applies the pose itself;
    // older versions take raw screen coordinates, so the pose is applied here.
    public static void enableScissor(GuiGraphics context, int x0, int y0, int x1, int y1) {
        //#if MC>=12106
        //$$ context.enableScissor(x0, y0, x1, y1);
        //#else
        org.joml.Matrix4f pose = context.pose().last().pose();
        context.enableScissor(
                (int) Math.floor(pose.m00() * x0 + pose.m30()),
                (int) Math.floor(pose.m11() * y0 + pose.m31()),
                (int) Math.ceil(pose.m00() * x1 + pose.m30()),
                (int) Math.ceil(pose.m11() * y1 + pose.m31())
        );
        //#endif
    }

    public static void drawOutline(GuiGraphics context,
                                   int x, int y,
                                   int w, int h,
                                   int color) {
        //#if MC<=12108
        context.renderOutline(x, y, w, h, color);
        //#elseif MC<=12111
        //$$ context.submitOutline(x, y, w, h, color);
        //#else
        //$$ context.outline(x, y, w, h, color);
        //#endif
    }

    public static void drawString(GuiGraphics context,
                                  Font font,
                                  Component text,
                                  int x, int y,
                                  int color, boolean shadow) {
        //#if MC<=12111
        context.drawString(font, text, x, y, color, shadow);
        //#else
        //$$ context.text(font, text, x, y, color, shadow);
        //#endif
    }

    public static void drawString(GuiGraphics context,
                                  Font font,
                                  String text,
                                  int x, int y,
                                  int color, boolean shadow) {
        //#if MC<=12111
        context.drawString(font, text, x, y, color, shadow);
        //#else
        //$$ context.text(font, text, x, y, color, shadow);
        //#endif
    }

    public static void vLine(GuiGraphics context, int x, int y, int height, int color) {
        //#if MC<=12111
        context.vLine(x, y, height, color);
        //#else
        //$$ context.verticalLine(x, y, height, color);
        //#endif
    }
}
