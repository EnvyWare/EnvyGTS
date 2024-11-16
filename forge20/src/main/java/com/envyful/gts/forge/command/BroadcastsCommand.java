package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;

@Command(
        value = {
                "broadcasts",
                "bc"
        }
)
@Permissible("com.envyful.gts.command.settings.broadcasts")
public class BroadcastsCommand {

    @CommandProcessor
    public void onCommand(@Sender ForgeEnvyPlayer sender, String[] args) {
        if (!sender.hasAttribute(GTSAttribute.class)) {
            return;
        }

        var attribute = sender.getAttributeNow(GTSAttribute.class);

        if (args.length == 0) {
            attribute.getSettings().setToggledBroadcasts(!attribute.getSettings().isToggledBroadcasts());

            if (attribute.getSettings().isToggledBroadcasts()) {
                sender.message(UtilChatColour.colour(
                        EnvyGTSForge.getLocale().getMessages().getToggledBroadcastsOn()
                ));
            } else {
                sender.message(UtilChatColour.colour(
                        EnvyGTSForge.getLocale().getMessages().getToggledBroadcastsOff()
                ));
            }

            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            attribute.getSettings().setToggledBroadcasts(true);
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getToggledBroadcastsOn()
            ));
        } else {
            attribute.getSettings().setToggledBroadcasts(false);
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getToggledBroadcastsOff()
            ));
        }
    }
}
