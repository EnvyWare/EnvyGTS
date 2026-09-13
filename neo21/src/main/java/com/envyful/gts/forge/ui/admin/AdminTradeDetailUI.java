package com.envyful.gts.forge.ui.admin;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.trade.SoldTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.ui.TradeHistoryDisplay;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class AdminTradeDetailUI {

    private ConfigInterface menuSettings = ConfigInterface.builder()
            .title("EnvyGTS Listing")
            .height(6)
            .fillType(ConfigInterface.FillType.BLOCK)
            .fillerItem(ConfigItem.builder()
                    .type("minecraft:black_stained_glass_pane")
                    .name(" ")
                    .amount(1)
                    .build())
            .build();

    private int itemPositionX = 4;

    private int itemPositionY = 1;

    private ExtendedConfigItem sellerItem = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bSeller")
            .lore("&f%seller%", "&8%seller_uuid%")
            .positions(Pair.of(2, 2))
            .build();

    private ExtendedConfigItem buyerItem = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bBuyer")
            .lore("&f%buyer%", "&8%buyer_uuid%")
            .positions(Pair.of(6, 2))
            .build();

    private ExtendedConfigItem noBuyerItem = ExtendedConfigItem.builder()
            .type("minecraft:barrier")
            .amount(1)
            .name("&7No Buyer")
            .lore("&7This listing was never sold")
            .positions(Pair.of(6, 2))
            .build();

    private ExtendedConfigItem pricingItem = ExtendedConfigItem.builder()
            .type("minecraft:gold_ingot")
            .amount(1)
            .name("&6Pricing")
            .lore("&7Listed for &a$%listed_price%", "&7Sold for &a$%sold_price%")
            .positions(Pair.of(2, 3))
            .build();

    private ExtendedConfigItem outcomeItem = ExtendedConfigItem.builder()
            .type("minecraft:paper")
            .amount(1)
            .name("&bOutcome")
            .lore("&f%outcome%", "&7%outcome_time%")
            .positions(Pair.of(4, 3))
            .build();

    private ExtendedConfigItem timelineItem = ExtendedConfigItem.builder()
            .type("minecraft:clock")
            .amount(1)
            .name("&eTimeline")
            .lore("&7Listed: &f%listed_time%", "&7Expiry: &f%expiry_time%", "&7Outcome: &f%outcome_time%")
            .positions(Pair.of(6, 3))
            .build();

    private ExtendedConfigItem identifiersItem = ExtendedConfigItem.builder()
            .type("minecraft:name_tag")
            .amount(1)
            .name("&bIdentifiers")
            .lore("&7Type: &f%type%", "&7Offer ID:", "&8%offer_id%")
            .positions(Pair.of(4, 4))
            .build();

    private ExtendedConfigItem copyButton = ExtendedConfigItem.builder()
            .type("minecraft:chest")
            .amount(1)
            .name("&aSend me a copy")
            .lore(
                    "&7Gives you &f%item%",
                    " ",
                    "&c&lThis creates a duplicate.",
                    "&cThe original still belongs to whoever",
                    "&cthe listing went to."
            )
            .positions(Pair.of(8, 5))
            .build();

    private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
            .type("pixelmon:eject_button")
            .amount(1)
            .name("&cBack")
            .positions(Pair.of(0, 5))
            .build();

    private ConfigInterface copyConfirmSettings = ConfigInterface.builder()
            .title("Take a copy?")
            .height(3)
            .fillType(ConfigInterface.FillType.BLOCK)
            .fillerItem(ConfigItem.builder()
                    .type("minecraft:black_stained_glass_pane")
                    .name(" ")
                    .amount(1)
                    .build())
            .build();

    private int copyConfirmItemPositionX = 4;

    private int copyConfirmItemPositionY = 1;

    private ExtendedConfigItem copyAcceptButton = ExtendedConfigItem.builder()
            .type("minecraft:lime_wool")
            .amount(1)
            .name("&a&lCONFIRM")
            .lore("&7Give me a copy of &f%item%")
            .positions(Pair.of(2, 1))
            .build();

    private ExtendedConfigItem copyDeclineButton = ExtendedConfigItem.builder()
            .type("minecraft:red_wool")
            .amount(1)
            .name("&c&lCANCEL")
            .lore("&7Go back without taking a copy")
            .positions(Pair.of(6, 1))
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
                .extendedConfigItem(player, pane, this.timelineItem, placeholder);

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, this.identifiersItem, placeholder);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> this.confirmCopy(player, trade, back))
                .extendedConfigItem(player, pane, this.copyButton, placeholder);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> back.run())
                .extendedConfigItem(player, pane, this.backButton);

        pane.open(player, this.menuSettings);
    }

    private void confirmCopy(ForgeEnvyPlayer player, Trade trade, Runnable back) {
        var pane = this.copyConfirmSettings.toPane();
        var placeholder = TradeHistoryDisplay.placeholder(trade);

        pane.set(this.copyConfirmItemPositionX, this.copyConfirmItemPositionY,
                GuiFactory.displayableBuilder(trade.offer().item().display()).build());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> PlatformProxy.runSync(() -> this.copy(player, trade, back)))
                .extendedConfigItem(player, pane, this.copyAcceptButton, placeholder);

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> this.openDetails(player, trade, back))
                .extendedConfigItem(player, pane, this.copyDeclineButton, placeholder);

        pane.open(player, this.copyConfirmSettings);
    }

    private void copy(ForgeEnvyPlayer player, Trade trade, Runnable back) {
        var displayName = trade.offer().item().displayName();

        if (!trade.offer().item().collect(player)) {
            player.message("&cThere was no room to give you a copy of &f" + displayName + "&c.");
            this.openDetails(player, trade, back);
            return;
        }

        EnvyGTSForge.getLogger().info("{} ({}) took a copy of {} from GTS listing {} using the admin menu",
                player.getName(), player.getUniqueId(), displayName, trade.offer().id());

        player.message("&aA copy of &f" + displayName + " &ahas been sent to you.");
        this.openDetails(player, trade, back);
    }
}
