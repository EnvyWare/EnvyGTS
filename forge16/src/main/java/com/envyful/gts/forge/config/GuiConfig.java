package com.envyful.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.forge.gui.type.ConfirmationUI;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.type.Pair;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
@ConfigPath("config/EnvyGTS/guis.yml")
public class GuiConfig extends AbstractYamlConfig {

    private SearchTradesConfig searchUIConfig = new SearchTradesConfig();
    private PartyPokemonConfig partyPokemonUIConfig = new PartyPokemonConfig();
    private SelectFromPCConfig pcConfig = new SelectFromPCConfig();
    private PokemonPriceConfig priceConfig = new PokemonPriceConfig();
    private EditPriceConfig editPriceUIConfig = new EditPriceConfig();
    private EditItemPriceConfig editItemPriceUIConfig = new EditItemPriceConfig();
    private EditDurationConfig editDurationUIConfig = new EditDurationConfig();
    private SellHandOrParty sellHandOrParty = new SellHandOrParty();

    public GuiConfig() {
        super();
    }

    public SearchTradesConfig getSearchUIConfig() {
        return this.searchUIConfig;
    }

    public PartyPokemonConfig getPartyPokemonUIConfig() {
        return this.partyPokemonUIConfig;
    }

    public SelectFromPCConfig getPcConfig() {
        return this.pcConfig;
    }

    public PokemonPriceConfig getPriceConfig() {
        return this.priceConfig;
    }

    public EditPriceConfig getEditPriceUIConfig() {
        return this.editPriceUIConfig;
    }

    public EditDurationConfig getEditDurationUIConfig() {
        return this.editDurationUIConfig;
    }

    public EditItemPriceConfig getEditItemPriceUIConfig() {
        return this.editItemPriceUIConfig;
    }

    public SellHandOrParty getSellHandOrParty() {
        return this.sellHandOrParty;
    }

    @ConfigSerializable
    public static class SellHandOrParty {

        private ConfigInterface guiSettings = new ConfigInterface(
                "EnvyGTS", 3, "BLOCK", ImmutableMap.of("one",
                ConfigItem.builder()
                        .type("minecraft:black_stained_glass_pane")
                        .name(" ")
                        .amount(1)
                        .build()
        ));

        private ExtendedConfigItem selectParty = ExtendedConfigItem.builder()
                .type("pixelmon:poke_ball")
                .amount(1)
                .name("&bSelect Party Member")
                .positions(Pair.of(1, 1))
                .build();

        private ExtendedConfigItem sellHand = ExtendedConfigItem.builder()
                .type("pixelmon:protein")
                .amount(1)
                .name("&bSell Hand")
                .positions(Pair.of(3, 1))
                .build();

        private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
                .type("pixelmon:eject_button")
                .amount(1)
                .name("&cBack")
                .positions(Pair.of(2, 1))
                .build();

        public SellHandOrParty() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ExtendedConfigItem getSelectParty() {
            return this.selectParty;
        }

        public ExtendedConfigItem getSellHand() {
            return this.sellHand;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }
    }

