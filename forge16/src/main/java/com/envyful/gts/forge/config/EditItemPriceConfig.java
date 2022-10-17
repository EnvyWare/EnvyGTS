package com.envyful.gts.forge.config;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class EditItemPriceConfig {

    private ConfigInterface guiSettings = new ConfigInterface(
            "EnvyGTS", 4, "BLOCK", ImmutableMap.of("one", new ConfigItem(
            "minecraft:black_stained_glass_pane", 1, (byte) 15, " ",
            Lists.newArrayList(), Maps.newHashMap()
    )));

    private ExtendedConfigItem minPriceItem = new ExtendedConfigItem(
            "pixelmon:relic_gold", 1, (byte) 0, "&bMin Price: &a$%min_price%",
            Lists.newArrayList(), 2, 1, Maps.newHashMap()
    );

    private ExtendedConfigItem currentPriceButton = new ExtendedConfigItem(
            "pixelmon:relic_silver", 1, (byte) 0, "&bCurrent Price: &a$%price%",
            Lists.newArrayList(), 1, 1, Maps.newHashMap()
    );

    private ExtendedConfigItem confirmItem = new ExtendedConfigItem(
            "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
            Lists.newArrayList(), 2, 2, Maps.newHashMap()
    );

    private int itemPosition = 19;

    private Map<String, ModifyPriceButton> priceButtons = new HashMap<String, ModifyPriceButton>() {
        {
            this.put("one", new ModifyPriceButton(new ExtendedConfigItem(
                    "minecraft:lime_stained_glass_pane", 1, (byte) 5, "&a&l+10",
                    Lists.newArrayList(), 4, 1, Maps.newHashMap()
            ), 10));

            this.put("two", new ModifyPriceButton(new ExtendedConfigItem(
                    "minecraft:lime_stained_glass_pane", 1, (byte) 5, "&a&l+100",
                    Lists.newArrayList(), 5, 1, Maps.newHashMap()
            ), 100));

            this.put("three", new ModifyPriceButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+1,000",
                    Lists.newArrayList(), 6, 1, Maps.newHashMap()
            ), 1000));

            this.put("four", new ModifyPriceButton(new ExtendedConfigItem(
                    "minecraft:lime_stained_glass_pane", 1, (byte) 5, "&a&l+10,000",
                    Lists.newArrayList(), 7, 1, Maps.newHashMap()
            ), 10000));

            this.put("five", new ModifyPriceButton(new ExtendedConfigItem(
                    "minecraft:red_stained_glass_pane", 1, (byte) 14, "&c&l-10",
                    Lists.newArrayList(), 4, 2, Maps.newHashMap()
            ), -10));

            this.put("six", new ModifyPriceButton(new ExtendedConfigItem(
                    "minecraft:red_stained_glass_pane", 1, (byte) 14, "&c&l-100",
                    Lists.newArrayList(), 5, 2, Maps.newHashMap()
            ), -100));

            this.put("seven", new ModifyPriceButton(new ExtendedConfigItem(
                    "minecraft:red_stained_glass_pane", 1, (byte) 14, "&c&l-1,000",
                    Lists.newArrayList(), 6, 2, Maps.newHashMap()
            ), -1000));

            this.put("eight", new ModifyPriceButton(new ExtendedConfigItem(
                    "minecraft:red_stained_glass_pane", 1, (byte) 14, "&c&l-10,000",
                    Lists.newArrayList(), 7, 2, Maps.newHashMap()
            ), -10000));
        }
    };

    public ConfigInterface getGuiSettings() {
        return this.guiSettings;
    }

    public ExtendedConfigItem getMinPriceItem() {
        return this.minPriceItem;
    }

    public ExtendedConfigItem getCurrentPriceButton() {
        return this.currentPriceButton;
    }

    public ExtendedConfigItem getConfirmItem() {
        return this.confirmItem;
    }

    public int getItemPosition() {
        return this.itemPosition;
    }

    public List<ModifyPriceButton> getPriceButtons() {
        return Lists.newArrayList(this.priceButtons.values());
    }

    @ConfigSerializable
    public static class ModifyPriceButton {

        private ExtendedConfigItem configItem;
        private double priceModifier;

        public ModifyPriceButton(ExtendedConfigItem configItem, double priceModifier) {
            this.configItem = configItem;
            this.priceModifier = priceModifier;
        }

        public ModifyPriceButton() {
        }

        public ExtendedConfigItem getConfigItem() {
            return this.configItem;
        }

        public double getPriceModifier() {
            return this.priceModifier;
        }
    }
}
