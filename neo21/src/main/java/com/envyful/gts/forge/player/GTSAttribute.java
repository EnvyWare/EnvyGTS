package com.envyful.gts.forge.player;

import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.neoforge.player.attribute.ManagedForgeAttribute;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.player.PlayerSettings;
import com.envyful.gts.forge.EnvyGTSForge;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

public class GTSAttribute extends ManagedForgeAttribute<EnvyGTSForge> {

    protected List<Trade> ownedTrades = Lists.newArrayList();
    protected int selectedSlot = -1;
    protected double currentPrice = -1;
    protected double currentMinPrice = -1;
    protected long currentDuration = -1;
    protected PlayerSettings settings = new PlayerSettings();
    protected String name;

    public GTSAttribute(UUID id) {
        super(id, EnvyGTSForge.getInstance());
    }

    public List<Trade> getOwnedTrades() {
        return this.ownedTrades;
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public double getCurrentPrice() {
        return this.currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public long getCurrentDuration() {
        return this.currentDuration;
    }

    public void setCurrentDuration(long currentDuration) {
        this.currentDuration = currentDuration;
    }

    public double getCurrentMinPrice() {
        return this.currentMinPrice;
    }

    public void setCurrentMinPrice(double currentMinPrice) {
        this.currentMinPrice = currentMinPrice;
    }

    public PlayerSettings getSettings() {
        return this.settings;
    }

    @Override
    public void setParent(ForgeEnvyPlayer parent) {
        super.setParent(parent);

        this.name = parent.getName();

        PlatformProxy.runSync(() -> {
            boolean returnMessage = false;

            for (var ownedTrade : Lists.newArrayList(this.ownedTrades)) {
                if (ownedTrade.hasExpired() || ownedTrade.wasPurchased() || ownedTrade.wasRemoved()) {
                    returnMessage = true;
                    ownedTrade.collect(this.parent, null);
                    this.ownedTrades.remove(ownedTrade);
                }
            }

            if (returnMessage) {
                this.parent.message(UtilChatColour.colour(EnvyGTSForge.getLocale().getMessages().getItemsToClaim()));
            }
        });
    }
}