    @ConfigSerializable
    public static class SearchTradesConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "EnvyGTS", 6, "BLOCK", ImmutableMap.of("one", ConfigItem.builder()
                .type("minecraft:black_stained_glass_pane")
                .name(" ")
                .amount(1)
                .build()));

        private ConfirmationUI.ConfirmConfig confirmGuiConfig = new ConfirmationUI.ConfirmConfig();

        private ExtendedConfigItem nextPageItem = ExtendedConfigItem.builder()
                .type("pixelmon:trade_holder_right")
                .amount(1)
                .name("&aNext Page")
                .positions(Pair.of(8, 5))
                .build();

        private ExtendedConfigItem previousPageItem = ExtendedConfigItem.builder()
                .type("pixelmon:trade_holder_left")
                .amount(1)
                .name("&aPrevious Page")
                .positions(Pair.of(0, 5))
                .build();

        private ExtendedConfigItem sellButton = ExtendedConfigItem.builder()
                .type("pixelmon:eject_button")
                .amount(1)
                .name("&aSell Stuff")
                .positions(Pair.of(4, 5))
                .build();

        private ExtendedConfigItem filterButton = new ExtendedConfigItem(
                "pixelmon:net_ball", 1, (byte) 0, "&bChange filter",
                Lists.newArrayList("&aCurrent Filter: &f%filter%"), 2, 5, Maps.newHashMap()
        );

        private ExtendedConfigItem orderButton = new ExtendedConfigItem(
                "pixelmon:blue_orb", 1, (byte) 0, "&bChange order",
                Lists.newArrayList("&aCurrent order: &f%order%"), 6, 5, Maps.newHashMap()
        );

        private SpriteConfig spriteConfig = new SpriteConfig();

        public SearchTradesConfig() {
        }

        public ConfirmationUI.ConfirmConfig getConfirmGuiConfig() {
            return this.confirmGuiConfig;
        }

        public SpriteConfig getSpriteConfig() {
            return this.spriteConfig;
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ExtendedConfigItem getNextPageItem() {
            return this.nextPageItem;
        }

        public ExtendedConfigItem getPreviousPageItem() {
            return this.previousPageItem;
        }

        public ExtendedConfigItem getSellButton() {
            return this.sellButton;
        }

        public ExtendedConfigItem getFilterButton() {
            return this.filterButton;
        }

        public ExtendedConfigItem getOrderButton() {
            return this.orderButton;
        }
    }

    @ConfigSerializable
    public static class PartyPokemonConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "EnvyGTS", 3, "BLOCK", ImmutableMap.of("one", ConfigItem.builder()
                .type("minecraft:black_stained_glass_pane")
                .name(" ")
                .amount(1)
                .build()));

        private ExtendedConfigItem backButton = new ExtendedConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 0, 0, Maps.newHashMap()
        );

        private List<Integer> partySelectionPositions = Lists.newArrayList(
                10, 11, 12, 14, 15, 16
        );

        private ExtendedConfigItem confirmItem = new ExtendedConfigItem(
                "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
                Lists.newArrayList(), 4, 2, Maps.newHashMap()
        );

        private ExtendedConfigItem viewPCButton = new ExtendedConfigItem(
                "pixelmon:pc", 1, (byte) 0, "&aYour PC",
                Lists.newArrayList(), 4, 0, Maps.newHashMap()
        );

        private ConfigItem noPokemonItem = new ConfigItem(
                "minecraft:barrier",
                1, (byte) 0, "&cNo pokemon in this position",
                Lists.newArrayList(), Maps.newHashMap()
        );

        private ConfigItem untradeablePokemonItem = new ConfigItem(
                "minecraft:barrier",
                1, (byte) 0, "&c&lUNTRADEABLE",
                Lists.newArrayList(), Maps.newHashMap()
        );

        private SpriteConfig spriteConfig = new SpriteConfig();

        public PartyPokemonConfig() {
        }

        public SpriteConfig getSpriteConfig() {
            return this.spriteConfig;
        }

        public ConfigItem getUntradeablePokemonItem() {
            return this.untradeablePokemonItem;
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }

        public List<Integer> getPartySelectionPositions() {
            return this.partySelectionPositions;
        }

        public ExtendedConfigItem getConfirmItem() {
            return this.confirmItem;
        }

        public ExtendedConfigItem getViewPCButton() {
            return this.viewPCButton;
        }

        public ConfigItem getNoPokemonItem() {
            return this.noPokemonItem;
        }
    }

    @ConfigSerializable
    public static class SelectFromPCConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "EnvyGTS", 6, "BLOCK", ImmutableMap.of("one", ConfigItem.builder()
                .type("minecraft:black_stained_glass_pane")
                .name(" ")
                .amount(1)
                .build()));

        private ExtendedConfigItem backButton = new ExtendedConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 0, 5, Maps.newHashMap()
        );

        private ExtendedConfigItem nextPageButton = new ExtendedConfigItem(
                "pixelmon:trade_holder_right", 1, (byte) 0, "&aNext Page",
                Lists.newArrayList(), 8, 0, Maps.newHashMap()
        );

        private ExtendedConfigItem previousPageButton = new ExtendedConfigItem(
                "pixelmon:trade_holder_left", 1, (byte) 0, "&aPrevious Page",
                Lists.newArrayList(), 0, 0, Maps.newHashMap()
        );

        private ExtendedConfigItem confirmButton = new ExtendedConfigItem(
                "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
                Lists.newArrayList(), 7, 3, Maps.newHashMap()
        );

        private ConfigItem noPokemonItem = new ConfigItem(
                "minecraft:barrier",
                1, (byte) 0, "&cNo pokemon in this position",
                Lists.newArrayList(), Maps.newHashMap()
        );

        private ConfigItem untradeablePokemonItem = new ConfigItem(
                "minecraft:barrier",
                1, (byte) 0, "&c&lUNTRADEABLE",
                Lists.newArrayList(), Maps.newHashMap()
        );

        private SpriteConfig spriteConfig = new SpriteConfig();

        private int perPage = 30;

        private int confirmSlot = 25;

        public SelectFromPCConfig() {}

        public SpriteConfig getSpriteConfig() {
            return this.spriteConfig;
        }

        public ConfigItem getUntradeablePokemonItem() {
            return this.untradeablePokemonItem;
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }

        public ExtendedConfigItem getNextPageButton() {
            return this.nextPageButton;
        }

        public ExtendedConfigItem getPreviousPageButton() {
            return this.previousPageButton;
        }

        public int getPerPage() {
            return this.perPage;
        }

        public ExtendedConfigItem getConfirmButton() {
            return this.confirmButton;
        }

        public int getConfirmSlot() {
            return this.confirmSlot;
        }

        public ConfigItem getNoPokemonItem() {
            return this.noPokemonItem;
        }
    }

    @ConfigSerializable
    public static class PokemonPriceConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "EnvyGTS", 3, "BLOCK", ImmutableMap.of("one", ConfigItem.builder()
                .type("minecraft:black_stained_glass_pane")
                .name(" ")
                .amount(1)
                .build()));

        private int pokemonPosition = 10;

        private ExtendedConfigItem minPriceItem = new ExtendedConfigItem(
                "pixelmon:relic_gold", 1, (byte) 0, "&bMin Price: &a$%min_price%",
                Lists.newArrayList(), 2, 1, Maps.newHashMap()
        );

        private ExtendedConfigItem modifyPriceButton = new ExtendedConfigItem(
                "pixelmon:relic_silver", 1, (byte) 0, "&bEdit Price",
                Lists.newArrayList("&bCurrent Price: &e%price%"), 4, 1, Maps.newHashMap()
        );

        private ExtendedConfigItem modifyDurationButton = new ExtendedConfigItem(
                "minecraft:clock", 1, (byte) 0, "&bEdit Duration",
                Lists.newArrayList("&bDuration: &e%time%"), 5, 1, Maps.newHashMap()
        );

        private ExtendedConfigItem confirmItem = new ExtendedConfigItem(
                "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
                Lists.newArrayList(), 7, 1, Maps.newHashMap()
        );

        private ExtendedConfigItem backButton = new ExtendedConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 0, 0, Maps.newHashMap()
        );

        private SpriteConfig spriteConfig = new SpriteConfig();

        public PokemonPriceConfig() {
        }

        public SpriteConfig getSpriteConfig() {
            return this.spriteConfig;
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public int getPokemonPosition() {
            return this.pokemonPosition;
        }

        public ExtendedConfigItem getMinPriceItem() {
            return this.minPriceItem;
        }

        public ExtendedConfigItem getModifyPriceButton() {
            return this.modifyPriceButton;
        }

        public ExtendedConfigItem getModifyDurationButton() {
            return this.modifyDurationButton;
        }

        public ExtendedConfigItem getConfirmItem() {
            return this.confirmItem;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }
    }
}
