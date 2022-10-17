package com.envyful.gts.api.gui;

import com.envyful.gts.api.gui.impl.AllFilterType;
import com.envyful.gts.api.gui.impl.InstantBuyFilterType;
import com.envyful.gts.api.gui.impl.OwnFilterType;

import java.util.ArrayList;
import java.util.List;

public class FilterTypeFactory {

    private static final List<FilterType> FILTER_TYPES = new ArrayList<>();

    public static void init() {
        register(new AllFilterType());
        register(new InstantBuyFilterType());
        register(new OwnFilterType());
    }

    public static void register(FilterType filterType) {
        FILTER_TYPES.add(filterType);
    }

    public static FilterType getNext(FilterType filterType) {
        int index = FILTER_TYPES.indexOf(filterType);
        return FILTER_TYPES.get((index + 1) % FILTER_TYPES.size());
    }
}
