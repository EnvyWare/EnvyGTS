package com.envyful.gts.api.discord;

public class DiscordEvent {

    private final boolean enabled;
    private final String pokemonJSON;
    private final String itemJSON;

    public DiscordEvent(boolean enabled, String pokemonJSON, String itemJSON) {
        this.enabled = enabled;
        this.pokemonJSON = pokemonJSON;
        this.itemJSON = itemJSON;
    }

    public DiscordEvent() {
        this(false, "", "");
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getPokemonJSON() {
        return this.pokemonJSON;
    }

    public String getItemJSON() {
        return this.itemJSON;
    }

    public boolean isPokemonEnabled() {
        return !this.pokemonJSON.isEmpty();
    }

    public boolean isItemEnabled() {
        return !this.itemJSON.isEmpty();
    }
}
