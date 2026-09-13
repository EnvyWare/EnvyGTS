package com.envyful.gts.forge.ui;

import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.gts.forge.EnvyGTSForge;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import com.pixelmonmod.pixelmon.api.dialogue.InputPattern;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.awt.Color;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * The dialogue a player types one of a trade lookup's search terms into
 *
 */
@ConfigSerializable
public class TradeSearchConfig {

    private static final String DEFAULT_ALLOWED_INPUT = "[A-Za-z0-9 ._:'-]{1,64}";

    private String title = "&bSearch The GTS";

    private String description = "&7Enter a Pokemon species, or an item name, to look up.";

    private String invalidDescription = "&cThat cannot be searched for. Try 'charizard' or 'diamond sword'.";

    private String searchButtonText = "Search";

    private String clearButtonText = "Show Everything";

    @Comment("What the player is allowed to type. The default allows letters, numbers, spaces, and the punctuation Pokemon and item names use")
    private String allowedInput = DEFAULT_ALLOWED_INPUT;

    private int maxInputLength = 64;

    private transient Pattern cachedAllowedInput = null;

    public TradeSearchConfig() {
    }

    public TradeSearchConfig(String title, String description, String invalidDescription, String clearButtonText,
                             String allowedInput, int maxInputLength) {
        this.title = title;
        this.description = description;
        this.invalidDescription = invalidDescription;
        this.clearButtonText = clearButtonText;
        this.allowedInput = allowedInput;
        this.maxInputLength = maxInputLength;
    }

    /**
     *
     * Asks the player what they want to look up, then hands the updated filter back to the UI that asked
     *
     * @param player The player searching
     * @param filter The filter they are currently viewing
     * @param current The value this dialogue is editing, so that it can be pre-filled
     * @param applied Applies what they typed, or null when they clear it, to the filter
     * @param reopen Re-opens the UI with the filter it should now show
     *
     */
    public void openInput(ForgeEnvyPlayer player, TradeFilter filter, @Nullable String current,
                          BiFunction<TradeFilter, String, TradeFilter> applied, Consumer<TradeFilter> reopen,
                          Runnable cancelled) {
        player.getParent().closeContainer();

        PlatformProxy.runLater(() -> this.inputBuilder(player, filter, current, applied, reopen, cancelled, false)
                .sendTo(player.getParent()), 5);
    }

    private DialogueFactory.Builder inputBuilder(ForgeEnvyPlayer player, TradeFilter filter, @Nullable String current,
                                                 BiFunction<TradeFilter, String, TradeFilter> applied,
                                                 Consumer<TradeFilter> reopen, Runnable cancelled, boolean error) {
        return DialogueFactory.builder()
                .title(PlatformProxy.<Component>flatParse(this.title))
                .description(UtilChatColour.colour(error ? this.invalidDescription : this.description))
                .defaultText(current == null ? "" : current)
                .maxInputLength(this.maxInputLength)
                .closeOnEscape()
                .hideUI()
                .onClose(closedScreen -> cancelled.run())
                .buttons(
                        DialogueButton.builder()
                                .text(this.searchButtonText)
                                .backgroundColor(Color.GRAY)
                                .acceptedInputs(InputPattern.of(this.allowedInput(),
                                        UtilChatColour.colour(this.invalidDescription)))
                                .onClick(submitted -> {
                                    var input = submitted.getInput().trim();

                                    if (input.isBlank()) {
                                        submitted.setSettings(this.inputBuilder(player, filter, current, applied,
                                                reopen, cancelled, true).createSettings());
                                        submitted.setCloseUI(false);
                                        return;
                                    }

                                    reopen.accept(applied.apply(filter, input));
                                })
                                .build(),
                        DialogueButton.builder()
                                .text(this.clearButtonText)
                                .backgroundColor(Color.GRAY)
                                .onClick(submitted -> reopen.accept(applied.apply(filter, null)))
                                .build()
                );
    }

    private Pattern allowedInput() {
        if (this.cachedAllowedInput == null) {
            try {
                this.cachedAllowedInput = Pattern.compile(this.allowedInput);
            } catch (PatternSyntaxException e) {
                EnvyGTSForge.getLogger().error("'{}' is not a valid GTS search input pattern, falling back to {}",
                        this.allowedInput, DEFAULT_ALLOWED_INPUT, e);
                this.cachedAllowedInput = Pattern.compile(DEFAULT_ALLOWED_INPUT);
            }
        }

        return this.cachedAllowedInput;
    }
}
