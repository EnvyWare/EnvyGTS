package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.ui.ViewTradesUI;

@Command(
        value = {
                "gts",
                "globaltrade"
        }
)
@Permissible("com.envyful.gts.command.gts")
@SubCommands({
        SellCommand.class, ReloadCommand.class, BroadcastsCommand.class
})
public class GTSCommand {

    @CommandProcessor
    public void onCommand(@Sender ForgeEnvyPlayer player, String[] args) {
        if (player.getParent().isPassenger()) {
            player.message(EnvyGTSForge.getLocale().getMessages().getCannotRideAndGts());
            return;
        }

        player.message(EnvyGTSForge.getLocale().getMessages().getOpeningUi());
        player.getParent().getPartyNow().retrieveAll("GTS");
        ViewTradesUI.openUI(player);
    }
}
