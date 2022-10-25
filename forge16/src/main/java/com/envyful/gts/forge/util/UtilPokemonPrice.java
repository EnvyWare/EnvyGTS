package com.envyful.gts.forge.util;

import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.PokeSpecPricing;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.util.Comparator;
import java.util.List;

public class UtilPokemonPrice {

    public static double getMinPrice(Pokemon pokemon) {
        double defaultPrice = EnvyGTSForge.getInstance().getConfig().getMinPokemonPrice();
        List<PokeSpecPricing> applicable = Lists.newArrayList();

        for (PokeSpecPricing minPriceModifier : EnvyGTSForge.getInstance().getConfig().getMinPriceModifiers()) {
            if (minPriceModifier.getSpec().matches(pokemon)) {
                applicable.add(minPriceModifier);
            }
        }

        applicable.sort(Comparator.comparing(PokeSpecPricing::getPriority));

        for (PokeSpecPricing pokeSpecPricing : applicable) {
            defaultPrice = pokeSpecPricing.apply(defaultPrice);
        }

        return defaultPrice;
    }

}
