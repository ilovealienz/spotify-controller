package com.tiji.spotify_controller.ui;

import net.minecraft.client.gui.GuiGraphics;
//#if MC<=12108
import com.mojang.blaze3d.platform.InputConstants;
//#endif
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//#if MC>=12109
//$$ import net.minecraft.client.input.KeyEvent;
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif

public abstract class SafeAbstractScreen extends Screen {
    protected SafeAbstractScreen(Component title) {
        super(title);
    }

    public boolean safeKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    private Component pendingTooltip;

    // Size multiplier for everything the screen draws; 1 = normal size
    protected float uiScale() {
        return 1f;
    }

    // Screen size in scaled units; use these instead of width/height for layout
    protected int sw() {
        return (int) Math.ceil(width / uiScale());
    }

    protected int sh() {
        return (int) Math.ceil(height / uiScale());
    }

    // Tooltips are drawn after the scale is removed so they keep their normal size and position
    protected void queueTooltip(Component text) {
        pendingTooltip = text;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float a) {
        // Widgets are drawn by safeRender, so Screen.render isn't called here
        //#if MC<=12106
        super.renderBackground(graphics, mouseX, mouseY, a);
        //#endif

        float scale = uiScale();
        pendingTooltip = null;

        //#if MC>=12106
        //$$ graphics.pose().pushMatrix();
        //$$ graphics.pose().scale(scale, scale);
        //#else
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1);
        //#endif

        safeRender(graphics, (int) (mouseX / scale), (int) (mouseY / scale), a);

        //#if MC>=12106
        //$$ graphics.pose().popMatrix();
        //#else
        graphics.pose().popPose();
        //#endif

        if (pendingTooltip != null) {
            graphics.renderTooltip(font, pendingTooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX / uiScale(), mouseY / uiScale(), horizontalAmount, verticalAmount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX / uiScale(), mouseY / uiScale());
    }

    //#if MC<=12108
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX / uiScale(), mouseY / uiScale(), button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX / uiScale(), mouseY / uiScale(), button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX / uiScale(), mouseY / uiScale(), button, dragX / uiScale(), dragY / uiScale());
    }
    //#else
    //$$ private MouseButtonEvent scaled(MouseButtonEvent event) {
    //$$     return new MouseButtonEvent(event.x() / uiScale(), event.y() / uiScale(), event.buttonInfo());
    //$$ }
    //$$
    //$$ @Override
    //$$ public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
    //$$     return super.mouseClicked(scaled(event), doubleClicked);
    //$$ }
    //$$
    //$$ @Override
    //$$ public boolean mouseReleased(MouseButtonEvent event) {
    //$$     return super.mouseReleased(scaled(event));
    //$$ }
    //$$
    //$$ @Override
    //$$ public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
    //$$     return super.mouseDragged(scaled(event), dragX / uiScale(), dragY / uiScale());
    //$$ }
    //#endif

    public abstract void safeRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    //#if MC<=12108
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle escape first so a focused input can't swallow it
        if (keyCode == InputConstants.KEY_ESCAPE && shouldCloseOnEsc()) {
            onClose();
            return true;
        }
        return safeKeyPressed(keyCode, scanCode, modifiers);
    }
    public boolean keyPressedSuper(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    //#else
    //$$ @Override
    //$$ public boolean keyPressed(KeyEvent event) {
    //$$     // Handle escape first so a focused input can't swallow it
    //$$     if (event.isEscape() && shouldCloseOnEsc()) {
    //$$         onClose();
    //$$         return true;
    //$$     }
    //$$     return safeKeyPressed(event.key(), event.scancode(), event.modifiers());
    //$$ }
    //$$
    //$$ public boolean keyPressedSuper(int keyCode, int scanCode, int modifiers) {
    //$$     return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    //$$ }
    //$$ public static KeyEvent convertToKeyEvent(int keyCode, int scanCode, int modifiers) {
    //$$     return new KeyEvent(keyCode, scanCode, modifiers);
    //$$ }
    //#endif

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
