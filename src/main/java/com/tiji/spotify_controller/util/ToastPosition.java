package com.tiji.spotify_controller.util;

public enum ToastPosition {
    TOP_RIGHT,
    TOP_LEFT,
    BOTTOM_RIGHT,
    BOTTOM_LEFT;

    public boolean isLeft() {
        return this == TOP_LEFT || this == BOTTOM_LEFT;
    }

    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
    }
}
