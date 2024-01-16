package com.github.sachin.cosmin.compat;

import com.github.sachin.cosmin.Cosmin;
import org.bukkit.inventory.ItemStack;

public class ItemsAddersAPI {

    public static final boolean isEnabled;

    static {
        isEnabled = Cosmin.getInstance().getServer().getPluginManager().isPluginEnabled("ItemsAdder");
    }

    public static ItemStack getItem(String name){
        return dev.lone.itemsadder.api.CustomStack.getInstance(name).getItemStack();
    }

    public static boolean isInRegistry(String name){
        return dev.lone.itemsadder.api.CustomStack.isInRegistry(name);
    }
}
