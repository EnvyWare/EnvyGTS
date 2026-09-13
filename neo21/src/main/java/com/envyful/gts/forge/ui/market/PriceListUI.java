package com.envyful.gts.forge.ui.market;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.ui.TradeFilter;
import com.envyful.gts.forge.ui.TradeHistoryDisplay;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * The UI showing players the sales behind the highest and lowest prices in the {@link PriceStatisticsUI}
 *
 */
@ConfigSerializable
public class PriceListUI {

    private PaginatedConfigInterface highestPricesSettings = PaginatedConfigInterface.paginatedBuilder()
            .title("EnvyGTS Highest Prices")
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

    private PaginatedConfigInterface lowestPricesSettings = PaginatedConfigInterface.paginatedBuilder()
            .title("EnvyGTS Lowest Prices")
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

    private List<String> listingLore = List.of(
            " ",
            "&6Sold for &a$%sold_price%",
            "&7Listed for &a$%listed_price%",
            "&bSeller: &f%seller%",
            "&bDate: &f%outcome_date%",
            " ",
            "&eClick for more details"
    );

    public void openUI(ForgeEnvyPlayer player, PriceOrder order, TradeFilter filter) {
        this.openUI(player, order, filter, 1);
    }

    public void openUI(ForgeEnvyPlayer player, PriceOrder order, TradeFilter filter, int page) {
        UtilConcurrency.runAsync(() -> {
            var history = order.lookup(filter.toQuery());

            TradeHistoryDisplay.reportFailures(player, history);

            if (history.trades().isEmpty()) {
                EnvyGTSForge.getGui().getPriceStatisticsUI().getFilterConfig().reportNoSales(player, filter);
            }

            this.openPane(player, history.trades(), order, filter, page);
        });
    }

    @SuppressWarnings("unchecked")
    private void openPane(ForgeEnvyPlayer player, List<Trade> trades, PriceOrder order, TradeFilter filter, int page) {
        var openPage = new AtomicInteger(page);
        var settings = order == PriceOrder.HIGHEST ? this.highestPricesSettings : this.lowestPricesSettings;

        UtilConfigInterface.paginatedBuilder(trades)
                .itemConversion(trade -> TradeHistoryDisplay.build(trade, this.listingLore)
                        .singleClick()
                        .asyncClick()
                        .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getMarketTradeDetailUI()
                                .openDetails(player, trade,
                                        () -> this.openPane(player, trades, order, filter, openPage.get())))
                        .build())
                .configSettings(settings)
                .extraItems((pane, currentPage) -> {
                    openPage.set(currentPage);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getPriceStatisticsUI()
                                    .openUI(player, filter))
                            .extendedConfigItem(player, pane, this.backButton);
                })
                .open(player, page);
    }
}
