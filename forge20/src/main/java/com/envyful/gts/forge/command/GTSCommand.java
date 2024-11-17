package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.ui.ViewTradesUI;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.server.level.ServerPlayer;

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
        StorageProxy.getPartyNow(player.getParent()).retrieveAll("GTS");
        ViewTradesUI.openUI(player);
    }
}
