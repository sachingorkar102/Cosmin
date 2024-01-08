package com.github.sachin.cosmin.compat;

import com.github.sachin.cosmin.Cosmin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CosmeticCoreAPI {

    public static final boolean isEnabled;

    static {
        isEnabled = Cosmin.getInstance().getServer().getPluginManager().isPluginEnabled("CosmeticsCore");
    }


    public static ItemStack getHatItem(Player player){
        for(Object obj : new dev.lone.cosmeticscore.api.temporary.CosmeticsCoreApi().getEquippedCosmeticsAccessors(player)){
            dev.lone.cosmeticscore.api.temporary.CosmeticAccessor accessor = (dev.lone.cosmeticscore.api.temporary.CosmeticAccessor) obj;

            if(accessor.getKey().endsWith("_hat")){
                return accessor.getGuiModelItem();
            }
        }
        return null;
    }

}
