package com.tiji.spotify_controller.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // Widgets are drawn by safeRender, so Screen.extractRenderState isn't called here
        float scale = uiScale();
        pendingTooltip = null;

        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);

        safeRender(graphics, (int) (mouseX / scale), (int) (mouseY / scale), a);

        graphics.pose().popMatrix();

        if (pendingTooltip != null) {
            graphics.renderTooltip(font, pendingTooltip, mouseX, mouseY);
        }
    }

    public abstract void safeRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks);

    private MouseButtonEvent scaled(MouseButtonEvent event) {
        return new MouseButtonEvent(event.x() / uiScale(), event.y() / uiScale(), event.buttonInfo());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        return super.mouseClicked(scaled(event), doubleClicked);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return super.mouseReleased(scaled(event));
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return super.mouseDragged(scaled(event), dragX / uiScale(), dragY / uiScale());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX / uiScale(), mouseY / uiScale(), horizontalAmount, verticalAmount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX / uiScale(), mouseY / uiScale());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // Handle escape first so a focused input can't swallow it
        if (event.isEscape() && shouldCloseOnEsc()) {
            onClose();
            return true;
        }
        //#if MC<=26200
        return safeKeyPressed(event.key(), event.scancode(), event.modifiers());
        //#else
        //$$ return safeKeyPressed(event.key(), event.keycode(), event.modifiers());
        //#endif
    }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }
    public boolean keyPressedSuper(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }
    public static KeyEvent convertToKeyEvent(int keyCode, int scanCode, int modifiers) {
        return new KeyEvent(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
