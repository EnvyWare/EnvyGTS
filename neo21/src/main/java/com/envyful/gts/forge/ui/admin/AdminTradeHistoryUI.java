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
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.trade.Trade;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import com.pixelmonmod.pixelmon.api.dialogue.InputPattern;
import net.minecraft.network.chat.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

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

    private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
            .type("pixelmon:eject_button")
            .amount(1)
            .name("&cBack")
            .positions(Pair.of(4, 5))
            .build();

    public void openGlobalHistory(ForgeEnvyPlayer player) {
        UtilConcurrency.runAsync(() -> {
            var history = EnvyGTSForge.getTradeService().historicalListings();

            AdminTradeDisplay.reportFailures(player, history);
            this.openHistory(player, history.trades(), 1);
        });
    }

    public void openPlayerHistory(ForgeEnvyPlayer player, String playerQuery) {
        UtilConcurrency.runAsync(() -> {
            var history = EnvyGTSForge.getTradeService().historicalListings(playerQuery);

            AdminTradeDisplay.reportFailures(player, history);

            if (history.trades().isEmpty()) {
                player.message("&cNo GTS history found for " + playerQuery);
            }

            this.openHistory(player, history.trades(), 1);
        });
    }

    @SuppressWarnings("unchecked")
    public void openHistory(ForgeEnvyPlayer player, List<Trade> trades, int page) {
        var openPage = new AtomicInteger(page);

        UtilConfigInterface.paginatedBuilder(trades)
                .itemConversion(trade -> AdminTradeDisplay.build(trade, false)
                        .singleClick()
                        .asyncClick()
                        .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradeDetailUI()
                                .openDetails(player, trade, () -> this.openHistory(player, trades, openPage.get())))
                        .build())
                .configSettings(this.historySettings)
                .extraItems((pane, currentPage) -> {
                    openPage.set(currentPage);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradesUI().openMenu(player))
                            .extendedConfigItem(player, pane, this.backButton);
                })
                .open(player, page);
    }

    public void openPlayerHistoryInput(ForgeEnvyPlayer player) {
        player.getParent().closeContainer();

        PlatformProxy.runLater(() -> DialogueFactory.builder()
                .title(PlatformProxy.<Component>flatParse("&bPlayer Trade History"))
                .description(UtilChatColour.colour("&7Enter a player name or UUID to inspect their GTS history."))
                .defaultText(player.getName())
                .maxInputLength(36)
                .closeOnEscape()
                .hideUI()
                .onClose(closedScreen -> EnvyGTSForge.getGui().getAdminTradesUI().openMenu(player))
                .buttons(DialogueButton.builder()
                        .text("Search")
                        .backgroundColor(Color.GRAY)
                        .acceptedInputs(InputPattern.of(Pattern.compile("[A-Za-z0-9_\\-]{1,36}"),
                                UtilChatColour.colour("&cEnter a valid player name or UUID.")))
                        .onClick(submitted -> this.openPlayerHistory(player, submitted.getInput().trim()))
                        .build())
                .sendTo(player.getParent()), 5);
    }
}
