package com.tiji.spotify_controller.util;

import com.tiji.spotify_controller.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

// Moves a toast away from vanilla's top-right slot. The toast manager has already
// translated the pose to the toast's position, so this reads that position and
// mirrors it to the configured corner, keeping the slide animation and stacking.
public final class ToastPlacement {
    private ToastPlacement() {}

    public static void push(GuiGraphics context, int toastWidth, int toastHeight) {
        ToastPosition position = Main.CONFIG.toastPosition();
        int padding = Main.CONFIG.toastPadding();

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        //#if MC>=12106
        //$$ float currentX = context.pose().m20();
        //$$ float currentY = context.pose().m21();
        //#else
        float currentX = context.pose().last().pose().m30();
        float currentY = context.pose().last().pose().m31();
        //#endif

        float targetX = position.isLeft() ? screenWidth - currentX - toastWidth : currentX;
        float targetY = position.isBottom() ? screenHeight - currentY - toastHeight - padding : currentY + padding;

        //#if MC>=12106
        //$$ context.pose().pushMatrix();
        //$$ context.pose().translate(targetX - currentX, targetY - currentY);
        //#else
        context.pose().pushPose();
        context.pose().translate(targetX - currentX, targetY - currentY, 0);
        //#endif
    }

    public static void pop(GuiGraphics context) {
        //#if MC>=12106
        //$$ context.pose().popMatrix();
        //#else
        context.pose().popPose();
        //#endif
    }
}
