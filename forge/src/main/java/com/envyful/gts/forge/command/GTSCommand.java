package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.ui.MainUI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

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
    public void onCommand(@Sender EntityPlayerMP player, String[] args) {
        if (EnvyGTSForge.getInstance().getConfig().isEnableOpeningUIMessage()) {
            player.sendMessage(new TextComponentString(EnvyGTSForge.getInstance().getLocale().getMessages().getOpeningUi()));
        }

        MainUI.open(EnvyGTSForge.getInstance().getPlayerManager().getPlayer(player));
    }
}
