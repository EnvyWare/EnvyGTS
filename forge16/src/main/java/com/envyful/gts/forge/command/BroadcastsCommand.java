package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;

@Command(
        value = "broadcasts",
        description = "Broadcasts settings command",
        aliases = {
                "bc"
        }
)
@Permissible("com.envyful.gts.command.settings.broadcasts")
@Child
public class BroadcastsCommand {

    @CommandProcessor
    public void onCommand(@Sender ServerPlayerEntity player, String[] args) {
        ForgeEnvyPlayer sender = EnvyGTSForge.getInstance().getPlayerManager().getPlayer(player);
        GTSAttribute attribute = sender.getAttribute(EnvyGTSForge.class);

        if (attribute == null) {
            return;
        }

        if (args.length == 0) {
            attribute.getSettings().setToggledBroadcasts(!attribute.getSettings().isToggledBroadcasts());

            if (attribute.getSettings().isToggledBroadcasts()) {
                sender.message(UtilChatColour.translateColourCodes(
                        '&',
                        EnvyGTSForge.getInstance().getLocale().getMessages().getToggledBroadcastsOn()
                ));
            } else {
                sender.message(UtilChatColour.translateColourCodes(
                        '&',
                        EnvyGTSForge.getInstance().getLocale().getMessages().getToggledBroadcastsOff()
                ));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            attribute.getSettings().setToggledBroadcasts(true);
            sender.message(UtilChatColour.translateColourCodes(
                    '&',
                    EnvyGTSForge.getInstance().getLocale().getMessages().getToggledBroadcastsOn()
            ));
        } else {
            attribute.getSettings().setToggledBroadcasts(false);
            sender.message(UtilChatColour.translateColourCodes(
                    '&',
                    EnvyGTSForge.getInstance().getLocale().getMessages().getToggledBroadcastsOff()
            ));
        }
    }
}
