package com.tiji.spotify_controller;

import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.util.TextUtils;
import com.tiji.spotify_controller.util.Theme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class SongToast implements Toast {
    private static final int TITLE_Y = 6;
    private static final int ARTIST_Y = 18;
    private static final int TOAST_WIDTH = Toast.DEFAULT_WIDTH;
    private static final int TOAST_HEIGHT = 32;
    private static final int MARGIN = 5;
    private static final int IMAGE_WIDTH = TOAST_HEIGHT;
    private static final long DISPLAY_DURATION_MS = 3000L;
    private static final int TEXT_WIDTH = TOAST_WIDTH - MARGIN*2 - IMAGE_WIDTH;

    private final ImageWithColor cover;
    private final Component artist;
    private final Component title;
    private Toast.Visibility visibility;

    public SongToast(ImageWithColor cover, String artist, Component title) {
        this.cover = cover;
        this.artist = TextUtils.getTrantedText(Component.nullToEmpty(artist), TEXT_WIDTH);
        this.title =  TextUtils.getTrantedText(title          , TEXT_WIDTH);

        this.visibility = Visibility.HIDE;
    }

    public void show(ToastManager manager) {
        manager.addToast(this);
    }

    @Override
    public Visibility getWantedVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        this.visibility = DISPLAY_DURATION_MS * manager.getNotificationDisplayTimeMultiplier() <= time
                ? Visibility.HIDE
                : Visibility.SHOW;
    }

    @Override
    //#if MC<26100
    public void render(GuiGraphics context, Font textRenderer, long startTime)
    //#else
    //$$ public void extractRenderState(GuiGraphicsExtractor context, Font textRenderer, long startTime)
    //#endif
    {
        boolean album = Theme.usesAlbumColor();
        int albumLabel = cover.shouldUseDarkUI ? CommonColors.WHITE : CommonColors.BLACK;

        context.fill(0, 0, TOAST_WIDTH, TOAST_HEIGHT, album ? cover.color : Theme.background());

        SafeDrawer.drawString(context, textRenderer, title , IMAGE_WIDTH + MARGIN, TITLE_Y , album ? albumLabel : Theme.text(), false);
        SafeDrawer.drawString(context, textRenderer, artist, IMAGE_WIDTH + MARGIN, ARTIST_Y, album ? albumLabel : Theme.secondaryText(), false);

        SafeDrawer.drawImage(context, cover.getImage(), 0, 0, 0, 0, IMAGE_WIDTH, TOAST_HEIGHT);

        if (!album) SafeDrawer.drawOutline(context, 0, 0, TOAST_WIDTH, TOAST_HEIGHT, Theme.accent());
    }
}
