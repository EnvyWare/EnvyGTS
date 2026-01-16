package com.envyful.gts.forge.api;

import com.envyful.gts.forge.api.player.PlayerInfo;

import java.time.Instant;

public record RemovalInfo(PlayerInfo remover, Instant time, String reason) {
}
