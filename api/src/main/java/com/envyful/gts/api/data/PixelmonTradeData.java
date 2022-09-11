package com.envyful.gts.api.data;

import com.envyful.gts.api.TradeData;

import java.util.UUID;

/**
 *
 * Data representing a Pixelmon trade
 *
 */
public class PixelmonTradeData extends TradeData {

    private final String nbt;

    public PixelmonTradeData(UUID uuid, String name, long expiry, String nbt) {
        super(uuid, name, expiry);

        this.nbt = nbt;
    }

    public String getNbt() {
        return this.nbt;
    }
}
