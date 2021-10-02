package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/ReforgedGTS/config.yml")
@ConfigSerializable
public class ReforgedGTSConfig extends AbstractYamlConfig {

    private SQLDatabaseDetails databaseDetails = new SQLDatabaseDetails("ReforgedGTS", "0.0.0.0", 3306, "admin",
                                                                        "password", "database");
    private ConfigInterface guiSettings = new ConfigInterface();

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

    private ConfigItem noPokemonItem = new ConfigItem(
            "minecraft:barrier",
            1, (byte) 0, "&cNo pokemon in this position",
            Lists.newArrayList(), Maps.newHashMap()
    );

    private int tradeDurationSeconds = 86400;

    private List<Integer> partySelectionPositions = Lists.newArrayList(
            10, 11, 12, 14, 15, 16
    );

    private int confirmDisplay = 13;

    private PositionableConfigItem confirmItem = new PositionableConfigItem(
            "pixelmon:poke_ball", 1, (byte) 0, "&a&lCONFIRM",
            Lists.newArrayList(), 2, 4, Maps.newHashMap()
    );

    public ReforgedGTSConfig() {
        super();
    }

    public PositionableConfigItem getConfirmItem() {
        return this.confirmItem;
    }

    public int getConfirmDisplay() {
        return this.confirmDisplay;
    }

    public ConfigItem getNoPokemonItem() {
        return this.noPokemonItem;
    }

    public List<Integer> getPartySelectionPositions() {
        return this.partySelectionPositions;
    }

    public PositionableConfigItem getSellItemButton() {
        return this.sellItemButton;
    }

    public PositionableConfigItem getViewTimeoutButton() {
        return this.viewTimeoutButton;
    }

    public PositionableConfigItem getViewClaimsButton() {
        return this.viewClaimsButton;
    }

    public PositionableConfigItem getViewTradesButton() {
        return this.viewTradesButton;
    }

    public SQLDatabaseDetails getDatabaseDetails() {
        return this.databaseDetails;
    }

    public int getTradeDurationSeconds() {
        return this.tradeDurationSeconds;
    }

    public ConfigInterface getGuiSettings() {
        return this.guiSettings;
    }
}
