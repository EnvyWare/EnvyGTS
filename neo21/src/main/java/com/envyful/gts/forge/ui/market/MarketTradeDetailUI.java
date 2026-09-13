package com.envyful.gts.forge.ui.market;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.api.trade.SoldTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.ui.TradeHistoryDisplay;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 *
 * The read only breakdown of a single historical listing, for players browsing the market UIs
 *
 */
@ConfigSerializable
public class MarketTradeDetailUI {

    private ConfigInterface menuSettings = ConfigInterface.builder()
            .title("EnvyGTS Listing")
            .height(3)
            .fillType(ConfigInterface.FillType.BLOCK)
            .fillerItem(ConfigItem.builder()
                    .type("minecraft:black_stained_glass_pane")
                    .name(" ")
                    .amount(1)
                    .build())
            .build();

    private int itemPositionX = 4;

    private int itemPositionY = 0;

    private ExtendedConfigItem sellerItem = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bSeller")
            .lore("&f%seller%")
            .positions(Pair.of(1, 1))
            .build();

    private ExtendedConfigItem buyerItem = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bBuyer")
            .lore("&f%buyer%")
            .positions(Pair.of(3, 1))
            .build();

    private ExtendedConfigItem noBuyerItem = ExtendedConfigItem.builder()
            .type("minecraft:barrier")
            .amount(1)
            .name("&7No Buyer")
            .lore("&7This listing was never sold")
            .positions(Pair.of(3, 1))
            .build();

    private ExtendedConfigItem pricingItem = ExtendedConfigItem.builder()
            .type("minecraft:gold_ingot")
            .amount(1)
            .name("&6Pricing")
            .lore("&7Listed for &a$%listed_price%", "&7Sold for &a$%sold_price%")
            .positions(Pair.of(5, 1))
            .build();

    private ExtendedConfigItem outcomeItem = ExtendedConfigItem.builder()
            .type("minecraft:clock")
            .amount(1)
            .name("&bOutcome")
            .lore("&f%outcome%", " ", "&7Listed: &f%listed_time%", "&7Finished: &f%outcome_time%")
            .positions(Pair.of(7, 1))
            .build();

    private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
            .type("pixelmon:eject_button")
            .amount(1)
            .name("&cBack")
            .positions(Pair.of(4, 2))
            .build();

    public void openDetails(ForgeEnvyPlayer player, Trade trade, Runnable back) {
        var pane = this.menuSettings.toPane();
        var placeholder = TradeHistoryDisplay.placeholder(trade);

        pane.set(this.itemPositionX, this.itemPositionY,
                GuiFactory.displayableBuilder(trade.offer().item().display()).build());

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, this.sellerItem, placeholder);

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, trade instanceof SoldTrade ? this.buyerItem : this.noBuyerItem, placeholder);

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, this.pricingItem, placeholder);

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, this.outcomeItem, placeholder);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> back.run())
                .extendedConfigItem(player, pane, this.backButton);

        pane.open(player, this.menuSettings);
    }
}
