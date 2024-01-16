package com.github.sachin.cosmin.compat;

import com.github.sachin.cosmin.Cosmin;
import org.bukkit.inventory.ItemStack;

public class OraxenAPI {

    public static final boolean isEnabled;

    static {
        isEnabled = Cosmin.getInstance().getServer().getPluginManager().isPluginEnabled("Oraxen");
    }

    public static ItemStack getItem(String itemId){
        return io.th0rgal.oraxen.api.OraxenItems.getItemById(itemId).build();
    }

    public static boolean isItem(String itemId){
        return io.th0rgal.oraxen.api.OraxenItems.exists(itemId);
    }
}
