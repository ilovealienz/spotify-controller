package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.util.PanelSide;
import com.tiji.spotify_controller.util.SafeScreenUtils;
import com.tiji.spotify_controller.util.Theme;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BaseScreen extends SafeAbstractScreen {
    protected long startTime = Util.getMillis();
    protected int widgetsOffset;
    protected static final int ANIMATION_AMOUNT = 100;
    private static final int animationTime = 250;
    private static final int GLOW_WIDTH = 255;
    protected final boolean rightSide;

    public BaseScreen(boolean animate) {
        super(Component.nullToEmpty(""));

        rightSide = useRightSide();
        widgetsOffset = slideDirection() * ANIMATION_AMOUNT;

        if (!animate) {
            startTime -= animationTime;
            widgetsOffset = 0;
        }
    }

    protected boolean useRightSide() {
        return Main.CONFIG.panelSide() == PanelSide.RIGHT;
    }

    // -1 slides in from the left edge, 1 from the right edge
    private int slideDirection() {
        return rightSide ? 1 : -1;
    }

    // Width of the panel content, used to anchor it to the right edge
    protected int panelWidth() {
        return 220;
    }

    protected int glowWidth() {
        return GLOW_WIDTH;
    }

    // Left edge of the panel without the slide animation offset
    protected int panelX() {
        return rightSide ? width - panelWidth() : 0;
    }

    // Left edge of the panel including the slide animation offset
    protected int px() {
        return panelX() + widgetsOffset;
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        //#if MC<=12106
        super.renderBackground(context, mouseX, mouseY, delta);
        //#endif

        long totalTime = Util.getMillis() - startTime;
        float normalized = (float) Math.min(totalTime, animationTime) / animationTime;
        int previousOffset = widgetsOffset;

        // Glow
        ImageWithColor cover = Main.currentlyPlaying.coverImage;
        int color = Theme.glow(cover);

        SafeDrawer.drawImage(
                context,
                ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, rightSide ? "ui/gradient_right.png" : "ui/gradient.png"),
                (rightSide ? width - glowWidth() : 0) + widgetsOffset, 0,
                0, 0,
                glowWidth(), height,
                GLOW_WIDTH, 1,
                GLOW_WIDTH, 1,
                color
        );

        widgetsOffset = (int) (slideDirection() * (ANIMATION_AMOUNT - easeInOut(normalized) * ANIMATION_AMOUNT));

        for (GuiEventListener child : children()) {
            if (child instanceof LayoutElement widget) {
                widget.setX(widget.getX() + (widgetsOffset - previousOffset));
            }
        }

        //#if MC<26100
        renderables.forEach(it -> it.render(context, mouseX, mouseY, delta));
        //#else
        //$$ renderables.forEach(it -> it.extractRenderState(context, mouseX, mouseY, delta));
        //#endif
    }

    private float easeInOut(float t) {
        return t * t * (3 - 2 * t);
    }

    @Override
    public boolean safeKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyPressedSuper(keyCode, scanCode, modifiers)) return true;

        boolean handled = Main.SETUP_KEY
                //#if MC<=12108
                .matches(keyCode, scanCode);
                //#else
                //$$ .matches(convertToKeyEvent(keyCode, scanCode, modifiers));
                //#endif

        if (handled) {
            assert minecraft != null;
            SafeScreenUtils.setScreen(minecraft, null);
            return true;
        }
        return false;
    }
}