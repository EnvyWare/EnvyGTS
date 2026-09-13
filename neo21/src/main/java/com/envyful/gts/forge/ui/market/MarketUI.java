package com.envyful.gts.forge.ui.market;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 * The hub the player reaches from the main GTS UI, for looking into what the market has been doing
 *
 */
@ConfigSerializable
public class MarketUI {

    private ConfigInterface menuSettings = ConfigInterface.builder()
            .title("EnvyGTS Market")
            .height(3)
            .fillType(ConfigInterface.FillType.BLOCK)
            .fillerItem(ConfigItem.builder()
                    .type("minecraft:black_stained_glass_pane")
                    .name(" ")
                    .amount(1)
                    .build())
            .build();

    private ExtendedConfigItem recentTradesButton = ExtendedConfigItem.builder()
            .type("minecraft:book")
            .amount(1)
            .name("&bRecent Trades")
            .lore("&7See what has been bought, expired,", "&7and removed on the GTS recently")
            .positions(Pair.of(2, 1))
            .build();

    private ExtendedConfigItem priceStatisticsButton = ExtendedConfigItem.builder()
            .type("minecraft:gold_ingot")
            .amount(1)
            .name("&6Sell Prices")
            .lore("&7See the highest, lowest, and mean", "&7prices things have sold for")
            .positions(Pair.of(6, 1))
            .build();

    private ExtendedConfigItem myTradesButton = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bMy Trades")
            .lore("&7See everything you have", "&7sold and bought")
            .positions(Pair.of(4, 1))
            .build();

    private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
            .type("pixelmon:eject_button")
            .amount(1)
            .name("&cBack")
            .positions(Pair.of(4, 2))
            .build();

    public void openMenu(ForgeEnvyPlayer player) {
        var pane = this.menuSettings.toPane();

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getRecentTradesUI().openUI(player))
                .extendedConfigItem(player, pane, this.recentTradesButton);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getPriceStatisticsUI().openUI(player))
                .extendedConfigItem(player, pane, this.priceStatisticsButton);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getRecentTradesUI()
                        .openOwnTrades(player))
                .extendedConfigItem(player, pane, this.myTradesButton);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getViewTradesUI().openUI(player))
                .extendedConfigItem(player, pane, this.backButton);

        pane.open(player, this.menuSettings);
    }
}
