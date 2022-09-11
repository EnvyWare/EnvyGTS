package com.envyful.gts.forge.config;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PokeSpecPricing {

    private String spec;
    private MathHandler minPrice;

    private transient PokemonSpec cachedSpec = null;

    public PokeSpecPricing(String spec, MathHandler minPrice) {
        this.spec = spec;
        this.minPrice = minPrice;
    }

    public PokeSpecPricing() {
    }

    public PokemonSpec getSpec() {
        if (this.cachedSpec == null) {
            this.cachedSpec = new PokemonSpec(this.spec);
        }

        return this.cachedSpec;
    }

    public double apply(double currentValue) {
        switch (this.minPrice.getType().toLowerCase()) {
            default : case "add" : case "addition" : case "+" :
                return currentValue + this.minPrice.getValue();
            case "subtract" : case "subtraction" : case "-" :
                return currentValue - this.minPrice.getValue();
            case "multiply" : case "multiplication" : case "*" :
                return currentValue * this.minPrice.getValue();
            case "divide" : case "division" : case "/" :
                return currentValue / Math.max(0.00001, this.minPrice.getValue());
        }
    }

    @ConfigSerializable
    public static class MathHandler {

        private String type;
        private double value;

        public MathHandler(String type, double value) {
            this.type = type;
            this.value = value;
        }

        public MathHandler() {
        }

        public String getType() {
            return this.type;
        }

        public double getValue() {
            return this.value;
        }
    }
}
