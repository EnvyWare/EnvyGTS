package com.envyful.reforged.gts.forge.impl.trade;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.gui.FilterType;
import com.envyful.reforged.gts.forge.impl.trade.type.ItemTrade;
import com.envyful.reforged.gts.forge.impl.trade.type.PokemonTrade;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * Abstract implementation of the {@link Trade} interface covering the basics
 *
 */
public abstract class ForgeTrade implements Trade {

    protected final UUID owner;
    protected final double cost;
    protected final long expiry;

    protected ForgeTrade(UUID owner, double cost, long expiry) {
        this.owner = owner;
        this.cost = cost;
        this.expiry = expiry;
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return Objects.equals(this.owner, uuid);
    }

    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() >= this.expiry;
    }

    @Override
    public boolean filter(EnvyPlayer<?> filterer, FilterType filterType) {
        return filterType.isAllowed(filterer, this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        protected UUID owner = null;
        protected double cost = -1;
        protected long expiry = -1;

        protected Builder() {}

        public Builder owner(EnvyPlayer<?> player) {
            return this.owner(player.getUuid());
        }

        public Builder owner(UUID owner) {
            this.owner = owner;
            return this;
        }

        public Builder cost(double cost) {
            this.cost = cost;
            return this;
        }

        public Builder expiry(long expiry) {
            this.expiry = expiry;
            return this;
        }

        public Builder content(String type) {
            Builder builder = null;

            switch (type.toLowerCase()) {
                case "p":
                    builder = new PokemonTrade.Builder();
                    break;
                case "i":
                default:
                    builder = new ItemTrade.Builder();
                    break;
            }

            if (this.owner != null) {
                builder.owner(this.owner);
            }

            if (this.cost != -1) {
                builder.cost(this.cost);
            }

            if (this.expiry != -1) {
                builder.expiry(expiry);
            }

            return builder;
        }

        public Builder contents(String contents) {
            return this;
        }

        public Trade build() {
            return null;
        }
    }
}
