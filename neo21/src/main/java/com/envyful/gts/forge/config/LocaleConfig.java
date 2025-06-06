package com.envyful.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

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

    private String noEnchantsText = "NO ENCHANTS!";
    private String enchantHeader = "ENCHANTS:";
    private String enchantFooter = "WOW!";
    private String enchantSeperator = "\n";
    private String enchantFormat = "%enchant% - %level%";
    @Comment("The message sent to the player if they try to add an item to the GTS that fits one of the regex filters in the blacklist")
    private List<String> blockedItem = List.of(
            "&c&l(!) &cThat item was blocked from being added to the GTS because %reason%"
    );

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

    public String getNoEnchantsText() {
        return this.noEnchantsText;
    }

    public String getEnchantHeader() {
        return this.enchantHeader;
    }

    public String getEnchantFooter() {
        return this.enchantFooter;
    }

    public String getEnchantSeperator() {
        return this.enchantSeperator;
    }

    public String getEnchantFormat() {
        return this.enchantFormat;
    }

    public List<String> getBlockedItem() {
        return this.blockedItem;
    }

    @ConfigSerializable
    public static class Messages {

        private List<String> cannotRideAndGts = List.of("&c&l(!) Please stop riding whilst trying to open the GTS");
        private List<String> openingUi = List.of("&e&l(!) &eOpening GTS...");
        private String sellInsuffucientArgs = "&c&l(!) &cInsufficient args! /gts s <amount> <price> [duration]";
        private String sellNoItemInHand = "&c&l(!) &cYou must have an item in your hand to sell!";

        private String amountMustBePositive = "&c&l(!) &cAmount must be a positive number!";
        private String priceMustBeMoreThanOne = "&c&l(!) &cPrice cannot be less than $1";
        private String inventoryFull = "&c&l(!) &cYour inventory is full!";
        private String insufficientFunds = "&c&l(!) &cYou don't have enough money!";
        private String maxTradesAlreadyReached = "&c&l(!) &cYou cannot add anymore trades to the GTS";
        private String notEnoughItems = "&c&l(!) &cYou don't have enough in your hand to sell this amount!";
        private String cannotSellBlacklisted = "&c&l(!) &cYou cannot sell this item as it's blacklisted!";
        private String cannotGoAboveMaxPrice = "&c&l(!) &cYou cannot sell for more than $%max_price%";
        private String cannotGoBelowMinTime = "&c&l(!) &cYou cannot sell for less than %min_duration% seconds";
        private String cannotGoAboveMaxTime = "&c&l(!) &cYou cannot sell for more than %max_duration% seconds";

        private String addedItemToGts = "&e&l(!) &eSuccessfully listed item on GTS";
        private String adminRemoveTrade = "&e&l(!) &eYou successfully removed the trade from the GTS";
        private String purchasedTrade = "&e&l(!) &eSuccessfully purchased trade from GTS";
        private String removedOwnTrade = "&e&l(!) &eSuccessfully removed your trade";

        private String toggledBroadcastsOn = "&e&l(!) &eToggled broadcasts &a&lON";
        private String toggledBroadcastsOff = "&e&l(!) &eToggled broadcasts &c&lOFF";

        private String itemWasPurchased = "&e&l(!) &eYour %item% auction was purchased by %buyer% for %price% and &a$%tax%&e was taken!";
        private String itemsToClaim = "&c&l(!) &cYou have auctions to claim in the GTS!";

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

        public Messages() {}

        public String getCannotSellBlacklisted() {
            return this.cannotSellBlacklisted;
        }

        public String getRemovedOwnTrade() {
            return this.removedOwnTrade;
        }

        public String getInsufficientFunds() {
            return this.insufficientFunds;
        }

        public String getPurchasedTrade() {
            return this.purchasedTrade;
        }

        public String getAdminRemoveTrade() {
            return this.adminRemoveTrade;
        }

        public String getInventoryFull() {
            return this.inventoryFull;
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

        public String getAddedItemToGts() {
            return this.addedItemToGts;
        }

        public String getToggledBroadcastsOn() {
            return this.toggledBroadcastsOn;
        }

        public String getToggledBroadcastsOff() {
            return this.toggledBroadcastsOff;
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

        public String getItemWasPurchased() {
            return this.itemWasPurchased;
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
