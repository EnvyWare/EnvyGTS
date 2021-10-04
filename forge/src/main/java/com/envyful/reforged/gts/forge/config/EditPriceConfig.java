package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class EditPriceConfig {

    private ConfigInterface guiSettings = new ConfigInterface(
            "Reforged GTS", 4, "BLOCK", ImmutableMap.of("one", new ConfigItem(
            "minecraft:stained_glass_pane", 1, (byte) 15, " ",
            Lists.newArrayList(), Maps.newHashMap()
    )));

    private PositionableConfigItem minPriceItem = new PositionableConfigItem(
            "pixelmon:relic_gold", 1, (byte) 0, "&bMin Price: &a$%min_price%",
            Lists.newArrayList(), 2, 1, Maps.newHashMap()
    );

    private PositionableConfigItem currentPriceButton = new PositionableConfigItem(
            "pixelmon:relic_silver", 1, (byte) 0, "&bCurrent Price: &e%price%",
            Lists.newArrayList(), 4, 2, Maps.newHashMap()
    );

    private PositionableConfigItem confirmItem = new PositionableConfigItem(
            "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
            Lists.newArrayList(), 2, 3, Maps.newHashMap()
    );

    private int pokemonPosition = 19;

    private Map<String, ModifyPriceButton> priceButtons = new HashMap<String, ModifyPriceButton>() {
        {
            this.put("one", new ModifyPriceButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+10",
                    Lists.newArrayList(), 4, 1, Maps.newHashMap()
            ), 10));

            this.put("two", new ModifyPriceButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+100",
                    Lists.newArrayList(), 5, 1, Maps.newHashMap()
            ), 100));

            this.put("three", new ModifyPriceButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+1,000",
                    Lists.newArrayList(), 6, 1, Maps.newHashMap()
            ), 1000));

            this.put("four", new ModifyPriceButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+10,000",
                    Lists.newArrayList(), 7, 1, Maps.newHashMap()
            ), 10000));

            this.put("five", new ModifyPriceButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-10",
                    Lists.newArrayList(), 4, 2, Maps.newHashMap()
            ), -10));

            this.put("six", new ModifyPriceButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-100",
                    Lists.newArrayList(), 5, 2, Maps.newHashMap()
            ), -100));

            this.put("seven", new ModifyPriceButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-1,000",
                    Lists.newArrayList(), 6, 2, Maps.newHashMap()
            ), -1000));

            this.put("eight", new ModifyPriceButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-10,000",
                    Lists.newArrayList(), 7, 2, Maps.newHashMap()
            ), -10000));
        }
    };

    public ConfigInterface getGuiSettings() {
        return this.guiSettings;
    }

    public PositionableConfigItem getMinPriceItem() {
        return this.minPriceItem;
    }

    public PositionableConfigItem getCurrentPriceButton() {
        return this.currentPriceButton;
    }

    public PositionableConfigItem getConfirmItem() {
        return this.confirmItem;
    }

    public int getPokemonPosition() {
        return this.pokemonPosition;
    }

    public List<ModifyPriceButton> getPriceButtons() {
        return Lists.newArrayList(this.priceButtons.values());
    }

    @ConfigSerializable
    public static class ModifyPriceButton {

        private PositionableConfigItem configItem;
        private double priceModifier;

        public ModifyPriceButton(PositionableConfigItem configItem, double priceModifier) {
            this.configItem = configItem;
            this.priceModifier = priceModifier;
        }

        public ModifyPriceButton() {
        }

        public PositionableConfigItem getConfigItem() {
            return this.configItem;
        }

        public double getPriceModifier() {
            return this.priceModifier;
        }
    }
}
