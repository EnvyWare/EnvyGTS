package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

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

    private int tradeDurationSeconds = 86400;

    public ReforgedGTSConfig() {
        super();
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
