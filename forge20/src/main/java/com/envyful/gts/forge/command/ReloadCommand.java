package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.GTSReloadEvent;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

@Command(
        value = "reload",
        description = "Reloads the configs"
)
@Child
@Permissible("com.envyful.gts.command.reload")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender, String[] args) {
        EnvyGTSForge.getInstance().loadConfig();
        MinecraftForge.EVENT_BUS.post(new GTSReloadEvent());
        sender.sendSystemMessage(Component.literal("Reloaded config"));
    }
}
