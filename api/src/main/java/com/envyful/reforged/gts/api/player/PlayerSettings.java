package com.envyful.reforged.gts.api.player;

public class PlayerSettings {

    private boolean toggledBroadcasts = false;

    public PlayerSettings() {}

    public boolean isToggledBroadcasts() {
        return this.toggledBroadcasts;
    }

    public void setToggledBroadcasts(boolean toggledBroadcasts) {
        this.toggledBroadcasts = toggledBroadcasts;
    }
}
