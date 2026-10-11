package com.tiji.spotify_controller.widgets;

import com.tiji.spotify_controller.util.Theme;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ProgressWidget extends SafeAbstractWidget {
    private static final int h = 10;
    private static final int RAIL_HEIGHT = 2;
    private static final int RAIL_Y = (h - RAIL_HEIGHT) / 2;
    private static final int THUMB_SIZE = 4;

    private float value;
    private final Consumer<Float> action;
    private boolean dragging;

    public ProgressWidget(int x, int y, int w, float value, Consumer<Float> action) {
        super(x, y, w, h, Component.empty());
        this.value = value;
        this.action = action;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        dragging = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        dragging = false;
        action.accept(value);
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (dragging) {
            setValue(getValue(mouseX - getX()), true);
        }

        int thumbPosition = getThumbPosition();
        int played = Theme.accent();
        int remaining = Theme.usesAlbumColor() ? Theme.withAlpha(Theme.text(), 0x77) : Theme.muted();

        context.fill(getX(), getY() + RAIL_Y,
                getX() + thumbPosition, getY() + RAIL_Y + RAIL_HEIGHT, played); // Rail left
        context.fill(getX() + thumbPosition, getY() + RAIL_Y,
                getX() + getWidth(), getY() + RAIL_Y + RAIL_HEIGHT, remaining); // Rail right
        context.fill(getX() + thumbPosition, getY() + (h - THUMB_SIZE) / 2,
                getX() + thumbPosition + THUMB_SIZE, getY() + (h + THUMB_SIZE) / 2, played); // Thumb
    }

    protected int getThumbPosition() {
        return (int) (value * (getWidth() - THUMB_SIZE));
    }

    protected float getValue(int x) {
        return x / (float) getWidth();
    }

    public void setValue(float value) {
        setValue(value, false);
    }

    private void setValue(float value, boolean force) {
        if (dragging && !force) return;

        this.value = Math.clamp(value, 0f, 1f);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}
}