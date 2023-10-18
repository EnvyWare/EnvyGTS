package com.envyful.gts.forge.config;

import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PokeSpecPricing {

    private String spec;
    private MathHandler minPrice;

    private transient PokemonSpecification cachedSpec = null;

    public PokeSpecPricing(String spec, MathHandler minPrice) {
        this.spec = spec;
        this.minPrice = minPrice;
    }

    public PokeSpecPricing() {
    }

    public PokemonSpecification getSpec() {
        if (this.cachedSpec == null) {
            this.cachedSpec = PokemonSpecificationProxy.create(this.spec).get();
        }

        return this.cachedSpec;
    }

    public int getPriority() {
        return this.minPrice.priority;
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
            case "set" :
                return this.minPrice.getValue();
        }
    }

    @ConfigSerializable
    public static class MathHandler {

        private String type;
        private double value;
        private int priority = 1;

        public MathHandler(String type, double value, int priority) {
            this.type = type;
            this.value = value;
            this.priority = priority;
        }

        public MathHandler() {
        }

        public String getType() {
            return this.type;
        }

        public double getValue() {
            return this.value;
        }

        public int getPriority() {
            return this.priority;
        }
    }
}
