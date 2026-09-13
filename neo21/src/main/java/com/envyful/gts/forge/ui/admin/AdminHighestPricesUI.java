package com.envyful.gts.forge.ui.admin;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.config.UtilConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.time.UtilTime;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.item.TradeItemType;
import com.envyful.gts.forge.api.item.TradeItemTypeFactory;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.ui.TradeFilter;
import com.envyful.gts.forge.ui.TradeFilterConfig;
import com.envyful.gts.forge.ui.TradeHistoryDisplay;
import com.envyful.gts.forge.ui.TradeWindowConfig;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import com.pixelmonmod.pixelmon.api.dialogue.InputPattern;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.awt.Color;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@ConfigSerializable
public class AdminHighestPricesUI {

    private PaginatedConfigInterface pricesSettings = PaginatedConfigInterface.paginatedBuilder()
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

    private TradeFilterConfig filterConfig = new TradeFilterConfig(
            new TradeWindowConfig("1d", List.of("1h", "1d", "7d", "30d")));

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
            .positions(Pair.of(6, 5))
            .build();

    private ExtendedConfigItem priceTypeButton = ExtendedConfigItem.builder()
            .type("pixelmon:poke_ball")
            .amount(1)
            .name("&bType: &f%type%")
            .lore("&7Click to switch between all, pokemon, and items")
            .positions(Pair.of(3, 5))
            .build();

    private ExtendedConfigItem priceWindowButton = ExtendedConfigItem.builder()
            .type("minecraft:clock")
            .amount(1)
            .name("&eWindow: &f%window%")
            .lore("&7Click to cycle quick windows")
            .positions(Pair.of(5, 5))
            .build();

    private List<String> listingLore = List.of(
            " ",
            "&6Sold for &a$%sold_price%",
            "&bSeller: &f%seller%",
            "&bBuyer: &f%buyer%",
            "&bDate: &f%outcome_date%",
            " ",
            "&eClick for full details"
    );

    public void openInput(ForgeEnvyPlayer player) {
        player.getParent().closeContainer();

        PlatformProxy.runLater(() -> this.inputBuilder(player, false)
                .sendTo(player.getParent()), 5);
    }

    public void openHighestPrices(ForgeEnvyPlayer player, Duration window, @Nullable TradeItemType type) {
        this.openHighestPrices(player, TradeFilter.of(window).withItemType(type), 1);
    }

    public void openHighestPrices(ForgeEnvyPlayer player, TradeFilter filter, int page) {
        UtilConcurrency.runAsync(() -> {
            var history = EnvyGTSForge.getTradeService().highestPrices(filter.toQuery());

            TradeHistoryDisplay.reportFailures(player, history);

            if (history.trades().isEmpty()) {
                this.filterConfig.reportNoSales(player, filter);
            }

            this.openPane(player, history.trades(), filter, page);
        });
    }

    @SuppressWarnings("unchecked")
    private void openPane(ForgeEnvyPlayer player, List<Trade> trades, TradeFilter filter, int page) {
        var openPage = new AtomicInteger(page);
        var placeholder = this.filterConfig.placeholder(filter);

        UtilConfigInterface.paginatedBuilder(trades)
                .itemConversion(trade -> TradeHistoryDisplay.build(trade, this.listingLore)
                        .singleClick()
                        .asyncClick()
                        .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradeDetailUI()
                                .openDetails(player, trade, () -> this.openPane(player, trades, filter, openPage.get())))
                        .build())
                .configSettings(this.pricesSettings)
                .extraItems((pane, currentPage) -> {
                    openPage.set(currentPage);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradesUI().openMenu(player))
                            .extendedConfigItem(player, pane, this.backButton);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.filterConfig.openItemSearch(player, filter,
                                    searched -> this.openHighestPrices(player, searched, 1)))
                            .extendedConfigItem(player, pane, this.searchButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.filterConfig.openPlayerSearch(player, filter,
                                    searched -> this.openHighestPrices(player, searched, 1)))
                            .extendedConfigItem(player, pane, this.playerButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.openHighestPrices(player,
                                    filter.withItemType(TradeItemTypeFactory.getNext(filter.itemType())), 1))
                            .extendedConfigItem(player, pane, this.priceTypeButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.openHighestPrices(player,
                                    filter.withWindow(this.filterConfig.getWindowConfig().getNext(filter.window())), 1))
                            .extendedConfigItem(player, pane, this.priceWindowButton, placeholder);
                })
                .open(player, page);
    }

    private DialogueFactory.Builder inputBuilder(ForgeEnvyPlayer player, boolean error) {
        return DialogueFactory.builder()
                .title(PlatformProxy.<Component>flatParse("&6Highest Sold Prices"))
                .description(UtilChatColour.colour(error ?
                        "&cInvalid time window. Use values like 24h or 7d." :
                        "&7Enter a time window, for example 1h, 24h, 7d, or 30d."))
                .defaultText(TradeWindowConfig.format(this.filterConfig.getWindowConfig().getDefaultWindow()))
                .maxInputLength(10)
                .closeOnEscape()
                .hideUI()
                .onClose(closedScreen -> EnvyGTSForge.getGui().getAdminTradesUI().openMenu(player))
                .buttons(DialogueButton.builder()
                        .text("Search")
                        .backgroundColor(Color.GRAY)
                        .acceptedInputs(InputPattern.of(Pattern.compile("[0-9A-Za-z]+"),
                                UtilChatColour.colour("&cEnter a time window like 24h or 7d.")))
                        .onClick(submitted -> {
                            var parsedDuration = UtilTime.attemptParseTime(submitted.getInput());

                            if (parsedDuration.isEmpty() || parsedDuration.get() <= 0) {
                                submitted.setSettings(this.inputBuilder(player, true).createSettings());
                                submitted.setCloseUI(false);
                                return;
                            }

                            this.openHighestPrices(player, Duration.ofMillis(parsedDuration.get()), null);
                        })
                        .build());
    }
}
