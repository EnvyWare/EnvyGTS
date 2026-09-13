package com.envyful.gts.forge.ui.market;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.item.TradeItemTypeFactory;
import com.envyful.gts.forge.api.trade.PriceStatistics;
import com.envyful.gts.forge.ui.TradeFilter;
import com.envyful.gts.forge.ui.TradeFilterConfig;
import com.envyful.gts.forge.ui.TradeHistoryDisplay;
import com.envyful.gts.forge.ui.TradeWindowConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

/**
 *
 * The UI showing players the highest, lowest, and mean price things have sold for on the GTS
 *
 */
@ConfigSerializable
public class PriceStatisticsUI {

    private ConfigInterface menuSettings = ConfigInterface.builder()
            .title("EnvyGTS Sell Prices")
            .height(5)
            .fillType(ConfigInterface.FillType.BLOCK)
            .fillerItem(ConfigItem.builder()
                    .type("minecraft:black_stained_glass_pane")
                    .name(" ")
                    .amount(1)
                    .build())
            .build();

    private TradeFilterConfig filterConfig = new TradeFilterConfig(
            new TradeWindowConfig("1d", List.of("1d", "3d", "7d", "30d")));

    private ExtendedConfigItem summaryItem = ExtendedConfigItem.builder()
            .type("minecraft:paper")
            .amount(1)
            .name("&6Sell Prices")
            .lore(
                    "&7What things have sold for",
                    " ",
                    "&bSearch: &f%search%",
                    "&bPlayer: &f%player%",
                    "&bShowing: &f%type%",
                    "&bWindow: &f%window%",
                    "&bSales: &f%sales%"
            )
            .positions(Pair.of(4, 1))
            .build();

    private ExtendedConfigItem highestItem = ExtendedConfigItem.builder()
            .type("minecraft:gold_block")
            .amount(1)
            .name("&6Highest: &a$%highest%")
            .lore("&7The most &f%search% &7has sold for", " ", "&eClick to see the biggest sales")
            .positions(Pair.of(2, 2))
            .build();

    private ExtendedConfigItem meanItem = ExtendedConfigItem.builder()
            .type("minecraft:gold_ingot")
            .amount(1)
            .name("&eMean: &a$%mean%")
            .lore("&7The average of the &f%sales% &7sale(s)", "&7in this window")
            .positions(Pair.of(4, 2))
            .build();

    private ExtendedConfigItem lowestItem = ExtendedConfigItem.builder()
            .type("minecraft:gold_nugget")
            .amount(1)
            .name("&aLowest: &a$%lowest%")
            .lore("&7The least &f%search% &7has sold for", " ", "&eClick to see the cheapest sales")
            .positions(Pair.of(6, 2))
            .build();

    private ExtendedConfigItem noSalesItem = ExtendedConfigItem.builder()
            .type("minecraft:barrier")
            .amount(1)
            .name("&cNo Sales")
            .lore("&7Nothing matching &f%search% &7has sold", "&7in the last &f%window%")
            .positions(Pair.of(4, 2))
            .build();

    private ExtendedConfigItem searchButton = ExtendedConfigItem.builder()
            .type("minecraft:oak_sign")
            .amount(1)
            .name("&bSearch: &f%search%")
            .lore("&7Click to price up a Pokemon", "&7species or an item by name")
            .positions(Pair.of(3, 4))
            .build();

    private ExtendedConfigItem playerButton = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bPlayer: &f%player%")
            .lore("&7Click to only price up the", "&7trades a player sold or bought")
            .positions(Pair.of(5, 4))
            .build();

    private ExtendedConfigItem typeButton = ExtendedConfigItem.builder()
            .type("pixelmon:poke_ball")
            .amount(1)
            .name("&bShowing: &f%type%")
            .lore("&7Click to switch between all,", "&7pokemon, and items")
            .positions(Pair.of(2, 4))
            .build();

    private ExtendedConfigItem windowButton = ExtendedConfigItem.builder()
            .type("minecraft:clock")
            .amount(1)
            .name("&eLast &f%window%")
            .lore("&7Click to change how far back", "&7the prices shown go")
            .positions(Pair.of(6, 4))
            .build();

    private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
            .type("pixelmon:eject_button")
            .amount(1)
            .name("&cBack")
            .positions(Pair.of(4, 4))
            .build();

    public void openUI(ForgeEnvyPlayer player) {
        this.openUI(player, this.filterConfig.defaultFilter());
    }

    public void openSearch(ForgeEnvyPlayer player, String search) {
        this.openUI(player, this.filterConfig.defaultFilter().withSearch(search));
    }

    public void openPlayer(ForgeEnvyPlayer player, String searchedPlayer) {
        this.openUI(player, this.filterConfig.defaultFilter().withPlayer(searchedPlayer));
    }

    public void openUI(ForgeEnvyPlayer player, TradeFilter filter) {
        UtilConcurrency.runAsync(() -> this.openPane(player,
                EnvyGTSForge.getTradeService().priceStatistics(filter.toQuery()), filter));
    }

    private void openPane(ForgeEnvyPlayer player, PriceStatistics statistics, TradeFilter filter) {
        var pane = this.menuSettings.toPane();
        var placeholder = this.placeholder(statistics, filter);

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, this.summaryItem, placeholder);

        if (statistics.hasSales()) {
            UtilConfigItem.builder()
                    .asyncClick(false)
                    .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getPriceListUI()
                            .openUI(player, PriceOrder.HIGHEST, filter))
                    .extendedConfigItem(player, pane, this.highestItem, placeholder);

            UtilConfigItem.builder()
                    .extendedConfigItem(player, pane, this.meanItem, placeholder);

            UtilConfigItem.builder()
                    .asyncClick(false)
                    .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getPriceListUI()
                            .openUI(player, PriceOrder.LOWEST, filter))
                    .extendedConfigItem(player, pane, this.lowestItem, placeholder);
        } else {
            UtilConfigItem.builder()
                    .extendedConfigItem(player, pane, this.noSalesItem, placeholder);
        }

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> this.filterConfig.openItemSearch(player, filter,
                        searched -> this.openUI(player, searched)))
                .extendedConfigItem(player, pane, this.searchButton, placeholder);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> this.filterConfig.openPlayerSearch(player, filter,
                        searched -> this.openUI(player, searched)))
                .extendedConfigItem(player, pane, this.playerButton, placeholder);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> this.openUI(player,
                        filter.withItemType(TradeItemTypeFactory.getNext(filter.itemType()))))
                .extendedConfigItem(player, pane, this.typeButton, placeholder);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> this.openUI(player,
                        filter.withWindow(this.filterConfig.getWindowConfig().getNext(filter.window()))))
                .extendedConfigItem(player, pane, this.windowButton, placeholder);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getMarketUI().openMenu(player))
                .extendedConfigItem(player, pane, this.backButton);

        pane.open(player, this.menuSettings);
    }

    public TradeFilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    private SimplePlaceholder placeholder(PriceStatistics statistics, TradeFilter filter) {
        var filters = this.filterConfig.placeholder(filter);

        return line -> filters.replace(line)
                .replace("%sales%", String.valueOf(statistics.sales()))
                .replace("%highest%", TradeHistoryDisplay.formatMoney(statistics.highest()))
                .replace("%lowest%", TradeHistoryDisplay.formatMoney(statistics.lowest()))
                .replace("%mean%", TradeHistoryDisplay.formatMoney(statistics.mean()));
    }
}
