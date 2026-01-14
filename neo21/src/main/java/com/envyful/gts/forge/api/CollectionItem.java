package com.envyful.gts.forge.api;

public class CollectionItem {

    private final TradeOffer offer;
    private final Sale sale;

    private boolean collected;

    public CollectionItem(TradeOffer offer, Sale sale, boolean collected) {
        this.offer = offer;
        this.sale = sale;
        this.collected = collected;
    }

    public TradeOffer getOffer() {
        return this.offer;
    }

    public Sale getSale() {
        return this.sale;
    }

    public boolean isCollected() {
        return this.collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}
