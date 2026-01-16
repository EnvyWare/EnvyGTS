package com.envyful.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.neoforge.config.yaml.YamlOps;
import com.envyful.api.neoforge.gui.type.ConfirmationUI;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.type.Pair;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.init.registry.PixelmonDataComponents;
import net.minecraft.core.component.DataComponentMap;
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

    private SpriteConfig spriteConfig = SpriteConfig.DEFAULT;

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

    public SpriteConfig getSpriteConfig() {
        return this.spriteConfig;
    }

    @ConfigSerializable
    public static class Returns {

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

        private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
                .type("pixelmon:eject_button")
                .amount(1)
                .name("&cBack")
                .positions(Pair.of(4, 5))
                .build();

        public Returns() {
        }

        public PaginatedConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public ExtendedConfigItem getBackButton() {
            return this.backButton;
        }
    }

    @ConfigSerializable
    public static class SellHandOrParty {

        private ConfigInterface guiSettings = ConfigInterface.builder()
                .title("EnvyGTS")
                .height(3)
                .fillType(ConfigInterface.FillType.BLOCK)
                .fillerItem(ConfigItem.builder()
                        .type("minecraft:black_stained_glass_pane")
                        .name(" ")
                        .amount(1)
                        .build())
                .build();

        private ExtendedConfigItem selectParty = ExtendedConfigItem.builder()
                .type("pixelmon:poke_ball")
                .amount(1)
                .name("&bSelect Party Member")
                .positions(Pair.of(2, 1))
                .build();

        private ExtendedConfigItem sellHand = ExtendedConfigItem.builder()
                .type("pixelmon:protein")
                .amount(1)
                .name("&bSell Hand")
                .positions(Pair.of(6, 1))
                .build();

        private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
                .type("pixelmon:eject_button")
                .amount(1)
                .name("&cBack")
                .positions(Pair.of(4, 1))
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

        private ExtendedConfigItem filterButton = ExtendedConfigItem.builder()
                .type("pixelmon:poke_ball")
                .amount(1)
                .name("&bChange filter")
                .lore("&aCurrent Filter: &f%filter%")
                .positions(2, 5)
                .dataComponents(DataComponentMap.CODEC.encode(
                        DataComponentMap.builder()
                                .set(PixelmonDataComponents.POKE_BALL, PokeBallRegistry.NET_BALL).build(), YamlOps.INSTANCE, YamlOps.INSTANCE.empty()).getOrThrow())
                .build();

        private ExtendedConfigItem orderButton = ExtendedConfigItem.builder()
                .type("pixelmon:blue_orb")
                .amount(1)
                .name("&bChange order")
                .lore("&aCurrent order: &f%order%")
                .positions(6, 5)
                .build();

        public SearchTradesConfig() {
        }

        public ConfirmationUI.ConfirmConfig getConfirmGuiConfig() {
            return this.confirmGuiConfig;
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

        private ConfigInterface guiSettings = ConfigInterface.builder()
                .title("EnvyGTS")
                .height(3)
                .fillType(ConfigInterface.FillType.BLOCK)
                .fillerItem(ConfigItem.builder()
                        .type("minecraft:black_stained_glass_pane")
                        .name(" ")
                        .amount(1)
                        .build())
                .build();

        private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
                .type("pixelmon:eject_button")
                .amount(1)
                .name("&cBack")
                .positions(0, 0)
                .build();

        private List<Integer> partySelectionPositions = Lists.newArrayList(
                10, 11, 12, 14, 15, 16
        );

        private ExtendedConfigItem viewPCButton =
                ExtendedConfigItem.builder()
                        .type("pixelmon:trade_holder_right")
                        .amount(1)
                        .name("&aView PC")
                        .positions(8, 0)
                        .build();

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

        public PartyPokemonConfig() {
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

        public ExtendedConfigItem getViewPCButton() {
            return this.viewPCButton;
        }

        public ConfigItem getNoPokemonItem() {
            return this.noPokemonItem;
        }
    }

    @ConfigSerializable
    public static class SelectFromPCConfig {

        private ConfigInterface guiSettings = ConfigInterface.defaultInterface("EnvyGTS");

        private ExtendedConfigItem backButton =
                ExtendedConfigItem.builder()
                        .type("pixelmon:eject_button")
                        .amount(1)
                        .name("&cBack")
                        .positions(0, 5)
                        .build();

        private ExtendedConfigItem nextPageButton =
                ExtendedConfigItem.builder()
                        .type("pixelmon:trade_holder_right")
                        .amount(1)
                        .name("&aNext Page")
                        .positions(8, 0)
                        .build();

        private ExtendedConfigItem previousPageButton =
                ExtendedConfigItem.builder()
                        .type("pixelmon:trade_holder_left")
                        .amount(1)
                        .name("&aPrevious Page")
                        .positions(0, 0)
                        .build();

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

        private int perPage = 30;

        private int confirmSlot = 25;

        public SelectFromPCConfig() {}

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

        public int getConfirmSlot() {
            return this.confirmSlot;
        }

        public ConfigItem getNoPokemonItem() {
            return this.noPokemonItem;
        }
    }
}
