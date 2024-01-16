package com.envyful.gts.api.discord;

import com.envyful.api.concurrency.UtilLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class DiscordEventManager {

    private static DiscordEvent publishHandler;
    private static DiscordEvent purchaseHandler;
    private static DiscordEvent removeHandler;

    public static void init() {
        try {
            publishHandler = loadHandler("config/EnvyGTS/webhooks/publisher");
            purchaseHandler = loadHandler("config/EnvyGTS/webhooks/purchaser");
            removeHandler = loadHandler("config/EnvyGTS/webhooks/remover");
        } catch (IOException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Failed to load discord handlers", e));
        }
    }

    public static DiscordEvent loadHandler(String filePath) throws IOException {
        File pokemonFile = new File(filePath + "-pokemon.json");
        File itemFile = new File(filePath + "-item.json");
        String pokemonJSON = "";
        String itemJSON = "";

        if (!pokemonFile.exists() && !itemFile.exists()) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Neither the pokemon or item webhook exists for {}", filePath));
            return new DiscordEvent();
        }

        if (pokemonFile.exists()) {
            pokemonJSON = String.join(System.lineSeparator(), Files.readAllLines(pokemonFile.toPath(), StandardCharsets.UTF_8));
            UtilLogger.logger().ifPresent(logger -> logger.error("The pokemon webhook does not exist for {}", filePath));
        }

        if (itemFile.exists()) {
            itemJSON = String.join(System.lineSeparator(), Files.readAllLines(itemFile.toPath(), StandardCharsets.UTF_8));
        } else {
            UtilLogger.logger().ifPresent(logger -> logger.error("The item webhook does not exist for {}", filePath));
        }

        return new DiscordEvent(true, pokemonJSON, itemJSON);
    }

    public static DiscordEvent getPublishHandler() {
        return publishHandler;
    }

    public static DiscordEvent getPurchaseHandler() {
        return purchaseHandler;
    }

    public static DiscordEvent getRemoveHandler() {
        return removeHandler;
    }
}
