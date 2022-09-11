package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.ui.MainUI;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;

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
    public void onCommand(@Sender ServerPlayerEntity player, String[] args) {
        if (EnvyGTSForge.getInstance().getConfig().isEnableOpeningUIMessage()) {
            player.sendMessage(UtilChatColour.colour(EnvyGTSForge.getInstance().getLocale().getMessages().getOpeningUi()), Util.NIL_UUID);
        }

        MainUI.open(EnvyGTSForge.getInstance().getPlayerManager().getPlayer(player));
    }
}
