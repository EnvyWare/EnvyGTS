package com.envyful.gts.forge.ui;

import com.envyful.api.time.UtilTime;
import com.envyful.gts.forge.EnvyGTSForge;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * The time windows a trade lookup UI can be viewed with. Windows are written the same way as trade durations,
 * for example 1h, 24h, or 7d
 *
 */
@ConfigSerializable
public class TradeWindowConfig {

    private static final Duration FALLBACK_WINDOW = Duration.ofDays(1);

    @Comment("The window the UI is opened with")
    private String defaultWindow = "1d";

    @Comment("The windows the player can cycle through in the UI. The default window does not have to be one of these")
    private List<String> windowOptions = List.of("1d", "3d", "7d", "30d");

    @Comment("Whether cycling past the last window shows every trade ever, instead of going back to the first window")
    private boolean includeAllTime = false;

    private transient Duration cachedDefaultWindow = null;
    private transient List<Duration> cachedWindowOptions = null;

    public TradeWindowConfig() {
    }

    public TradeWindowConfig(String defaultWindow, List<String> windowOptions) {
        this.defaultWindow = defaultWindow;
        this.windowOptions = windowOptions;
    }

    public TradeWindowConfig(String defaultWindow, List<String> windowOptions, boolean includeAllTime) {
        this(defaultWindow, windowOptions);
        this.includeAllTime = includeAllTime;
    }

    public Duration getDefaultWindow() {
        if (this.cachedDefaultWindow == null) {
            this.cachedDefaultWindow = parse(this.defaultWindow).orElseGet(() -> {
                EnvyGTSForge.getLogger().error("'{}' is not a valid GTS trade window, falling back to {}",
                        this.defaultWindow, format(FALLBACK_WINDOW));
                return FALLBACK_WINDOW;
            });
        }

        return this.cachedDefaultWindow;
    }

    public List<Duration> getWindowOptions() {
        if (this.cachedWindowOptions == null) {
            var parsed = new ArrayList<Duration>();

            for (var windowOption : this.windowOptions) {
                var window = parse(windowOption);

                if (window.isEmpty()) {
                    EnvyGTSForge.getLogger().error("'{}' is not a valid GTS trade window, so has been ignored", windowOption);
                    continue;
                }

                parsed.add(window.get());
            }

            this.cachedWindowOptions = List.copyOf(parsed);
        }

        return this.cachedWindowOptions;
    }

    /**
     *
     * Gets the next window the player should be shown when they cycle the window button. Windows that are not
     * one of the {@link #getWindowOptions()}, such as the default, cycle round to the first option
     *
     * @param current The window currently being viewed, or null when every trade ever is being shown
     * @return The next window, or null for every trade ever
     *
     */
    @Nullable
    public Duration getNext(@Nullable Duration current) {
        var options = this.getWindowOptions();

        if (options.isEmpty()) {
            return current;
        }

        var next = options.indexOf(current) + 1;

        if (next >= options.size()) {
            return this.includeAllTime ? null : options.get(0);
        }

        return options.get(next);
    }

    /**
     *
     * Formats a window the same way it is written in the config, for example 1d or 12h
     *
     * @param window The window to format
     * @return The formatted window
     *
     */
    public static String format(Duration window) {
        var totalSeconds = window.toSeconds();
        var days = totalSeconds / 86_400L;
        var hours = (totalSeconds % 86_400L) / 3_600L;
        var minutes = (totalSeconds % 3_600L) / 60L;
        var seconds = totalSeconds % 60L;
        var result = new StringBuilder();

        if (days > 0) {
            result.append(days).append("d");
        }

        if (hours > 0) {
            result.append(hours).append("h");
        }

        if (minutes > 0) {
            result.append(minutes).append("m");
        }

        if (seconds > 0 || result.isEmpty()) {
            result.append(seconds).append("s");
        }

        return result.toString();
    }

    private static Optional<Duration> parse(String window) {
        var millis = UtilTime.attemptParseTime(window);

        if (millis.isEmpty() || millis.get() <= 0) {
            return Optional.empty();
        }

        return Optional.of(Duration.ofMillis(millis.get()));
    }
}
