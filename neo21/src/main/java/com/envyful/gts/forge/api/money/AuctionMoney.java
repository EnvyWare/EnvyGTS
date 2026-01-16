package com.envyful.gts.forge.api.money;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.api.player.PlayerInfo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AuctionMoney implements Money {

    private final double startingPrice;

    private List<Bid> bidHistory = new ArrayList<>();
    private Bid highestBid;

    public AuctionMoney(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    @Override
    public double getPrice() {
        if (this.highestBid == null) {
            return this.getStartingPrice();
        }

        return this.highestBid.amount();
    }

    @Override
    public double getStartingPrice() {
        return this.startingPrice;
    }

    @Override
    public void bid(EnvyPlayer<?> player, double amount) {
        this.highestBid = new Bid(new PlayerInfo(player), amount, Instant.now());
    }
}
