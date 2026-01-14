package com.envyful.gts.forge.api.money;

import com.envyful.api.player.EnvyPlayer;

public class InstantPurchaseMoney implements Money {

    private final double price;

    public InstantPurchaseMoney(double price) {
        this.price = price;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public double getStartingPrice() {
        return this.price;
    }

    @Override
    public void bid(EnvyPlayer<?> player, double amount) {

    }
}
