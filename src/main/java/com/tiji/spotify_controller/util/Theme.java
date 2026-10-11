package com.tiji.spotify_controller.util;

import com.tiji.spotify_controller.util.Theme;
import com.tiji.spotify_controller.Main;

public final class Theme {
    // Desert Night palette
    public static final int DESERT_BG     = 0xFF24221C;
    public static final int DESERT_FG     = 0xFFD4B07B;
    public static final int DESERT_MUTED  = 0xFF87765D;
    public static final int DESERT_ACCENT = 0xFFE5A440;

    public static final String DEFAULT_CUSTOM_BACKGROUND = "24221c";
    public static final String DEFAULT_CUSTOM_TEXT       = "d4b07b";
    public static final String DEFAULT_CUSTOM_ACCENT     = "e5a440";

    // Original look used with the ALBUM scheme
    private static final int ALBUM_TEXT  = 0xFFFFFFFF;
    private static final int ALBUM_MUTED = 0x77FFFFFF;
    private static final int ALBUM_HOVER = 0xFFAAAAAA;

    private Theme() {}

    public static ColorScheme scheme() {
        ColorScheme scheme = Main.CONFIG.colorScheme();
        return scheme == null ? ColorScheme.DESERT_NIGHT : scheme;
    }

    public static boolean usesAlbumColor() {
        return scheme() == ColorScheme.ALBUM;
    }

    public static int background() {
        return switch (scheme()) {
            case CUSTOM -> parse(Main.CONFIG.customBackground(), DESERT_BG);
            default -> DESERT_BG;
        };
    }

    public static int text() {
        return switch (scheme()) {
            case ALBUM -> ALBUM_TEXT;
            case CUSTOM -> parse(Main.CONFIG.customText(), DESERT_FG);
            default -> DESERT_FG;
        };
    }

    public static int muted() {
        return switch (scheme()) {
            case ALBUM -> ALBUM_MUTED;
            case CUSTOM -> mix(text(), background(), 0.55f);
            default -> DESERT_MUTED;
        };
    }

    // Artist names and other second-line text
    public static int secondaryText() {
        return usesAlbumColor() ? ALBUM_TEXT : muted();
    }

    public static int accent() {
        return switch (scheme()) {
            case ALBUM -> ALBUM_TEXT;
            case CUSTOM -> parse(Main.CONFIG.customAccent(), DESERT_ACCENT);
            default -> DESERT_ACCENT;
        };
    }

    public static int hover() {
        return usesAlbumColor() ? ALBUM_HOVER : accent();
    }

    // Colour the side panel gradient is tinted with
    public static int glow(ImageWithColor cover) {
        if (usesAlbumColor()) return cover.color;

        int color = background();
        //#if MC<12102
        //$$ color = swapRedBlue(color);
        //#endif
        return color;
    }

    // Same colour with a different alpha (0-255)
    public static int withAlpha(int color, int alpha) {
        return (alpha & 0xFF) << 24 | (color & 0x00FFFFFF);
    }

    public static int swapRedBlue(int color) {
        return (color & 0xFF00FF00) | ((color & 0x00FF0000) >> 16) | ((color & 0x000000FF) << 16);
    }

    // Accepts "rrggbb" or "#rrggbb"; returns fallback when invalid
    public static int parse(String hex, int fallback) {
        if (hex == null) return fallback;
        String value = hex.trim();
        if (value.startsWith("#")) value = value.substring(1);
        if (value.length() != 6) return fallback;
        try {
            return 0xFF000000 | Integer.parseInt(value, 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    // Blend a towards b; t is how much of a to keep
    public static int mix(int a, int b, float t) {
        int r = Math.round(((a >> 16) & 0xFF) * t + ((b >> 16) & 0xFF) * (1 - t));
        int g = Math.round(((a >> 8) & 0xFF) * t + ((b >> 8) & 0xFF) * (1 - t));
        int bl = Math.round((a & 0xFF) * t + (b & 0xFF) * (1 - t));
        return 0xFF000000 | r << 16 | g << 8 | bl;
    }
}
