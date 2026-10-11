package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.util.Theme;
import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import java.util.ArrayList;

import com.tiji.spotify_controller.util.SafeScreenUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

public class SecondaryBaseScreen extends BaseScreen {
    private static final int IMAGE_SIZE = 30;
    private static final int MARGIN = 10;
    private static final int TITLE_Y = 26;
    private static final int ARTIST_Y = 11;

    protected static final int INFO_HEIGHT = MARGIN*2 + IMAGE_SIZE;

    private final ArrayList<Renderable> drawables = new ArrayList<>();

    public SecondaryBaseScreen() {
        super(false);
    }

    @Override
    protected int panelWidth() {
        return 300;
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();

        drawables.clear();
    }

    @Override
    public void safeRender(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.safeRender(context, mouseX, mouseY, delta);

        // Playback info
        ImageWithColor cover = Main.currentlyPlaying.coverImage;
        SafeDrawer.drawImage(
                context,
                cover.getImage(),
                px() + MARGIN, sh() - IMAGE_SIZE - MARGIN,
                0, 0,
                IMAGE_SIZE, IMAGE_SIZE
        );
        int nextX = (int) (px() + MARGIN*1.5 + IMAGE_SIZE + 3);

        SafeDrawer.drawString(context, 
                font,
                Main.currentlyPlaying.title,
                nextX, sh() - (MARGIN + TITLE_Y),
                Theme.text(), false
        ); // title
        SafeDrawer.drawString(context, 
                font,
                Main.currentlyPlaying.artist,
                nextX, sh() - (MARGIN + ARTIST_Y),
                Theme.secondaryText(), false
        ); // artist

        SafeDrawer.enableScissor(context, 0, 0, sw(), sh() - INFO_HEIGHT);
        //#if MC<26100
        drawables.forEach(drawable -> drawable.render(context, mouseX, mouseY, delta));
        //#else
        //$$ drawables.forEach(drawable -> drawable.extractRenderState(context, mouseX, mouseY, delta));
        //#endif
        context.disableScissor();
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        SafeScreenUtils.setScreen(minecraft, Main.nowPlayingScreen);
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(T drawableElement) {
        super.addWidget(drawableElement);
        drawables.add(drawableElement);
        return drawableElement;
    }

    public void songChangeCallback() {}
}
