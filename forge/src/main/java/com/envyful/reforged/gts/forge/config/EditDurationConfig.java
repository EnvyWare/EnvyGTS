package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
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
            "Reforged GTS", 4, "BLOCK", ImmutableMap.of("one", new ConfigItem(
            "minecraft:stained_glass_pane", 1, (byte) 15, " ",
            Lists.newArrayList(), Maps.newHashMap()
    )));

    private PositionableConfigItem minTimeItem = new PositionableConfigItem(
            "pixelmon:relic_gold", 1, (byte) 0, "&bMin Time: &e%min_time%",
            Lists.newArrayList(), 2, 1, Maps.newHashMap()
    );

    private PositionableConfigItem currentTimeButton = new PositionableConfigItem(
            "pixelmon:relic_silver", 1, (byte) 0, "&bCurrent Time: &e%time%",
            Lists.newArrayList(), 1, 1, Maps.newHashMap()
    );

    private PositionableConfigItem confirmItem = new PositionableConfigItem(
            "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
            Lists.newArrayList(), 2, 2, Maps.newHashMap()
    );

    private SpriteConfig spriteConfig = new SpriteConfig();

    private int pokemonPosition = 19;

    private Map<String, ModifyTimeButton> timeButtons = new HashMap<String, ModifyTimeButton>() {
        {
            this.put("one", new ModifyTimeButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+5 minutes",
                    Lists.newArrayList(), 4, 1, Maps.newHashMap()
            ), 300));

            this.put("two", new ModifyTimeButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+30 minutes",
                    Lists.newArrayList(), 5, 1, Maps.newHashMap()
            ), 1800));

            this.put("three", new ModifyTimeButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+1 hour",
                    Lists.newArrayList(), 6, 1, Maps.newHashMap()
            ), 3600));

            this.put("four", new ModifyTimeButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 5, "&a&l+6 hours",
                    Lists.newArrayList(), 7, 1, Maps.newHashMap()
            ), 21600));

            this.put("five", new ModifyTimeButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-5 minutes",
                    Lists.newArrayList(), 4, 2, Maps.newHashMap()
            ), -300));

            this.put("six", new ModifyTimeButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-30 minutes",
                    Lists.newArrayList(), 5, 2, Maps.newHashMap()
            ), -1800));

            this.put("seven", new ModifyTimeButton(new PositionableConfigItem(
                    "minecraft:stained_glass_pane", 1, (byte) 14, "&c&l-1 hour",
                    Lists.newArrayList(), 6, 2, Maps.newHashMap()
            ), -3600));

            this.put("eight", new ModifyTimeButton(new PositionableConfigItem(
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

    public PositionableConfigItem getMinTimeItem() {
        return this.minTimeItem;
    }

    public PositionableConfigItem getCurrentTimeButton() {
        return this.currentTimeButton;
    }

    public PositionableConfigItem getConfirmItem() {
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

        private PositionableConfigItem configItem;
        private long timeModifier;

        public ModifyTimeButton(PositionableConfigItem configItem, long timeModifier) {
            this.configItem = configItem;
            this.timeModifier = timeModifier;
        }

        public ModifyTimeButton() {
        }

        public PositionableConfigItem getConfigItem() {
            return this.configItem;
        }

        public long getTimeModifier() {
            return this.timeModifier;
        }
    }
}
