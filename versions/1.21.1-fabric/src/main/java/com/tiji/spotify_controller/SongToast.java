package com.tiji.spotify_controller;

import com.tiji.spotify_controller.util.ImageWithColor;
import com.tiji.spotify_controller.util.SafeDrawer;
import com.tiji.spotify_controller.util.TextUtils;
import com.tiji.spotify_controller.util.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class SongToast implements Toast {
    private static final int TITLE_Y = 6;
    private static final int ARTIST_Y = 18;
    private static final int TOAST_WIDTH = 160; // Hard-coded until 1.21.2
    private static final int TOAST_HEIGHT = 32;
    private static final int MARGIN = 5;
    private static final int IMAGE_WIDTH = TOAST_HEIGHT;
    private static final long DISPLAY_DURATION_MS = 3000L;
    private static final int TEXT_WIDTH = TOAST_WIDTH - MARGIN*2 - IMAGE_WIDTH;

    private final ImageWithColor cover;
    private final Component artist;
    private final Component title;

    public SongToast(ImageWithColor cover, String artist, Component title) {
        this.cover = cover;
        this.artist = TextUtils.getTrantedText(Component.nullToEmpty(artist), TEXT_WIDTH);
        this.title =  TextUtils.getTrantedText(title          , TEXT_WIDTH);
    }

    public void show(ToastComponent manager) {
        manager.addToast(this);
    }

    @Override
    public Visibility render(GuiGraphics context, ToastComponent manager, long timePast) {
        Font textRenderer = Minecraft.getInstance().font;

        boolean album = Theme.usesAlbumColor();
        int albumLabel = cover.shouldUseDarkUI ? CommonColors.WHITE : CommonColors.BLACK;

        context.fill(0, 0, TOAST_WIDTH, TOAST_HEIGHT, album ? fixIncapableColor(cover.color) : Theme.background());

        SafeDrawer.drawString(context, textRenderer, title , IMAGE_WIDTH + MARGIN, TITLE_Y , album ? albumLabel : Theme.text(), false);
        SafeDrawer.drawString(context, textRenderer, artist, IMAGE_WIDTH + MARGIN, ARTIST_Y, album ? albumLabel : Theme.secondaryText(), false);

        context.blit(cover.getImage(), 0, 0, 0, 0, IMAGE_WIDTH, TOAST_HEIGHT, IMAGE_WIDTH, TOAST_HEIGHT);

        if (!album) SafeDrawer.drawOutline(context, 0, 0, TOAST_WIDTH, TOAST_HEIGHT, Theme.accent());

        return DISPLAY_DURATION_MS * manager.getNotificationDisplayTimeMultiplier() <= timePast
                ? Visibility.HIDE
                : Visibility.SHOW;
    }

    private static int fixIncapableColor(int color) {
        return (color & 0xFF00FF00) | ((color & 0x00FF0000) >> 16) | ((color & 0x000000FF) << 16);
    }
}