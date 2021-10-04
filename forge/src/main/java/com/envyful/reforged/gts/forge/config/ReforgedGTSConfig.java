package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@ConfigPath("config/ReforgedGTS/config.yml")
@ConfigSerializable
public class ReforgedGTSConfig extends AbstractYamlConfig {

    private SQLDatabaseDetails databaseDetails = new SQLDatabaseDetails("ReforgedGTS", "0.0.0.0", 3306, "admin",
                                                                        "password", "database"
    );

    private long minTradeDuration = 300;
    private long defaultTradeDurationSeconds = 300;
    private long maxTradeDurationSeconds = 172800;
    private double minPokemonPrice = 10_000.00;

    private Map<String, PokeSpecPricing> minPriceModifiers = ImmutableMap.of(
            "example", new PokeSpecPricing("shiny:1", new PokeSpecPricing.MathHandler("*", 2.0))
    );

    private List<String> unbreedableConditions = Lists.newArrayList("abs:2");

    public ReforgedGTSConfig() {
        super();
    }

    public SQLDatabaseDetails getDatabaseDetails() {
        return this.databaseDetails;
    }

    public long getMinTradeDuration() {
        return this.minTradeDuration;
    }

    public long getDefaultTradeDurationSeconds() {
        return this.defaultTradeDurationSeconds;
    }

    public double getMinPokemonPrice() {
        return this.minPokemonPrice;
    }

    public long getMaxTradeDurationSeconds() {
        return this.maxTradeDurationSeconds;
    }

    public List<PokeSpecPricing> getMinPriceModifiers() {
        return Lists.newArrayList(this.minPriceModifiers.values());
    }

    public List<String> getUnbreedableConditions() {
        return this.unbreedableConditions;
    }

}
