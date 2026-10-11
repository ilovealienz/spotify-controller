package com.tiji.spotify_controller;

import com.google.gson.Gson;
import com.tiji.spotify_controller.util.ColorScheme;
import com.tiji.spotify_controller.util.PanelSide;
import com.tiji.spotify_controller.util.Theme;
import com.tiji.spotify_controller.util.ToastPosition;
import com.tiji.spotify_controller.widgets.EnumCycleWidget;
import com.tiji.spotify_controller.widgets.StringInputWidget;
import com.tiji.spotify_controller.widgets.BooleanToggleWidget;
import com.tiji.spotify_controller.widgets.IntInputWidget;
import com.tiji.spotify_controller.widgets.ValueHolder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class SpotifyControllerConfig {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface EditableField {
        String translationKey();
        Class<? extends ValueHolder> widget();
        int column() default 0;
    }

    private String clientId = "";
    private String clientSecret = "";
    private String authToken = "";
    private String refreshToken = "";
    private long lastRefresh = 0;

    @EditableField(translationKey = "ui.spotify_controller.config.show_toast"       , widget = BooleanToggleWidget.class)
    private boolean shouldShowToasts = true;

    @EditableField(translationKey = "ui.spotify_controller.config.thread_image_io"  , widget = IntInputWidget.class)
    private int imageIoThreadCount  = 4;

    @EditableField(translationKey = "ui.spotify_controller.config.brightness_weight", widget = IntInputWidget.class)
    private int brightnessFactor     = 20;

    @EditableField(translationKey = "ui.spotify_controller.config.saturation_weight", widget = IntInputWidget.class)
    private int saturationFactor     = 5;

    @EditableField(translationKey = "ui.spotify_controller.config.target_brightness", widget = IntInputWidget.class)
    private int targetBrightness     = 70;

    @EditableField(translationKey = "ui.spotify_controller.config.sample_size"      , widget = IntInputWidget.class)
    private int sampleSize           = 10;

    @EditableField(translationKey = "ui.spotify_controller.config.panel_side"       , widget = EnumCycleWidget.class, column = 1)
    private PanelSide panelSide      = PanelSide.LEFT;

    @EditableField(translationKey = "ui.spotify_controller.config.panel_scale"      , widget = IntInputWidget.class, column = 1)
    private int panelScale           = 100;

    @EditableField(translationKey = "ui.spotify_controller.config.toast_position"   , widget = EnumCycleWidget.class, column = 1)
    private ToastPosition toastPosition = ToastPosition.TOP_RIGHT;

    @EditableField(translationKey = "ui.spotify_controller.config.toast_scale"      , widget = IntInputWidget.class, column = 1)
    private int toastScale           = 100;

    @EditableField(translationKey = "ui.spotify_controller.config.toast_padding"    , widget = IntInputWidget.class, column = 1)
    private int toastPadding         = 0;

    @EditableField(translationKey = "ui.spotify_controller.config.toast_side_padding", widget = IntInputWidget.class, column = 1)
    private int toastSidePadding     = 0;

    @EditableField(translationKey = "ui.spotify_controller.config.color_scheme"     , widget = EnumCycleWidget.class, column = 1)
    private ColorScheme colorScheme  = ColorScheme.DESERT_NIGHT;

    @EditableField(translationKey = "ui.spotify_controller.config.custom_background", widget = StringInputWidget.class, column = 1)
    private String customBackground  = Theme.DEFAULT_CUSTOM_BACKGROUND;

    @EditableField(translationKey = "ui.spotify_controller.config.custom_text"      , widget = StringInputWidget.class, column = 1)
    private String customText        = Theme.DEFAULT_CUSTOM_TEXT;

    @EditableField(translationKey = "ui.spotify_controller.config.custom_accent"    , widget = StringInputWidget.class, column = 1)
    private String customAccent      = Theme.DEFAULT_CUSTOM_ACCENT;

    public String clientId() {return clientId;}
    public void clientId(String value) {clientId = value; writeToFile();}

    public String clientSecret() {return clientSecret;}
    public void clientSecret(String value) {clientSecret = value; writeToFile();}

    public String authToken() {return authToken;}
    public void authToken(String value) {authToken = value; writeToFile();}

    public String refreshToken() {return refreshToken;}
    public void refreshToken(String value) {refreshToken = value; writeToFile();}

    public long lastRefresh() {return lastRefresh;}
    public void lastRefresh(long value) {lastRefresh = value; writeToFile();}

    public boolean shouldShowToasts() {return shouldShowToasts;}
    public void shouldShowToasts(boolean value) {shouldShowToasts = value; writeToFile();}

    public int imageIoThreadCount() {return imageIoThreadCount;}
    public void imageIoThreadCount(byte value) {imageIoThreadCount = value; writeToFile();}

    public int brightnessFactor() {return brightnessFactor;}
    public void brightnessFactor(int value) {brightnessFactor = value; writeToFile();}

    public int saturationFactor() {return saturationFactor;}
    public void saturationFactor(int value) {saturationFactor = value; writeToFile();}

    public int targetBrightness() {return targetBrightness;}
    public void targetBrightness(int value) {targetBrightness = value; writeToFile();}

    public int sampleSize() {return sampleSize;}
    public void sampleSize(int value) {sampleSize = value; writeToFile();}

    public PanelSide panelSide() {return panelSide;}
    public void panelSide(PanelSide value) {panelSide = value; writeToFile();}

    // Percentages, clamped so a typo can't make the UI unusable
    public int panelScale() {return Math.clamp(panelScale, 50, 300);}
    public int toastScale() {return Math.clamp(toastScale, 50, 300);}

    public ToastPosition toastPosition() {return toastPosition;}
    public void toastPosition(ToastPosition value) {toastPosition = value; writeToFile();}

    public int toastPadding() {return toastPadding;}
    public void toastPadding(int value) {toastPadding = value; writeToFile();}

    public int toastSidePadding() {return toastSidePadding;}
    public void toastSidePadding(int value) {toastSidePadding = value; writeToFile();}

    public ColorScheme colorScheme() {return colorScheme;}
    public void colorScheme(ColorScheme value) {colorScheme = value; writeToFile();}

    public String customBackground() {return customBackground;}
    public String customText() {return customText;}
    public String customAccent() {return customAccent;}

    // Fields missing or invalid in an older config file come back as null from Gson
    private SpotifyControllerConfig fillDefaults() {
        if (panelSide == null) panelSide = PanelSide.LEFT;
        if (toastPosition == null) toastPosition = ToastPosition.TOP_RIGHT;
        if (colorScheme == null) colorScheme = ColorScheme.DESERT_NIGHT;
        if (customBackground == null) customBackground = Theme.DEFAULT_CUSTOM_BACKGROUND;
        if (customText == null) customText = Theme.DEFAULT_CUSTOM_TEXT;
        if (customAccent == null) customAccent = Theme.DEFAULT_CUSTOM_ACCENT;
        return this;
    }

    public static SpotifyControllerConfig generate() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        Path configPath = configDir.resolve("spotify_controller.json");
        Path oldPath = configDir.resolve("media.json");

        try {
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);

                return new Gson().fromJson(json, SpotifyControllerConfig.class).fillDefaults();
            } else if (Files.exists(oldPath)) {
                Files.copy(oldPath, configPath, StandardCopyOption.REPLACE_EXISTING);

                return generate();
            } else {
                SpotifyControllerConfig newInstance = new SpotifyControllerConfig();
                newInstance.writeToFile(false);
                return newInstance;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(boolean delete) {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spotify_controller.json");

        String config = new Gson().toJson(this);

        try {
            Files.writeString(configPath, config, delete ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeToFile() {
        writeToFile(true);
    }

    public void resetConnection() {
        this.lastRefresh(0);
        this.clientId("");
        this.clientSecret("");
        this.authToken("");
        this.refreshToken("");
    }

    public void invalidateAuth() {
        this.authToken("");
        this.refreshToken("");
    }

    public String constructLoginURL() {
        return "https://accounts.spotify.com/authorize?response_type=code&client_id=" +

            this.clientId() +

            "&redirect_uri=http://127.0.0.1:25566/callback&show_dialog=true&scope=user" +
            "-read-playback-state%20user-modify-playback-state%20user-read-currently-playing" +
            "%20user-read-private%20user-library-read%20user-library-modify"; // holy this thing is long
    }
}