package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.ui.ViewTradesUI;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.server.level.ServerPlayer;

@Command(
        value = "gts",
        description = "Main GTS command",
        aliases = {
                "globaltrade"
        }
)
@Permissible("com.envyful.gts.command.gts")
@SubCommands({
        SellCommand.class, ReloadCommand.class, BroadcastsCommand.class
})
public class GTSCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayer player, String[] args) {
        if (player.isPassenger()) {
            player.sendSystemMessage(UtilChatColour.colour(EnvyGTSForge.getLocale().getMessages().getCannotRideAndGts()));
            return;
        }

        if (EnvyGTSForge.getConfig().isEnableOpeningUIMessage()) {
            player.sendSystemMessage(UtilChatColour.colour(EnvyGTSForge.getLocale().getMessages().getOpeningUi()));
        }

        StorageProxy.getParty(player).retrieveAll("GTS");
        ViewTradesUI.openUI(EnvyGTSForge.getPlayerManager().getPlayer(player));
    }
}
