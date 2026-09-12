package com.envyful.gts.forge.ui.admin;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class AdminTradesUI {

    private ConfigInterface menuSettings = ConfigInterface.builder()
            .title("EnvyGTS Admin")
            .height(3)
            .fillType(ConfigInterface.FillType.BLOCK)
            .fillerItem(ConfigItem.builder()
                    .type("minecraft:black_stained_glass_pane")
                    .name(" ")
                    .amount(1)
                    .build())
            .build();

    private ExtendedConfigItem globalHistoryButton = ExtendedConfigItem.builder()
            .type("minecraft:book")
            .amount(1)
            .name("&bGlobal Trade History")
            .lore("&7View all completed GTS trades")
            .positions(Pair.of(2, 1))
            .build();

    private ExtendedConfigItem playerHistoryButton = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bPlayer Trade History")
            .lore("&7Search by player name or UUID")
            .positions(Pair.of(4, 1))
            .build();

    private ExtendedConfigItem highestPricesButton = ExtendedConfigItem.builder()
            .type("minecraft:gold_ingot")
            .amount(1)
            .name("&6Highest Sold Prices")
            .lore("&7Enter a time window to inspect market prices")
            .positions(Pair.of(6, 1))
            .build();

    public void openMenu(ForgeEnvyPlayer player) {
        var pane = this.menuSettings.toPane();

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradeHistoryUI().openGlobalHistory(player))
                .extendedConfigItem(player, pane, this.globalHistoryButton);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminTradeHistoryUI().openPlayerHistoryInput(player))
                .extendedConfigItem(player, pane, this.playerHistoryButton);

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getAdminHighestPricesUI().openInput(player))
                .extendedConfigItem(player, pane, this.highestPricesButton);

        pane.open(player, this.menuSettings);
    }
}
