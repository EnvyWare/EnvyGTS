package com.envyful.gts.forge.player;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.neoforge.player.attribute.ManagedForgeAttribute;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.attribute.adapter.SelfAttributeAdapter;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.CollectionItem;
import com.envyful.gts.forge.api.player.PlayerSettings;
import com.envyful.gts.forge.api.trade.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GTSAttribute extends ManagedForgeAttribute<EnvyGTSForge> implements SelfAttributeAdapter {

    private List<CollectionItem> collections = new ArrayList<>();

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
        return EnvyGTSForge.getTradeService().userListings(this.parent);
    }

    public List<CollectionItem> getCollections() {
        return List.copyOf(this.collections);
    }

    public void removeCollectionItem(CollectionItem item) {
        this.collections.remove(item);
    }

    public void addCollectionItem(CollectionItem item) {
        this.collections.add(item);
    }

    public boolean hasReachedMaximumTrades() {
        var ownedTrades = this.getOwnedTrades();

        return ownedTrades.size() >= EnvyGTSForge.getConfig().getMaxListingsPerUser();
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

            //TODO: check collections

            if (returnMessage) {
                this.parent.message(EnvyGTSForge.getLocale().getMessages().getItemsToClaim());
            }
        });
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }
}
