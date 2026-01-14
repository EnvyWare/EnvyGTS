package com.envyful.gts.forge.api.item;

import com.envyful.api.gui.item.Displayable;
import com.envyful.api.player.EnvyPlayer;
import net.minecraft.world.item.ItemStack;

public interface TradeItem {

    String displayName();

    Displayable.Builder<ItemStack> display();

    boolean collect(EnvyPlayer<?> player);

    String serialize();

}
