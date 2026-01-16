package com.envyful.gts.forge.api.money;

import com.envyful.api.player.EnvyPlayer;

public interface Money {

    double getPrice();

    double getStartingPrice();

    void bid(EnvyPlayer<?> player, double amount);

}
