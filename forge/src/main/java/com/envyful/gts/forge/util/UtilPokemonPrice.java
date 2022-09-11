package com.envyful.gts.forge.util;

import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.PokeSpecPricing;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

public class UtilPokemonPrice {

    public static double getMinPrice(Pokemon pokemon) {
        double defaultPrice = EnvyGTSForge.getInstance().getConfig().getMinPokemonPrice();

        for (PokeSpecPricing minPriceModifier : EnvyGTSForge.getInstance().getConfig().getMinPriceModifiers()) {
            if (minPriceModifier.getSpec().matches(pokemon)) {
                defaultPrice = minPriceModifier.apply(defaultPrice);
            }
        }

        return defaultPrice;
    }

}
