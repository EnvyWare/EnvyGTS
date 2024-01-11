package com.envyful.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
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
    private SellHandOrParty sellHandOrParty = new SellHandOrParty();
    private Returns returnsGui = new Returns();

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

    public SellHandOrParty getSellHandOrParty() {
        return this.sellHandOrParty;
    }

    public Returns getReturnsGui() {
        return this.returnsGui;
    }

    @ConfigSerializable
    public static class Returns {

        private ConfigInterface guiSettings = new ConfigInterface(
                "EnvyGTS", 6, "BLOCK", ImmutableMap.of("one",
                ConfigItem.builder()
                        .type("minecraft:black_stained_glass_pane")
                        .name(" ")
                        .amount(1)
                        .build()
        ));

        private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
                .type("pixelmon:eject_button")
                .amount(1)
                .name("&cBack")
                .positions(Pair.of(4, 5))
                .build();

        private List<Integer> returnPositions = Lists.newArrayList(
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37
        );

        public Returns() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }

        public List<Integer> getReturnPositions() {
            return this.returnPositions;
        }
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
                .nbt("PokeBallID", new ConfigItem.NBTValue("string", "poke_ball"))
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

        private PaginatedConfigInterface guiSettings = PaginatedConfigInterface.paginatedBuilder()
                .title("EnvyGTS")
                .height(6)
                .fillType(ConfigInterface.FillType.BLOCK)
                .nextPageButton(ExtendedConfigItem.builder()
                        .type("pixelmon:trade_holder_right")
                        .amount(1)
                        .name("&aNext Page")
                        .positions(Pair.of(8, 5))
                        .build())
                .previousPageButton(ExtendedConfigItem.builder()
                        .type("pixelmon:trade_holder_left")
                        .amount(1)
                        .name("&aPrevious Page")
                        .positions(Pair.of(0, 5))
                        .build())
                .build();

        private ConfirmationUI.ConfirmConfig confirmGuiConfig = new ConfirmationUI.ConfirmConfig();

        private ExtendedConfigItem sellButton = ExtendedConfigItem.builder()
                .type("pixelmon:eject_button")
                .amount(1)
                .name("&aSell Stuff")
                .positions(Pair.of(4, 5))
                .build();

        private ExtendedConfigItem returnsButton = ExtendedConfigItem.builder()
                .type("minecraft:diamond")
                .amount(1)
                .name("&aCollect your returns")
                .positions(Pair.of(5, 5))
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

        public PaginatedConfigInterface getGuiSettings() {
            return this.guiSettings;
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

        public ExtendedConfigItem getReturnsButton() {
            return this.returnsButton;
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

        private ConfigItem noPokemonItem = ConfigItem.builder()
                        .type("minecraft:barrier")
                        .name("&cNo pokemon in this position")
                        .amount(1)
                        .build();

        private ConfigItem untradeablePokemonItem = ConfigItem.builder()
                .type("minecraft:barrier")
                .name("&c&lUNTRADEABLE")
                .amount(1)
                .build();

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

        private ConfigItem noPokemonItem = ConfigItem.builder()
                .type("minecraft:barrier")
                .name("&cNo pokemon in this position")
                .amount(1)
                .build();

        private ConfigItem untradeablePokemonItem = ConfigItem.builder()
                .type("minecraft:barrier")
                .name("&c&lUNTRADEABLE")
                .amount(1)
                .build();

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
}
