package com.envyful.gts.forge.api.trade;

import com.envyful.gts.forge.api.Sale;
import com.envyful.gts.forge.api.TradeOffer;

public record SoldTrade(TradeOffer offer, Sale sale) implements Trade {
}
