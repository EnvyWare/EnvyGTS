package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.forge.chat.UtilChatColour;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigPath("config/ReforgedGTS/locale.yml")
@ConfigSerializable
public class LocaleConfig extends AbstractYamlConfig {

    private String guiName = "GTS";

    public LocaleConfig() {
        super();
    }

    public String getGuiName() {
        return UtilChatColour.translateColourCodes('&', this.guiName);
    }
}
