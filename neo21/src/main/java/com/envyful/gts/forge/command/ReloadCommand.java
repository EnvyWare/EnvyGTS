package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.platform.Messageable;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.GTSReloadEvent;
import net.neoforged.neoforge.common.NeoForge;

@Command(
        value = "reload"
)
@Permissible("com.envyful.gts.command.reload")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender Messageable<?> sender, String[] args) {
        EnvyGTSForge.getInstance().loadConfig();
        NeoForge.EVENT_BUS.post(new GTSReloadEvent());
        sender.message("Reloaded config");
    }
}
