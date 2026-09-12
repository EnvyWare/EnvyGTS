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
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.api.time.UtilTime;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.api.trade.TradeHistoryItemType;
import com.envyful.gts.forge.api.trade.TradeHistoryItemTypeFactory;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import com.pixelmonmod.pixelmon.api.dialogue.InputPattern;
import net.minecraft.network.chat.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@ConfigSerializable
public class AdminHighestPricesUI {

    private static final Duration DEFAULT_PRICE_WINDOW = Duration.ofHours(24);
    private static final List<Duration> PRICE_WINDOWS = List.of(
            Duration.ofHours(1),
            Duration.ofHours(24),
            Duration.ofDays(7),
            Duration.ofDays(30)
    );

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

    private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
            .type("pixelmon:eject_button")
            .amount(1)
            .name("&cBack")
            .positions(Pair.of(4, 5))
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

    public void openInput(ForgeEnvyPlayer player) {
        player.getParent().closeContainer();

        PlatformProxy.runLater(() -> this.inputBuilder(player, false)
                .sendTo(player.getParent()), 5);
    }

    public void openHighestPrices(ForgeEnvyPlayer player, Duration window, TradeHistoryItemType type) {
        this.openHighestPrices(player, window, type, 1);
    }

    public void openHighestPrices(ForgeEnvyPlayer player, Duration window, TradeHistoryItemType type, int page) {
        UtilConcurrency.runAsync(() -> {
            var history = EnvyGTSForge.getTradeService().highestPrices(Instant.now().minus(window), type);

            AdminTradeDisplay.reportFailures(player, history);
            this.openPane(player, history.trades(), window, type, page);
        });
    }

    @SuppressWarnings("unchecked")
    private void openPane(ForgeEnvyPlayer player, List<Trade> trades, Duration window, TradeHistoryItemType type, int page) {
        var openPage = new AtomicInteger(page);

        UtilConfigInterface.paginatedBuilder(trades)
                .itemConversion(trade -> AdminTradeDisplay.build(trade, true)
                        .singleClick()
                        .asyncClick()
                        .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradeDetailUI()
                                .openDetails(player, trade, () -> this.openPane(player, trades, window, type, openPage.get())))
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
                            .clickHandler((envyPlayer, clickType) -> this.openHighestPrices(player, window, type.getNext()))
                            .extendedConfigItem(player, pane, this.priceTypeButton,
                                    (SimplePlaceholder) input -> input.replace("%type%", type.getDisplayName()));

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.openHighestPrices(player, this.nextWindow(window), type))
                            .extendedConfigItem(player, pane, this.priceWindowButton,
                                    (SimplePlaceholder) input -> input.replace("%window%", this.formatDuration(window)));
                })
                .open(player, page);
    }

    private DialogueFactory.Builder inputBuilder(ForgeEnvyPlayer player, boolean error) {
        return DialogueFactory.builder()
                .title(PlatformProxy.<Component>flatParse("&6Highest Sold Prices"))
                .description(UtilChatColour.colour(error ?
                        "&cInvalid time window. Use values like 24h or 7d." :
                        "&7Enter a time window, for example 1h, 24h, 7d, or 30d."))
                .defaultText(this.formatDuration(DEFAULT_PRICE_WINDOW))
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

                            this.openHighestPrices(player, Duration.ofMillis(parsedDuration.get()), TradeHistoryItemTypeFactory.getDefault());
                        })
                        .build());
    }

    private Duration nextWindow(Duration current) {
        for (int i = 0; i < PRICE_WINDOWS.size(); i++) {
            if (PRICE_WINDOWS.get(i).equals(current)) {
                return PRICE_WINDOWS.get((i + 1) % PRICE_WINDOWS.size());
            }
        }

        return PRICE_WINDOWS.get(0);
    }

    private String formatDuration(Duration duration) {
        var totalSeconds = duration.toSeconds();
        var days = totalSeconds / 86_400L;
        var hours = (totalSeconds % 86_400L) / 3_600L;
        var minutes = (totalSeconds % 3_600L) / 60L;
        var seconds = totalSeconds % 60L;
        var result = new StringBuilder();

        if (days > 0) {
            result.append(days).append("d");
        }

        if (hours > 0) {
            result.append(hours).append("h");
        }

        if (minutes > 0) {
            result.append(minutes).append("m");
        }

        if (seconds > 0 || result.length() == 0) {
            result.append(seconds).append("s");
        }

        return result.toString();
    }
}
