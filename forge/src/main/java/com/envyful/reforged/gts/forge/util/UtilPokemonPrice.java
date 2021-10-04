package com.envyful.reforged.gts.forge.util;

import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.config.PokeSpecPricing;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

public class UtilPokemonPrice {

    public static double getMinPrice(Pokemon pokemon) {
        double defaultPrice = ReforgedGTSForge.getInstance().getConfig().getMinPokemonPrice();

        for (PokeSpecPricing minPriceModifier : ReforgedGTSForge.getInstance().getConfig().getMinPriceModifiers()) {
            if (minPriceModifier.getSpec().matches(pokemon)) {
                defaultPrice = minPriceModifier.apply(defaultPrice);
            }
        }

        return defaultPrice;
    }

}
