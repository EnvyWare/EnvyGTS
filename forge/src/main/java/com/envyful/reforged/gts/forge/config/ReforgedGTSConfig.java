package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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

    private int minTradeDuration = 300;
    private int tradeDurationSeconds = 86400;
    private double minPokemonPrice = 10_000.00;

    private Map<String, PokeSpecPricing> minPriceModifiers = ImmutableMap.of(
            "example", new PokeSpecPricing("shiny:1", new PokeSpecPricing.MathHandler("*", 2.0))
    );

    private List<String> unbreedableConditions = Lists.newArrayList("abs:2");

    private MainUIConfig mainUIConfig = new MainUIConfig();
    private SearchTradesConfig searchUIConfig = new SearchTradesConfig();
    private TimedOutTradesConfig timedOutUIConfig = new TimedOutTradesConfig();
    private ClaimTradesConfig claimTradesUIConfig = new ClaimTradesConfig();
    private PartyPokemonConfig partyPokemonUIConfig = new PartyPokemonConfig();
    private SelectFromPCConfig pcConfig = new SelectFromPCConfig();
    private PokemonPriceConfig priceConfig = new PokemonPriceConfig();
    private EditPriceConfig editPriceUIConfig = new EditPriceConfig();
    private EditDurationConfig editDurationUIConfig = new EditDurationConfig();

    public ReforgedGTSConfig() {
        super();
    }

    public SQLDatabaseDetails getDatabaseDetails() {
        return this.databaseDetails;
    }

    public int getMinTradeDuration() {
        return this.minTradeDuration;
    }

    public int getTradeDurationSeconds() {
        return this.tradeDurationSeconds;
    }

    public double getMinPokemonPrice() {
        return this.minPokemonPrice;
    }

    public List<PokeSpecPricing> getMinPriceModifiers() {
        return Lists.newArrayList(this.minPriceModifiers.values());
    }

    public List<String> getUnbreedableConditions() {
        return this.unbreedableConditions;
    }

    public MainUIConfig getMainUIConfig() {
        return this.mainUIConfig;
    }

    public SearchTradesConfig getSearchUIConfig() {
        return this.searchUIConfig;
    }

    public TimedOutTradesConfig getTimedOutUIConfig() {
        return this.timedOutUIConfig;
    }

    public ClaimTradesConfig getClaimTradesUIConfig() {
        return this.claimTradesUIConfig;
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

    @ConfigSerializable
    public static class MainUIConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "Reforged GTS", 3, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                "minecraft:stained_glass_pane", 1, (byte) 15, " ",
                Lists.newArrayList(), Maps.newHashMap()
        )));

        private PositionableConfigItem viewTradesButton = new PositionableConfigItem(
                "pixelmon:poke_ball",
                1, (byte) 0, "&bView Trades",
                Lists.newArrayList(), 1, 1, Maps.newHashMap()
        );

        private PositionableConfigItem viewClaimsButton = new PositionableConfigItem(
                "pixelmon:protein",
                1, (byte) 0, "&bClaim Trades",
                Lists.newArrayList(), 3, 1, Maps.newHashMap()
        );

        private PositionableConfigItem viewTimeoutButton = new PositionableConfigItem(
                Item.getIdFromItem(Items.CLOCK) + "",
                1, (byte) 0, "&bTimed out trades",
                Lists.newArrayList(), 5, 1, Maps.newHashMap()
        );

        private PositionableConfigItem sellItemButton = new PositionableConfigItem(
                "pixelmon:trade_machine",
                1, (byte) 0, "&bSell Item",
                Lists.newArrayList(), 7, 1, Maps.newHashMap()
        );

        public MainUIConfig() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public PositionableConfigItem getViewTradesButton() {
            return this.viewTradesButton;
        }

        public PositionableConfigItem getViewClaimsButton() {
            return this.viewClaimsButton;
        }

        public PositionableConfigItem getViewTimeoutButton() {
            return this.viewTimeoutButton;
        }

        public PositionableConfigItem getSellItemButton() {
            return this.sellItemButton;
        }
    }

    @ConfigSerializable
    public static class SearchTradesConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "Reforged GTS", 6, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                "minecraft:stained_glass_pane", 1, (byte) 15, " ",
                Lists.newArrayList(), Maps.newHashMap()
        )));

        private PositionableConfigItem nextPageItem = new PositionableConfigItem(
                "pixelmon:trade_holder_right", 1, (byte) 0, "&aNext Page",
                Lists.newArrayList(), 8, 5, Maps.newHashMap()
        );

        private PositionableConfigItem previousPageItem = new PositionableConfigItem(
                "pixelmon:trade_holder_left", 1, (byte) 0, "&aPrevious Page",
                Lists.newArrayList(), 0, 5, Maps.newHashMap()
        );

        private PositionableConfigItem backButton = new PositionableConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 4, 5, Maps.newHashMap()
        );

        private PositionableConfigItem filterButton = new PositionableConfigItem(
                "pixelmon:net_ball", 1, (byte) 0, "&bChange filter",
                Lists.newArrayList("&aCurrent Filter: &f%filter%"), 2, 5, Maps.newHashMap()
        );

        private PositionableConfigItem orderButton = new PositionableConfigItem(
                "pixelmon:blue_orb", 1, (byte) 0, "&bChange order",
                Lists.newArrayList("&aCurrent order: &f%order%"), 6, 5, Maps.newHashMap()
        );

        public SearchTradesConfig() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public PositionableConfigItem getNextPageItem() {
            return this.nextPageItem;
        }

        public PositionableConfigItem getPreviousPageItem() {
            return this.previousPageItem;
        }

        public PositionableConfigItem getBackButton() {
            return this.backButton;
        }

        public PositionableConfigItem getFilterButton() {
            return this.filterButton;
        }

        public PositionableConfigItem getOrderButton() {
            return this.orderButton;
        }
    }

    @ConfigSerializable
    public static class TimedOutTradesConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "Reforged GTS", 6, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                "minecraft:stained_glass_pane", 1, (byte) 15, " ",
                Lists.newArrayList(), Maps.newHashMap()
        )));

        private PositionableConfigItem nextPageItem = new PositionableConfigItem(
                "pixelmon:trade_holder_right", 1, (byte) 0, "&aNext Page",
                Lists.newArrayList(), 8, 5, Maps.newHashMap()
        );

        private PositionableConfigItem previousPageItem = new PositionableConfigItem(
                "pixelmon:trade_holder_left", 1, (byte) 0, "&aPrevious Page",
                Lists.newArrayList(), 0, 5, Maps.newHashMap()
        );

        private PositionableConfigItem backButton = new PositionableConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 4, 5, Maps.newHashMap()
        );

        public TimedOutTradesConfig() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public PositionableConfigItem getNextPageItem() {
            return this.nextPageItem;
        }

        public PositionableConfigItem getPreviousPageItem() {
            return this.previousPageItem;
        }

        public PositionableConfigItem getBackButton() {
            return this.backButton;
        }
    }


    @ConfigSerializable
    public static class ClaimTradesConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "Reforged GTS", 6, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                "minecraft:stained_glass_pane", 1, (byte) 15, " ",
                Lists.newArrayList(), Maps.newHashMap()
        )));

        private PositionableConfigItem nextPageItem = new PositionableConfigItem(
                "pixelmon:trade_holder_right", 1, (byte) 0, "&aNext Page",
                Lists.newArrayList(), 8, 5, Maps.newHashMap()
        );

        private PositionableConfigItem previousPageItem = new PositionableConfigItem(
                "pixelmon:trade_holder_left", 1, (byte) 0, "&aPrevious Page",
                Lists.newArrayList(), 0, 5, Maps.newHashMap()
        );

        private PositionableConfigItem backButton = new PositionableConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 4, 5, Maps.newHashMap()
        );

        public ClaimTradesConfig() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public PositionableConfigItem getNextPageItem() {
            return this.nextPageItem;
        }

        public PositionableConfigItem getPreviousPageItem() {
            return this.previousPageItem;
        }

        public PositionableConfigItem getBackButton() {
            return this.backButton;
        }
    }

    @ConfigSerializable
    public static class PartyPokemonConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "Reforged GTS", 3, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                        "minecraft:stained_glass_pane", 1, (byte) 15, " ",
                        Lists.newArrayList(), Maps.newHashMap()
        )));

        private PositionableConfigItem backButton = new PositionableConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 0, 0, Maps.newHashMap()
        );

        private List<Integer> partySelectionPositions = Lists.newArrayList(
                10, 11, 12, 14, 15, 16
        );

        private int confirmDisplay = 13;

        private PositionableConfigItem confirmItem = new PositionableConfigItem(
                "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
                Lists.newArrayList(), 4, 2, Maps.newHashMap()
        );

        private PositionableConfigItem viewPCButton = new PositionableConfigItem(
                "pixelmon:blue_orb", 1, (byte) 0, "&aYour PC",
                Lists.newArrayList(), 4, 0, Maps.newHashMap()
        );

        private ConfigItem noPokemonItem = new ConfigItem(
                "minecraft:barrier",
                1, (byte) 0, "&cNo pokemon in this position",
                Lists.newArrayList(), Maps.newHashMap()
        );

        public PartyPokemonConfig() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public PositionableConfigItem getBackButton() {
            return this.backButton;
        }

        public List<Integer> getPartySelectionPositions() {
            return this.partySelectionPositions;
        }

        public int getConfirmDisplay() {
            return this.confirmDisplay;
        }

        public PositionableConfigItem getConfirmItem() {
            return this.confirmItem;
        }

        public PositionableConfigItem getViewPCButton() {
            return this.viewPCButton;
        }

        public ConfigItem getNoPokemonItem() {
            return this.noPokemonItem;
        }
    }

    @ConfigSerializable
    public static class SelectFromPCConfig {

        private ConfigInterface guiSettings = new ConfigInterface(
                "Reforged GTS", 6, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                "minecraft:stained_glass_pane", 1, (byte) 15, " ",
                Lists.newArrayList(), Maps.newHashMap()
        )));

        private PositionableConfigItem backButton = new PositionableConfigItem(
                "pixelmon:eject_button", 1, (byte) 0, "&cBack",
                Lists.newArrayList(), 0, 5, Maps.newHashMap()
        );

        private PositionableConfigItem nextPageButton = new PositionableConfigItem(
                "pixelmon:trade_holder_right", 1, (byte) 0, "&aNext Page",
                Lists.newArrayList(), 8, 0, Maps.newHashMap()
        );

        private PositionableConfigItem previousPageButton = new PositionableConfigItem(
                "pixelmon:trade_holder_left", 1, (byte) 0, "&aPrevious Page",
                Lists.newArrayList(), 0, 0, Maps.newHashMap()
        );

        private PositionableConfigItem confirmButton = new PositionableConfigItem(
                "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
                Lists.newArrayList(), 7, 3, Maps.newHashMap()
        );

        private ConfigItem noPokemonItem = new ConfigItem(
                "minecraft:barrier",
                1, (byte) 0, "&cNo pokemon in this position",
                Lists.newArrayList(), Maps.newHashMap()
        );

        private int perPage = 30;

        private int confirmSlot = 25;

        public SelectFromPCConfig() {}

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public PositionableConfigItem getBackButton() {
            return this.backButton;
        }

        public PositionableConfigItem getNextPageButton() {
            return this.nextPageButton;
        }

        public PositionableConfigItem getPreviousPageButton() {
            return this.previousPageButton;
        }

        public int getPerPage() {
            return this.perPage;
        }

        public PositionableConfigItem getConfirmButton() {
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
                "Reforged GTS", 3, "BLOCK", ImmutableMap.of("one", new ConfigItem(
                "minecraft:stained_glass_pane", 1, (byte) 15, " ",
                Lists.newArrayList(), Maps.newHashMap()
        )));

        private int pokemonPosition = 10;

        private PositionableConfigItem minPriceItem = new PositionableConfigItem(
                "pixelmon:relic_gold", 1, (byte) 0, "&bMin Price: &a$%min_price%",
                Lists.newArrayList(), 2, 1, Maps.newHashMap()
        );

        private PositionableConfigItem modifyPriceButton = new PositionableConfigItem(
                "pixelmon:relic_silver", 1, (byte) 0, "&bEdit Price",
                Lists.newArrayList("&bCurrent Price: &e%price%"), 4, 2, Maps.newHashMap()
        );

        private PositionableConfigItem modifyDurationButton = new PositionableConfigItem(
                "minecraft:clock", 1, (byte) 0, "&bEdit Duration",
                Lists.newArrayList("&bDuration: &e%time%"), 5, 1, Maps.newHashMap()
        );

        private PositionableConfigItem confirmItem = new PositionableConfigItem(
                "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
                Lists.newArrayList(), 7, 1, Maps.newHashMap()
        );

        public PokemonPriceConfig() {
        }

        public ConfigInterface getGuiSettings() {
            return this.guiSettings;
        }

        public int getPokemonPosition() {
            return this.pokemonPosition;
        }

        public PositionableConfigItem getMinPriceItem() {
            return this.minPriceItem;
        }

        public PositionableConfigItem getModifyPriceButton() {
            return this.modifyPriceButton;
        }

        public PositionableConfigItem getModifyDurationButton() {
            return this.modifyDurationButton;
        }

        public PositionableConfigItem getConfirmItem() {
            return this.confirmItem;
        }
    }
}
