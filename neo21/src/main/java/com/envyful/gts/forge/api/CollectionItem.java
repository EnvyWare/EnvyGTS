package com.envyful.gts.forge.api;

import com.envyful.gts.forge.api.trade.Trade;

import java.util.UUID;

public record CollectionItem(TradeOffer offer, Sale sale) {

    public CollectionItem(Trade trade, Sale sale) {
        this(trade.offer(), sale);
    }

    public UUID getId() {
        return this.offer().id();
    }
}
