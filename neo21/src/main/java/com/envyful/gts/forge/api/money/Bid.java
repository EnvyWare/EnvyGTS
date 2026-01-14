package com.envyful.gts.forge.api.money;

import com.envyful.gts.forge.api.player.PlayerInfo;

import java.time.Instant;

public record Bid(PlayerInfo bidder, double amount, Instant time) {
}
