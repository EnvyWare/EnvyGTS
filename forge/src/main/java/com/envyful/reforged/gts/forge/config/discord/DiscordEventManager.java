package com.envyful.reforged.gts.forge.config.discord;

import com.envyful.api.discord.DiscordWebHook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DiscordEventManager {

    private static DiscordEvent publishHandler;

    public static void init() {
        try {
            publishHandler = loadHandler("config/ReforgedGTS/webhooks/publisher.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiscordEvent loadHandler(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            return new DiscordEvent(false);
        }

        String actual = String.join(System.lineSeparator(), Files.readAllLines(file.toPath()));
        DiscordWebHook webHook = DiscordWebHook.fromJson(actual);

        return new DiscordEvent(webHook);
    }
}
