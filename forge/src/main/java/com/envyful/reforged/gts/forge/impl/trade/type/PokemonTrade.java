package com.envyful.reforged.gts.forge.impl.trade.type;

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
}
