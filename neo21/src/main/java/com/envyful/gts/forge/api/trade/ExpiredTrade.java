package com.envyful.gts.forge.api.trade;

import com.envyful.gts.forge.api.TradeOffer;

public record ExpiredTrade(TradeOffer offer) implements Trade {
}
