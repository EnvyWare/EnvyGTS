package com.envyful.gts.forge.api.item.type;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.item.TradeItem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class PokemonTradeItem implements TradeItem {

    private final Pokemon pokemon;

    public PokemonTradeItem(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public PokemonTradeItem(String serializedData) throws CommandSyntaxException {
        this.pokemon = PokemonFactory.create(TagParser.parseTag(serializedData), ServerLifecycleHooks.getCurrentServer().registryAccess());
    }

    @Override
    public String id() {
        return "pokemon";
    }

    @Override
    public ItemStack display() {
        return EnvyGTSForge.getGui().getSpriteConfig().fromPokemon(this.pokemon);
    }

    @Override
    public String displayName() {
        return this.pokemon.getDisplayName().getString();
    }

    public Pokemon getPokemon() {
        return this.pokemon;
    }

    @Override
    public boolean collect(EnvyPlayer<?> player) {
        var party = ((ServerPlayer) player.getParent()).getPartyNow();
        party.add(this.pokemon);
        return true;
    }

    @Override
    public String serialize() {
        return pokemon.writeToNBT(new CompoundTag(), ServerLifecycleHooks.getCurrentServer().registryAccess()).toString();
    }
}
