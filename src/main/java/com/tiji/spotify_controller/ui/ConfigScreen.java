package com.tiji.spotify_controller.ui;

import com.tiji.spotify_controller.DiagnosticData;
import com.tiji.spotify_controller.Main;
import com.tiji.spotify_controller.SpotifyControllerConfig;
import com.tiji.spotify_controller.WebGuideServer;
import com.tiji.spotify_controller.api.SpotifyApi;
import com.tiji.spotify_controller.util.SafeScreenUtils;
import com.tiji.spotify_controller.widgets.BorderedButtonWidget;
import com.tiji.spotify_controller.widgets.LabelWidget;
import com.tiji.spotify_controller.widgets.ValueHolder;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends BaseScreen {
    private enum ResetConfirmStatus {
        IDLE(Component.translatable("ui.spotify_controller.reset_config")),
        CONFIRM(Component.translatable("ui.spotify_controller.reset_config_confirm")),
        CONFIRMED(Component.translatable("ui.spotify_controller.reset_config_success"));

        public final Component text;
        ResetConfirmStatus(Component text) {
            this.text = text;
        }
    }

    private final Screen parent;
    private LabelWidget userNameWidget;
    private BorderedButtonWidget resetButton;
    private ResetConfirmStatus resetConfirmStatus = ResetConfirmStatus.IDLE;

    private String userName;

    private static final int WIDTH = 200;
    private static final int MARGIN = 10;
    private static final int FIELD_HEIGHT = 20;

    private final HashMap<Field, ValueHolder> map = new HashMap<>();

    private static final int SCROLL_SPEED = 20;
    private int scroll = 0;
    private int contentHeight = 0;

    public ConfigScreen(Screen parent) {
        super(true);

        this.parent = parent;
        if (!Main.isNotSetup()) {
            SpotifyApi.getUserName(name -> {
                userName = name;
                if (userNameWidget != null) {
                    userNameWidget.setText(Component.translatable("ui.spotify_controller.status.setup", userName));
                }
            });
        }
    }

    // Always on the left so the options don't move while changing the side
    @Override
    protected boolean useRightSide() {
        return false;
    }

    // Fixed size so the options stay usable whatever the panel size is set to
    @Override
    protected float uiScale() {
        return 1f;
    }

    @Override
    protected int glowWidth() {
        return WIDTH*3 + MARGIN*4;
    }

    public void init() {
        super.init();

        int y = MARGIN;

        Component statusText;
        if (Main.isNotSetup()) {
            statusText = Component.translatable("ui.spotify_controller.status.not_setup");
        } else if (userName == null) {
            statusText = Component.translatable("ui.spotify_controller.loading");
        } else {
            statusText = Component.translatable("ui.spotify_controller.status.setup", userName);
        }
        userNameWidget = new LabelWidget(MARGIN + widgetsOffset, y, statusText);
        addRenderableWidget(userNameWidget);
        y += font.lineHeight + MARGIN;

        resetButton = new BorderedButtonWidget(resetConfirmStatus.text, MARGIN + widgetsOffset, y, this::onResetButtonPress, false, WIDTH);
        addRenderableWidget(resetButton);
        y += resetButton.getHeight() + MARGIN*3;

        BorderedButtonWidget dumpButton = new BorderedButtonWidget(Component.translatable("ui.spotify_controller.dump_diagnostic")
                , MARGIN + widgetsOffset, y, DiagnosticData::dump, false, WIDTH);
        addRenderableWidget(dumpButton);
        y += resetButton.getHeight() + MARGIN*3;

        // Column 0 continues below the buttons, column 1 starts at the top
        int[] columnY = {y, MARGIN};

        // Column 1 starts after the widest column 0 label so long labels don't overlap it
        int firstColumnWidth = WIDTH;
        for (Field field : SpotifyControllerConfig.class.getDeclaredFields()) {
            SpotifyControllerConfig.EditableField metadata = field.getAnnotation(SpotifyControllerConfig.EditableField.class);
            if (metadata == null || metadata.column() != 0) continue;
            firstColumnWidth = Math.max(firstColumnWidth, font.width(Component.translatable(metadata.translationKey())));
        }

        for (Field field : SpotifyControllerConfig.class.getDeclaredFields()) {
            SpotifyControllerConfig.EditableField metadata = field.getAnnotation(SpotifyControllerConfig.EditableField.class);
            if (metadata == null) continue;

            int column = Math.clamp(metadata.column(), 0, columnY.length - 1);
            int x = MARGIN + widgetsOffset + column * (firstColumnWidth + MARGIN*3);
            y = columnY[column];

            try {
                ValueHolder widget = metadata.widget()
                        .getConstructor(int.class, int.class, int.class, int.class)
                        .newInstance(x, y + font.lineHeight, WIDTH, FIELD_HEIGHT);

                field.setAccessible(true);
                widget.setValue(field.get(Main.CONFIG));

                addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) widget);

                addRenderableWidget(new LabelWidget(x, y, Component.translatable(metadata.translationKey())));

                map.put(field, widget);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            columnY[column] = y + FIELD_HEIGHT + MARGIN + font.lineHeight;
        }

        // init() rebuilds widgets at their unscrolled positions
        contentHeight = Math.max(columnY[0], columnY[1]);
        int previous = scroll;
        scroll = 0;
        scrollBy(previous);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        scrollBy((int) (verticalAmount * SCROLL_SPEED));
        return true;
    }

    // Positive amounts move the content down
    private void scrollBy(int amount) {
        int min = Math.min(0, sh() - contentHeight);
        int next = Math.clamp(scroll + amount, min, 0);
        int delta = next - scroll;
        scroll = next;

        for (GuiEventListener child : children()) {
            if (child instanceof LayoutElement widget) {
                widget.setY(widget.getY() + delta);
            }
        }
    }

    private void onResetButtonPress() {
        if (Main.isNotSetup()) return;

        if (resetConfirmStatus == ResetConfirmStatus.IDLE) {
            resetConfirmStatus = ResetConfirmStatus.CONFIRM;
        } else if (resetConfirmStatus == ResetConfirmStatus.CONFIRM) {
            resetConfirmStatus = ResetConfirmStatus.CONFIRMED;
            Main.CONFIG.resetConnection();
            WebGuideServer.start();
        }
        resetButton.setLabel(resetConfirmStatus.text);
    }

    public void onClose() {
        for (Field field : map.keySet()) {
            field.setAccessible(true);
            try {
                field.set(Main.CONFIG, map.get(field).getValue_());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Main.CONFIG.writeToFile();

        SafeScreenUtils.setScreen(Minecraft.getInstance(), parent);
    }
}
