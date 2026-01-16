package com.envyful.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.TimeFormatConfig;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.sql.Time;
import java.util.List;
import java.util.Map;

@ConfigPath("config/EnvyGTS/locale.yml")
@ConfigSerializable
public class LocaleConfig extends AbstractYamlConfig {

    private List<String> listingBelowDataLore = Lists.newArrayList(
            "  ",
            "&bSeller: %seller%",
            "&bCost: &a$%price%",
            "&bRemaining Time: &e%expires_in%"
    );

    private List<String> listingBelowExpiredOrClaimableLore = Lists.newArrayList(
            "  ",
            "&bSeller: %original_owner%",
            "&bCost: &a$%price%"
    );

    private String moneyFormat = "%.2f";

    private String sellPriceInputDialogueTitle = "Enter price";
    private String sellPriceInputDialogueText = "Enter the price you wish to sell %pokemon% for";
    private String sellPriceInputDialogueErrorText = "Enter the price you wish to sell %pokemon% for. &cError:&r The value you entered was below the minimum sell price (%min_price%)";

    private String durationInputDialogueTitle = "Enter duration in minutes";
    private String durationInputDialogueText = "Enter the number of minutes you wish %pokemon% to be listed for";
    private String durationInputDialogueErrorText = "Enter the number of minutes you wish %pokemon% to be listed for. &cError:&r The value you entered was below the minimum duration (%min_duration%)";

    private TimeFormatConfig expiryTimeFormat = new TimeFormatConfig();

    private Messages messages = new Messages();

    public LocaleConfig() {
        super();
    }

    public Messages getMessages() {
        return this.messages;
    }

    public String getMoneyFormat() {
        return this.moneyFormat;
    }

    public List<String> getListingBelowDataLore() {
        return this.listingBelowDataLore;
    }

    public List<String> getListingBelowExpiredOrClaimableLore() {
        return this.listingBelowExpiredOrClaimableLore;
    }

    public String getSellPriceInputDialogueTitle() {
        return this.sellPriceInputDialogueTitle;
    }

    public String getSellPriceInputDialogueErrorText() {
        return this.sellPriceInputDialogueErrorText;
    }

    public String getSellPriceInputDialogueText() {
        return this.sellPriceInputDialogueText;
    }

    public String getDurationInputDialogueTitle() {
        return this.durationInputDialogueTitle;
    }

    public String getDurationInputDialogueText() {
        return this.durationInputDialogueText;
    }

    public String getDurationInputDialogueErrorText() {
        return this.durationInputDialogueErrorText;
    }

    public TimeFormatConfig getExpiryTimeFormat() {
        return this.expiryTimeFormat;
    }

    @ConfigSerializable
    public static class Messages {

        private List<String> cannotRideAndGts = List.of("&c&l(!) Please stop riding whilst trying to open the GTS");
        private List<String> openingUi = List.of("&e&l(!) &eOpening GTS...");
        private String sellInsuffucientArgs = "&c&l(!) &cInsufficient args! /gts s <amount> <price> [duration]";
        private String sellNoItemInHand = "&c&l(!) &cYou must have an item in your hand to sell!";

        private String amountMustBePositive = "&c&l(!) &cAmount must be a positive number!";
        private String priceMustBeMoreThanOne = "&c&l(!) &cPrice cannot be less than $1";
        private String insufficientFunds = "&c&l(!) &cYou don't have enough money!";
        private String maxTradesAlreadyReached = "&c&l(!) &cYou cannot add anymore trades to the GTS";
        private String notEnoughItems = "&c&l(!) &cYou don't have enough in your hand to sell this amount!";
        private String cannotSellBlacklisted = "&c&l(!) &cYou cannot sell this item as it's blacklisted!";
        private String cannotGoAboveMaxPrice = "&c&l(!) &cYou cannot sell for more than $%max_price%";
        private String cannotGoBelowMinTime = "&c&l(!) &cYou cannot sell for less than %min_duration% seconds";
        private String cannotGoAboveMaxTime = "&c&l(!) &cYou cannot sell for more than %max_duration% seconds";

        private String adminRemoveTrade = "&e&l(!) &eYou successfully removed the trade from the GTS";
        private String tradeRemovedByAdmin = "&e&l(!) &eYour trade was removed by an admin";
        private String purchasedTrade = "&e&l(!) &eSuccessfully purchased trade from GTS";
        private String removedOwnTrade = "&e&l(!) &eSuccessfully removed your trade";
        private String cannotPurchaseOwnTrade = "&c&l(!) &cYou cannot purchase your own trade!";
        private String returnNoLongerAvailable = "&c&l(!) &cThe collection you're trying to claim is no longer available!";
        private String returnCollected = "&e&l(!) &eSuccessfully collected your item from the GTS!";
        private String blockedPokemon = "&c&l(!) &cYou cannot sell that pokemon on the GTS as it is blacklisted!";
        private String insufficientPartyMembers = "&c&l(!) &cYou need one or more Pokemon in your party to sell to the GTS!";
        private String noPokemonInSlot = "&c&l(!) &cYou don't have a pokemon in that slot!";

