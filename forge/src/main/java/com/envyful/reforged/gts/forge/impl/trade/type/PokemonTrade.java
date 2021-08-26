package com.envyful.reforged.gts.forge.impl.trade.type;

import com.envyful.api.json.UtilGson;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.gui.FilterType;
import com.envyful.reforged.gts.api.gui.SortType;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

/**
 *
 * Represents a pokemon {@link Trade} in the GTS
 *
 */
public class PokemonTrade extends ForgeTrade {

    private final Pokemon pokemon;

    public PokemonTrade(UUID owner, double cost, long expiry, FilterType type, Pokemon pokemon) {
        super(owner, cost, expiry, type);

        this.pokemon = pokemon;
    }

    @Override
    public boolean attemptPurchase(EnvyPlayer<?> player) {
        return false; //TODO
    }

    @Override
    public void collect(EnvyPlayer<?> player) {
        UtilPixelmonPlayer.getParty((EntityPlayerMP) player.getParent()).add(this.pokemon);
    }

    @Override
    public void adminRemove(EnvyPlayer<?> admin) {
        UtilPixelmonPlayer.getParty((EntityPlayerMP) admin.getParent()).add(this.pokemon);
    }

    @Override
    public int compare(Trade other, SortType type) {
        return 0;
    }

    @Override
    public void delete() {
        //TODO:
    }

    @Override
    public void save() {
        //TODO:
    }

    public static class Builder extends ForgeTrade.Builder {

        private Pokemon pokemon = null;

        public Builder() {}

        @Override
        public Builder owner(EnvyPlayer<?> player) {
            return (Builder) super.owner(player);
        }

        @Override
        public Builder owner(UUID owner) {
            return (Builder) super.owner(owner);
        }

        @Override
        public Builder cost(double cost) {
            return (Builder) super.cost(cost);
        }

        @Override
        public Builder expiry(long expiry) {
            return (Builder) super.expiry(expiry);
        }

        @Override
        public Builder type(FilterType type) {
            return (Builder) super.type(type);
        }

        @Override
        public Builder content(String type) {
            return (Builder) super.content(type);
        }

        @Override
        public Builder contents(String contents) {
            return this.contents(UtilGson.GSON.fromJson(contents, Pokemon.class));
        }

        public Builder contents(Pokemon pokemon) {
            this.pokemon = pokemon;
            return this;
        }

        @Override
        public PokemonTrade build() {
            if (this.pokemon == null) {
                return null;
            }

            return new PokemonTrade(this.owner, this.cost, this.expiry, this.type, this.pokemon);
        }
    }
}
