package com.envyful.gts.forge.ui.admin;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.item.TradeItemTypeFactory;
import com.envyful.gts.forge.api.trade.SoldTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.ui.TradeFilter;
import com.envyful.gts.forge.ui.TradeFilterConfig;
import com.envyful.gts.forge.ui.TradeHistoryDisplay;
import com.envyful.gts.forge.ui.TradeWindowConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ConfigSerializable
public class AdminTradeHistoryUI {

    private PaginatedConfigInterface historySettings = PaginatedConfigInterface.paginatedBuilder()
            .title("EnvyGTS History")
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

    private TradeFilterConfig filterConfig = new TradeFilterConfig(
            new TradeWindowConfig("1d", List.of("1d", "7d", "30d"), true));

    private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
            .type("pixelmon:eject_button")
            .amount(1)
            .name("&cBack")
            .positions(Pair.of(4, 5))
            .build();

    private ExtendedConfigItem searchButton = ExtendedConfigItem.builder()
            .type("minecraft:oak_sign")
            .amount(1)
            .name("&bSearch: &f%search%")
            .lore("&7Click to look up a Pokemon", "&7species or an item by name")
            .positions(Pair.of(2, 5))
            .build();

    private ExtendedConfigItem playerButton = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bPlayer: &f%player%")
            .lore("&7Click to search by player", "&7name or UUID")
            .positions(Pair.of(5, 5))
            .build();

    private ExtendedConfigItem typeButton = ExtendedConfigItem.builder()
            .type("pixelmon:poke_ball")
            .amount(1)
            .name("&bShowing: &f%type%")
            .lore("&7Click to switch between all,", "&7pokemon, and items")
            .positions(Pair.of(3, 5))
            .build();

    private ExtendedConfigItem windowButton = ExtendedConfigItem.builder()
            .type("minecraft:clock")
            .amount(1)
            .name("&eWindow: &f%window%")
            .lore("&7Click to change how far back", "&7the trades shown go")
            .positions(Pair.of(6, 5))
            .build();

    private List<String> soldListingLore = List.of(
            " ",
            "&bOutcome: &f%outcome%",
            "&bSeller: &f%seller%",
            "&bBuyer: &f%buyer%",
            "&bDate: &f%outcome_date%",
            " ",
            "&eClick for full details"
    );

    private List<String> unsoldListingLore = List.of(
            " ",
            "&bOutcome: &f%outcome%",
            "&bSeller: &f%seller%",
            "&bDate: &f%outcome_date%",
            " ",
            "&eClick for full details"
    );

    public void openGlobalHistory(ForgeEnvyPlayer player) {
        this.openHistory(player, TradeFilter.ALL_TIME, 1);
    }

    public void openPlayerHistory(ForgeEnvyPlayer player, String playerQuery) {
        this.openHistory(player, TradeFilter.ALL_TIME.withPlayer(playerQuery), 1);
    }

    /**
     *
     * Asks which player to look up before opening the history filtered to them
     *
     */
    public void openPlayerHistoryInput(ForgeEnvyPlayer player) {
        this.filterConfig.openPlayerSearch(player, TradeFilter.ALL_TIME,
                searched -> this.openHistory(player, searched, 1),
                () -> EnvyGTSForge.getGui().getAdminTradesUI().openMenu(player));
    }

    public void openHistory(ForgeEnvyPlayer player, TradeFilter filter, int page) {
        UtilConcurrency.runAsync(() -> {
            var history = EnvyGTSForge.getTradeService().completedListings(filter.toQuery());

            TradeHistoryDisplay.reportFailures(player, history);

            if (history.trades().isEmpty()) {
                this.filterConfig.reportNoTrades(player, filter);
            }

            this.openPane(player, history.trades(), filter, page);
        });
    }

    @SuppressWarnings("unchecked")
    private void openPane(ForgeEnvyPlayer player, List<Trade> trades, TradeFilter filter, int page) {
        var openPage = new AtomicInteger(page);
        var placeholder = this.filterConfig.placeholder(filter);

        UtilConfigInterface.paginatedBuilder(trades)
                .itemConversion(trade -> TradeHistoryDisplay.build(trade, this.listingLore(trade))
                        .singleClick()
                        .asyncClick()
                        .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradeDetailUI()
                                .openDetails(player, trade, () -> this.openPane(player, trades, filter, openPage.get())))
                        .build())
                .configSettings(this.historySettings)
                .extraItems((pane, currentPage) -> {
                    openPage.set(currentPage);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradesUI().openMenu(player))
                            .extendedConfigItem(player, pane, this.backButton);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.filterConfig.openItemSearch(player, filter,
                                    searched -> this.openHistory(player, searched, 1)))
                            .extendedConfigItem(player, pane, this.searchButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.filterConfig.openPlayerSearch(player, filter,
                                    searched -> this.openHistory(player, searched, 1)))
                            .extendedConfigItem(player, pane, this.playerButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.openHistory(player,
                                    filter.withItemType(TradeItemTypeFactory.getNext(filter.itemType())), 1))
                            .extendedConfigItem(player, pane, this.typeButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.openHistory(player,
                                    filter.withWindow(this.filterConfig.getWindowConfig().getNext(filter.window())), 1))
                            .extendedConfigItem(player, pane, this.windowButton, placeholder);
                })
                .open(player, page);
    }

    private List<String> listingLore(Trade trade) {
        return trade instanceof SoldTrade ? this.soldListingLore : this.unsoldListingLore;
    }
}
