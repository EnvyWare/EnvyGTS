package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.GTSReloadEvent;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;

@Command(
        value = "reload"
)
@Permissible("com.envyful.gts.command.reload")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        EnvyGTSForge.getInstance().loadConfig();
        MinecraftForge.EVENT_BUS.post(new GTSReloadEvent());
        sender.sendMessage(new StringTextComponent("Reloaded config"), Util.NIL_UUID);
    }
}
