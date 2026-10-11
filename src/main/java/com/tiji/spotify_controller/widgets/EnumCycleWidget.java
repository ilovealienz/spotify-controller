package com.tiji.spotify_controller.widgets;

import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.Theme;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class EnumCycleWidget extends SafeAbstractWidget implements ValueHolder {
    private Enum<?> value;

    public EnumCycleWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(""));
    }

    @Override
    public Object getValue_() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Enum<?> e) {
            this.value = e;
        } else {
            throw new IllegalArgumentException("Value must be an enum constant");
        }
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (value == null) return;

        Font textRenderer = Minecraft.getInstance().font;
        int yOffset = (getHeight() - textRenderer.lineHeight) / 2;

        Component text = Component.literal("< ")
                .append(Component.translatable("ui.spotify_controller.option." + value.name().toLowerCase(Locale.ROOT)))
                .append(" >");
        boolean hovered = isHovered(mouseX, mouseY);
        if (hovered) text = text.copy().withStyle(ChatFormatting.UNDERLINE);

        SafeDrawer.drawString(context, textRenderer, text, getX(), getY() + yOffset,
                hovered ? Theme.hover() : Theme.text(), false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (value == null || !isHovered(mouseX, mouseY)) return;

        Enum<?>[] constants = value.getDeclaringClass().getEnumConstants();
        value = constants[(value.ordinal() + 1) % constants.length];
    }

    private boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth()
                && mouseY >= getY() && mouseY <= getY() + getHeight();
    }
}
