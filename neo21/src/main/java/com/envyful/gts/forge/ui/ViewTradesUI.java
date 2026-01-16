package com.envyful.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.neoforge.config.UtilConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.gui.type.ConfirmationUI;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.CollectionItem;
import com.envyful.gts.forge.api.Sale;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.gui.FilterTypeFactory;
import com.envyful.gts.forge.api.gui.SortType;
import com.envyful.gts.forge.api.trade.ActiveTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.event.TradeViewFilterEvent;
import com.envyful.gts.forge.event.TradesGUISetupEvent;
import com.envyful.gts.forge.player.GTSAttribute;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class ViewTradesUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 1, FilterTypeFactory.getDefault(), SortType.MOST_RECENT);
    }

    @SuppressWarnings("unchecked")
    public static void openUI(ForgeEnvyPlayer player, int page, FilterType filter, SortType sort) {
        var config = EnvyGTSForge.getGui().getSearchUIConfig();
        var allTrades = getAllVisibleTrades(player, filter, sort);

        UtilConfigInterface.paginatedBuilder(allTrades)
                .itemConversion(trade -> GuiFactory.displayableBuilder(trade.offer().item().display())
                        .clickHandler((envyPlayer, clickType) -> {
                            var updatedTrade = EnvyGTSForge.getTradeService().activeListing(trade.offer().id());

                            if (!(updatedTrade instanceof ActiveTrade)) {
                                player.message(EnvyGTSForge.getLocale().getMessages().getTradeNoLongerAvailable());
                                return;
                            }

                            var activeTrade = (ActiveTrade) updatedTrade;

                            //TODO: check auction status before handling click when active
                            if (canAdminRemove(envyPlayer, clickType)) {
                                player.message(EnvyGTSForge.getLocale().getMessages().getAdminRemoveTrade());

                                if (trade.offer().seller().getPlayer() != null) {
                                    trade.offer().seller().getPlayer().message(
                                            EnvyGTSForge.getLocale().getMessages().getTradeRemovedByAdmin()
                                    );
                                }

                                EnvyGTSForge.getTradeService().adminRemoveListing(activeTrade);
                                openUI(player, page, filter, sort);
                                activeTrade.offer().item().collect(envyPlayer);
                                return;
                            }

                            if (isOwnerRemoveClick(clickType) && activeTrade.isSeller(envyPlayer)) {
                                EnvyGTSForge.getTradeService().ownerRemoveListing(activeTrade);
                                openUI(player, page, filter, sort);

                                activeTrade.offer().item().collect(envyPlayer);
                                player.message(EnvyGTSForge.getLocale().getMessages().getRemovedOwnTrade());
                                return;
                            }

                            if (activeTrade.isSeller(envyPlayer)) {
                                player.message(EnvyGTSForge.getLocale().getMessages().getCannotPurchaseOwnTrade());
                                return;
                            }

                            ConfirmationUI.builder()
                                    .player(envyPlayer)
                                    .playerManager(EnvyGTSForge.getPlayerManager())
                                    .config(EnvyGTSForge.getGui().getSearchUIConfig().getConfirmGuiConfig())
                                    .descriptionItem(activeTrade.offer().item().display())
                                    .confirmHandler((clicker, clickType1) ->
                                            PlatformProxy.runSync(() -> {
                                                var tradeAfterClick = EnvyGTSForge.getTradeService().activeListing(activeTrade.offer().id());

                                                if (!(tradeAfterClick instanceof ActiveTrade)) {
                                                    player.message(EnvyGTSForge.getLocale().getMessages().getTradeNoLongerAvailable());
                                                    openUI(player, page, filter, sort);
                                                    return;
                                                }

                                                if (attemptPurchase(clicker, tradeAfterClick)) {
                                                    openUI(player, page, filter, sort);
                                                    player.message(EnvyGTSForge.getLocale().getMessages().getPurchasedTrade());
                                                    return;
                                                }

                                                openUI(player, page, filter, sort);
                                                player.message(EnvyGTSForge.getLocale().getMessages().getInsufficientFunds());
                                            }))
                                    .returnHandler((envyPlayer1, clickType1) -> ViewTradesUI.openUI((ForgeEnvyPlayer) envyPlayer))
                                    .open();
                        })
                        .build())
                .configSettings(config.getGuiSettings())
                .extraItems((pane, currentPage) -> {
                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> SellHandOrParty.open(player))
                            .extendedConfigItem(player, pane, config.getSellButton());

                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> ReturnsUI.openUI(player))
                            .extendedConfigItem(player, pane, config.getReturnsButton());

                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> openUI(player, page, filter, sort.getNext()))
                            .extendedConfigItem(player, pane, config.getOrderButton(),
                                    (SimplePlaceholder) name -> name
                                            .replace("%filter%", filter.getDisplayName())
                                            .replace("%order%", sort.getDisplayName()));

                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> openUI(player, page, filter.getNext(), sort))
                            .extendedConfigItem(player, pane, filter.getDisplay(),
                                    (SimplePlaceholder) name -> name
                                            .replace("%filter%", filter.getDisplayName())
                                            .replace("%order%", sort.getDisplayName()));

                    NeoForge.EVENT_BUS.post(new TradesGUISetupEvent(player, pane, page, filter, sort));
                })
                .open(player, page);
    }

    private static List<Trade> getAllVisibleTrades(ForgeEnvyPlayer player, FilterType filter, SortType sortType) {
        List<Trade> allTrades = new ArrayList<>(EnvyGTSForge.getTradeService().activeListings());

        allTrades.removeIf(trade -> !filter.isAllowed(player, trade));
        var filterEvent = new TradeViewFilterEvent(player, allTrades, filter);

        NeoForge.EVENT_BUS.post(filterEvent);

        allTrades = filterEvent.getTrades();
        allTrades.sort(sortType::compare);

        return allTrades;
    }

    private static boolean canAdminRemove(EnvyPlayer<?> player, Displayable.ClickType clickType) {
        if (!isOwnerRemoveClick(clickType)) {
            return false;
        }

        return player.hasPermission("envygts.admin");
    }

    private static boolean isOwnerRemoveClick(Displayable.ClickType clickType){
        return clickType == EnvyGTSForge.getConfig().getOwnerRemoveButton();
    }

    private static boolean attemptPurchase(EnvyPlayer<?> player, Trade trade) {
        var gtsAttribute = player.getAttributeNow(GTSAttribute.class);
        var bank = ((ForgeEnvyPlayer) player).getParent().getPartyNow();

        if (bank.getBalance().doubleValue() < trade.offer().price().getPrice()) {
            return false;
        }

        var sale = new Sale(trade, (ForgeEnvyPlayer) player);

        bank.take(trade.offer().price().getPrice());
        EnvyGTSForge.getTradeService().addSale(sale);
        gtsAttribute.addCollectionItem(new CollectionItem(trade, sale));

        return true;
    }
}