        private String toggledBroadcastsOn = "&e&l(!) &eToggled broadcasts &a&lON";
        private String toggledBroadcastsOff = "&e&l(!) &eToggled broadcasts &c&lOFF";

        private String itemsToClaim = "&c&l(!) &cYou have auctions to claim in the GTS!";
        private String tradeNoLongerAvailable = "&c&l(!) &cThat trade is no longer available!";
        private String invenntoryFull = "&c&l(!) &cYou do not have enough inventory space to collect this item!";
        private String listedItem = "&e&l(!) &eYou have listed %name% for $%price% in the GTS!";

        private List<String> createTradeBroadcast = Lists.newArrayList(
                " ",
                "&a&lENVY GTS",
                "&e%seller%&7 added a new GTS listing for %name% for $%price%",
                " "
        );

        private Map<String, SpecBasedBroadcast> createTradeBroadcasts = ImmutableMap.of("example", new SpecBasedBroadcast("", Lists.newArrayList(
                " ",
                "&a&lENVY GTS",
                "&e%seller%&7 added a new GTS listing for %name% for $%price%",
                " "
        )));

        @Comment("The message sent to the player if they try to add an item to the GTS that fits one of the regex filters in the blacklist")
        private List<String> blockedItem = List.of(
                "&c&l(!) &cThat item was blocked from being added to the GTS because %reason%"
        );

        public Messages() {}

        public String getTradeNoLongerAvailable() {
            return this.tradeNoLongerAvailable;
        }

        public String getCannotSellBlacklisted() {
            return this.cannotSellBlacklisted;
        }

        public String getRemovedOwnTrade() {
            return this.removedOwnTrade;
        }

        public String getInsufficientFunds() {
            return this.insufficientFunds;
        }

        public String getCannotPurchaseOwnTrade() {
            return this.cannotPurchaseOwnTrade;
        }

        public String getPurchasedTrade() {
            return this.purchasedTrade;
        }

        public String getAdminRemoveTrade() {
            return this.adminRemoveTrade;
        }

        public String getTradeRemovedByAdmin() {
            return this.tradeRemovedByAdmin;
        }

        public String getReturnNoLongerAvailable() {
            return this.returnNoLongerAvailable;
        }

        public String getInventoryFull() {
            return this.invenntoryFull;
        }

        public String getReturnCollected() {
            return this.returnCollected;
        }

        public List<String> getOpeningUi() {
            return this.openingUi;
        }

        public String getSellInsuffucientArgs() {
            return this.sellInsuffucientArgs;
        }

        public String getSellNoItemInHand() {
            return this.sellNoItemInHand;
        }

        public String getAmountMustBePositive() {
            return this.amountMustBePositive;
        }

        public String getPriceMustBeMoreThanOne() {
            return this.priceMustBeMoreThanOne;
        }

        public String getToggledBroadcastsOn() {
            return this.toggledBroadcastsOn;
        }

        public String getToggledBroadcastsOff() {
            return this.toggledBroadcastsOff;
        }

        public String getBlockedPokemon() {
            return this.blockedPokemon;
        }

        public String getNoPokemonInSlot() {
            return this.noPokemonInSlot;
        }

        public String getInsufficientPartyMembers() {
            return this.insufficientPartyMembers;
        }

        public List<String> getCreateTradeBroadcast(Pokemon pokemon) {
            if (pokemon != null) {
                for (var value : this.createTradeBroadcasts.values()) {
                    if (value.getSpec() == null || (value.getSpec().matches(pokemon))) {
                        return value.getBroadcast();
                    }
                }
            }

            return this.createTradeBroadcast;
        }

        public String getMaxTradesAlreadyReached() {
            return this.maxTradesAlreadyReached;
        }

        public String getNotEnoughItems() {
            return this.notEnoughItems;
        }

        public String getCannotGoAboveMaxPrice() {
            return this.cannotGoAboveMaxPrice;
        }

        public String getCannotGoBelowMinTime() {
            return this.cannotGoBelowMinTime;
        }

        public String getItemsToClaim() {
            return this.itemsToClaim;
        }

        public String getCannotGoAboveMaxTime() {
            return this.cannotGoAboveMaxTime;
        }

        public List<String> getCannotRideAndGts() {
            return this.cannotRideAndGts;
        }

        public List<String> getBlockedItem() {
            return this.blockedItem;
        }

        public String getListedItem() {
            return this.listedItem;
        }
    }

    @ConfigSerializable
    public static class SpecBasedBroadcast {

        private String spec;
        private transient PokemonSpecification cachedSpec = null;
        private List<String> broadcast;

        public SpecBasedBroadcast(String spec, List<String> broadcast) {
            this.spec = spec;
            this.broadcast = broadcast;
        }

        public SpecBasedBroadcast() {
        }

        public PokemonSpecification getSpec() {
            if (this.spec.isBlank()) {
                return null;
            }

            if (this.cachedSpec == null) {
                this.cachedSpec = PokemonSpecificationProxy.create(this.spec).get();
            }

            return this.cachedSpec;
        }

        public List<String> getBroadcast() {
            return this.broadcast;
        }
    }
}
