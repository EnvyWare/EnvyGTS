package com.envyful.gts.forge.ui;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.neoforge.config.UtilConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.config.yaml.YamlOps;
import com.envyful.api.neoforge.gui.type.ConfirmationUI;
import com.envyful.api.neoforge.items.ItemBuilder;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.PlaceholderFactory;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.CollectionItem;
import com.envyful.gts.forge.api.Sale;
import com.envyful.gts.forge.api.event.TradePurchaseEvent;
import com.envyful.gts.forge.api.event.TradeViewFilterEvent;
import com.envyful.gts.forge.api.event.TradesGUISetupEvent;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.gui.FilterTypeFactory;
import com.envyful.gts.forge.api.gui.SortType;
import com.envyful.gts.forge.api.player.GTSAttribute;
import com.envyful.gts.forge.api.trade.ActiveTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.init.registry.PixelmonDataComponents;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ViewTradesUI {

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

    public void openUI(ForgeEnvyPlayer player) {
        openUI(player, 1, FilterTypeFactory.getDefault(), SortType.MOST_RECENT);
    }

    @SuppressWarnings("unchecked")
    public void openUI(ForgeEnvyPlayer player, int page, FilterType filter, SortType sort) {
        var allTrades = getAllVisibleTrades(player, filter, sort);

        UtilConfigInterface.paginatedBuilder(allTrades)
                .itemConversion(trade -> buildDisplay(trade)
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
                                    .config(this.confirmGuiConfig)
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
                                    .returnHandler((envyPlayer1, clickType1) -> this.openUI((ForgeEnvyPlayer) envyPlayer))
                                    .open();
                        })
                        .build())
                .configSettings(this.guiSettings)
                .extraItems((pane, currentPage) -> {
                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> SellHandOrParty.open(player))
                            .extendedConfigItem(player, pane, this.sellButton);

                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> ReturnsUI.openUI(player))
                            .extendedConfigItem(player, pane, this.returnsButton);

                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> openUI(player, page, filter, sort.getNext()))
                            .extendedConfigItem(player, pane, this.orderButton,
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

    private List<Trade> getAllVisibleTrades(ForgeEnvyPlayer player, FilterType filter, SortType sortType) {
        List<Trade> allTrades = new ArrayList<>(EnvyGTSForge.getTradeService().activeListings());

        allTrades.removeIf(trade -> !filter.isAllowed(player, trade));
        var filterEvent = new TradeViewFilterEvent(player, allTrades, filter);

        NeoForge.EVENT_BUS.post(filterEvent);

        allTrades = filterEvent.getTrades();
        allTrades.sort(sortType::compare);

        return allTrades;
    }

    private boolean canAdminRemove(EnvyPlayer<?> player, Displayable.ClickType clickType) {
        if (!isOwnerRemoveClick(clickType)) {
            return false;
        }

        return player.hasPermission("envygts.admin");
    }

    private boolean isOwnerRemoveClick(Displayable.ClickType clickType){
        return clickType == EnvyGTSForge.getConfig().getOwnerRemoveButton();
    }

    private boolean attemptPurchase(EnvyPlayer<?> player, Trade trade) {
        var gtsAttribute = player.getAttributeNow(GTSAttribute.class);
        var bank = ((ForgeEnvyPlayer) player).getParent().getBankAccountNow();

        if (bank.getBalance().doubleValue() < trade.offer().price().getPrice()) {
            return false;
        }

        var pre = new TradePurchaseEvent.Pre(trade.offer().seller().getPlayer(), (ForgeEnvyPlayer) player, trade);

        if (NeoForge.EVENT_BUS.post(pre).isCanceled()) {
            return false;
        }

        var sale = new Sale(trade, (ForgeEnvyPlayer) player);

        bank.take(trade.offer().price().getPrice());
        EnvyGTSForge.getTradeService().addSale(sale);
        gtsAttribute.addCollectionItem(new CollectionItem(trade, sale));
        NeoForge.EVENT_BUS.post(new TradePurchaseEvent.Post(trade.offer().seller().getPlayer(), (ForgeEnvyPlayer) player, trade));

        return true;
    }

    private Displayable.Builder<ItemStack> buildDisplay(Trade trade) {
        var item = new ItemBuilder(trade.offer().item().display(trade));

        for (var belowLoreMessage : PlaceholderFactory.handlePlaceholders(EnvyGTSForge.getLocale().getListingBelowDataLore(), trade)) {
            item.addLore(PlatformProxy.<Component>flatParse(belowLoreMessage));
        }

        return GuiFactory.displayableBuilder(item.build());
    }

    public ExtendedConfigItem getFilterButton() {
        return this.filterButton;
    }
}
