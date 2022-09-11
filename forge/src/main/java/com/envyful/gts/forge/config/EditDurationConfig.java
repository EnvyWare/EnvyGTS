package com.envyful.gts.forge.config;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class EditDurationConfig {

    private ConfigInterface guiSettings = new ConfigInterface(
            "EnvyGTS", 4, "BLOCK", ImmutableMap.of("one", new ConfigItem(
            "minecraft:stained_glass_pane", 1, (byte) 15, " ",
            Lists.newArrayList(), Maps.newHashMap()
    )));

    private ExtendedConfigItem minTimeItem = new ExtendedConfigItem(
            "pixelmon:relic_gold", 1, (byte) 0, "&bMin Time: &e%min_time%",
            Lists.newArrayList(), 2, 1, Maps.newHashMap()
    );

    private ExtendedConfigItem currentTimeButton = new ExtendedConfigItem(
            "pixelmon:relic_silver", 1, (byte) 0, "&bCurrent Time: &e%time%",
            Lists.newArrayList(), 1, 1, Maps.newHashMap()
    );

    private ExtendedConfigItem confirmItem = new ExtendedConfigItem(
            "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
            Lists.newArrayList(), 2, 2, Maps.newHashMap()
    );

    private SpriteConfig spriteConfig = new SpriteConfig();

    private int pokemonPosition = 19;

    private Map<String, ModifyTimeButton> timeButtons = new HashMap<String, ModifyTimeButton>() {
        {
            this.put("one", new ModifyTimeButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+5 minutes",
                    Lists.newArrayList(), 4, 1, Maps.newHashMap()
            ), 300));

            this.put("two", new ModifyTimeButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+30 minutes",
                    Lists.newArrayList(), 5, 1, Maps.newHashMap()
            ), 1800));

            this.put("three", new ModifyTimeButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+1 hour",
                    Lists.newArrayList(), 6, 1, Maps.newHashMap()
            ), 3600));

            this.put("four", new ModifyTimeButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+6 hours",
                    Lists.newArrayList(), 7, 1, Maps.newHashMap()
            ), 21600));

            this.put("five", new ModifyTimeButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-5 minutes",
                    Lists.newArrayList(), 4, 2, Maps.newHashMap()
            ), -300));

            this.put("six", new ModifyTimeButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-30 minutes",
                    Lists.newArrayList(), 5, 2, Maps.newHashMap()
            ), -1800));

            this.put("seven", new ModifyTimeButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-1 hour",
                    Lists.newArrayList(), 6, 2, Maps.newHashMap()
            ), -3600));

            this.put("eight", new ModifyTimeButton(new ExtendedConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-6 hours",
                    Lists.newArrayList(), 7, 2, Maps.newHashMap()
            ), -21600));
        }
    };

    public SpriteConfig getSpriteConfig() {
        return this.spriteConfig;
    }

    public ConfigInterface getGuiSettings() {
        return this.guiSettings;
    }

    public ExtendedConfigItem getMinTimeItem() {
        return this.minTimeItem;
    }

    public ExtendedConfigItem getCurrentTimeButton() {
        return this.currentTimeButton;
    }

    public ExtendedConfigItem getConfirmItem() {
        return this.confirmItem;
    }

    public int getPokemonPosition() {
        return this.pokemonPosition;
    }

    public List<ModifyTimeButton> getTimeButtons() {
        return Lists.newArrayList(this.timeButtons.values());
    }

    @ConfigSerializable
    public static class ModifyTimeButton {

        private ExtendedConfigItem configItem;
        private long timeModifier;

        public ModifyTimeButton(ExtendedConfigItem configItem, long timeModifier) {
            this.configItem = configItem;
            this.timeModifier = timeModifier;
        }

        public ModifyTimeButton() {
        }

        public ExtendedConfigItem getConfigItem() {
            return this.configItem;
        }

        public long getTimeModifier() {
            return this.timeModifier;
        }
    }
}
