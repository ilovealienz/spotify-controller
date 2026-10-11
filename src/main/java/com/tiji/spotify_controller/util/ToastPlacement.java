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
        int sidePadding = Main.CONFIG.toastSidePadding();
        float scale = Main.CONFIG.toastScale() / 100f;
        float scaledWidth = toastWidth * scale;
        float scaledHeight = toastHeight * scale;

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        //#if MC>=12106
        //$$ float currentX = context.pose().m20();
        //$$ float currentY = context.pose().m21();
        //#else
        float currentX = context.pose().last().pose().m30();
        float currentY = context.pose().last().pose().m31();
        //#endif

        // How far vanilla has slid the toast in (0 to 1), and its stack position scaled up
        float shown = (screenWidth - currentX) / toastWidth;
        float stackY = currentY * scale;

        float targetX = position.isLeft()
                ? scaledWidth * shown - scaledWidth + sidePadding
                : screenWidth - scaledWidth * shown - sidePadding;
        float targetY = position.isBottom()
                ? screenHeight - stackY - scaledHeight - padding
                : stackY + padding;

        //#if MC>=12106
        //$$ context.pose().pushMatrix();
        //$$ context.pose().translate(targetX - currentX, targetY - currentY);
        //$$ context.pose().scale(scale, scale);
        //#else
        context.pose().pushPose();
        context.pose().translate(targetX - currentX, targetY - currentY, 0);
        context.pose().scale(scale, scale, 1);
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
