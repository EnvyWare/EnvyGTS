package com.envyful.gts.forge.util;

import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.PokeSpecPricing;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UtilPokemonPrice {

    public static double getMinPrice(Pokemon pokemon) {
        var defaultPrice = EnvyGTSForge.getConfig().getMinPokemonPrice();
        List<PokeSpecPricing> applicable = new ArrayList<>();

        for (var minPriceModifier : EnvyGTSForge.getConfig().getMinPriceModifiers()) {
            if (minPriceModifier.getSpec().matches(pokemon)) {
                applicable.add(minPriceModifier);
            }
        }

        applicable.sort(Comparator.comparing(PokeSpecPricing::getPriority));

        for (var pokeSpecPricing : applicable) {
            defaultPrice = pokeSpecPricing.apply(defaultPrice);
        }

        return defaultPrice;
    }

}
