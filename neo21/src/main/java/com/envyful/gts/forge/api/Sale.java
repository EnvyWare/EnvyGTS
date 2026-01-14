package com.envyful.gts.forge.api;

import com.envyful.gts.forge.api.player.PlayerInfo;

import java.time.Instant;
import java.util.UUID;

public record Sale(UUID saleId, UUID offerId, PlayerInfo buyer, Instant time, double purchasePrice) {



}
