package com.envyful.gts.forge.api.trade;

import com.envyful.gts.forge.api.RemovalInfo;
import com.envyful.gts.forge.api.TradeOffer;

public record RemovedTrade(TradeOffer offer, RemovalInfo removalInfo) implements Trade {

}
