package com.github.sachin.cosmin.integration;

import com.github.sachin.cosmin.Cosmin;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenApi {

    public static final boolean isEnabled;

    static {
        isEnabled = Cosmin.getInstance().getServer().getPluginManager().isPluginEnabled("Oraxen");
    }

    public static ItemStack getItem(String name) {
        return OraxenItems.getItemById(name).build();
    }
}
