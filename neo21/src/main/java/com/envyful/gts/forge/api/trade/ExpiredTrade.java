package com.envyful.gts.forge.api.trade;

import com.envyful.gts.forge.api.TradeOffer;

import java.time.Instant;

public record ExpiredTrade(TradeOffer offer, Instant outcomeTime) implements Trade {

    public ExpiredTrade(TradeOffer offer) {
        this(offer, offer.expiryTime());
    }
}
