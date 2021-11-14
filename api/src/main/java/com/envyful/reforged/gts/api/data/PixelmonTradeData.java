package com.envyful.reforged.gts.api.data;

import com.envyful.reforged.gts.api.TradeData;

import java.util.UUID;

/**
 *
 * Data representing a Pixelmon trade
 *
 */
public class PixelmonTradeData extends TradeData {

    private final String spec;

    public PixelmonTradeData(UUID uuid, String name, long expiry, String spec) {
        super(uuid, name, expiry);

        this.spec = spec;
    }

    public String getSpec() {
        return this.spec;
    }
}
