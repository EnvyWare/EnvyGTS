package com.envyful.reforged.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.ui.MainUI;
import net.minecraft.entity.player.EntityPlayerMP;

@Command(
        value = "gts",
        description = "Main GTS command",
        aliases = {
                "globaltrade"
        }
)
@Permissible("reforged.gts.command.gts")
@SubCommands({
        SellCommand.class
})
public class GTSCommand {

    @CommandProcessor
    public void onCommand(@Sender EntityPlayerMP player, String[] args) {
        MainUI.open(ReforgedGTSForge.getInstance().getPlayerManager().getPlayer(player));
    }
}
